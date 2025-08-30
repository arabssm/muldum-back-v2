package co.kr.muldum.domain.item.repository;

import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByTeamIdAndStatus(int teamId, ItemStatus itemStatus);

    List<ItemRequest> findByStatus(ItemStatus itemStatus);
}