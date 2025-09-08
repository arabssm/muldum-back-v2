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

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadService {
  private final TeamRepository teamRepository;
  private final TeamspaceMemberRepository teamspaceMemberRepository;

  @Transactional
  public void uploadTeamFile(Long teamId, TeamBannerRequest teamBannerRequest, Long userId) {
    Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("팀을 찾을 수 없습니다."));

    //유저의 팀 아이디와 요청한 팀 아이디가 다르면 예외 발생
    // 권한 체크: 팀원 여부 확인 (레포지토리에 메서드 추가 필요)
    if (!teamspaceMemberRepository.existsByTeamIdAndUserId(team.getId(), userId)) {
      throw new AccessDeniedException("팀원만 배너를 수정할 수 있습니다.");
    }

    String url = teamBannerRequest.getUrl();

    Map<String, Object> config = new HashMap<>(team.getConfig());
    config.put("backgroundImagePath", url);

    team.setConfig(config);
    teamRepository.save(team);
  }
}
