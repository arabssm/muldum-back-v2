package co.kr.muldum.domain.teamspace.repository;

import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.model.TeamspaceMember;
import co.kr.muldum.domain.user.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamspaceMemberRepository extends JpaRepository<TeamspaceMember, Long> {

    boolean existsByTeam_IdAndUser_Id(Long teamId, Long userId);
    
    boolean existsByTeamAndUser(Team team, User user);

    @EntityGraph(attributePaths = "user")
    List<TeamspaceMember> findByTeam(Team team);

}
