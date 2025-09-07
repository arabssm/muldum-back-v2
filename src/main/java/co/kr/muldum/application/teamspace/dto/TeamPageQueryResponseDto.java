package co.kr.muldum.application.teamspace.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TeamPageQueryResponseDto {
    private final Long teamId;
    private final String teamName;
    private final String content;

    @Builder
    public TeamPageQueryResponseDto(Long teamId, String teamName, String content) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.content = content;
    }
}
