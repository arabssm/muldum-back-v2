package co.kr.muldum.application.teamspace;

import co.kr.muldum.application.teamspace.dto.TeamPageQueryResponseDto;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.teamspace.repository.TeamspaceMemberRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamPageQueryService {

    private final TeamRepository teamRepository;
    private final TeamspaceMemberRepository teamspaceMemberRepository;

    @Transactional(readOnly = true)
    public TeamPageQueryResponseDto getTeamPage(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        validateTeamMembership(teamId);

        return TeamPageQueryResponseDto.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .content(team.getContent())
                .build();
    }

    private void validateTeamMembership(Long teamId) {
        if (!teamspaceMemberRepository.existsByTeamIdAndUserId(teamId, SecurityUtil.getCurrentUserId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}
