package co.kr.muldum.calendar.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentCalendarRepository extends JpaRepository<StudentCalendar, Long> {
    List<StudentCalendar> findAllByTeamIdOrderByStartDateAsc(String teamId);
    Optional<StudentCalendar> findByIdAndTeamId(Long calendarId, String teamId);
    Optional<StudentCalendar> findByTaskId(Long taskId);
}
