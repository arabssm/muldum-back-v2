package co.kr.muldum.application.notice.query;

import co.kr.muldum.domain.notice.repository.NoticeRepository;
import co.kr.muldum.domain.user.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class NoticeQueryService {
  private final NoticeRepository noticeRepository;
  private final TeacherRepository teacherRepository;

  public Page<NoticeSimpleResponse> getAllNotices(Pageable pageable, Long userId) throws AccessDeniedException {
    boolean isTeacher = teacherRepository.existsById(userId);
    if(!isTeacher){
      //userId 가 teachers에 없을 경우
      throw new AccessDeniedException("해당 사용자는 교사가 아닙니다.");
    }

    return noticeRepository.getAllOrderByUpdatedAt(pageable)
            .map(NoticeSimpleResponse::fromEntity);
  }
}
