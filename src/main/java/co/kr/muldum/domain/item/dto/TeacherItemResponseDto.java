package co.kr.muldum.domain.item.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeacherItemResponseDto {
    private Integer team_id;
    private String type;
    private Long item_id;
    private String product_name;
    private Integer quantity;
    private String price;
    private String productLink;
    private String reason;
    private String status;
}
