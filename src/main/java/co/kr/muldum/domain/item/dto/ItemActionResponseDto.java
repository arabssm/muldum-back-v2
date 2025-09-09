package co.kr.muldum.domain.item.dto;

import co.kr.muldum.domain.item.model.enums.ItemStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemActionResponseDto {
    private ItemStatus status;
    private String message;
}