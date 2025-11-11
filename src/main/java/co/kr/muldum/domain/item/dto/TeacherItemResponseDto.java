package co.kr.muldum.domain.item.dto;

import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.enums.TeamType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeacherItemResponseDto {
    private Integer team_id;
    private TeamType type;
    private Long item_id;
    private String product_name;
    private Integer quantity;
    private String price;
    private String productLink;
    private String reason;
    private String status;
    private String deliveryNumber;
    private String deliveryPrice;
    private LocalDateTime deliveryTime;
    private String rejectReason;

    public static TeacherItemResponseDto from(ItemRequest itemRequest) {
        return TeacherItemResponseDto.builder()
                .team_id(itemRequest.getTeamId())
                .type(TeamType.NETWORK) // 고정값
                .item_id(itemRequest.getId())
                .product_name(itemRequest.getProductInfo() != null ?
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
                .deliveryNumber(itemRequest.getDeliveryNumber() != null ?
                        itemRequest.getDeliveryNumber() : null)
                .deliveryPrice(itemRequest.getProductInfo() != null ?
                        itemRequest.getProductInfo().getDeliveryPrice() : null)
                .deliveryTime(itemRequest.getProductInfo() != null ?
                        itemRequest.getProductInfo().getDeliveryTime() : null)
                .rejectReason(itemRequest.getRequestDetails() != null ?
                        itemRequest.getRequestDetails().getReason() : null)
                .build();
    }

    private TeacherItemResponseDto convertToTeacherItemResponseDto(ItemRequest itemRequest) {
        return TeacherItemResponseDto.builder()
                .team_id(itemRequest.getTeamId())
                .type(TeamType.NETWORK) // 고정값
                .item_id(itemRequest.getId())
                .product_name(itemRequest.getProductInfo() != null ?
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
                .deliveryNumber(itemRequest.getDeliveryNumber() != null ?
                        itemRequest.getDeliveryNumber() : null)
                .deliveryPrice(itemRequest.getProductInfo() != null ?
                        itemRequest.getProductInfo().getDeliveryPrice() : null)
                .deliveryTime(itemRequest.getProductInfo() != null ?
                        itemRequest.getProductInfo().getDeliveryTime() : null)
                .rejectReason(itemRequest.getRequestDetails() != null ?
                        itemRequest.getRequestDetails().getReason() : null)
                .build();
    }
}
