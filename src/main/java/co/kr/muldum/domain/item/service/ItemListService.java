package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.ItemListResponseDto;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import co.kr.muldum.domain.user.model.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemListService {

    private final ItemRequestRepository itemRequestRepository;

    public List<ItemListResponseDto> getTeamItemRequests(UserInfo userInfo) {
        log.debug("팀 물품 목록 조회 - teamId={}, userId={}",
                userInfo.getTeamId(), userInfo.getUserId());

        List<ItemRequest> itemRequests = itemRequestRepository
                .findByTeamIdAndStatusNot(userInfo.getTeamId().intValue(), ItemStatus.INTEMP);

        return itemRequests.stream()
                .map(this::convertToDto)
                .toList();
    }

    private ItemListResponseDto convertToDto(ItemRequest itemRequest) {
        // price를 안전하게 파싱
        Integer price = null;
        try {
            if (itemRequest.getProductInfo().getPrice() != null) {
                price = Integer.parseInt(itemRequest.getProductInfo().getPrice());
            }
        } catch (NumberFormatException e) {
            log.warn("가격 파싱 오류 - itemId={}, price={}",
                    itemRequest.getId(), itemRequest.getProductInfo().getPrice());
            price = 0;
        }

        return ItemListResponseDto.builder()
                .id(itemRequest.getId())
                .product_name(itemRequest.getProductInfo().getName())
                .quantity(itemRequest.getProductInfo().getQuantity())
                .price(price)
                .status(itemRequest.getStatus().name())
                .type(itemRequest.getTeamType() != null ?
                        itemRequest.getTeamType().name().toLowerCase() : "network")
                .build();
    }
}