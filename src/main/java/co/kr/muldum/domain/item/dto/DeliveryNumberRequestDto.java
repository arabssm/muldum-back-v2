package co.kr.muldum.domain.item.dto;

import lombok.Getter;

@Getter
public class DeliveryNumberRequestDto {
    private Long itemId;
    private String deliveryNumber;
}