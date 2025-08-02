package co.kr.muldum.application.notice.command;

import co.kr.muldum.domain.notice.model.enums.Status;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class CreateNoticeRequest {

  private String title;
  private String content;
  private List<FileRequest> files;
  private LocalDate deadlineDate;
  private Status status;
  private List<Long> teamIds;

  public static class FileRequest {
    private String url;

    public String getUrl() {
      return url;
    }
  }

  public boolean isTeamNotice() {
    return teamIds != null && !teamIds.isEmpty();
  }
}
