package co.kr.muldum.calendar.domain;

import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.global.util.ValidationUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_entries")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private String teamId;

    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, length = 2000)
    private String content;

    @Column(name = "start_date_time", nullable = false, columnDefinition = "DATE")
    private LocalDate startDate;

    @Column(name = "end_date_time", nullable = false, columnDefinition = "DATE")
    private LocalDate endDate;

    @Column(name = "google_event_id", length = 255)
    private String googleEventId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private StudentCalendar(String teamId, String creatorId, String title,
                            String content, LocalDate startDate,
                            LocalDate endDate, String googleEventId) {
        this.teamId = teamId;
        this.creatorId = creatorId;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.googleEventId = googleEventId;
    }

    public static StudentCalendar create(String teamId, String creatorId,
                                         LocalDate startDate, LocalDate endDate,
                                         String title, String content) {
        validate(teamId, creatorId, startDate, endDate, title, content);
        return new StudentCalendar(
                teamId,
                creatorId,
                title.trim(),
                content.trim(),
                startDate,
                endDate,
                null
        );
    }

    public void update(LocalDate startDate, LocalDate endDate,
                       String title, String content) {
        validate(this.teamId, this.creatorId, startDate, endDate, title, content);
        this.title = title.trim();
        this.content = content.trim();
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void linkGoogleEvent(String eventId) {
        if (eventId == null || eventId.isBlank()) {
            return;
        }
        this.googleEventId = eventId;
    }

    private static void validate(String teamId, String creatorId,
                                 LocalDate startDate, LocalDate endDate,
                                 String title, String content) {
        if (!ValidationUtils.isNotEmpty(teamId)) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_ENTRY, "팀 정보가 없습니다.");
        }
        if (!ValidationUtils.isNotEmpty(creatorId)) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_ENTRY, "사용자 정보가 없습니다.");
        }
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new CustomException(ErrorCode.INVALID_DATE_RANGE, "시작일과 종료일을 확인해주세요.");
        }
        if (!ValidationUtils.isNotEmpty(title)) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_ENTRY, "제목은 필수입니다.");
        }
        if (!ValidationUtils.isLengthInRange(title.trim(), 1, 200)) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_ENTRY, "제목은 1~200자 이내여야 합니다.");
        }
        if (!ValidationUtils.isNotEmpty(content)) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_ENTRY, "내용은 필수입니다.");
        }
        if (!ValidationUtils.isLengthInRange(content.trim(), 1, 2000)) {
            throw new CustomException(ErrorCode.INVALID_CALENDAR_ENTRY, "내용은 1~2000자 이내여야 합니다.");
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getGoogleEventId() {
        return googleEventId;
    }
}
