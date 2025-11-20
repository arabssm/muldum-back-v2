package co.kr.muldum.domain.item.repository;

import co.kr.muldum.domain.item.model.ItemGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemGuideRepository extends JpaRepository<ItemGuide, Long> {
    Optional<ItemGuide> findByIdAndProjectType(Long id, String projectType);
}
