package co.kr.muldum.domain.item.repository;

import co.kr.muldum.domain.item.model.NthStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NthStatusRepository extends JpaRepository<NthStatus, Long> {
    NthStatus findNthStatusById(Long id);

    @Query("SELECT ns FROM NthStatus ns WHERE ns.id = :id")
    Optional<NthStatus> findByNthStatusId(Long id);
}
