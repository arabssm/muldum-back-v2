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
import com.fasterxml.jackson.databind.ObjectMapper; // ✅ Jackson 추가
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

    @Transactional
    public TeamspaceInviteResponseDto inviteStudents(TeamspaceInviteRequestDto requestDto) {
        String googleSheetUrl = requestDto.getGoogleSheetUrl();

        if (googleSheetUrl == null || googleSheetUrl.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_GOOGLE_SHEET_URL);
        }

        List<Map<String, String>> rows = googleSheetImportService.parseTeamInviteRows(googleSheetUrl);

        for (Map<String, String> row : rows) {
            String teamName = row.get("team");
            String studentId = row.get("studentId");
            String studentName = row.get("name");
            String roleStr = row.get("role");

            if (teamName == null || studentId == null || studentName == null) {
                continue; // 데이터 불완전하면 스킵
            }

            // 팀 조회 or 생성
            Team team = teamRepository.findByName(teamName)
                    .orElseGet(() -> teamRepository.save(
                            Team.builder().name(teamName).config(new HashMap<>()).build()
                    ));

            // studentId → grade, class, number
            if (studentId.length() != 4) continue;
            String grade = studentId.substring(0, 1);
            String classNum = studentId.substring(1, 2);
            String number = String.valueOf(Integer.parseInt(studentId.substring(2))); // "09" → 9

            // DB에서 찾기
            userRepository.findByGradeClassNumberAndName(grade, classNum, number, studentName)
                    .ifPresent(user -> {
                        // teamId 추가
                        Map<String, Object> profile = new HashMap<>(user.getProfile());
                        profile.put("teamId", team.getId());
                        user.setProfile(profile);
                        userRepository.save(user);

                        // 역할 처리
                        Role role = (roleStr == null || roleStr.isBlank()) ? Role.MEMBER : Role.valueOf(roleStr);

                        // 팀-유저 관계 없으면 추가
                        if (!teamspaceMemberRepository.existsByTeamAndUser(team, user)) {
                            teamspaceMemberRepository.save(new TeamspaceMember(team, user, role));
                        }
                    });
        }

        return new TeamspaceInviteResponseDto("success");
    }

}
