package co.kr.muldum.domain.teamspace.repository;

import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.model.TeamType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);

    // 팀의 최대 id 조회
    @Query("SELECT MAX(t.id) FROM Team t")
    Optional<Long> findMaxId();

    // 팀 타입으로 팀 목록 조회
    @Query("SELECT t FROM Team t where t.type=?1 order by t.id ASC")
    List<Team> findByType(TeamType type);
}