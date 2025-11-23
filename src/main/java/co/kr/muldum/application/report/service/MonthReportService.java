package co.kr.muldum.application.report.service;

import co.kr.muldum.application.report.dto.response.TeacherMonthReportApplicationResponse;
import co.kr.muldum.application.report.port.in.SaveMonthReportCommand;
import co.kr.muldum.application.report.port.in.SubmitMonthReportCommand;
import co.kr.muldum.application.report.port.out.LoadMonthReportPort;
import co.kr.muldum.application.report.port.out.SaveMonthReportPort;
import co.kr.muldum.application.report.usecase.GetStudentMonthReportUseCase;
import co.kr.muldum.application.report.usecase.GetTeacherMonthReportUseCase;
import co.kr.muldum.application.report.usecase.SaveMonthReportUseCase;
import co.kr.muldum.application.report.usecase.ScoreMonthReportUseCase;
import co.kr.muldum.application.report.usecase.SubmitMonthReportUseCase;
import co.kr.muldum.domain.report.model.MonthReport;
import co.kr.muldum.domain.report.model.ReportStatus;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.repository.UserRepository;
import co.kr.muldum.global.exception.MonthReportNotFoundException;
import co.kr.muldum.global.exception.UnauthorizedReportAccessException;
import co.kr.muldum.global.exception.UnauthorizedTeamAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MonthReportService implements SaveMonthReportUseCase, SubmitMonthReportUseCase, GetStudentMonthReportUseCase, GetTeacherMonthReportUseCase, ScoreMonthReportUseCase {

    private final LoadMonthReportPort loadMonthReportPort;
    private final SaveMonthReportPort saveMonthReportPort;
    private final UserRepository userRepository;

    @Override
    public MonthReport save(SaveMonthReportCommand command) {
        int currentMonth = LocalDateTime.now().getMonthValue();
        Optional<MonthReport> existingReport = loadMonthReportPort.findByUserIdAndMonth(command.getUserId(), currentMonth);

        MonthReport monthReport = existingReport.map(report -> MonthReport.builder()
                .id(report.getId())
                .userId(report.getUserId())
                .teamId(resolveTeamId(report.getTeamId(), command.getTeamId(), command.getUserId()))
                .topic(command.getTopic())
                .goal(command.getGoal())
                .tech(command.getTech())
                .problem(command.getProblem())
                .teacherFeedback(command.getTeacherFeedback())
                .mentorFeedback(command.getMentorFeedback())
                .status(report.getStatus())
                .submittedAt(report.getSubmittedAt())
                .score(report.getScore())
                .createdAt(report.getCreatedAt())
                .build()
        ).orElseGet(() -> MonthReport.builder()
                .userId(command.getUserId())
                .teamId(command.getTeamId())
                .topic(command.getTopic())
                .goal(command.getGoal())
                .tech(command.getTech())
                .problem(command.getProblem())
                .teacherFeedback(command.getTeacherFeedback())
                .mentorFeedback(command.getMentorFeedback())
                .status(ReportStatus.DRAFT)
                .build());

        return saveMonthReportPort.save(monthReport);
    }

    @Override
    public void submit(SubmitMonthReportCommand command) {
        MonthReport existingReport = loadMonthReportPort.findById(
                        Optional.ofNullable(command.getReportId())
                                .orElseThrow(() -> new MonthReportNotFoundException(null)))
                .orElseThrow(() -> new MonthReportNotFoundException(command.getReportId()));

        if (!existingReport.getUserId().equals(command.getUserId())) {
            throw new UnauthorizedReportAccessException(command.getUserId(), existingReport.getId());
        }
        ensureSameTeam(existingReport.getTeamId(), command.getTeamId(), command.getUserId());

        ReportStatus status = Optional.ofNullable(command.getStatus()).orElse(ReportStatus.SUBMIT);

        MonthReport monthReport = MonthReport.builder()
                .id(existingReport.getId())
                .userId(existingReport.getUserId())
                .teamId(existingReport.getTeamId())
                .topic(command.getTopic())
                .goal(command.getGoal())
                .tech(command.getTech())
                .problem(command.getProblem())
                .teacherFeedback(command.getTeacherFeedback())
                .mentorFeedback(command.getMentorFeedback())
                .status(status)
                .submittedAt(LocalDateTime.now())
                .score(existingReport.getScore())
                .createdAt(existingReport.getCreatedAt())
                .build();

        saveMonthReportPort.save(monthReport);
    }

    @Override
    public MonthReport getByReportId(Long reportId, Long userId, Long teamId) {
        MonthReport report = loadMonthReportPort.findById(reportId)
                .orElseThrow(() -> new MonthReportNotFoundException(reportId));
        if (!report.getUserId().equals(userId)) {
            throw new UnauthorizedReportAccessException(userId, reportId);
        }
        ensureSameTeam(report.getTeamId(), teamId, userId);
        return report;
    }

    @Override
    public List<MonthReport> getByUserId(Long userId, Long teamId) {
        return loadMonthReportPort.findByUserId(userId).stream()
                .filter(report -> teamId.equals(report.getTeamId()))
                .collect(Collectors.toList());
    }

    @Override
    public TeacherMonthReportApplicationResponse getTeacherByReportId(Long reportId, Long teacherId) {
        MonthReport report = loadMonthReportPort.findById(reportId)
                .orElseThrow(() -> new MonthReportNotFoundException(reportId));
        String userName = resolveUserName(report.getUserId());
        return TeacherMonthReportApplicationResponse.builder()
                .reportId(report.getId())
                .userId(report.getUserId())
                .teamId(report.getTeamId())
                .name(userName)
                .topic(report.getTopic())
                .goal(report.getGoal())
                .tech(report.getTech())
                .problem(report.getProblem())
                .teacherFeedback(report.getTeacherFeedback())
                .mentorFeedback(report.getMentorFeedback())
                .status(report.getStatus())
                .submittedAt(report.getSubmittedAt())
                .score(report.getScore())
                .build();
    }

    @Override
    public List<TeacherMonthReportApplicationResponse> getByTeamAndMonth(Long teamId, int month, Long teacherId) {
        List<MonthReport> reports = loadMonthReportPort.findByTeamAndMonth(teamId, month);
        return reports.stream()
                .map(report -> TeacherMonthReportApplicationResponse.builder()
                        .reportId(report.getId())
                        .userId(report.getUserId())
                        .teamId(report.getTeamId())
                        .name(resolveUserName(report.getUserId()))
                        .topic(report.getTopic())
                        .status(report.getStatus())
                        .submittedAt(report.getSubmittedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void score(Long reportId, String feedback, Long teacherId) {
        MonthReport monthReport = loadMonthReportPort.findById(reportId)
                .orElseThrow(() -> new MonthReportNotFoundException(reportId));

        MonthReport scoredReport = MonthReport.builder()
                .id(monthReport.getId())
                .userId(monthReport.getUserId())
                .teamId(monthReport.getTeamId())
                .topic(monthReport.getTopic())
                .goal(monthReport.getGoal())
                .tech(monthReport.getTech())
                .problem(monthReport.getProblem())
                .teacherFeedback(feedback) // Update teacherFeedback with the provided feedback
                .mentorFeedback(monthReport.getMentorFeedback())
                .status(ReportStatus.DRAFT)
                .submittedAt(monthReport.getSubmittedAt())
                .score(monthReport.getScore()) // Keep the existing score, or remove if not applicable
                .createdAt(monthReport.getCreatedAt())
                .build();
        saveMonthReportPort.save(scoredReport);
    }

    private Long resolveTeamId(Long existingTeamId, Long providedTeamId, Long userId) {
        if (existingTeamId == null) {
            if (providedTeamId == null) {
                throw new UnauthorizedTeamAccessException(userId, null);
            }
            return providedTeamId;
        }
        ensureSameTeam(existingTeamId, providedTeamId, userId);
        return existingTeamId;
    }

    private void ensureSameTeam(Long existingTeamId, Long providedTeamId, Long userId) {
        if (providedTeamId == null || !existingTeamId.equals(providedTeamId)) {
            throw new UnauthorizedTeamAccessException(userId, providedTeamId);
        }
    }

    private String resolveUserName(Long userId) {
        return userRepository.findById(userId)
                .map(User::getName)
                .orElse("알 수 없음");
    }
}
