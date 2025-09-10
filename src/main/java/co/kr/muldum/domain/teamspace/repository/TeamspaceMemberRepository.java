package co.kr.muldum.domain.teamspace.repository;

import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.model.TeamspaceMember;
import co.kr.muldum.domain.teamspace.model.TeamType;
import co.kr.muldum.domain.user.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamspaceMemberRepository extends JpaRepository<TeamspaceMember, Long> {

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);
    
    boolean existsByTeamAndUser(Team team, User user);

    Optional<TeamspaceMember> findByTeamAndUser(Team team, User user);

    @EntityGraph(attributePaths = "user")
    List<TeamspaceMember> findByTeam(Team team);

    List<Team> findDistinctByUserAndTeamType(User user, TeamType teamType);
}
