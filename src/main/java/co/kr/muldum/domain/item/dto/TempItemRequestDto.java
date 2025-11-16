package co.kr.muldum.domain.item.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias("product_link")
    private String productLink;
    private String reason;
    @JsonAlias("delivery_price")
    private String deliveryPrice;
    @JsonAlias("delivery_time")
    private String deliveryTime;
}
