package co.kr.muldum.domain.notice.factory;

import co.kr.muldum.application.notice.command.CreateNoticeRequest;
import co.kr.muldum.domain.notice.exception.NotFoundException;
import co.kr.muldum.domain.notice.model.ContentData;
import co.kr.muldum.domain.notice.model.FileData;
import co.kr.muldum.domain.notice.model.Notice;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NoticeRequestFactory {
  private final UserRepository userRepository;

  public Notice createNotice(CreateNoticeRequest createNoticeRequest, Long authorUserId) {
    User user = userRepository.findById(authorUserId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 교사입니다. " + authorUserId));

    List<FileData> fileDataList = Optional.ofNullable(createNoticeRequest.getFiles())
            .orElse(Collections.emptyList())
            .stream()
            .filter(Objects::nonNull)
            .map(CreateNoticeRequest.FileRequest::getUrl)
            .filter(url -> url != null && !url.isBlank())
            .map(FileData::new)
            .toList();

    ContentData contentData = new ContentData(
            createNoticeRequest.getContent(),
            fileDataList
    );
    return Notice.builder()
            .user(user)
            .title(createNoticeRequest.getTitle())
            .contentData(contentData)
            .deadlineDate(createNoticeRequest.getDeadlineDate())
            .build();
  }
}
