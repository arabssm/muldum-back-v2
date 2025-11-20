package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.UsedBudgetResponseDto;
import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.dto.ItemResponseDto;
import co.kr.muldum.domain.item.dto.TempItemListResponseDto;
import co.kr.muldum.domain.item.dto.res.GetItemGuideResponse;
import co.kr.muldum.domain.item.model.ItemGuide;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.model.enums.TeamType;
import co.kr.muldum.domain.item.repository.ItemGuideRepository;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.model.UserInfo;
import co.kr.muldum.presentation.dto.item.ItemStatusResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ItemRequestService {

    private final ItemRequestFacade itemRequestFacade;
    private final ItemRequestRepository itemRequestRepository;
    private final UserReader userReader;
    private final ItemGuideRepository itemGuideRepository;

    public ItemResponseDto createTempItemRequest(TempItemRequestDto requestDto, Long userId) {
        log.info("임시 물품 생성 요청 - userId={}", userId);
        return itemRequestFacade.createTempItemRequest(requestDto, userId);
    }

    public ItemResponseDto updateTempItemRequest(Long itemId, TempItemRequestDto requestDto, Long userId) {
        log.info("임시 물품 수정 요청 - itemId={}, userId={}", itemId, userId);
        return itemRequestFacade.updateItemRequest(itemId, userId, requestDto);
    }

    public ItemResponseDto deleteItemRequest(Long itemId, Long userId) {
        log.info("물품 삭제 요청 - itemId={}, userId={}", itemId, userId);
        return itemRequestFacade.deleteItemRequest(itemId, userId);
    }

    public ItemResponseDto deleteTempItemRequest(Long itemId, Long userId) {
        log.info("임시 물품 삭제 요청 - itemId={}, userId={}", itemId, userId);
        return itemRequestFacade.deleteTempItemRequest(itemId, userId);
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
                .type(TeamType.NETWORK)
                .reason(itemRequest.getRequestDetails().getReason() != null ?
                        itemRequest.getRequestDetails().getReason() : "")
                .product_link(itemRequest.getProductInfo().getLink())
                .deliveryPrice(itemRequest.getProductInfo().getDeliveryPrice())
                .deliveryTime(itemRequest.getProductInfo().getDeliveryTime())
                .build();
    }

    public List<ItemStatusResponseDto> getApprovedItemStatuses(Long userId) {
        UserInfo userInfo = userReader.read(User.class, userId);
        log.debug("APPROVED 상태 물품 목록 조회 - teamId={}, userId={}",
                userInfo.getTeamId(), userInfo.getUserId());

        List<ItemRequest> approvedItems = itemRequestRepository
                .findByTeamIdAndStatus(userInfo.getTeamId().intValue(), ItemStatus.APPROVED);

        return approvedItems.stream()
                .map(item -> ItemStatusResponseDto.builder()
                        .productName(item.getProductInfo().getName())
                        .quantity(item.getProductInfo().getQuantity())
                        .status(item.getStatus().name())
                        .deliveryNumber(item.getRequestDetails() != null ?
                                item.getRequestDetails().getDeliveryInfo() : null)
                        .build())
                .toList();
    }

    public UsedBudgetResponseDto getUsedBudget(Long userId) {
        UserInfo userInfo = userReader.read(User.class, userId);
        log.info("사용된 예산 조회 - teamId={}, userId={}",
                userInfo.getTeamId(), userInfo.getUserId());

        List<ItemRequest> allItems = itemRequestRepository
                .findByTeamId(userInfo.getTeamId().intValue());

        List<ItemRequest> validItems = allItems.stream()
                .filter(item -> item.getStatus() != ItemStatus.REJECTED)
                .toList();

        long totalUsedBudget = 0L;
        for (ItemRequest item : validItems) {
            if (item.getProductInfo() != null
                    && item.getProductInfo().getPrice() != null
                    && item.getProductInfo().getQuantity() != null
                    && !item.getProductInfo().getPrice().trim().isEmpty()) {
                try {
                    long price = Long.parseLong(item.getProductInfo().getPrice());
                    int quantity = item.getProductInfo().getQuantity();
                    long itemTotal = price * quantity;
                    totalUsedBudget += itemTotal;
                } catch (NumberFormatException e) {
                    log.warn("가격 파싱 오류 - itemId={}, price={}",
                            item.getId(), item.getProductInfo().getPrice());
                }
            } else {
                log.warn("물품 정보 누락 - itemId={}", item.getId());
            }
        }

        return UsedBudgetResponseDto.builder()
                .usedBudget(totalUsedBudget)
                .build();
    }

    public GetItemGuideResponse getItemGuide(Long guideId, String projectType) {
        ItemGuide guide = itemGuideRepository.findByIdAndProjectType(guideId, projectType)
                .orElseThrow(() -> new IllegalArgumentException("해당 가이드를 찾을 수 없습니다."));

        return new GetItemGuideResponse(
                guide.getId(),
                guide.getContent()
        );
    }
}
