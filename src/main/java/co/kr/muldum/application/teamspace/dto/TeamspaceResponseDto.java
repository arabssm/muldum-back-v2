package co.kr.muldum.application.teamspace.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeamspaceResponseDto {

    private List<TeamDto> content;

    @Getter
    @Builder
    public static class TeamDto {
        private Long teamId;
        private String teamName;
        private List<MemberDto> member;
    }

    @Getter
    @Builder
    public static class MemberDto {
        private Long userId;
        private String userName;
    }
}
