package co.kr.muldum.application.teamspace.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamspaceMemberDto {

    private Long userId;
    private String role;
    private String studentName;
}
