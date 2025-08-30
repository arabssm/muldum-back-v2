package co.kr.muldum.domain.teamspace.model;

import co.kr.muldum.domain.user.model.User;
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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        LEADER,
        MEMBER
    }

    public TeamspaceMember(Team team, User user, Role role) {
        this.team = team;
        this.user = user;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
    }
}
