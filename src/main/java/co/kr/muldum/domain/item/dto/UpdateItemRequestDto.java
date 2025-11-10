package co.kr.muldum.domain.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateItemRequestDto {
    private String itemName;
    private Integer count;
    private Long price;
    private String description;
    private String imageUrl;
}
