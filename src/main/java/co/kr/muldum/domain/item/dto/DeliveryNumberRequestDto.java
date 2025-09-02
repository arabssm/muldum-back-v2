package co.kr.muldum.domain.item.dto;

import lombok.Getter;

@Getter
public class DeliveryNumberRequestDto {
    private Long item_id;
    private String delivery_number;
}