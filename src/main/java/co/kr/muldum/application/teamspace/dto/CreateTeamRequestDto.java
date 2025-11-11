package co.kr.muldum.application.teamspace.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateTeamRequestDto {
    String team;
    List<StudentInfo> students;
}
