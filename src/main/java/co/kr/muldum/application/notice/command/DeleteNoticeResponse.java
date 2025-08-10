package co.kr.muldum.application.notice.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteNoticeResponse {
  private String message;

  @Builder
  public DeleteNoticeResponse(String message) {
    this.message = message;
  }
}
