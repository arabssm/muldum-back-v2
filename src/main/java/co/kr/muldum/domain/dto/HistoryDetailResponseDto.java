package co.kr.muldum.domain.dto;

import co.kr.muldum.domain.history.model.History;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDetailResponseDto {

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

    public static HistoryDetailResponseDto fromEntity(History history) {
        if (history == null) return null;

        List<Contributor> contributors = (history.getContributors() == null)
                ? Collections.emptyList()
                : history.getContributors().stream()
                    .map(c -> Contributor.builder()
                            .name(c.getName())
                            .githubUrl(c.getGithubUrl())
                            .build())
                    .collect(Collectors.toList());

        Detail detail = Detail.builder()
                .background(history.getDetailBackground())
                .features(history.getDetailFeatures())
                .research(history.getDetailResearch())
                .contributors(contributors)
                .build();

        return HistoryDetailResponseDto.builder()
                .id(history.getId())
                .name(history.getName())
                .generation(history.getGeneration())
                .logoUrl(history.getLogoUrl())
                .description(history.getDescription())
                .slogan(history.getSlogan())
                .detail(detail)
                .build();
    }
}
