package co.kr.muldum.domain.item.model;

import co.kr.muldum.domain.item.model.enums.ItemSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfo {
    private String name;
    private Integer quantity;
    private String price;
    private String link;
    private ItemSource itemSource;
    private String description;
}