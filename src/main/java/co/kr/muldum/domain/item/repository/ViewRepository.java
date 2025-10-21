package co.kr.muldum.domain.item.repository;

import co.kr.muldum.domain.item.model.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViewRepository extends JpaRepository<View, Integer> {
    Optional<View> findTopByViewerAndViewedItemIdInOrderByWatchedAtDesc(Long viewer, List<Long> viewedItemIds);
}
