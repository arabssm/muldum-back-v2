package co.kr.muldum.domain.history.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "histories")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // PK

    @Column(nullable = false)
    private String name;          // 동아리명

    @Column(nullable = false)
    private Integer generation;   // 기수

    @Column(columnDefinition = "text")
    private String description;   // 설명

    @Column(name = "logo_url")
    private String logoUrl;       // 로고 URL

    @Column(columnDefinition = "text")
    private String slogan;        // 한 줄 요약 문구

    @Column(columnDefinition = "jsonb")
    private String detail;        // 배경/기능/조사/팀원 JSON

    @Builder.Default
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