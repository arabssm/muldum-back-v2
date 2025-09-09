package co.kr.muldum.domain.teamspace.repository;

import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.model.TeamspaceMember;
import co.kr.muldum.domain.user.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamspaceMemberRepository extends JpaRepository<TeamspaceMember, Long> {

    // 팀과 유저 기준으로 멤버가 이미 존재하는지 확인
    boolean existsByTeamAndUser(Team team, User user);

    //팀 조회
    @EntityGraph(attributePaths = "user")
    List<TeamspaceMember> findByTeam(Team team);

    List<Team> findDistinctByUserAndTeam_Type(User user, TeamType teamType);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);
}
