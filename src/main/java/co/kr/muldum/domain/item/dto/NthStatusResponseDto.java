package co.kr.muldum.domain.item.dto;

import co.kr.muldum.domain.item.dto.req.ItemMinPriceRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class NthStatusResponseDto {
    private Integer nth;
    private String projectType;
    private List<ItemMinPriceRequest> guide;
    private String deadlineDate;
    private Long teacherId;
    private LocalDateTime openedAt;
}
