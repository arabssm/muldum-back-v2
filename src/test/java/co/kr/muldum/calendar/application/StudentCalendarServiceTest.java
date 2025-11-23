package co.kr.muldum.calendar.application;

import co.kr.muldum.calendar.application.dto.StudentCalendarResponse;
import co.kr.muldum.calendar.domain.StudentCalendar;
import co.kr.muldum.calendar.domain.StudentCalendarRepository;
import co.kr.muldum.calendar.presentation.dto.StudentCalendarRequest;
import co.kr.muldum.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentCalendarServiceTest {

    @Mock
    private StudentCalendarRepository studentCalendarRepository;

    @Mock
    private CalendarTeamResolver calendarTeamResolver;

    @InjectMocks
    private StudentCalendarService studentCalendarService;

    private StudentCalendarRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleRequest = new StudentCalendarRequest();
        sampleRequest.setStartYear(2025);
        sampleRequest.setStartDate(1219);
        sampleRequest.setEndYear(2025);
        sampleRequest.setEndDate(1220);
        sampleRequest.setTitle("김현우 생일");
        sampleRequest.setContent("왜이렇게사니");
    }

    @Test
    @DisplayName("일정을 생성하면 저장된 정보를 반환한다")
    void createCalendar() {
        given(calendarTeamResolver.resolveTeamId(1L)).willReturn(99L);
        given(studentCalendarRepository.save(any(StudentCalendar.class))).willAnswer(invocation -> {
            StudentCalendar calendar = invocation.getArgument(0);
            ReflectionTestUtils.setField(calendar, "id", 1L);
            return calendar;
        });

        StudentCalendarResponse response = studentCalendarService.create(1L, sampleRequest);

        assertThat(response.getCalendarId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("김현우 생일");
        assertThat(response.getStartYear()).isEqualTo(2025);
        assertThat(response.getEndDate()).isEqualTo(1220);
        verify(studentCalendarRepository).save(any(StudentCalendar.class));
    }

    @Test
    @DisplayName("팀 ID로 일정 목록을 조회한다")
    void getCalendars() {
        given(calendarTeamResolver.resolveTeamId(2L)).willReturn(88L);

        StudentCalendar calendar = StudentCalendar.create(
                "88",
                "2",
                LocalDate.of(2025, 12, 19),
                LocalDate.of(2025, 12, 20),
                "승급 테스트",
                "테스트 내용"
        );
        ReflectionTestUtils.setField(calendar, "id", 10L);

        given(studentCalendarRepository.findAllByTeamIdOrderByStartDateAsc("88"))
                .willReturn(List.of(calendar));

        List<StudentCalendarResponse> result = studentCalendarService.getCalendars(2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCalendarId()).isEqualTo(10L);
        assertThat(result.get(0).getContent()).isEqualTo("테스트 내용");
    }

    @Test
    @DisplayName("일정을 수정하면 새로운 날짜와 내용을 확인할 수 있다")
    void updateCalendar() {
        given(calendarTeamResolver.resolveTeamId(3L)).willReturn(55L);
        StudentCalendar calendar = StudentCalendar.create(
                "55",
                "3",
                LocalDate.of(2025, 12, 19),
                LocalDate.of(2025, 12, 19),
                "기존 제목",
                "기존 내용"
        );
        ReflectionTestUtils.setField(calendar, "id", 5L);

        given(studentCalendarRepository.findByIdAndTeamId(5L, "55")).willReturn(Optional.of(calendar));

        sampleRequest.setEndDate(1225);
        sampleRequest.setContent("수정된 내용");

        StudentCalendarResponse response = studentCalendarService.update(3L, 5L, sampleRequest);

        assertThat(response.getEndDate()).isEqualTo(1225);
        assertThat(response.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("일정을 삭제하면 저장소에서도 제거된다")
    void deleteCalendar() {
        given(calendarTeamResolver.resolveTeamId(4L)).willReturn(22L);
        StudentCalendar calendar = StudentCalendar.create(
                "22",
                "4",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2),
                "삭제 테스트",
                "삭제 내용"
        );
        given(studentCalendarRepository.findByIdAndTeamId(7L, "22"))
                .willReturn(Optional.of(calendar));

        studentCalendarService.delete(4L, 7L);

        verify(studentCalendarRepository).delete(calendar);
    }

    @Test
    @DisplayName("잘못된 날짜 형식이면 생성 시 예외가 발생한다")
    void createCalendar_withInvalidDate_shouldThrow() {
        sampleRequest.setStartDate(1501);

        given(calendarTeamResolver.resolveTeamId(anyLong())).willReturn(33L);

        assertThatThrownBy(() -> studentCalendarService.create(1L, sampleRequest))
                .isInstanceOf(CustomException.class);
    }
}
