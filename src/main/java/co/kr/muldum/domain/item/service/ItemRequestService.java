package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.dto.TempItemResponseDto;
import co.kr.muldum.domain.item.dto.TempItemListResponseDto;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.ProductInfo;
import co.kr.muldum.domain.item.model.RequestDetails;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.Student;
import co.kr.muldum.domain.user.model.UserInfo;
import co.kr.muldum.presentation.dto.item.ItemStatusResponseDto;
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

    private final ItemRequestRepository itemRequestRepository;
    private final UserReader userReader;

    public TempItemResponseDto createTempItemRequest(TempItemRequestDto requestDto, Long userId) {
        UserInfo userInfo = userReader.read(Student.class, userId);

        log.debug("물품 신청 - 사용자 정보: userId={}, teamId={}, userType={}",
                userInfo.getUserId(), userInfo.getTeamId(), userInfo.getUserType());

        // teamId null 체크 추가
        if (userInfo.getTeamId() == null) {
            log.warn("물품 신청 실패 - teamId가 null입니다. userId={}", userId);
            return TempItemResponseDto.builder()
                    .status("REJECTED")
                    .message("팀 정보가 없습니다. 팀에 소속되어야 물품을 신청할 수 있습니다.")
                    .build();
        }

        if (requestDto.getProductLink() == null || requestDto.getProductLink().isBlank()) {
            log.warn("물품 신청 실패 - productLink가 비었습니다. userId={}", userId);
            return TempItemResponseDto.builder()
            .status("REJECTED")
            .message("상품 링크가 유효하지 않습니다.")
            .build();
        }
        ItemSource itemSource = ItemSource.fromUrl(requestDto.getProductLink());

        ItemStatus status;
        String message;

        if (itemSource == ItemSource.DEVICEMART || itemSource == ItemSource.ELEVENMARKET) {
            status = ItemStatus.INTEMP;
            message = "임시 신청이 완료되었습니다.";
        } else {
            status = ItemStatus.REJECTED;
            message = "허용되지 않은 쇼핑몰입니다. 디바이스마트(devicemart.co.kr) 또는 11번가(11st.co.kr)에서만 신청 가능합니다.";
        }

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
                .teamId(userInfo.getTeamId().intValue())
                .requesterUserId(userId.intValue())
                .productInfo(productInfo)
                .status(status)
                .requestDetails(requestDetails)
                .build();

        itemRequestRepository.save(itemRequest);

        return TempItemResponseDto.builder()
                .status(status.name())
                .message(message)
                .build();
    }

    public List<TempItemListResponseDto> getTempItemRequests(Long userId) {
        UserInfo userInfo = userReader.read(Student.class, userId);
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
                .productName(itemRequest.getProductInfo().getName())
                .quantity(itemRequest.getProductInfo().getQuantity())
                .price(itemRequest.getProductInfo().getPrice())
                .status(itemRequest.getStatus().name())
                .type("network")
                .build();
    }

    public List<ItemStatusResponseDto> getApprovedItemStatuses(Long userId) {
        UserInfo userInfo = userReader.read(Student.class, userId);
        log.debug("APPROVED 상태 물품 목록 조회 - teamId={}, userId={}", userInfo.getTeamId(), userInfo.getUserId());

        List<ItemRequest> approvedItems = itemRequestRepository
                .findByTeamIdAndStatus(userInfo.getTeamId().intValue(), ItemStatus.APPROVED);

        return approvedItems.stream()
                .map(item -> ItemStatusResponseDto.builder()
                        .productName(item.getProductInfo().getName())
                        .quantity(item.getProductInfo().getQuantity())
                        .status(item.getStatus().name())
                        .deliveryNumber(item.getDeliveryNumber())
                        .build())
                .toList();
    }
}