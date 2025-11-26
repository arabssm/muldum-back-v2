package co.kr.muldum.presentation.report.controller;

import co.kr.muldum.application.report.usecase.GetTeacherMonthReportUseCase;
import co.kr.muldum.application.report.usecase.ScoreMonthReportUseCase;
import co.kr.muldum.global.dto.MessageResponse;
import co.kr.muldum.global.util.SecurityUtil;
import co.kr.muldum.presentation.report.dto.request.ScoreMonthReportRequest;
import co.kr.muldum.presentation.report.dto.response.TeacherMonthReportDetailResponse;
import co.kr.muldum.presentation.report.dto.response.TeacherMonthReportListResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tch/major/report")
public class TeacherMonthReportController {

    private static final String TEACHER_ROLE = "TEACHER";

    private final GetTeacherMonthReportUseCase getTeacherMonthReportUseCase;
    private final ScoreMonthReportUseCase scoreMonthReportUseCase;
    private final MonthReportWebMapper monthReportWebMapper;

    @GetMapping("/{report_id}")
    public ResponseEntity<TeacherMonthReportDetailResponse> getTeacherMonthReportById(
            @PathVariable("report_id") Long reportId
    ) {
        validateTeacherRole(SecurityUtil.getCurrentUserType());
        Long teacherId = SecurityUtil.getCurrentUserId();
        var report = getTeacherMonthReportUseCase.getTeacherByReportId(reportId, teacherId);
        return new ResponseEntity<>(monthReportWebMapper.toTeacherDetailResponse(report), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<TeacherMonthReportListResponse> getTeacherMonthReportsByTeamAndMonth(
            @RequestParam(value = "team", required = false) Long teamId,
            @RequestParam(value = "month", required = false) Integer month
    ) {
        validateTeacherRole(SecurityUtil.getCurrentUserType());
        Long teacherId = SecurityUtil.getCurrentUserId();
        var reports = getTeacherMonthReportUseCase.getByTeamAndMonth(teamId, month, teacherId);
        var reportResponses = reports.stream()
                .map(monthReportWebMapper::toTeacherSimpleResponse)
                .collect(Collectors.toList());
        var response = TeacherMonthReportListResponse.builder()
                .month(month)
                .reports(reportResponses)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{report_id}")
    public ResponseEntity<MessageResponse> scoreMonthReport(
            @PathVariable("report_id") Long reportId,
            @RequestBody @Valid ScoreMonthReportRequest request
    ) {
        validateTeacherRole(SecurityUtil.getCurrentUserType());
        Long teacherId = SecurityUtil.getCurrentUserId();
        scoreMonthReportUseCase.score(reportId, request.getFeedback(), teacherId);
        return new ResponseEntity<>(new MessageResponse("채점 완료"), HttpStatus.OK);
    }

    private void validateTeacherRole(String userType) {
        if (userType == null || !TEACHER_ROLE.equalsIgnoreCase(userType)) {
            throw new UnauthorizedRoleException(userType);
        }
    }
}
