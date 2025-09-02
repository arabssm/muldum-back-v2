package co.kr.muldum.domain.item.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemActionResponseDto {
    private String status;
    private String message;
}