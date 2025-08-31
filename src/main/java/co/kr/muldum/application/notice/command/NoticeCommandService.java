package co.kr.muldum.application.notice.command;

import co.kr.muldum.domain.file.model.File;
import co.kr.muldum.domain.file.model.FileBook;
import co.kr.muldum.domain.file.repository.FileBookRepository;
import co.kr.muldum.domain.file.repository.FileRepository;
import co.kr.muldum.domain.notice.exception.NotFoundException;
import co.kr.muldum.domain.notice.factory.NoticeRequestFactory;
import co.kr.muldum.domain.notice.model.Notice;
import co.kr.muldum.domain.notice.repository.NoticeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeCommandService {
  private final NoticeRepository noticeRepository;
  private final NoticeRequestFactory noticeRequestFactory;
  private final FileBookRepository fileBookRepository;
  private final FileRepository fileRepository;

  @Transactional
  public Long createNotice(CreateNoticeRequest createNoticeRequest, Long authorUserId) {
    Notice notice = noticeRequestFactory.createNotice(createNoticeRequest, authorUserId);
    noticeRepository.save(notice);

    saveFileBooks(notice, createNoticeRequest.getFiles());

    return notice.getId();
  }

  @Transactional
  public void updateNotice(Long noticeId, CreateNoticeRequest createNoticeRequest, Long authorUserId) throws AccessDeniedException {
    Notice notice = noticeRepository.findById(noticeId)
            .orElseThrow(() -> new NotFoundException("공지사항이 존재하지 않습니다: " + noticeId));

    if (!Objects.equals(notice.getUser().getId(), authorUserId)) {
      throw new AccessDeniedException("공지사항 작성자만 수정할 수 있습니다.");
    }

    notice.updateNotice(createNoticeRequest);

    fileBookRepository.deleteAllByNoticeId(noticeId);
    saveFileBooks(notice, createNoticeRequest.getFiles());
  }

  @Transactional
  public void deleteNotice(Long noticeId, Long authorUserId) {
    Notice notice = noticeRepository.findById(noticeId)
            .orElseThrow(() -> new NotFoundException("공지사항이 존재하지 않습니다: " + noticeId));

    if (!Objects.equals(notice.getUser().getId(), authorUserId)) {
      throw new AccessDeniedException("공지사항 작성자만 삭제할 수 있습니다.");
    }

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

  private void saveFileBooks(Notice notice, List<CreateNoticeRequest.FileRequest> files) {
    List<FileBook> fileBooks = files.stream()
            .map(fileDto -> fileRepository.findByPath(fileDto.getUrl())
                    .orElseThrow(() -> new NotFoundException("해당 URL의 파일이 존재하지 않습니다: " + fileDto.getUrl())))
            .map(file -> new FileBook(notice, file))
            .toList();
    fileBookRepository.saveAll(fileBooks);
  }
}
