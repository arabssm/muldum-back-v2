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
    @Column(name = "team_settings", columnDefinition = "jsonb")
    private TeamSettings teamSettings;

    @Column(columnDefinition = "text")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Builder
    public Team(String name, TeamSettings teamSettings, TeamType type, LocalDateTime createdAt, LocalDateTime updatedAt, String content) {
        this.name = name;
        this.teamSettings = teamSettings;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        this.type = type;
        this.content = content;
    }

    public void updateBackgroundImage(String url) {
      // TeamSettings는 immutable이므로 새로운 객체를 생성해야 함
      // 추후 TeamSettings에 setter나 builder 메서드 추가 필요
    }

    public void updateIconImage(String url) {
      // TeamSettings는 immutable이므로 새로운 객체를 생성해야 함  
      // 추후 TeamSettings에 setter나 builder 메서드 추가 필요
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
