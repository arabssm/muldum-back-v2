package co.kr.muldum.domain.teamspace.model;

import co.kr.muldum.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "team_invitations")
@Getter
@Setter
@NoArgsConstructor
public class TeamInvitation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "role", nullable = false, length = 32)
    private String role;
}
