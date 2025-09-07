package co.kr.muldum.application.teamspace;

import co.kr.muldum.application.teamspace.dto.TeamPageQueryResponseDto;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.teamspace.repository.TeamspaceMemberRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal(); // 도메인에 맞게 Principal→userId 매핑
        boolean isMember = teamspaceMemberRepository.existsByTeam_IdAndUser_Id(teamId, userId);
        if (!isMember) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        return TeamPageQueryResponseDto.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .content(team.getContent())
                .build();
    }
}
