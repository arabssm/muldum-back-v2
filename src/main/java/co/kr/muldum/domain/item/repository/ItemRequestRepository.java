package co.kr.muldum.domain.item.repository;

import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByTeamIdAndStatus(Integer teamId, ItemStatus itemStatus);
    List<ItemRequest> findByIdAndRequesterUserIdAndStatus(Long id, Integer requesterUserId, ItemStatus itemStatus);
    List<ItemRequest> findByTeamId(Integer teamId);
    List<ItemRequest> findByStatusIn(List<ItemStatus> statuses);
    List<ItemRequest> findByTeamIdAndStatusIn(Integer teamId, List<ItemStatus> statuses);
    List<ItemRequest> findByStatus(ItemStatus itemStatus);
    List<ItemRequest> findByRequesterUserIdAndStatus(Integer requesterUserId, ItemStatus itemStatus);
    List<ItemRequest> findByTeamIdAndStatusNot(Integer teamId, ItemStatus excludeStatus);
    List<ItemRequest> findByStatusAndNth(ItemStatus status, Integer nth);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.status = :itemStatus AND ir.nth = :nth AND ir.createdAt BETWEEN :start AND :end")
    List<ItemRequest> findByStatusAndNthAndStartAndEnd(ItemStatus itemStatus, Integer nth, LocalDateTime start, LocalDateTime end);
}