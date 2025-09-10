package co.kr.muldum.application.teamspace.dto;

import co.kr.muldum.domain.teamspace.model.TeamSettings;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TeamPageQueryResponseDto {
    private final Long teamId;
    private final String teamName;
    private final String content;
    private final TeamSettings teamSettings;

    @Builder
    public TeamPageQueryResponseDto(Long teamId, String teamName, String content, TeamSettings teamSettings) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.content = content;
        this.teamSettings = teamSettings;
    }
}
