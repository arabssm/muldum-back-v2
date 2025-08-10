package co.kr.muldum.application.history.dto;

import co.kr.muldum.domain.history.model.History;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponseDto {
    private Long id;
    private String name;
    private Integer generation;
    private String description;
    private String logoUrl;
    private List<AwardDto> awards;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AwardDto {
        private String awardType;
        private LocalDate givenAt;
    }

    public static HistoryResponseDto fromEntity(History history) {
        return HistoryResponseDto.builder()
                .id(history.getId())
                .name(history.getName())
                .generation(history.getGeneration())
                .description(history.getDescription())
                .logoUrl(history.getLogoUrl())
                .awards(history.getAwards() == null ? List.of() :
                        history.getAwards().stream()
                                .map(a -> AwardDto.builder()
                                        .awardType(a.getAwardType())
                                        .givenAt(a.getGivenAt())
                                        .build())
                                .collect(Collectors.toList()))
                .build();
    }
}
