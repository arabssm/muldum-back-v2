package co.kr.muldum.domain.teamspace.repository;

import co.kr.muldum.domain.teamspace.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);
}
