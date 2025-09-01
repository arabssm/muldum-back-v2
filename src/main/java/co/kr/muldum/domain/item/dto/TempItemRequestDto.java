package co.kr.muldum.domain.item.dto;

import co.kr.muldum.domain.item.model.enums.ItemSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TempItemRequestDto {
    private String product_name;
    private Integer quantity;
    private String price;
    private String productLink;
    private String reason;
}