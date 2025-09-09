package co.kr.muldum.domain.teamspace.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config", columnDefinition = "jsonb")
    private TeamSettings config;

    @Column(columnDefinition = "text")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Builder
    public Team(String name, TeamSettings config, TeamType type, LocalDateTime createdAt, LocalDateTime updatedAt, String content) {
        this.name = name;
        this.config = config;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        this.type = type;
        this.content = content;
    }

    public void updateBackgroundImage(String url) {
      Map<String, Object> newConfig = new HashMap<>(this.config);
      newConfig.put("backgroundImagePath", url);
      this.config = newConfig;
    }

    public void updateIconImage(String url) {
      Map<String, Object> newConfig = new HashMap<>(this.config);
      newConfig.put("iconImagePath", url);
      this.config = newConfig;
    }

    public void changeContent(String content) {
        this.content = content;
        updateTimestamp();
    }

    public void changeType(TeamType type) {
        this.type = type;
        updateTimestamp();
    }
}
