package co.kr.muldum.application.teamspace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TeamFileRequest {

  @NotBlank(message = "파일 url은 필수입니다.")
  private String url;
}
