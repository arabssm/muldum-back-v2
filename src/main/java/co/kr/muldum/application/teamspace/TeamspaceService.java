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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamspaceService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamspaceMemberRepository teamspaceMemberRepository;
    private final GoogleSheetImportService googleSheetImportService;

    // 구글 시트의 이메일 목록을 기반으로 팀 멤버 초대
    public TeamspaceInviteResponseDto inviteStudents(TeamspaceInviteRequestDto requestDto) {
        String googleSheetUrl = requestDto.getGoogleSheetUrl();

        // 1. URL 검증
        if (googleSheetUrl == null || googleSheetUrl.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_GOOGLE_SHEET_URL);
        }

        // 2. 팀 존재 여부 확인
        Team team = teamRepository.findById(1L)   // 👈 임시로 teamId를 1로 고정
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        // 실제 구글 시트에서 이메일 추출
        List<User> users = googleSheetImportService.importFromGoogleSheet(googleSheetUrl);

        for (User user : users) {
            if (!teamspaceMemberRepository.existsByTeamAndUser(team, user)) {
                teamspaceMemberRepository.save(new TeamspaceMember(team, user, "MEMBER"));
            }
        }

        // 3. 이메일 기반 멤버 추가
        for (User user : users) {
            if (!teamspaceMemberRepository.existsByTeamAndUser(team, user)) {
                teamspaceMemberRepository.save(new TeamspaceMember(team, user, "MEMBER"));
            }
        }

        // 4. 결과 반환
        return new TeamspaceInviteResponseDto("success");
    }
}