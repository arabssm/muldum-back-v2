package co.kr.muldum.application.teamspace;

import co.kr.muldum.application.teamspace.dto.TeamspaceInviteRequestDto;
import co.kr.muldum.application.teamspace.dto.TeamspaceInviteResponseDto;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.model.TeamspaceMember;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.teamspace.repository.TeamspaceMemberRepository;
import co.kr.muldum.domain.user.model.Role;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.model.UserType;
import co.kr.muldum.domain.user.repository.UserRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 구글 시트 기반 팀 멤버 초대
    @Transactional
    public TeamspaceInviteResponseDto inviteStudents(TeamspaceInviteRequestDto requestDto) {
        String googleSheetUrl = requestDto.getGoogleSheetUrl();

        // 1. URL 검증
        if (googleSheetUrl == null || googleSheetUrl.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_GOOGLE_SHEET_URL);
        }

        // 2. 구글 시트에서 데이터 추출
        List<Map<String, String>> rows = googleSheetImportService.parseTeamInviteRows(googleSheetUrl);

        for (Map<String, String> row : rows) {
            // 2-1. 팀 이름 확인
            String teamName = row.get("team");
            if (teamName == null || teamName.isBlank()) {
                throw new CustomException(ErrorCode.TEAM_NOT_FOUND);
            }

            // 2-2. 팀 조회 (없으면 새 팀 생성)
            Team team = teamRepository.findByName(teamName)
                    .orElseGet(() -> teamRepository.save(
                            Team.builder()
                                    .name(teamName)
                                    .config(new HashMap<>())
                                    .build()
                    ));

            // 2-3. 유저 조회 (학번 + 이름 기준)
            String studentId = row.get("studentId");
            String studentName = row.get("name");

            if (studentId == null || studentName == null) {
                throw new CustomException(ErrorCode.UNREGISTERED_USER);
            }

            User user = userRepository.findByStudentIdAndName(studentId, studentName)
                    .orElseGet(() -> {
                        try {
                            JSONObject profile = new JSONObject();
                            profile.put("studentId", studentId);
                            profile.put("name", studentName);
                            profile.put("teamId", team.getId());

                            Map<String, Object> profileMap = objectMapper.readValue(profile.toString(), Map.class);

                            User newUser = User.builder()
                                    .userType(UserType.STUDENT)
                                    .name(studentName)
                                    .profile(profileMap)
                                    .build();

                            return userRepository.save(newUser);
                        } catch (JSONException | IOException e) {
                            throw new CustomException(ErrorCode.INVALID_GOOGLE_SHEET_URL);
                        }
                    });

            // 2-4. 유저 프로필에 teamId 업데이트 (이미 존재하는 유저라도)
            try {
                JSONObject profile = user.getProfile() != null
                        ? new JSONObject(user.getProfile())
                        : new JSONObject();
                profile.put("teamId", team.getId());

                Map<String, Object> profileMap = objectMapper.readValue(profile.toString(), Map.class);

                user.setProfile(profileMap);
                userRepository.save(user);
            } catch (JSONException | IOException e) {
                throw new CustomException(ErrorCode.INVALID_GOOGLE_SHEET_URL);
            }

            // 2-5. 역할 확인 (없으면 기본 MEMBER)
          String roleStr = row.get("role"); // row에서 가져온 값 (String)
          Role role;

          if (roleStr == null || roleStr.isBlank()) {
            role = Role.MEMBER; // 기본값
          } else {
            role = Role.valueOf(roleStr); // 문자열을 enum으로 변환
          }

            // 2-6. 팀-유저 관계 저장 (중복 방지)
            if (!teamspaceMemberRepository.existsByTeamAndUser(team, user)) {
                teamspaceMemberRepository.save(new TeamspaceMember(team, user, role));
            }
        }

        // 3. 결과 반환
        return new TeamspaceInviteResponseDto("success");
    }
}
