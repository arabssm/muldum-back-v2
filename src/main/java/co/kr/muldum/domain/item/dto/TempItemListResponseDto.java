package co.kr.muldum.domain.item.dto;

import co.kr.muldum.domain.item.model.enums.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempItemListResponseDto {
    private Long id;
    private String product_name;
    private Integer quantity;
    private String price;
    private String status;
    private TeamType type;
    private String reason;
    private String product_link;
}