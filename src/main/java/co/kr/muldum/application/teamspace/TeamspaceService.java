package co.kr.muldum.application.teamspace;

import co.kr.muldum.application.teamspace.dto.TeamspaceInviteRequestDto;
import co.kr.muldum.application.teamspace.dto.TeamspaceInviteResponseDto;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.model.TeamspaceMember;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.teamspace.repository.TeamspaceMemberRepository;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.repository.UserRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamspaceService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamspaceMemberRepository teamspaceMemberRepository;

    // 구글 시트의 이메일 목록을 기반으로 팀 멤버 초대
    public TeamspaceInviteResponseDto inviteStudents(Long teamId, TeamspaceInviteRequestDto requestDto) {
        String googleSheetUrl = requestDto.getGoogleSheetUrl();

        // 1. URL 검증
        if (googleSheetUrl == null || googleSheetUrl.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_GOOGLE_SHEET_URL);
        }

        // TODO: GoogleSheet 파싱 로직 구현 (임시로 더미 리스트)
        List<String> emails = List.of("student1@bssm.hs.kr", "student2@bssm.hs.kr");

        // 2. 팀 존재 여부 확인
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        // 3. 이메일 기반 멤버 추가
        for (String email : emails) {
            if (!email.endsWith("@bssm.hs.kr")) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_DOMAIN);
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ErrorCode.UNREGISTERED_USER));

            if (!teamspaceMemberRepository.existsByTeamAndUser(team, user)) {
                teamspaceMemberRepository.save(new TeamspaceMember(team, user, "MEMBER"));
            }
        }

        // 4. 결과 반환
        return new TeamspaceInviteResponseDto("success");
    }
}