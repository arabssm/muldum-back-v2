package co.kr.muldum.application.notice.command;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;


import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateNoticeRequest {

  @NotBlank(message = "제목은 필수 입력값입니다.")
  private String title;

  @NotBlank(message = "내용은 필수 입력값입니다.")
  private String content;

  @JsonSetter(nulls = Nulls.AS_EMPTY)
  private List<FileRequest> files = Collections.emptyList();

  private LocalDate deadlineDate;

  @Getter
  public static class FileRequest {
    private String url;

  }
}
