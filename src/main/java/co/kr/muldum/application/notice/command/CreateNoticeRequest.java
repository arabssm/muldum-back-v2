package co.kr.muldum.application.notice.command;

import co.kr.muldum.domain.notice.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;


import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateNoticeRequest {

  @NotBlank(message = "제목은 필수 입력값입니다.")
  private String title;
  @NotBlank(message = "내용은 필수 입력값입니다.")
  private String content;
  private List<FileRequest> files;
  private LocalDate deadlineDate;
  private Status status;
  private List<Long> teamIds;

  @Getter
  public static class FileRequest {
    private String url;

  }

  public boolean isTeamNotice() {
    return teamIds != null && !teamIds.isEmpty();
  }
}
