package co.kr.muldum.application.teamspace;

import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.teamspace.repository.TeamspaceMemberRepository;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Transactional
    public void updateTeamPage(Long teamId, String teamName, String content, Long currentUserId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        boolean isMember = teamspaceMemberRepository.existsByTeam_IdAndUser_Id(teamId, currentUserId);
        boolean isTeacher = user.getId().equals(currentUserId);
        if (!isMember && !isTeacher) {
            throw new CustomException(ErrorCode.NOT_TEAM_MEMBER);
        }

        String newName = (teamName == null || teamName.trim().isEmpty()) ? team.getName() : teamName;
        team.changeContent(newName, content);
    }
}
