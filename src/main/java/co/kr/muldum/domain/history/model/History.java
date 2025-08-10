package co.kr.muldum.domain.history.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "histories")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer generation;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @ElementCollection
    @CollectionTable(name = "history_awards", joinColumns = @JoinColumn(name = "history_id"))
    private List<HistoryAward> awards = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class HistoryAward {

        @Column(name = "award_type", nullable = false)
        private String awardType;

        @Column(name = "given_at")
        private LocalDate givenAt;
    }
}
