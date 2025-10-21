package co.kr.muldum.application.teamspace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeamspaceTeamDto {

    private Long teamId;
    private String teamName;
    @JsonProperty("class")
    private Integer classNum;
    private List<TeamspaceMemberDto> members;
}
