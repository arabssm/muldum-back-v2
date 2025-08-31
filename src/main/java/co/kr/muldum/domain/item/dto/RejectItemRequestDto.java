package co.kr.muldum.domain.item.dto;

import lombok.Getter;

@Getter
public class RejectItemRequestDto {
    private Long itemId;
    private String reason;
}