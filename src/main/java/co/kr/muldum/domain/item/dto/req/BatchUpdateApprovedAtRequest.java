package co.kr.muldum.domain.item.dto.req;

import java.time.LocalDateTime;

public record BatchUpdateApprovedAtRequest(
        LocalDateTime startDate,
        LocalDateTime endDate) {
}
