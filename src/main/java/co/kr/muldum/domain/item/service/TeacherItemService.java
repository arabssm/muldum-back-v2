package co.kr.muldum.domain.item.service;

import co.kr.muldum.application.teamspace.ExcelExportService;
import co.kr.muldum.domain.item.dto.*;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.RequestDetails;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.model.enums.TeamType;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.model.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeacherItemService {

    private final ItemRequestRepository itemRequestRepository;
    private final ExcelExportService excelExportService;
    private final UserReader userReader;

    public ByteArrayInputStream getApprovedItemsAsXlsx() throws IOException {
        List<ItemRequest> items = itemRequestRepository.findByStatus(ItemStatus.APPROVED);
        List<ItemExcelResponseDto> dtos = items.stream()
                .map(this::convertToItemExcelResponseDto)
                .collect(Collectors.toList());
        return excelExportService.createXlsx(dtos);
    }

    private ItemExcelResponseDto convertToItemExcelResponseDto(ItemRequest itemRequest) {
        UserInfo requester = userReader.read(UserInfo.class, itemRequest.getRequesterUserId().longValue());
        String requesterName = (requester != null) ? requester.getName() : "Unknown";

        String productName = null;
        String price = null;
        Integer quantity = null;
        String productLink = null;

        if (itemRequest.getProductInfo() != null) {
            productName = itemRequest.getProductInfo().getName();
            price = itemRequest.getProductInfo().getPrice();
            quantity = itemRequest.getProductInfo().getQuantity();
            productLink = itemRequest.getProductInfo().getLink();
        }

        return ItemExcelResponseDto.builder()
                .itemId(itemRequest.getId())
                .productName(productName)
                .price(price)
                .quantity(quantity)
                .productLink(productLink)
                .requesterName(requesterName)
                .build();
    }


    public List<TeacherItemResponseDto> getAllPendingItems() {
        log.info("모든 팀의 PENDING, APPROVED 물품 조회 시작");

        List<ItemRequest> items = itemRequestRepository.findByStatus(ItemStatus.PENDING);

        log.info("조회된 물품 수: {}", items.size());

        return items.stream()
                .map(this::convertToTeacherItemResponseDto)
                .toList();
    }

    public List<TeacherItemResponseDto> getAllApprovedItems() {
        log.info("모든 팀의 PENDING, APPROVED 물품 조회 시작");

        List<ItemRequest> items = itemRequestRepository.findByStatus(ItemStatus.APPROVED);

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

    public List<TeacherItemResponseDto> getAllNotApprovedItems() {
        log.info("모든 팀의 승인 안된 물품 조회 시작");

        List<ItemRequest> items = itemRequestRepository.findByStatus(ItemStatus.PENDING);

        log.info("조회된 승인 안된 물품 수: {}", items.size());

        return items.stream()
                .map(this::convertToTeacherItemResponseDto)
                .toList();
    }

    public List<TeacherItemResponseDto> getItemsByTeamIdNotApproved(Integer teamId) {
        log.info("팀별 승인 안된 물품 조회 시작 - teamId: {}", teamId);

        List<ItemRequest> items = itemRequestRepository.findByTeamIdAndStatus(
                teamId,
                ItemStatus.PENDING
        );

        log.info("팀 {}의 승인 안된 물품 수: {}", teamId, items.size());

        return items.stream()
                .map(this::convertToTeacherItemResponseDto)
                .toList();
    }

    public List<TeacherItemResponseDto> getItemsByTeamIdApproved(Integer teamId) {
        log.info("팀별 승인 상태 물품 조회 시작 - teamId: {}", teamId);

        List<ItemRequest> items = itemRequestRepository.findByTeamIdAndStatus(
                teamId,
                ItemStatus.APPROVED
        );

        log.info("팀 {}의 승인된 물품 수: {}", teamId, items.size());

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
                ItemRequest item = itemRequestRepository.findById(request.getItem_id())
                        .orElse(null);

                if (item != null) {
                    item.updateStatus(ItemStatus.REJECTED);
                    // 거절 사유 저장 (RequestDetails 업데이트)
                    if (item.getRequestDetails() != null) {
                        item.getRequestDetails().updateReason(request.getReason());
                    }
                    itemRequestRepository.save(item);
                    processedCount++;
                    log.info("물품 거절 완료 - itemId: {}, reason: {}", request.getItem_id(), request.getReason());
                } else {
                    log.warn("물품을 찾을 수 없음 - itemId: {}", request.getItem_id());
                }
            } catch (Exception e) {
                log.error("물품 거절 처리 중 오류 - itemId: {}, error: {}", request.getItem_id(), e.getMessage());
            }
        }

        log.info("물품 거절 처리 완료 - 총 처리된 물품 수: {}/{}", processedCount, rejectRequests.size());

        return ItemActionResponseDto.builder()
                .status(ItemStatus.REJECTED)
                .message("거절 사유가 등록되었습니다.")
                .build();
    }

    @Transactional
    public ItemActionResponseDto approveItems(List<ApproveItemRequestDto> approveRequests) {
        log.info("물품 승인 처리 시작 - 처리할 물품 수: {}", approveRequests.size());

        int processedCount = 0;
        for (ApproveItemRequestDto request : approveRequests) {
            try {
                ItemRequest item = itemRequestRepository.findById(request.getItem_id())
                        .orElse(null);

                if (item != null) {
                    item.updateStatus(ItemStatus.APPROVED);
                    itemRequestRepository.save(item);
                    processedCount++;
                    log.info("물품 승인 완료 - itemId: {}", request.getItem_id());
                } else {
                    log.warn("물품을 찾을 수 없음 - itemId: {}", request.getItem_id());
                }
            } catch (Exception e) {
                log.error("물품 승인 처리 중 오류 - itemId: {}, error: {}", request.getItem_id(), e.getMessage());
            }
        }

        log.info("물품 승인 처리 완료 - 총 처리된 물품 수: {}/{}", processedCount, approveRequests.size());

        return ItemActionResponseDto.builder()
                .status(ItemStatus.APPROVED)
                .message("물품이 승인되었습니다.")
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
                .build();
    }

    @Transactional
    public DeliveryNumberResponseDto registerDeliveryNumber(DeliveryNumberRequestDto request) {
        log.info("운송장 번호 등록 시작 - itemId: {}, deliveryNumber: {}",
                request.getItem_id(), request.getDelivery_number());

        ItemRequest item = itemRequestRepository.findById(request.getItem_id())
                .orElseThrow(() -> new IllegalArgumentException("물품을 찾을 수 없습니다. itemId: " + request.getItem_id()));

        // RequestDetails의 deliveryInfo에 운송장 번호 저장
        RequestDetails updatedDetails = RequestDetails.builder()
                .reason(item.getRequestDetails() != null ? item.getRequestDetails().getReason() : null)
                .deliveryInfo(request.getDelivery_number())
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

        log.info("운송장 번호 등록 완료 - itemId: {}", request.getItem_id());

        return DeliveryNumberResponseDto.builder()
                .message("운송장 번호가 등록되었습니다.")
                .build();
    }
}