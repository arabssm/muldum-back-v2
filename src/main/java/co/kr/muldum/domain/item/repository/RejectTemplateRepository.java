package co.kr.muldum.domain.item.repository;

import co.kr.muldum.domain.item.model.RejectTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RejectTemplateRepository extends JpaRepository<RejectTemplate, Long> {
}
