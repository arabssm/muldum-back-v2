package co.kr.muldum.domain.item.repository;

import co.kr.muldum.domain.item.model.ItemApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemApprovalHistoryRepository extends JpaRepository<ItemApprovalHistory, Long> {
}
