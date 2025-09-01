package co.kr.muldum.domain.item.dto;

import co.kr.muldum.domain.item.model.ItemRequest;
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

    public static TeacherItemResponseDto from(ItemRequest itemRequest) {
        return TeacherItemResponseDto.builder()
                .teamId(itemRequest.getTeamId())
                .type("NETWORK") // 고정값
                .itemId(itemRequest.getId())
                .productName(itemRequest.getProductInfo() != null ?
                        itemRequest.getProductInfo().getName() : null)
                .quantity(itemRequest.getProductInfo() != null ?
                        itemRequest.getProductInfo().getQuantity() : null)
                .price(itemRequest.getProductInfo() != null ?
                        itemRequest.getProductInfo().getPrice() : null)
                .productLink(itemRequest.getProductInfo() != null ?
                        itemRequest.getProductInfo().getLink() : null)
                .reason(itemRequest.getRequestDetails() != null ?
                        itemRequest.getRequestDetails().getReason() : null)
                .status(itemRequest.getStatus().name())
                .build();
    }

    private TeacherItemResponseDto convertToTeacherItemResponseDto(ItemRequest itemRequest) {
        return TeacherItemResponseDto.builder()
                .teamId(itemRequest.getTeamId())
                .type("NETWORK") // 고정값
                .itemId(itemRequest.getId())
                .productName(itemRequest.getProductInfo() != null ?
                        itemRequest.getProductInfo().getName() : null)
                .quantity(itemRequest.getProductInfo() != null ?
                        itemRequest.getProductInfo().getQuantity() : null)
                .price(itemRequest.getProductInfo() != null ?
                        itemRequest.getProductInfo().getPrice() : null)
                .productLink(itemRequest.getProductInfo() != null ?
                        itemRequest.getProductInfo().getLink() : null)
                .reason(itemRequest.getRequestDetails() != null ?
                        itemRequest.getRequestDetails().getReason() : null)
                .status(itemRequest.getStatus().name())
                .build();
    }
}
