package co.kr.muldum.application.notice.command;

import co.kr.muldum.domain.file.model.File;
import co.kr.muldum.domain.file.model.FileBook;
import co.kr.muldum.domain.file.repository.FileBookRepository;
import co.kr.muldum.domain.file.repository.FileRepository;
import co.kr.muldum.domain.notice.exception.NotFoundException;
import co.kr.muldum.domain.notice.factory.NoticeRequestFactory;
import co.kr.muldum.domain.notice.model.Notice;
import co.kr.muldum.domain.notice.model.NoticeTeam;
import co.kr.muldum.domain.notice.repository.NoticeRepository;
import co.kr.muldum.domain.notice.repository.NoticeTeamRepository;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
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

    if(createNoticeRequest.isTeamNotice()) {
      saveNoticeTeams(notice, createNoticeRequest.getTeamIds());
    }
    saveFileBooks(notice, createNoticeRequest.getFiles());

    return notice.getId();
  }

  @Transactional
  public void updateNotice(Long noticeId, CreateNoticeRequest createNoticeRequest, Long authorUserId) throws AccessDeniedException {
    Notice notice = noticeRepository.findById(noticeId)
            .orElseThrow(() -> new NotFoundException("공지사항이 존재하지 않습니다: "));

    if (!Objects.equals(notice.getTeacher().getId(), authorUserId)) {
      throw new AccessDeniedException("공지사항 작성자만 수정할 수 있습니다.");
    }

    notice.updateNotice(createNoticeRequest);

    // 팀 매핑
    noticeTeamRepository.deleteAllByNoticeId(noticeId);
    if (createNoticeRequest.isTeamNotice()) {
      saveNoticeTeams(notice, createNoticeRequest.getTeamIds());
    }

    // 파일 매핑
    fileBookRepository.deleteAllByNoticeId(noticeId);
    saveFileBooks(notice, createNoticeRequest.getFiles());
  }

  @Transactional
  public void deleteNotice(Long noticeId, Long authorUserId) throws AccessDeniedException {
    Notice notice = noticeRepository.findById(noticeId)
            .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다: " + noticeId));

    if (!Objects.equals(notice.getTeacher().getId(), authorUserId)) {
      throw new AccessDeniedException("공지사항 작성자만 삭제할 수 있습니다.");
    }

    noticeTeamRepository.deleteAllByNoticeId(noticeId);

    List<FileBook> fileBooks = fileBookRepository.findAllByNoticeId(noticeId);

    List<File> files = fileBooks.stream()
            .map(FileBook::getFile)
            .toList();

    fileBookRepository.deleteAllByNoticeId(noticeId);
    fileBookRepository.flush();

    if (!files.isEmpty()) {
      fileRepository.deleteAllByFiles(files);
      fileRepository.flush();
    }

    noticeRepository.delete(notice);

  }

  private void saveNoticeTeams(Notice notice, List<Long> teamIds) {
    log.info("saveNoticeTeams 호출 - teamIds: {}", teamIds);
    List<NoticeTeam> noticeTeams = teamIds.stream()
            .map(teamId -> new NoticeTeam(notice, teamRepository.getReferenceById(teamId)))
            .toList();
    noticeTeamRepository.saveAll(noticeTeams);
  }

  private void saveFileBooks(Notice notice, List<CreateNoticeRequest.FileRequest> files) {
    List<FileBook> fileBooks = files.stream()
            .map(fileDto -> fileRepository.findByPath(fileDto.getUrl())
                    .orElseThrow(() -> new IllegalArgumentException("해당 URL의 파일이 존재하지 않습니다: " + fileDto.getUrl())))
            .map(file -> new FileBook(notice, file))
            .toList();
    fileBookRepository.saveAll(fileBooks);
  }
}
