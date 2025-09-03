package co.kr.muldum.application.teamspace;

import co.kr.muldum.application.teamspace.dto.TeamspaceInviteRequestDto;
import co.kr.muldum.application.teamspace.dto.TeamspaceInviteResponseDto;
import co.kr.muldum.application.teamspace.dto.TeamspaceResponseDto;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.model.TeamType;
import co.kr.muldum.domain.teamspace.model.TeamspaceMember;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.teamspace.repository.TeamspaceMemberRepository;
import co.kr.muldum.domain.user.model.Role;
import co.kr.muldum.domain.user.repository.UserRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
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
                                    .config(new HashMap<>())
                                    .build()
                    ));

            if (studentId.length() != 4) continue;
            String grade = studentId.substring(0, 1);
            String classNum = studentId.substring(1, 2);
            String number = String.valueOf(Integer.parseInt(studentId.substring(2)));

            userRepository.findByGradeClassNumberAndName(grade, classNum, number, studentName)
                    .ifPresent(user -> {
                        Map<String, Object> profile = new HashMap<>(user.getProfile());
                        profile.put("teamId", team.getId());
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
    public TeamspaceResponseDto getTeamspace(Long userId) {

        var user = userRepository.findById(userId)
                .orElseThrow(()->new CustomException(ErrorCode.UNREGISTERED_USER));

        List<Team> teams = teamspaceMemberRepository.findTeamsByUserAndType(user, TeamType.NETWORK);

        List<TeamspaceResponseDto.TeamDto> teamDtos = teams.stream()
                .map(team -> {
                    List<TeamspaceMember> members = teamspaceMemberRepository.findByTeam(team);
                    List<TeamspaceResponseDto.MemberDto> memberDtos = members.stream()
                            .map(member -> TeamspaceResponseDto.MemberDto.builder()
                                    .userId(member.getUser().getId())
                                    .userName(member.getUser().getName())
                                    .build())
                            .toList();

                    return TeamspaceResponseDto.TeamDto.builder()
                            .teamId(team.getId())
                            .teamName(team.getName())
                            .member(memberDtos)
                            .build();
                })
                .toList();
        return TeamspaceResponseDto.builder()
                .content(teamDtos)
                .build();
    }

}
