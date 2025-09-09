package co.kr.muldum.application.teamspace;

import co.kr.muldum.application.teamspace.dto.TeamBannerRequest;
import co.kr.muldum.domain.notice.exception.NotFoundException;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.teamspace.repository.TeamspaceMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FileUploadService {
  private final TeamRepository teamRepository;
  private final TeamspaceMemberRepository teamspaceMemberRepository;

  @Transactional
  public void uploadTeamFile(Long teamId, TeamBannerRequest teamBannerRequest, Long userId) {
    Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("팀을 찾을 수 없습니다."));

    //유저의 팀 아이디와 요청한 팀 아이디 검증
    validateTeamMember(team.getId(), userId);

    String url = teamBannerRequest.getUrl();
    team.updateBackgroundImage(url);

    teamRepository.save(team);
  }
  private void validateTeamMember(Long teamId, Long userId) {
    if (!teamspaceMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
      throw new AccessDeniedException("팀원만 배너를 수정할 수 있습니다.");
    }
  }
}
