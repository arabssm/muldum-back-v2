package co.kr.muldum.application.notice.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateNoticeResponse {

  private Long id;
  private String message;

  @Builder
  public CreateNoticeResponse(Long id, String message) {
    this.id = id;
    this.message = message;
  }
}
