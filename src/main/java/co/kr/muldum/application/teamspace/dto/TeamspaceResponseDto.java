package co.kr.muldum.application.teamspace.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeamspaceResponseDto {

    private List<TeamspaceTeamDto> teams;
}
