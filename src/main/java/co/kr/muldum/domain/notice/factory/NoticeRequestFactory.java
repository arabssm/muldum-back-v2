package co.kr.muldum.domain.notice.factory;

import co.kr.muldum.application.notice.command.CreateNoticeRequest;
import co.kr.muldum.domain.notice.model.ContentData;
import co.kr.muldum.domain.notice.model.FileData;
import co.kr.muldum.domain.notice.model.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeRequestFactory {
  public Notice createNotice(CreateNoticeRequest createNoticeRequest, Long fakeUserId) {
    ContentData contentData = new ContentData(
            createNoticeRequest.getContent(),
            createNoticeRequest.getFiles().stream()
                    .map(file -> new FileData(file.getUrl()))
                    .toList()
    );
    return Notice.builder()
            .authorUserId(fakeUserId)
            .title(createNoticeRequest.getTitle())
            .contentData(contentData)
            .deadlineDate(createNoticeRequest.getDeadlineDate())
            .status(createNoticeRequest.getStatus())
            .build();
  }

}
