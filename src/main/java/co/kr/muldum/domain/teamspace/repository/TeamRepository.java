package co.kr.muldum.domain.teamspace.repository;

import co.kr.muldum.domain.teamspace.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
