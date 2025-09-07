package co.kr.muldum.application.teamspace;

import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.teamspace.repository.TeamspaceMemberRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamspaceMemberRepository teamspaceMemberRepository;

    @Transactional
    public void updateTeamPage(Long teamId, String content, Long currentUserId) {
        // 팀 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        // 권한 확인: 팀 멤버/교사/관리자만 허용
        boolean isMember = teamspaceMemberRepository.existsByTeam_IdAndUser_Id(teamId, currentUserId);
        if (!isMember) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 내용 변경 (세터 금지)
        team.changeContent(content);
        // JPA dirty checking으로 반영
    }
}
