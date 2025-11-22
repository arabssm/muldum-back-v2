package co.kr.muldum.infrastructure.report.persistence.adapter;

import co.kr.muldum.application.report.port.out.LoadMonthReportPort;
import co.kr.muldum.application.report.port.out.SaveMonthReportPort;
import co.kr.muldum.domain.report.model.MonthReport;
import co.kr.muldum.domain.report.model.ReportStatus;
import co.kr.muldum.infrastructure.report.persistence.repository.MonthReportJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MonthReportPersistenceAdapter implements LoadMonthReportPort, SaveMonthReportPort {

    private final MonthReportJpaRepository monthReportJpaRepository;
    private final MonthReportMapper monthReportMapper;

    @Override
    public Optional<MonthReport> findById(Long reportId) {
        return monthReportJpaRepository.findById(reportId)
                .map(monthReportMapper::toDomain);
    }

    @Override
    public List<MonthReport> findByUserId(Long userId) {
        return monthReportJpaRepository.findByUserId(userId).stream()
                .map(monthReportMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MonthReport> findByTeamAndMonth(Long teamId, int month) {
        return monthReportJpaRepository.findByTeamIdAndMonth(teamId, month).stream()
                .map(monthReportMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MonthReport> findByUserIdAndMonth(Long userId, int month) {
        return monthReportJpaRepository.findByUserIdAndMonth(userId, month)
                .map(monthReportMapper::toDomain);
    }

    @Override
    public Optional<MonthReport> findByTeamIdAndStatus(Long teamId, ReportStatus status) {
        return monthReportJpaRepository.findByTeamIdAndStatus(teamId, status)
                .map(monthReportMapper::toDomain);
    }

    @Override
    public MonthReport save(MonthReport monthReport) {
        var entity = monthReportMapper.toEntity(monthReport);
        var savedEntity = monthReportJpaRepository.save(entity);
        return monthReportMapper.toDomain(savedEntity);
    }
}
