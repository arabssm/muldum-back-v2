package co.kr.muldum.application.teamspace;

import co.kr.muldum.application.teamspace.dto.TeamBannerRequest;
import co.kr.muldum.domain.notice.exception.NotFoundException;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadService {
  private final TeamRepository teamRepository;

  @Transactional
  public void uploadTeamFile(Long teamId, TeamBannerRequest teamBannerRequest, String type) {
    Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("팀을 찾을 수 없습니다."));

    String url = teamBannerRequest.getUrl();

    Map<String, Object> config = new HashMap<>(team.getConfig());
    config.put("backgroundImagePath", url);

    team.setConfig(config);
    teamRepository.save(team);
  }
}
