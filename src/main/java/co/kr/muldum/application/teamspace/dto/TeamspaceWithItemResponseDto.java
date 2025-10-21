package co.kr.muldum.application.teamspace.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeamspaceWithItemResponseDto {

    private Long teamId;
    private String teamName;
    private List<TeamspaceMemberDto> members;
    private Boolean hasNewItems;

}
