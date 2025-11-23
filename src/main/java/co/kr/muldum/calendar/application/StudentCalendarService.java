package co.kr.muldum.calendar.application;

import co.kr.muldum.calendar.application.dto.StudentCalendarResponse;
import co.kr.muldum.calendar.domain.StudentCalendar;
import co.kr.muldum.calendar.domain.StudentCalendarRepository;
import co.kr.muldum.calendar.presentation.dto.StudentCalendarRequest;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentCalendarService {

    private final StudentCalendarRepository studentCalendarRepository;
    private final CalendarTeamResolver calendarTeamResolver;

    @Transactional(readOnly = true)
    public List<StudentCalendarResponse> getCalendars(Long userId) {
        String teamId = resolveTeamIdAsString(userId);
        return studentCalendarRepository.findAllByTeamIdOrderByStartDateAsc(teamId)
                .stream()
                .map(StudentCalendarResponse::from)
                .toList();
    }

    public StudentCalendarResponse create(Long userId, StudentCalendarRequest request) {
        String teamId = resolveTeamIdAsString(userId);
        LocalDate startDate = toDate(request.getStartYear(), request.getStartDate(), "Start");
        LocalDate endDate = toDate(request.getEndYear(), request.getEndDate(), "End");

        StudentCalendar calendar = StudentCalendar.create(
                teamId,
                String.valueOf(userId),
                startDate,
                endDate,
                request.getTitle(),
                request.getContent()
        );

        StudentCalendar saved = studentCalendarRepository.save(calendar);
        return StudentCalendarResponse.from(saved);
    }

    public StudentCalendarResponse update(Long userId, Long calendarId, StudentCalendarRequest request) {
        String teamId = resolveTeamIdAsString(userId);
        StudentCalendar calendar = studentCalendarRepository.findByIdAndTeamId(calendarId, teamId)
                .orElseThrow(() -> new CustomException(ErrorCode.CALENDAR_ENTRY_NOT_FOUND));

        LocalDate startDate = toDate(request.getStartYear(), request.getStartDate(), "Start");
        LocalDate endDate = toDate(request.getEndYear(), request.getEndDate(), "End");
        calendar.update(startDate, endDate, request.getTitle(), request.getContent());

        return StudentCalendarResponse.from(calendar);
    }

    public void delete(Long userId, Long calendarId) {
        String teamId = resolveTeamIdAsString(userId);
        StudentCalendar calendar = studentCalendarRepository.findByIdAndTeamId(calendarId, teamId)
                .orElseThrow(() -> new CustomException(ErrorCode.CALENDAR_ENTRY_NOT_FOUND));

        studentCalendarRepository.delete(calendar);
    }

    private String resolveTeamIdAsString(Long userId) {
        Long teamId = calendarTeamResolver.resolveTeamId(userId);
        return String.valueOf(teamId);
    }

    private LocalDate toDate(Integer year, Integer monthDay, String prefix) {
        if (year == null || monthDay == null) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_ENTRY, prefix + " 날짜 정보가 필요합니다.");
        }
        if (monthDay < 101 || monthDay > 1231) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_ENTRY, prefix + " 날짜 형식이 올바르지 않습니다.");
        }

        String padded = String.format("%04d", monthDay);
        int month = Integer.parseInt(padded.substring(0, 2));
        int day = Integer.parseInt(padded.substring(2));

        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_ENTRY, prefix + " 날짜가 올바르지 않습니다.");
        }
    }
}
