package co.kr.muldum.domain.item.repository;

import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByTeamIdAndStatus(Integer teamId, ItemStatus itemStatus);

    List<ItemRequest> findByTeamId(Integer teamId);
    List<ItemRequest> findByStatusIn(List<ItemStatus> statuses);
    List<ItemRequest> findByTeamIdAndStatusIn(Integer teamId, List<ItemStatus> statuses);
    List<ItemRequest> findByTeamId(Integer teamId);
    List<ItemRequest> findByStatus(ItemStatus itemStatus);
    List<ItemRequest> findByRequesterUserIdAndStatus(Integer requesterUserId, ItemStatus itemStatus);
    List<ItemRequest> findByTeamIdAndStatusNot(Integer teamId, ItemStatus excludeStatus);

    List<ItemRequest> findByStatusIn(List<ItemStatus> pending);

    List<ItemRequest> findByTeamIdAndStatusIn(Integer teamId, List<ItemStatus> pending);
}