package co.kr.muldum.domain.item.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeacherItemResponseDto {
    private Integer teamId;
    private String type;
    private Long itemId;
    private String productName;
    private Integer quantity;
    private String price;
    private String productLink;
    private String reason;
    private String status;
}
