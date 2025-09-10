package co.kr.muldum.application.teamspace;

import co.kr.muldum.application.teamspace.dto.TeamFileRequest;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.teamspace.repository.TeamspaceMemberRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FileUploadService {
  private final TeamRepository teamRepository;
  private final TeamspaceMemberRepository teamspaceMemberRepository;

  @Transactional
  public void uploadTeamBanner(Long teamId, TeamFileRequest teamFileRequest, Long userId) {
    Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

    validateTeamMember(team.getId(), userId);

    teamRepository.save(team);
  }

  public void uploadTeamIcon(Long teamId, TeamFileRequest teamFileRequest, Long userId) {
    Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

    validateTeamMember(team.getId(), userId);

    teamRepository.save(team);
  }

  private void validateTeamMember(Long teamId, Long userId) {
    if (!teamspaceMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
      throw new CustomException(ErrorCode.NOT_TEAM_MEMBER);
    }
  }
}
