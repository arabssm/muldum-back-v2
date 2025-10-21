package co.kr.muldum.application.teamspace;

import co.kr.muldum.application.teamspace.dto.*;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.model.TeamSettings;
import co.kr.muldum.domain.teamspace.model.TeamType;
import co.kr.muldum.domain.teamspace.model.TeamspaceMember;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.teamspace.repository.TeamspaceMemberRepository;
import co.kr.muldum.domain.user.model.Role;
import co.kr.muldum.domain.user.repository.UserRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TeamspaceService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamspaceMemberRepository teamspaceMemberRepository;
    private final GoogleSheetImportService googleSheetImportService;

    @Value("${team.default-banner-image}")
    private String defaultTeamBannerImage;

    private TeamType getTeamTypeFromSheetName(String sheetName) {
        if (sheetName == null) {
            throw new CustomException(ErrorCode.INVALID_GOOGLE_SHEET_URL);
        }

        String normalized = sheetName.trim();  // 공백 제거
        System.out.println("현재 시트 이름: " + normalized); // 실제 이름 디버깅

        if (normalized.equalsIgnoreCase("네트워크")) {
            return TeamType.NETWORK;
        } else if (normalized.equalsIgnoreCase("전공동아리")) {
            return TeamType.MAJOR;
        }

        throw new CustomException(ErrorCode.INVALID_GOOGLE_SHEET_URL);
    }

    @Transactional
    public TeamspaceInviteResponseDto inviteStudents(TeamspaceInviteRequestDto requestDto) {
        String googleSheetUrl = requestDto.getGoogleSheetUrl();
        if (googleSheetUrl == null || googleSheetUrl.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_GOOGLE_SHEET_URL);
        }

        Map<String, Object> sheetData = googleSheetImportService.parseTeamInviteRows(googleSheetUrl);
        String sheetName = (String) sheetData.get("sheetName");
        List<Map<String, String>> rows = (List<Map<String, String>>) sheetData.get("rows");

        TeamType teamType = getTeamTypeFromSheetName(sheetName);

        for (Map<String, String> row : rows) {
            String teamName = row.get("team");
            String studentId = row.get("studentId");
            String studentName = row.get("name");
            String roleStr = row.get("role");

            if (teamName == null || studentId == null || studentName == null) continue;

            Team team = teamRepository.findByName(teamName)
                    .map(existing -> {
                        if (existing.getType() == null) {
                            existing.setType(teamType);
                            return teamRepository.save(existing);
                        }
                        return existing;
                    })
                    .orElseGet(() -> teamRepository.save(
                            Team.builder()
                                    .name(teamName)
                                    .type(teamType)
                                    .config(TeamSettings.builder().build())
                                    .build()
                    ));

            if (studentId.length() != 4) continue;
            String grade = studentId.substring(0, 1);
            String classNum = studentId.substring(1, 2);
            String number = String.valueOf(Integer.parseInt(studentId.substring(2)));

            userRepository.findByGradeClassNumberAndName(grade, classNum, number, studentName)
                    .ifPresent(user -> {
                        Map<String, Object> profile = new HashMap<>(user.getProfile());
                        profile.put("team_id", team.getId());
                        user.setProfile(profile);
                        userRepository.save(user);

                        Role role = (roleStr == null || roleStr.isBlank()) ? Role.MEMBER : Role.valueOf(roleStr);
                        if (!teamspaceMemberRepository.existsByTeamAndUser(team, user)) {
                            teamspaceMemberRepository.save(new TeamspaceMember(team, user, role));
                        }
                    });
        }

        return new TeamspaceInviteResponseDto("success");
    }

    @Transactional(readOnly = true)
    public TeamspaceResponseDto getTeamspace(String classId) {

        // NETWORK 타입의 모든 팀 조회
        List<Team> teams = teamRepository.findByType(TeamType.NETWORK);

        List<TeamspaceTeamDto> teamDtos = teams.stream()
                .map(team -> {
                    List<TeamspaceMember> members = teamspaceMemberRepository.findByTeam(team);

                    // classId가 null이면 전체 조회, 있으면 해당 반만 필터링
                    List<TeamspaceMemberDto> memberDtos = members.stream()
                            .filter(member -> {
                                // classId가 null이면 필터링하지 않음
                                if (classId == null) {
                                    return true;
                                }
                                Map<String, Object> profile = member.getUser().getProfile();
                                if (profile == null) return false;
                                String memberClass = (String) profile.get("class");
                                return classId.equals(memberClass);
                            })
                            .sorted((m1, m2) -> {
                                // LEADER를 먼저 정렬
                                if (m1.getRole() == Role.LEADER && m2.getRole() != Role.LEADER) {
                                    return -1;
                                } else if (m1.getRole() != Role.LEADER && m2.getRole() == Role.LEADER) {
                                    return 1;
                                }
                                return 0;
                            })
                            .map(member -> TeamspaceMemberDto.builder()
                                    .userId(member.getUser().getId())
                                    .userName(member.getUser().getName())
                                    .build())
                            .toList();

                    // 해당 반에 멤버가 있는 팀만 반환
                    if (memberDtos.isEmpty()) {
                        return null;
                    }

                    // 팀의 반 정보 설정
                    Integer teamClass = null;
                    if (classId != null) {
                        try {
                            teamClass = Integer.parseInt(classId);
                        } catch (NumberFormatException e) {
                            // classId가 숫자가 아닌 경우 null 유지
                        }
                    }

                    return TeamspaceTeamDto.builder()
                            .teamId(team.getId())
                            .teamName(team.getName())
                            .classNum(teamClass)
                            .members(memberDtos)
                            .build();
                })
                .filter(dto -> dto != null)  // null인 팀 제외
                .toList();

        return TeamspaceResponseDto.builder()
                .teams(teamDtos)
                .build();
    }

    // 전공동아리 팀 조회
    @Transactional(readOnly = true)
    public TeamspaceResponseDto getMajorTeams() {
        // MAJOR 타입 팀 조회
        List<Team> teams = teamRepository.findByType(TeamType.MAJOR);

        List<TeamspaceTeamDto> teamDtos = teams.stream()
                .map(team -> {
                    List<TeamspaceMember> members = teamspaceMemberRepository.findByTeam(team);
                    List<TeamspaceMemberDto> memberDtos = members.stream()
                            .sorted((m1, m2) -> {
                                // LEADER를 먼저 정렬
                                if (m1.getRole() == Role.LEADER && m2.getRole() != Role.LEADER) {
                                    return -1;
                                } else if (m1.getRole() != Role.LEADER && m2.getRole() == Role.LEADER) {
                                    return 1;
                                }
                                return 0;
                            })
                            .map(member -> TeamspaceMemberDto.builder()
                                    .userId(member.getUser().getId())
                                    .userName(member.getUser().getName())
                                    .build())
                            .toList();

                    return TeamspaceTeamDto.builder()
                            .teamId(team.getId())
                            .teamName(team.getName())
                            .classNum(null)  // 전공동아리는 반 정보 없음
                            .members(memberDtos)
                            .build();
                })
                .toList();

        return TeamspaceResponseDto.builder()
                .teams(teamDtos)
                .build();
    }
}
