package co.kr.muldum.presentation.notice;

import lombok.Getter;

@Getter
public enum NoticeMessage {
  NOTICE_CREATED_SUCCESS("공지사항이 성공적으로 등록되었습니다.");

  private final String message;

  NoticeMessage(String message) {
    this.message = message;
  }

}
