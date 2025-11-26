package co.kr.muldum.domain.item.repository;

import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.sql.Date;
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

        List<ItemRequest> findByTeamIdAndStatusAndIdIn(Integer teamId, ItemStatus itemStatus, List<Long> ids);

        @Query("SELECT ir FROM ItemRequest ir WHERE ir.status = :status AND ir.updatedAt BETWEEN :start AND :end")
        List<ItemRequest> findByStatusAndUpdatedAtBetween(
                        @Param("status") ItemStatus status,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        @Query(value = """
                        SELECT *
                        FROM item_requests
                        WHERE id <> :excludeId
                          AND (product_info ->> 'link') ILIKE CONCAT('%', :domain, '%')
                          AND (:keyword IS NULL OR :keyword = '' OR (product_info ->> 'name') ILIKE CONCAT('%', :keyword, '%'))
                        ORDER BY updated_at DESC
                        LIMIT :limit
                        """, nativeQuery = true)
        List<ItemRequest> findSimilarItems(
                        @Param("domain") String domain,
                        @Param("excludeId") Long excludeId,
                        @Param("keyword") String keyword,
                        @Param("limit") int limit);

        List<ItemRequest> findByApprovedAtBetween(LocalDateTime start, LocalDateTime end);

        List<ItemRequest> findByRejectedAtBetween(LocalDateTime start, LocalDateTime end);

        @Query(value = """
                        SELECT DISTINCT DATE(ir.approved_at) AS approved_date
                        FROM item_requests ir
                        WHERE ir.approved_at IS NOT NULL
                          AND (:start IS NULL OR ir.approved_at >= CAST(:start AS TIMESTAMP))
                          AND (:end IS NULL OR ir.approved_at <= CAST(:end AS TIMESTAMP))
                        ORDER BY approved_date
                        """, nativeQuery = true)
        List<Date> findDistinctApprovedDates(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        @Query(value = """
                        SELECT DISTINCT DATE(ir.rejected_at) AS rejected_date
                        FROM item_requests ir
                        WHERE ir.rejected_at IS NOT NULL
                          AND ir.rejected_at >= COALESCE(CAST(:start AS TIMESTAMP), ir.rejected_at)
                          AND ir.rejected_at <= COALESCE(CAST(:end AS TIMESTAMP), ir.rejected_at)
                        ORDER BY rejected_date
                        """, nativeQuery = true)
        List<Date> findDistinctRejectedDates(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
        @Query("UPDATE ItemRequest i SET i.approvedAt = :approvedAt WHERE i.createdAt >= :start AND i.createdAt < :end")
        int bulkUpdateApprovedAt(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("approvedAt") LocalDateTime approvedAt);
}
