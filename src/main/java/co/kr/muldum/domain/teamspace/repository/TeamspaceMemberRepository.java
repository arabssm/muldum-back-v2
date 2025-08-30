package co.kr.muldum.domain.teamspace.repository;

import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.model.TeamspaceMember;
import co.kr.muldum.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamspaceMemberRepository extends JpaRepository<TeamspaceMember, Long> {
    boolean existsByTeamAndUser(Team team, User user);
}
