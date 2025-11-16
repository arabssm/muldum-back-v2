package co.kr.muldum.domain.item.repository;

import co.kr.muldum.domain.item.model.NthStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NthStatusHistoryRepository extends JpaRepository<NthStatusHistory, Long> {
}
