package co.kr.muldum.domain.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemListResponseDto {
    private Long id;
    private String product_name;
    private Integer quantity;
    private Integer price;
    private String productLink;
    private String status;
    private String type;
    private String deliveryPrice;
    private LocalDateTime deliveryTime;
    private String rejectReason;
}
