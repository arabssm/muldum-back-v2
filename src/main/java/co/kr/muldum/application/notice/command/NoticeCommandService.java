package co.kr.muldum.application.notice.command;

import co.kr.muldum.domain.file.model.FileBook;
import co.kr.muldum.domain.file.repository.FileBookRepository;
import co.kr.muldum.domain.file.repository.FileRepository;
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
  private final FileBookRepository fileBookRepository;
  private final FileRepository fileRepository;

  @Transactional
  public Long createNotice(CreateNoticeRequest createNoticeRequest, Long authorUserId) {
    Notice notice = noticeRequestFactory.createNotice(createNoticeRequest, authorUserId);
    noticeRepository.save(notice);

    if(createNoticeRequest.isTeamNotice()){
      List<NoticeTeam> noticeTeams = createNoticeRequest.getTeamIds().stream()
              .map(teamId -> new NoticeTeam(notice, teamRepository.getReferenceById(teamId)))
              .toList();

      noticeTeamRepository.saveAll(noticeTeams);
    }
    List<FileBook> fileBooks = createNoticeRequest.getFiles().stream()
            .map(fileDto -> fileRepository.findByPath(fileDto.getUrl())
                    .orElseThrow(() -> new IllegalArgumentException("해당 URL의 파일이 존재하지 않습니다: " + fileDto.getUrl())))
            .map(file -> new FileBook(notice, file))
            .toList();
    fileBookRepository.saveAll(fileBooks);
    return notice.getId();
  }
}
