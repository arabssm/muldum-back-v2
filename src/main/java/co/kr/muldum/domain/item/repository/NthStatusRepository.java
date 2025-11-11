package co.kr.muldum.domain.item.repository;

import co.kr.muldum.domain.item.model.NthStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NthStatusRepository extends JpaRepository<NthStatus, Long> {
    NthStatus findNthStatusById(Long id);
}
