package co.kr.muldum.domain.teamspace.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
        name = "members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_member_team_student",
                        columnNames = {"team_id", "student_id"})
        },
        indexes = {
                @Index(
                        name = "idx_member_team_id",
                        columnList = "team_id"),
                @Index(name = "idx_member_student_id",
                        columnList = "student_id")
        }
    )
@Getter
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
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> joinedSnapshot;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
