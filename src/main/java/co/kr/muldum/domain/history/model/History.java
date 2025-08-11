package co.kr.muldum.domain.history.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column
    private String slogan;

    @Column(name = "detail_background", columnDefinition = "text")
    private String detailBackground;

    @Column(name = "detail_features", columnDefinition = "text")
    private String detailFeatures;

    @Column(name = "detail_research", columnDefinition = "text")
    private String detailResearch;

    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoryAward> awards = new ArrayList<>();

    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contributor> contributors = new ArrayList<>();

    public void updateDescription(String description) {
        this.description = description;
    }

    public void changeLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void addAward(String awardType) {
        if (this.awards == null) {
            this.awards = new ArrayList<>();
        }
        this.awards.add(HistoryAward.of(this, awardType));
    }

    public void removeAward(String awardType) {
        if (this.awards == null) return;
        this.awards.removeIf(a -> awardType != null && awardType.equals(a.getAwardType()));
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Entity
    @Table(name = "history_awards")
    public static class HistoryAward {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "history_id", nullable = false)
        private History history;

        @Column(name = "award_type", nullable = false)
        private String awardType;

        public static HistoryAward of(History history, String awardType) {
            HistoryAward a = new HistoryAward();
            a.history = history;
            a.awardType = awardType;
            return a;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Entity
    @Table(name = "contributors")
    public static class Contributor {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "history_id", nullable = false)
        private History history;

        @Column(nullable = false)
        private String name;

        @Column(name = "github_url")
        private String githubUrl;
    }
}
