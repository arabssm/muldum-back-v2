package co.kr.muldum.presentation.dto.item;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemStatusResponseDto {
    private String productName;
    private int quantity;
    private String status;
    private String deliveryNumber;
}
