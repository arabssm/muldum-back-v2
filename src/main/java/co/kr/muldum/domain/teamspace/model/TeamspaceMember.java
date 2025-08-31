package co.kr.muldum.domain.teamspace.model;

import co.kr.muldum.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "members",
    uniqueConstraints = {
            @UniqueConstraint(columnNames = {"team_id", "user_id"})
})

@Getter
@NoArgsConstructor
public class TeamspaceMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //팀 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // 유저 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 멤버 역할 (leader, member …)
    @Column(nullable = false)
    private String role;

    // 생성/수정 시각
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public TeamspaceMember(Team team, User user, String role) {
        this.team = team;
        this.user = user;
        this.role = role;
    }
}
