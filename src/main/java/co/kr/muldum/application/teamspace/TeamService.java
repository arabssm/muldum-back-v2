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
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        boolean isMember = teamspaceMemberRepository.existsByTeamIdAndUserId(teamId, currentUserId);
        if (!isMember) {
            throw new CustomException(ErrorCode.NOT_TEAM_MEMBER);
        }

        team.changeContent(content);
    }
}
