package co.kr.muldum.domain.teamspace.model;

import co.kr.muldum.domain.user.model.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

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
