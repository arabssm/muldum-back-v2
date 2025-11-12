package co.kr.muldum.domain.item.model;

import co.kr.muldum.domain.item.model.enums.ItemSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String deliveryPrice;
    private LocalDateTime deliveryTime;

    public void updateInfo(String name, Integer quantity, Long price, String description, String link, String deliveryPrice, LocalDateTime deliveryTime) {
        if (name != null) {
            this.name = name;
        }
        if (quantity != null) {
            this.quantity = quantity;
        }
        if (price != null) {
            this.price = String.valueOf(price);
        }
        if (description != null) {
            this.description = description;
        }
        if (link != null) {
            this.link = link;
        }
        if (deliveryPrice != null) {
            this.deliveryPrice = deliveryPrice;
        }
        if (deliveryTime != null) {
            this.deliveryTime = deliveryTime;
        }
    }
}