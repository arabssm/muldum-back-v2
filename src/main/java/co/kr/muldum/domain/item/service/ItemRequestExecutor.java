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
public class ItemRequestExecutor {

    private final ItemRequestRepository itemRequestRepository;

    public void deleteTempItemRequest(Long itemRequestId) {
        itemRequestRepository.deleteById(itemRequestId);
        log.info("임시 물품 삭제 완료 - itemId={}", itemRequestId);
    }

    public void deleteItemRequest(Long itemRequestId) {
        itemRequestRepository.deleteById(itemRequestId);
        log.info("물품 삭제 완료 - itemId={}", itemRequestId);
    }

    public ItemRequest createTempItemRequest(TempItemRequestDto requestDto, Long userId, int teamId) {
        ItemSource itemSource = ItemSource.fromUrl(requestDto.getProductLink());

        ProductInfo productInfo = ProductInfo.builder()
                .name(requestDto.getProduct_name())
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

    public ItemRequest updateItemRequest(Long itemId, TempItemRequestDto requestDto, Long userId, int teamId) {
        ItemRequest itemRequest = itemRequestRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("물품을 찾을 수 없습니다. itemId: " + itemId));

        // Update ProductInfo
        ProductInfo productInfo = itemRequest.getProductInfo();
        if (productInfo == null) {
            productInfo = ProductInfo.builder().build();
        }
        productInfo.updateInfo(
                requestDto.getProduct_name(),
                requestDto.getQuantity(),
                requestDto.getPrice() != null ? Long.parseLong(requestDto.getPrice()) : null,
                null, // TempItemRequestDto does not have description
                requestDto.getProductLink()
        );
        itemRequest.setProductInfo(productInfo);

        // Update RequestDetails
        RequestDetails requestDetails = itemRequest.getRequestDetails();
        if (requestDetails == null) {
            requestDetails = RequestDetails.builder().build();
        }
        requestDetails.updateReason(requestDto.getReason());
        itemRequest.setRequestDetails(requestDetails);

        return itemRequestRepository.save(itemRequest);
    }
}