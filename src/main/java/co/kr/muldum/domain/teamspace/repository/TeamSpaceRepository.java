package co.kr.muldum.domain.teamspace.repository;

import co.kr.muldum.domain.teamspace.model.TeamSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamSpaceRepository extends JpaRepository<TeamSpace, Long> {
}
