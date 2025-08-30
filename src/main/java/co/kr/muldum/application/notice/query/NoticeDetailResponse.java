package co.kr.muldum.application.notice.query;

import co.kr.muldum.domain.notice.model.FileData;
import co.kr.muldum.domain.notice.model.Notice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public record NoticeDetailResponse(
        Long id,
        String title,
        String content,
        List<FileData> files,
        String teacher,
        LocalDate deadlineDate,
        LocalDateTime updatedAt) {

  public static NoticeDetailResponse fromEntity(Notice notice) {
    return new NoticeDetailResponse(
            notice.getId(),
            notice.getTitle(),
            notice.getContentData().getContent(),
            notice.getContentData().getFiles() != null
                    ? notice.getContentData().getFiles()
                    : Collections.emptyList(),
            notice.getUser().getName(),
            notice.getDeadlineDate(),
            notice.getUpdatedAt()
    );
  }
}
