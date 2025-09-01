package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.dto.TempItemListResponseDto;
import co.kr.muldum.domain.item.dto.ItemResponseDto;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.model.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemRequestService {

    private final ItemRequestFacade itemRequestFacade;
    private final ItemRequestRepository itemRequestRepository;
    private final UserReader userReader;

    public ItemResponseDto createTempItemRequest(TempItemRequestDto requestDto, Long userId) {
        return itemRequestFacade.createTempItemRequest(requestDto, userId);
    }

    public ItemResponseDto finalizeItemRequest(Long userId) {
        return itemRequestFacade.finalizeItemRequest(userId);
    }

    public List<TempItemListResponseDto> getTempItemRequests(Long userId) {
        UserInfo userInfo = userReader.read(User.class, userId);
        log.debug("임시 물품 목록 조회 - teamId={}, userId={}",
                userInfo.getTeamId(), userInfo.getUserId());

        List<ItemRequest> tempItems = itemRequestRepository
                .findByTeamIdAndStatus(userInfo.getTeamId().intValue(), ItemStatus.INTEMP);

        return tempItems.stream()
                .map(this::convertToTempListDto)
                .toList();
    }

    private TempItemListResponseDto convertToTempListDto(ItemRequest itemRequest) {
        return TempItemListResponseDto.builder()
                .id(itemRequest.getId())
                .product_name(itemRequest.getProductInfo().getName())
                .quantity(itemRequest.getProductInfo().getQuantity())
                .price(itemRequest.getProductInfo().getPrice())
                .status(itemRequest.getStatus().name())
                .type("network")
                .build();
    }
}