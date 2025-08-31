package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.*;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.RequestDetails;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeacherItemService {

    private final ItemRequestRepository itemRequestRepository;

    public List<TeacherItemResponseDto> getAllPendingAndApprovedItems() {
        log.info("모든 팀의 PENDING, APPROVED 물품 조회 시작");

        List<ItemRequest> items = itemRequestRepository.findByStatusIn(
                List.of(ItemStatus.PENDING, ItemStatus.APPROVED)
        );

        log.info("조회된 물품 수: {}", items.size());

        return items.stream()
                .map(this::convertToTeacherItemResponseDto)
                .toList();
    }

    public List<TeacherItemResponseDto> getItemsByTeamId(Integer teamId) {
        log.info("팀별 PENDING, APPROVED 물품 조회 시작 - teamId: {}", teamId);

        List<ItemRequest> items = itemRequestRepository.findByTeamIdAndStatusIn(
                teamId,
                List.of(ItemStatus.PENDING, ItemStatus.APPROVED)
        );

        log.info("팀 {}의 조회된 물품 수: {}", teamId, items.size());

        return items.stream()
                .map(this::convertToTeacherItemResponseDto)
                .toList();
    }

    @Transactional
    public ItemActionResponseDto rejectItems(List<RejectItemRequestDto> rejectRequests) {
        log.info("물품 거절 처리 시작 - 처리할 물품 수: {}", rejectRequests.size());

        int processedCount = 0;
        for (RejectItemRequestDto request : rejectRequests) {
            try {
                ItemRequest item = itemRequestRepository.findById(request.getItemId())
                        .orElse(null);

                if (item != null) {
                    item.updateStatus(ItemStatus.REJECTED);
                    // 거절 사유 저장 (RequestDetails 업데이트)
                    if (item.getRequestDetails() != null) {
                        // RequestDetails에 거절 사유 추가하는 로직 필요
                        // 현재 구조상 reason 필드 업데이트
                    }
                    itemRequestRepository.save(item);
                    processedCount++;
                    log.info("물품 거절 완료 - itemId: {}, reason: {}", request.getItemId(), request.getReason());
                } else {
                    log.warn("물품을 찾을 수 없음 - itemId: {}", request.getItemId());
                }
            } catch (Exception e) {
                log.error("물품 거절 처리 중 오류 - itemId: {}, error: {}", request.getItemId(), e.getMessage());
            }
        }

        log.info("물품 거절 처리 완료 - 총 처리된 물품 수: {}/{}", processedCount, rejectRequests.size());

        return ItemActionResponseDto.builder()
                .status("REJECTED")
                .message("거절 사유가 등록되었습니다.")
                .build();
    }

    @Transactional
    public ItemActionResponseDto approveItems(List<ApproveItemRequestDto> approveRequests) {
        log.info("물품 승인 처리 시작 - 처리할 물품 수: {}", approveRequests.size());

        int processedCount = 0;
        for (ApproveItemRequestDto request : approveRequests) {
            try {
                ItemRequest item = itemRequestRepository.findById(request.getItemId())
                        .orElse(null);

                if (item != null) {
                    item.updateStatus(ItemStatus.APPROVED);
                    itemRequestRepository.save(item);
                    processedCount++;
                    log.info("물품 승인 완료 - itemId: {}", request.getItemId());
                } else {
                    log.warn("물품을 찾을 수 없음 - itemId: {}", request.getItemId());
                }
            } catch (Exception e) {
                log.error("물품 승인 처리 중 오류 - itemId: {}, error: {}", request.getItemId(), e.getMessage());
            }
        }

        log.info("물품 승인 처리 완료 - 총 처리된 물품 수: {}/{}", processedCount, approveRequests.size());

        return ItemActionResponseDto.builder()
                .status("APPROVED")
                .message("물품이 승인되었습니다.")
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

    @Transactional
    public DeliveryNumberResponseDto registerDeliveryNumber(DeliveryNumberRequestDto request) {
        log.info("운송장 번호 등록 시작 - itemId: {}, deliveryNumber: {}",
                request.getItemId(), request.getDeliveryNumber());

        ItemRequest item = itemRequestRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("물품을 찾을 수 없습니다. itemId: " + request.getItemId()));

        // RequestDetails의 deliveryInfo에 운송장 번호 저장
        RequestDetails updatedDetails = RequestDetails.builder()
                .reason(item.getRequestDetails() != null ? item.getRequestDetails().getReason() : null)
                .deliveryInfo(request.getDeliveryNumber())
                .approvalNotes(item.getRequestDetails() != null ? item.getRequestDetails().getApprovalNotes() : null)
                .build();

        // ItemRequest 업데이트 (새로운 RequestDetails로 교체)
        ItemRequest updatedItem = ItemRequest.builder()
                .id(item.getId())
                .teamId(item.getTeamId())
                .requesterUserId(item.getRequesterUserId())
                .productInfo(item.getProductInfo())
                .status(item.getStatus())
                .rejectId(item.getRejectId())
                .requestDetails(updatedDetails)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();

        itemRequestRepository.save(updatedItem);

        log.info("운송장 번호 등록 완료 - itemId: {}", request.getItemId());

        return DeliveryNumberResponseDto.builder()
                .message("운송장 번호가 등록되었습니다.")
                .build();
    }
}