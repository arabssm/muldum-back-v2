package co.kr.muldum.calendar.application;

import co.kr.muldum.calendar.application.dto.GoogleCalendarEventsResponse;
import co.kr.muldum.calendar.presentation.dto.GoogleCalendarSyncRequest;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.infrastructure.calendar.GoogleCalendarClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleCalendarService {

    private static final Duration DEFAULT_PAST_WINDOW = Duration.ofDays(30);
    private static final Duration DEFAULT_FUTURE_WINDOW = Duration.ofDays(60);

    private final GoogleCalendarClient googleCalendarClient;
    private final CalendarTeamResolver calendarTeamResolver;
    private final TeamRepository teamRepository;

    public GoogleCalendarEventsResponse getEvents(Long userId, GoogleCalendarSyncRequest request) {
        Long teamId = calendarTeamResolver.resolveTeamId(userId);
        Optional<String> calendarIdOpt = resolveTeamCalendarId(teamId, request.getCalendarId());

        ZoneId zoneId = resolveZone(request.getTimeZone());
        Instant now = Instant.now();
        Instant timeMin = parseDateTime(request.getTimeMin(), zoneId, now.minus(DEFAULT_PAST_WINDOW));
        Instant timeMax = parseDateTime(request.getTimeMax(), zoneId, now.plus(DEFAULT_FUTURE_WINDOW));

        if (timeMax.isBefore(timeMin)) {
            throw new CustomException(ErrorCode.INVALID_DATE_RANGE, "조회 종료일이 시작일보다 빠릅니다.");
        }

        if (calendarIdOpt.isEmpty()) {
            return GoogleCalendarEventsResponse.builder()
                    .calendarId(null)
                    .events(Collections.emptyList())
                    .build();
        }

        return googleCalendarClient.listEvents(
                calendarIdOpt.get(),
                timeMin,
                timeMax,
                request.getMaxResults(),
                request.getPageToken(),
                zoneId
        );
    }

    private Instant parseDateTime(String value, ZoneId zoneId, Instant defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return OffsetDateTime.parse(value).toInstant();
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        try {
            return java.time.LocalDateTime.parse(value).atZone(zoneId).toInstant();
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        try {
            return LocalDate.parse(value).atStartOfDay(zoneId).toInstant();
        } catch (DateTimeParseException ignored) {
            // fall through
        }

        throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "timeMin/timeMax는 ISO-8601 날짜 또는 날짜시간 형식이어야 합니다.");
    }

    private ZoneId resolveZone(String requestedZoneId) {
        if (requestedZoneId == null || requestedZoneId.isBlank()) {
            return googleCalendarClient.getDefaultZoneId();
        }
        try {
            return ZoneId.of(requestedZoneId);
        } catch (DateTimeException ex) {
            return googleCalendarClient.getDefaultZoneId();
        }
    }

    private Optional<String> resolveTeamCalendarId(Long teamId, String requestedCalendarId) {
        if (StringUtils.hasText(requestedCalendarId)) {
            return Optional.of(requestedCalendarId);
        }
        return teamRepository.findById(teamId)
                .map(Team::getGoogleCalendarId)
                .filter(StringUtils::hasText);
    }
}
