package co.kr.muldum.application.teamspace.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamspaceMemberDto {

    private Long userId;
    private String userName;
    private String studentId;
}
