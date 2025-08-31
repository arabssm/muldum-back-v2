package co.kr.muldum.domain.teamspace.repository;

import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.model.TeamspaceMember;
import co.kr.muldum.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamspaceMemberRepository extends JpaRepository<TeamspaceMember, Long> {

    // 팀과 유저 기준으로 멤버가 이미 존재하는지 확인
    boolean existsByTeamAndUser(Team team, User user);
}