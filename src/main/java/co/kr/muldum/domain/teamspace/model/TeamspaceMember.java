package co.kr.muldum.domain.teamspace.model;

import co.kr.muldum.domain.user.model.Student;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "teamspace_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamspaceMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        LEADER,
        MEMBER
    }

    public TeamspaceMember(Team team, Student student, Role role) {
        this.team = team;
        this.student = student;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
    }
}
