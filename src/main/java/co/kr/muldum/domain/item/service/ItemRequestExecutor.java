package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.ProductInfo;
import co.kr.muldum.domain.item.model.RequestDetails;
import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.model.enums.TeamType;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

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

    public void deleteTempItemRequests(List<Long> itemRequestIds) {
        itemRequestRepository.deleteAllById(itemRequestIds);
        log.info("임시 물품 일괄 삭제 완료 - itemIds={}", itemRequestIds);
    }

    public void deleteItemRequest(Long itemRequestId) {
        itemRequestRepository.deleteById(itemRequestId);
        log.info("물품 삭제 완료 - itemId={}", itemRequestId);
    }

    private LocalDateTime parseDeliveryTime(String deliveryTime) {
        if (deliveryTime == null || deliveryTime.isEmpty()) {
            return null;
        }

        try {
            // LocalDate로 파싱 시도 (yyyy-MM-dd 형식)
            LocalDate date = LocalDate.parse(deliveryTime);
            return date.atStartOfDay(); // 00:00:00으로 설정
        } catch (DateTimeParseException e1) {
            try {
                // LocalDateTime으로 파싱 시도 (yyyy-MM-ddTHH:mm:ss 형식)
                return LocalDateTime.parse(deliveryTime);
            } catch (DateTimeParseException e2) {
                log.warn("배송시간 파싱 실패: {}", deliveryTime);
                return null;
            }
        }
    }

    public ItemRequest createTempItemRequest(TempItemRequestDto requestDto, Long userId, int teamId, Integer nth) {
        ItemSource itemSource = ItemSource.fromUrl(requestDto.getProductLink());

        ProductInfo productInfo = ProductInfo.builder()
                .name(requestDto.getProduct_name())
                .quantity(requestDto.getQuantity())
                .price(requestDto.getPrice())
                .link(requestDto.getProductLink())
                .itemSource(itemSource)
                .deliveryPrice(requestDto.getDeliveryPrice())
                .deliveryTime(parseDeliveryTime(requestDto.getDeliveryTime()))
                .build();

        RequestDetails requestDetails = RequestDetails.builder()
                .reason(requestDto.getReason())
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .teamId(teamId)
                .requesterUserId(userId.intValue())
                .productInfo(productInfo)
                .status(ItemStatus.INTEMP)
                .teamType(TeamType.NETWORK)
                .requestDetails(requestDetails)
                .nth(nth)
                .build();

        return itemRequestRepository.save(itemRequest);
    }

    public ItemRequest updateItemRequest(Long itemId, TempItemRequestDto requestDto, Long userId, int teamId) {
        ItemRequest itemRequest = itemRequestRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("물품을 찾을 수 없습니다. itemId: " + itemId));

        // 기존 ProductInfo 가져오기
        ProductInfo oldProductInfo = itemRequest.getProductInfo();

        // 새로운 ProductInfo 생성 (JSONB 변경 감지를 위해)
        ProductInfo newProductInfo = ProductInfo.builder()
                .name(requestDto.getProduct_name() != null ? requestDto.getProduct_name() :
                      (oldProductInfo != null ? oldProductInfo.getName() : null))
                .quantity(requestDto.getQuantity() != null ? requestDto.getQuantity() :
                         (oldProductInfo != null ? oldProductInfo.getQuantity() : null))
                .price(requestDto.getPrice() != null ? requestDto.getPrice() :
                      (oldProductInfo != null ? oldProductInfo.getPrice() : null))
                .link(requestDto.getProductLink() != null ? requestDto.getProductLink() :
                     (oldProductInfo != null ? oldProductInfo.getLink() : null))
                .itemSource(requestDto.getProductLink() != null ?
                           ItemSource.fromUrl(requestDto.getProductLink()) :
                           (oldProductInfo != null ? oldProductInfo.getItemSource() : null))
                .description(oldProductInfo != null ? oldProductInfo.getDescription() : null)
                .deliveryPrice(requestDto.getDeliveryPrice() != null ? requestDto.getDeliveryPrice() :
                              (oldProductInfo != null ? oldProductInfo.getDeliveryPrice() : null))
                .deliveryTime(requestDto.getDeliveryTime() != null ? parseDeliveryTime(requestDto.getDeliveryTime()) :
                             (oldProductInfo != null ? oldProductInfo.getDeliveryTime() : null))
                .build();

        itemRequest.updateProductInfo(newProductInfo);

        // Update RequestDetails
        RequestDetails requestDetails = itemRequest.getRequestDetails();
        if (requestDetails == null) {
            requestDetails = RequestDetails.builder().build();
        }
        requestDetails.updateReason(requestDto.getReason());
        itemRequest.updateRequestDetails(requestDetails);

        return itemRequestRepository.save(itemRequest);
    }

    public ItemRequest duplicateRejectedItemAsTemp(ItemRequest source, Integer nth) {
        if (source.getProductInfo() == null) {
            throw new CustomException(ErrorCode.INVALID_PRODUCT_LINK);
        }

        ProductInfo originalInfo = source.getProductInfo();
        ProductInfo copiedInfo = ProductInfo.builder()
                .name(originalInfo.getName())
                .quantity(originalInfo.getQuantity())
                .price(originalInfo.getPrice())
                .link(originalInfo.getLink())
                .itemSource(originalInfo.getItemSource())
                .description(originalInfo.getDescription())
                .deliveryPrice(originalInfo.getDeliveryPrice())
                .deliveryTime(originalInfo.getDeliveryTime())
                .build();

        RequestDetails newDetails = RequestDetails.builder()
                .reason(null)
                .deliveryInfo(null)
                .approvalNotes(null)
                .build();

        ItemRequest copy = ItemRequest.builder()
                .teamId(source.getTeamId())
                .requesterUserId(source.getRequesterUserId())
                .productInfo(copiedInfo)
                .status(ItemStatus.INTEMP)
                .teamType(source.getTeamType())
                .requestDetails(newDetails)
                .nth(nth != null ? nth : source.getNth())
                .build();

        ItemRequest saved = itemRequestRepository.save(copy);
        log.info("거절 물품 재신청 복사 완료 - originalId={}, newId={}", source.getId(), saved.getId());
        return saved;
    }
}
