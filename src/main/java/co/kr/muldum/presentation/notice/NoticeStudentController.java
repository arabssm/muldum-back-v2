package co.kr.muldum.presentation.notice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("std/notice")
public class NoticeStudentController {

  @GetMapping
  public String getNoticeList() {
    // 학생용 공지사항 조회 로직을 여기에 구현합니다.
    // 현재는 단순히 문자열을 반환합니다.
    return "학생용 공지사항 목록";
  }
}
