package co.kr.muldum.application.teamspace.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeamspaceTeamDto {

    private Long teamId;
    private String teamName;
    private List<TeamspaceMemberDto> members;
}
