package co.kr.muldum.domain.teamspace.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "members", indexes = {
        @Index(name = "idx_team_student", columnList = "teamId,studentId", unique = true),
        @Index(name = "idx_team", columnList = "teamId"),
        @Index(name = "idx_student", columnList = "studentId")
})
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long teamId;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String role;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "joined_snapshot", columnDefinition = "jsonb")
    private String joinedSnapshot;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
