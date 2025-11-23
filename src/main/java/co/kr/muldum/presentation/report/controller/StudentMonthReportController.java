package co.kr.muldum.presentation.report.controller;

import co.kr.muldum.application.report.support.UserTeamResolver;
import co.kr.muldum.application.report.usecase.GetDraftReportUseCase;
import co.kr.muldum.application.report.usecase.GetStudentMonthReportUseCase;
import co.kr.muldum.application.report.usecase.SaveMonthReportUseCase;
import co.kr.muldum.application.report.usecase.SubmitMonthReportUseCase;
import co.kr.muldum.global.dto.MessageResponse;
import co.kr.muldum.global.util.SecurityUtil;
import co.kr.muldum.presentation.report.dto.request.MonthReportRequest;
import co.kr.muldum.presentation.report.dto.response.MonthReportDetailResponse;
import co.kr.muldum.presentation.report.dto.response.SaveMonthReportResponse;
import co.kr.muldum.presentation.report.dto.response.StudentMonthReportListResponse;
import co.kr.muldum.presentation.report.exception.UnauthorizedRoleException;
import co.kr.muldum.presentation.report.mapper.MonthReportWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/std/major/report")
public class StudentMonthReportController {

    private static final String STUDENT_ROLE = "STUDENT";

    private final SaveMonthReportUseCase saveMonthReportUseCase;
    private final SubmitMonthReportUseCase submitMonthReportUseCase;
    private final GetStudentMonthReportUseCase getStudentMonthReportUseCase;
    private final GetDraftReportUseCase getDraftReportUseCase;
    private final MonthReportWebMapper monthReportWebMapper;
    private final UserTeamResolver userTeamResolver;

    @GetMapping("/draft")
    public ResponseEntity<MonthReportDetailResponse> getDraftMonthReportForTeam() {
        Long userId = SecurityUtil.getCurrentUserId();
        validateStudentRole(SecurityUtil.getCurrentUserType());
        Long teamId = userTeamResolver.resolveTeamId(userId);
        var report = getDraftReportUseCase.getDraftReport(teamId);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @PostMapping("/draft")
    public ResponseEntity<SaveMonthReportResponse> saveDraft(
            @RequestBody @Valid MonthReportRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        validateStudentRole(SecurityUtil.getCurrentUserType());
        Long teamId = userTeamResolver.resolveTeamId(userId);
        var savedReport = saveMonthReportUseCase.save(monthReportWebMapper.toCommand(request, userId, teamId));
        var response = new SaveMonthReportResponse(savedReport.getId(), "임시 저장되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{report_id}")
    public ResponseEntity<SaveMonthReportResponse> updateDraft(
            @PathVariable("report_id") Long reportId,
            @RequestBody @Valid MonthReportRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        validateStudentRole(SecurityUtil.getCurrentUserType());
        Long teamId = userTeamResolver.resolveTeamId(userId);
        getStudentMonthReportUseCase.getByReportId(reportId, userId, teamId);
        var savedReport = saveMonthReportUseCase.save(monthReportWebMapper.toCommand(request, userId, teamId));
        var response = new SaveMonthReportResponse(savedReport.getId(), "수정 사항이 임시 저장되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{report_id}/submit")
    public ResponseEntity<MessageResponse> submitMonthReport(
            @PathVariable("report_id") Long reportId,
            @RequestBody @Valid MonthReportRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        validateStudentRole(SecurityUtil.getCurrentUserType());
        Long teamId = userTeamResolver.resolveTeamId(userId);
        getStudentMonthReportUseCase.getByReportId(reportId, userId, teamId);
        submitMonthReportUseCase.submit(monthReportWebMapper.toSubmitCommand(request, userId, teamId, reportId));
        return new ResponseEntity<>(new MessageResponse("제출되었습니다."), HttpStatus.OK);
    }

    @GetMapping("/{report_id}")
    public ResponseEntity<MonthReportDetailResponse> getStudentMonthReportById(
            @PathVariable("report_id") Long reportId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        validateStudentRole(SecurityUtil.getCurrentUserType());
        Long teamId = userTeamResolver.resolveTeamId(userId);
        var report = getStudentMonthReportUseCase.getByReportId(reportId, userId, teamId);
        return new ResponseEntity<>(monthReportWebMapper.toDetailResponse(report), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<StudentMonthReportListResponse> getStudentMonthReports(
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        validateStudentRole(SecurityUtil.getCurrentUserType());
        Long teamId = userTeamResolver.resolveTeamId(userId);
        var reports = getStudentMonthReportUseCase.getByUserId(userId, teamId);
        var reportResponses = reports.stream()
                .map(monthReportWebMapper::toSimpleResponse)
                .collect(Collectors.toList());
        var response = StudentMonthReportListResponse.builder()
                .month(LocalDate.now().getMonthValue())
                .reports(reportResponses)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void validateStudentRole(String userType) {
        if (userType == null || !STUDENT_ROLE.equalsIgnoreCase(userType)) {
            throw new UnauthorizedRoleException(userType);
        }
    }
}
