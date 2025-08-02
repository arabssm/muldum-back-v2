package co.kr.muldum.application.notice.command;

import co.kr.muldum.domain.notice.factory.NoticeRequestFactory;
import co.kr.muldum.domain.notice.model.Notice;
import co.kr.muldum.domain.notice.model.NoticeTeam;
import co.kr.muldum.domain.notice.repository.NoticeRepository;
import co.kr.muldum.domain.notice.repository.NoticeTeamRepository;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeCommandService {
  private final NoticeRepository noticeRepository;
  private final NoticeRequestFactory noticeRequestFactory;
  private final NoticeTeamRepository noticeTeamRepository;
  private final TeamRepository teamRepository;

  @Transactional
  public Long createNotice(CreateNoticeRequest createNoticeRequest, Long fakeUserId) {
    Notice notice = noticeRequestFactory.createNotice(createNoticeRequest, fakeUserId);
    noticeRepository.save(notice);

    if(createNoticeRequest.isTeamNotice()){
      List<NoticeTeam> noticeTeams = createNoticeRequest.getTeamIds().stream()
              .map(teamId -> new NoticeTeam(notice, teamRepository.getReferenceById(teamId)))
              .toList();

      noticeTeamRepository.saveAll(noticeTeams);
    }
    return notice.getId();
  }
}
