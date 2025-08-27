package co.kr.muldum.domain.item.repository;

import co.kr.muldum.domain.item.model.ItemRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByTeamId(Integer teamId);
    Optional<ItemRequest> findByIdAndTeamId(Long id, Integer teamId);
}