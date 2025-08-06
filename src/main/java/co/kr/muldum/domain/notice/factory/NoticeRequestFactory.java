package co.kr.muldum.domain.notice.factory;

import co.kr.muldum.application.notice.command.CreateNoticeRequest;
import co.kr.muldum.domain.notice.model.ContentData;
import co.kr.muldum.domain.notice.model.FileData;
import co.kr.muldum.domain.notice.model.Notice;
import co.kr.muldum.domain.user.model.Teacher;
import co.kr.muldum.domain.user.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeRequestFactory {
  private final TeacherRepository teacherRepository;

  public Notice createNotice(CreateNoticeRequest createNoticeRequest, Long authorUserId) {
    Teacher teacher = teacherRepository.findById(authorUserId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 교사입니다. " + authorUserId));

    ContentData contentData = new ContentData(
            createNoticeRequest.getContent(),
            createNoticeRequest.getFiles().stream()
                    .map(file -> file == null ? null : new FileData(file.getUrl()))
                    .toList()
    );
    return Notice.builder()
            .teacher(teacher)
            .title(createNoticeRequest.getTitle())
            .contentData(contentData)
            .deadlineDate(createNoticeRequest.getDeadlineDate())
            .status(createNoticeRequest.getStatus())
            .build();
  }

}
