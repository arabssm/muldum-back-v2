package co.kr.muldum.domain.teamspace.model;

import co.kr.muldum.domain.item.model.RequestDetails;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "teams")
@Getter
@NoArgsConstructor // JPA 기본 생성자
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 자동 생성
    private Long id;


    @Column(nullable = false)
    private String name;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "team_config", columnDefinition = "jsonb")
    private TeamConfig teamConfig;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> config;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    // ✅ id는 빌더에 포함하지 않음
    @Builder
    public Team(String name, Map<String, Object> config, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.config = config;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    // ✅ updatedAt 자동 갱신 편의 메서드
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}