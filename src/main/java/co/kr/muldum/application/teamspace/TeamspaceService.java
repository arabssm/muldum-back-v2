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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TeamspaceService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamspaceMemberRepository teamspaceMemberRepository;
    private final GoogleSheetImportService googleSheetImportService;

    // 구글 시트 기반 팀 멤버 초대
    @Transactional
    public TeamspaceInviteResponseDto inviteStudents(TeamspaceInviteRequestDto requestDto) {
        String googleSheetUrl = requestDto.getGoogleSheetUrl();

        // 1. URL 검증
        if (googleSheetUrl == null || googleSheetUrl.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_GOOGLE_SHEET_URL);
        }

        // 2. 팀 존재 여부 확인 (임시로 teamId = 1 고정)
        Team team = teamRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        // 3. 구글 시트에서 데이터 추출
        List<Map<String, String>> rows = googleSheetImportService.parseTeamInviteRows(googleSheetUrl);

        for (Map<String, String> row : rows) {
            // studentNumber 기준으로 유저 조회
            User user = userRepository.findByStudentNumber(row.get("studentNumber"))
                    .orElseThrow(() -> new CustomException(ErrorCode.UNREGISTERED_USER));

            // 중복 방지 후 멤버 저장
            if (!teamspaceMemberRepository.existsByTeamAndUser(team, user)) {
                teamspaceMemberRepository.save(new TeamspaceMember(team, user, row.get("role")));
            }
        }

        // 4. 결과 반환
        return new TeamspaceInviteResponseDto("success");
    }
}