package co.kr.muldum.domain.teamspace.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config", columnDefinition = "jsonb")
    private TeamSettings config;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Team(String name, String content, TeamSettings config, TeamType type) {
        this.name = name;
        this.content = content;
        this.config = config;
        this.type = type;
    }

    public void changeContent(String teamName, String content) {
        this.name = teamName;
        this.content = content;
    }

    public void updateBackgroundImage(String backgroundImageUrl) {
        if (this.config == null) {
            this.config = TeamSettings.builder().build();
        }
        this.config = TeamSettings.builder()
                .theme(this.config.getTheme())
                .notificationsEnabled(this.config.isNotificationsEnabled())
                .language(this.config.getLanguage())
                .maxMembers(this.config.getMaxMembers())
                .backgroundImageUrl(backgroundImageUrl)
                .backgroundImagePath(this.config.getBackgroundImagePath())
                .iconImageUrl(this.config.getIconImageUrl())
                .build();
    }

    public void updateIconImage(String iconImageUrl) {
        if (this.config == null) {
            this.config = TeamSettings.builder().build();
        }
        this.config = TeamSettings.builder()
                .theme(this.config.getTheme())
                .notificationsEnabled(this.config.isNotificationsEnabled())
                .language(this.config.getLanguage())
                .maxMembers(this.config.getMaxMembers())
                .backgroundImageUrl(this.config.getBackgroundImageUrl())
                .backgroundImagePath(this.config.getBackgroundImagePath())
                .iconImageUrl(iconImageUrl)
                .build();
    }
}
