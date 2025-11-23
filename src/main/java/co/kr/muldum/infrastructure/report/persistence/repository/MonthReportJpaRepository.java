package co.kr.muldum.infrastructure.report.persistence.repository;

import co.kr.muldum.infrastructure.report.persistence.entity.MonthReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MonthReportJpaRepository extends JpaRepository<MonthReportEntity, Long> {
    List<MonthReportEntity> findByUserId(Long userId);

    @Query(value = "SELECT * FROM month_report m WHERE m.team_id = :teamId AND EXTRACT(MONTH FROM m.created_at) = :month", nativeQuery = true)
    List<MonthReportEntity> findByTeamIdAndMonth(@Param("teamId") Long teamId, @Param("month") int month);

    @Query(value = "SELECT * FROM month_report m WHERE m.user_id = :userId AND EXTRACT(MONTH FROM m.created_at) = :month LIMIT 1", nativeQuery = true)
    Optional<MonthReportEntity> findByUserIdAndMonth(@Param("userId") Long userId, @Param("month") int month);
}
