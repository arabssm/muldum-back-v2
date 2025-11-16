package co.kr.muldum.domain.item.dto;

import co.kr.muldum.domain.item.dto.req.ItemGuide;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class NthStatusHistoryResponseDto {
    private Integer nth;
    private String projectType;
    private List<ItemGuide> guide;
    private String deadlineDate;
    private Long teacherId;
    private LocalDateTime openedAt;
}
