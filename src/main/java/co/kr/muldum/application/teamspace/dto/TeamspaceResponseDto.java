package co.kr.muldum.application.teamspace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeamspaceResponseDto {

    @JsonProperty("content")
    private List<TeamspaceTeamDto> content;
}
