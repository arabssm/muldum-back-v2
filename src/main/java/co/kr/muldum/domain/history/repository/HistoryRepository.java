package co.kr.muldum.domain.history.repository;

import co.kr.muldum.domain.history.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> findByGeneration(Integer generation);

    @EntityGraph(attributePaths = {"contributors", "awards"})
    Optional<History> findByIdWithContributorsAndAwards(Long id);
}
