package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.ProductInfo;
import co.kr.muldum.domain.item.model.RequestDetails;
import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.model.enums.TeamType;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemRequestCreator {

    private final ItemRequestRepository itemRequestRepository;

    public ItemRequest createTempItemRequest(TempItemRequestDto requestDto, Long userId, int teamId) {
        ItemSource itemSource = ItemSource.fromUrl(requestDto.getProductLink());

        ProductInfo productInfo = ProductInfo.builder()
                .name(requestDto.getProductName())
                .quantity(requestDto.getQuantity())
                .price(requestDto.getPrice())
                .link(requestDto.getProductLink())
                .itemSource(itemSource)
                .build();

        RequestDetails requestDetails = RequestDetails.builder()
                .reason(requestDto.getReason())
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .teamId(teamId)
                .requesterUserId(userId.intValue())
                .productInfo(productInfo)
                .status(ItemStatus.INTEMP)
                .teamType(TeamType.NETWORK)  // 이 줄 추가
                .requestDetails(requestDetails)
                .build();

        return itemRequestRepository.save(itemRequest);
    }
}