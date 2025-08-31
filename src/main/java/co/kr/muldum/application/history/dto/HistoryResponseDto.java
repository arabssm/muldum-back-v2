package co.kr.muldum.application.history.dto;

import co.kr.muldum.domain.history.model.History;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponseDto {
    private Long id;
    private String name;
    private Integer generation;
    private String logoUrl;
    private String description;
    private String slogan;
    private Detail detail;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail {
        private String background;
        private String features;
        private String research;
        private List<Contributor> contributors;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Contributor {
        private String name;
        private String githubUrl;
    }

    public static HistoryResponseDto fromEntity(History history, String slogan, Detail detail) {
        return HistoryResponseDto.builder()
                .id(history.getId())
                .name(history.getName())
                .generation(history.getGeneration())
                .logoUrl(history.getLogoUrl())
                .description(history.getDescription())
                .slogan(slogan)   // Entity가 아닌 Service에서 전달
                .detail(detail)   // ObjectMapper로 파싱해서 전달
                .build();
    }
}