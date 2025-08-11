package co.kr.muldum.application.teamspace;

import lombok.Builder;

import java.util.List;

@Builder
public record StudentCsvImportResponse(int totalCount, List<StudentInfo> students) {

  public record StudentInfo(String name, String email) {}

  public static StudentCsvImportResponse of(int totalCount, List<StudentInfo> students) {
    return new StudentCsvImportResponse(totalCount, students);
  }
}
