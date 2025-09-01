package co.kr.muldum.domain.item.dto;

import lombok.Getter;

@Getter
public class RejectItemRequestDto {
    private Long item_id;
    private String reason;
}