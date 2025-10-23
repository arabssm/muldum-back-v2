package co.kr.muldum.domain.item.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemExcelResponseDto {
    private Long itemId;
    private String productName;
    private String price;
    private Integer quantity;
    private String productLink;
    private String requesterName;
}
