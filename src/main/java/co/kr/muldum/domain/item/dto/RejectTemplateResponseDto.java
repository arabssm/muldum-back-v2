package co.kr.muldum.domain.item.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RejectTemplateResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
}
