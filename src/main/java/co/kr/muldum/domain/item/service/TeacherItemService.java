package co.kr.muldum.domain.item.service;

import co.kr.muldum.application.teamspace.ExcelExportService;
import co.kr.muldum.domain.item.dto.*;
import co.kr.muldum.domain.item.dto.req.ItemMinPriceRequest;
import co.kr.muldum.domain.item.dto.NthStatusHistoryResponseDto;
import co.kr.muldum.domain.item.dto.NthOpenCountResponseDto;
import co.kr.muldum.domain.item.dto.NthOpenedListResponseDto;
import co.kr.muldum.domain.item.dto.req.ItemGuideRequest;
import co.kr.muldum.domain.item.dto.res.ItemGuideResponse;
import co.kr.muldum.domain.item.model.*;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.model.enums.TeamType;
import co.kr.muldum.domain.item.repository.ItemApprovalHistoryRepository;
import co.kr.muldum.domain.item.repository.ItemGuideRepository;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import co.kr.muldum.domain.item.repository.NthStatusRepository;
import co.kr.muldum.domain.item.repository.NthStatusHistoryRepository;
import co.kr.muldum.domain.item.repository.ViewRepository;
import co.kr.muldum.domain.teamspace.model.Team;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeacherItemService {

    private final ItemRequestRepository itemRequestRepository;
    private final NthStatusRepository nthStatusRepository;
    private final ExcelExportService excelExportService;
    private final UserReader userReader;
    private final NthStatusQueryService nthStatusQueryService;
    private final NthStatusHistoryRepository nthStatusHistoryRepository;
    private final ItemGuideRepository itemGuideRepository;
    private final ViewRepository viewRepository;
    private final ItemApprovalHistoryRepository itemApprovalHistoryRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public String fixNthIssues() {
        log.info("물품신청 n차 문제 해결 시작");

        try {
            // JPA를 사용하여 조회 또는 생성
            NthStatus nthStatus = nthStatusRepository.findById(1L)
                    .orElse(null);

            if (nthStatus == null) {
                // 새로 생성 (ID는 자동 생성되도록 설정하지 않음)
                nthStatus = NthStatus.builder()
                        .nthValue(0)
                        .build();
                nthStatus = nthStatusRepository.save(nthStatus);
                log.info("NthStatus 엔티티 생성 완료 - id: {}, nthValue: 0", nthStatus.getId());
            } else if (nthStatus.getNthValue() == null) {
                // nthValue가 null인 경우 0으로 초기화
                nthStatus.updateNthValue(0, null, null, null, null);
                log.info("NthStatus의 nthValue를 0으로 초기화함 - id: {}", nthStatus.getId());
            } else {
                log.info("NthStatus가 이미 정상 상태임 - id: {}, nthValue: {}",
                        nthStatus.getId(), nthStatus.getNthValue());
            }

            return "물품신청 n차 문제 해결이 완료되었습니다.";
        } catch (Exception e) {
            log.error("물품신청 n차 문제 해결 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("NthStatus 초기화 실패: " + e.getMessage(), e);
        }
    }

    public NthStatusResponseDto getNthStatus() {
        log.info("현재 물품 신청 차수 조회 시작");

        NthStatusResponseDto nthStatus = nthStatusQueryService.getCurrentStatus();
        log.info("현재 물품 신청 차수: {}차", nthStatus.getNth());

        return nthStatus;
    }

    public ByteArrayInputStream getApprovedItemsAsXlsx() throws IOException {
        List<ItemRequest> items = itemRequestRepository.findByStatus(ItemStatus.APPROVED);
        List<ItemExcelResponseDto> dtos = items.stream()
                .map(this::convertToItemExcelResponseDto)
                .collect(Collectors.toList());
        return excelExportService.createXlsx(dtos);
    }

    public ByteArrayInputStream getApprovedItemsAsXlsxOnDate(LocalDate targetDate) throws IOException {
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

        List<ItemRequest> items = itemRequestRepository.findByApprovedAtBetween(startOfDay, endOfDay);
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


    @Transactional
    public List<TeacherItemResponseDto> getAllPendingItems(Long teacherId) {
        return buildResponse(itemRequestRepository.findByStatus(ItemStatus.PENDING), teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getAllApprovedItems(Long teacherId, LocalDate targetDate) {
        List<ItemRequest> items = itemRequestRepository.findByStatus(ItemStatus.APPROVED);
        if (targetDate != null) {
            items = filterByDate(items, targetDate, true);
        }
        return buildResponse(items, teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getAllMajorPendingItems(Long teacherId) {
        return buildResponse(filterByTeamType(itemRequestRepository.findByStatus(ItemStatus.PENDING), TeamType.MAJOR), teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getAllMajorApprovedItems(Long teacherId, LocalDate targetDate) {
        List<ItemRequest> items = filterByTeamType(itemRequestRepository.findByStatus(ItemStatus.APPROVED), TeamType.MAJOR);
        if (targetDate != null) {
            items = filterByDate(items, targetDate, true);
        }
        return buildResponse(items, teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getAllMajorRejectedItems(Long teacherId) {
        return buildResponse(filterByTeamType(itemRequestRepository.findByStatus(ItemStatus.REJECTED), TeamType.MAJOR), teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getItemsApprovedOn(LocalDate targetDate, Long teacherId, String teamName) {
        log.info("특정 날짜 승인 물품 조회 시작 - date: {}, teamName: {}", targetDate, teamName);
        Integer teamId = resolveTeamIdFromName(teamName);
        List<ItemRequest> items = findItemsByStatusAndDate(targetDate, ItemStatus.APPROVED, teamId);
        log.info("특정 날짜 승인 물품 조회 완료 - 총 {}건", items.size());
        return buildResponse(items, teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getItemsRejectedOn(LocalDate targetDate, Long teacherId, String teamName) {
        log.info("특정 날짜 거절 물품 조회 시작 - date: {}, teamName: {}", targetDate, teamName);
        Integer teamId = resolveTeamIdFromName(teamName);
        List<ItemRequest> items = findItemsByStatusAndDate(targetDate, ItemStatus.REJECTED, teamId);
        log.info("특정 날짜 거절 물품 조회 완료 - 총 {}건", items.size());
        return buildResponse(items, teacherId);
    }

    @Transactional(readOnly = true)
    public List<LocalDate> getApprovedDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate는 endDate보다 이후일 수 없습니다.");
        }
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
        return itemRequestRepository.findDistinctApprovedDates(start, end).stream()
                .map(java.sql.Date::toLocalDate)
                .toList();
    }

    @Transactional
    public List<TeacherItemResponseDto> getItemsByTeamId(Integer teamId, Long teacherId) {
        log.info("팀별 PENDING, APPROVED 물품 조회 시작 - teamId: {}", teamId);

        List<ItemRequest> items = itemRequestRepository.findByTeamIdAndStatusIn(
                teamId,
                List.of(ItemStatus.PENDING, ItemStatus.APPROVED)
        );

        log.info("팀 {}의 조회된 물품 수: {}", teamId, items.size());

        return buildResponse(items, teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getMajorItemsByTeamId(Integer teamId, Long teacherId) {
        validateTeamType(teamId, TeamType.MAJOR);
        return getItemsByTeamId(teamId, teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getAllNotApprovedItems(Long teacherId) {
        return getAllPendingItems(teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getAllMajorNotApprovedItems(Long teacherId) {
        return getAllMajorPendingItems(teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getItemsByTeamIdNotApproved(Integer teamId, Long teacherId) {
        log.info("팀별 승인 안된 물품 조회 시작 - teamId: {}", teamId);

        List<ItemRequest> items = itemRequestRepository.findByTeamIdAndStatus(
                teamId,
                ItemStatus.PENDING
        );

        log.info("팀 {}의 승인 안된 물품 수: {}", teamId, items.size());

        return buildResponse(items, teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getMajorItemsByTeamIdNotApproved(Integer teamId, Long teacherId) {
        validateTeamType(teamId, TeamType.MAJOR);
        return getItemsByTeamIdNotApproved(teamId, teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getItemsByTeamIdApproved(Integer teamId, Long teacherId, LocalDate targetDate) {
        log.info("팀별 승인 상태 물품 조회 시작 - teamId: {}", teamId);

        List<ItemRequest> items = itemRequestRepository.findByTeamIdAndStatus(
                teamId,
                ItemStatus.APPROVED
        );

        if (targetDate != null) {
            LocalDateTime start = targetDate.atStartOfDay();
            LocalDateTime end = targetDate.atTime(LocalTime.MAX);
            items = items.stream()
                    .filter(item -> item.getApprovedAt() != null
                            && !item.getApprovedAt().isBefore(start)
                            && !item.getApprovedAt().isAfter(end))
                    .toList();
        }

        log.info("팀 {}의 승인된 물품 수: {}", teamId, items.size());

        return buildResponse(items, teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getMajorItemsByTeamIdApproved(Integer teamId, Long teacherId, LocalDate targetDate) {
        validateTeamType(teamId, TeamType.MAJOR);
        return getItemsByTeamIdApproved(teamId, teacherId, targetDate);
    }

    @Transactional
    public List<TeacherItemResponseDto>  getItemsByTeamIdRejected(Integer teamId, Long teacherId) {
        log.info("팀별 거절 상태 물품 조회 시작 - teamId: {}", teamId);

        List<ItemRequest> items = itemRequestRepository.findByTeamIdAndStatus(
                teamId,
                ItemStatus.REJECTED
        );

        log.info("팀 {}의 거절된 물품 수: {}", teamId, items.size());

        return buildResponse(items, teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getMajorItemsByTeamIdRejected(Integer teamId, Long teacherId) {
        validateTeamType(teamId, TeamType.MAJOR);
        return getItemsByTeamIdRejected(teamId, teacherId);
    }

    @Transactional
    public List<TeacherItemResponseDto> getAllRejectedItems(Long teacherId) {
        return buildResponse(itemRequestRepository.findByStatus(ItemStatus.REJECTED), teacherId);
    }

    private List<ItemRequest> findItemsByStatusEntities(ItemStatus status) {
        log.info("전체 {} 상태 물품 조회 시작", status);
        List<ItemRequest> items = itemRequestRepository.findByStatus(status);
        log.info("조회된 물품 수: {}", items.size());
        return items;
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
                    LocalDateTime rejectedAt = LocalDateTime.now();
                    item.updateStatus(ItemStatus.REJECTED);
                    item.updateRejectedAt(rejectedAt);
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
    public ItemActionResponseDto approveItems(List<ApproveItemRequestDto> approveRequests, Long teacherId) {
        log.info("물품 승인 처리 시작 - 처리할 물품 수: {}", approveRequests.size());

        int processedCount = 0;
        for (ApproveItemRequestDto request : approveRequests) {
            try {
                ItemRequest item = itemRequestRepository.findById(request.getItem_id())
                        .orElse(null);

                if (item != null) {
                    LocalDateTime approvedAt = LocalDateTime.now();
                    item.updateStatus(ItemStatus.APPROVED);
                    item.updateApprovedAt(approvedAt);
                    itemRequestRepository.save(item);
                    itemApprovalHistoryRepository.save(
                            ItemApprovalHistory.builder()
                                    .itemRequest(item)
                                    .teacherId(teacherId)
                                    .approvedAt(approvedAt)
                                    .build()
                    );
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

    private TeacherItemResponseDto convertToTeacherItemResponseDto(ItemRequest itemRequest, String teamName) {
        return TeacherItemResponseDto.builder()
                .team_id(itemRequest.getTeamId())
                .team_name(teamName)
                .type(itemRequest.getTeamType() != null ? itemRequest.getTeamType() : TeamType.NETWORK)
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
                .updatedAt(itemRequest.getUpdatedAt())
                .approvedAt(itemRequest.getApprovedAt())
                .rejectedAt(itemRequest.getRejectedAt())
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

    @Transactional
    public ItemActionResponseDto updateItem(Long itemId, UpdateItemRequestDto requestDto) {
        log.info("물품 수정 요청 시작 - itemId: {}", itemId);

        ItemRequest item = itemRequestRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("물품을 찾을 수 없습니다. itemId: " + itemId));

        // ProductInfo 업데이트
        if (item.getProductInfo() != null) {
            item.getProductInfo().updateInfo(
                    requestDto.getItemName(),
                    requestDto.getCount(),
                    requestDto.getPrice(),
                    requestDto.getDescription(),
                    requestDto.getLink(),
                    requestDto.getDeliveryPrice(),
                    requestDto.getDeliveryTime() != null ? LocalDateTime.parse(requestDto.getDeliveryTime()) : null
            );
        } else {
            // If ProductInfo is null, create a new one
            ProductInfo newProductInfo = ProductInfo.builder()
                    .name(requestDto.getItemName())
                    .quantity(requestDto.getCount())
                    .price(requestDto.getPrice() != null ? String.valueOf(requestDto.getPrice()) : null)
                    .link(requestDto.getLink())
                    .description(requestDto.getDescription())
                    .deliveryPrice(requestDto.getDeliveryPrice())
                    .deliveryTime(requestDto.getDeliveryTime() != null ? LocalDateTime.parse(requestDto.getDeliveryTime()) : null)
                    .build();
            item.updateProductInfo(newProductInfo);
        }

        itemRequestRepository.save(item);
        log.info("물품 수정 완료 - itemId: {}", itemId);

        return ItemActionResponseDto.builder()
                .status(item.getStatus())
                .message("물품 정보가 성공적으로 수정되었습니다.")
                .build();
    }

    @Transactional
    public void openNthItemRequestPeriod(
            Integer nth, String type,
            List<ItemMinPriceRequest> guide,
            String deadlineDate,
            Long teacherId
    ) {
        log.info("{}차 물품 신청 기간 오픈 처리 시작", nth);

        NthStatus nthStatus = nthStatusRepository.findByNthStatusId(1L)
                .orElseGet(() -> nthStatusRepository.save(new NthStatus()));
        nthStatus.updateNthValue(nth, type, guide, deadlineDate, teacherId);
        nthStatusRepository.save(nthStatus);

        nthStatusHistoryRepository.save(
                NthStatusHistory.builder()
                        .nthValue(nth)
                        .projectType(type)
                        .guide(guide)
                        .deadlineDate(deadlineDate)
                        .teacherId(teacherId)
                        .build()
        );

        log.info("{}차 물품 신청 기간 오픈 완료", nth);
    }

    public List<NthStatusHistoryResponseDto> getNthOpenHistory() {
        return nthStatusQueryService.getOpenHistory();
    }

    public NthOpenCountResponseDto getNthOpenCount() {
        return nthStatusQueryService.getOpenCount();
    }

    public NthOpenedListResponseDto getOpenedNthValues() {
        return nthStatusQueryService.getOpenedNthValues();
    }

    @Transactional
    public ItemGuideResponse createItemGuide(ItemGuideRequest request, Long teacherId) {
        ItemGuide guide = ItemGuide.create(
                teacherId,
                request.getContent(),
                request.getProjectType()
        );

        itemGuideRepository.save(guide);

        return ItemGuideResponse.builder()
                .id(guide.getId())
                .message(guide.getProjectType() + " 물품 신청 가이드가 등록되었습니다.")
                .build();
    }

    @Transactional
    public ItemGuideResponse updateItemGuide(ItemGuideRequest request, Long guideId) {
        ItemGuide guide = itemGuideRepository.findById(guideId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가이드를 찾을 수 없음"));

        guide.update(request.getContent(), request.getProjectType());

        return ItemGuideResponse.builder()
                .id(guide.getId())
                .message(guide.getProjectType() + " 물품 신청 가이드가 수정되었습니다.")
                .build();
    }

    private List<TeacherItemResponseDto> buildResponse(List<ItemRequest> items, Long teacherId) {
        saveViewForItems(items, teacherId);
        Map<Integer, String> teamNames = resolveTeamNames(items);
        return items.stream()
                .map(item -> convertToTeacherItemResponseDto(item, teamNames.get(item.getTeamId())))
                .toList();
    }

    private void saveViewForItems(List<ItemRequest> items, Long teacherId) {
        if (items == null || items.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        items.forEach(item -> viewRepository.save(
                View.builder()
                        .viewer(teacherId)
                        .viewedItemId(item.getId())
                        .watchedAt(now)
                        .build()
        ));
    }

    private List<ItemRequest> filterByTeamType(List<ItemRequest> items, TeamType teamType) {
        if (teamType == null || items == null) {
            return items;
        }
        return items.stream()
                .filter(item -> item.getTeamType() == teamType)
                .toList();
    }

    private List<ItemRequest> filterByTeamId(List<ItemRequest> items, Integer teamId) {
        if (teamId == null || items == null) {
            return items;
        }
        return items.stream()
                .filter(item -> item.getTeamId() != null && Objects.equals(item.getTeamId(), teamId))
                .toList();
    }

    private List<ItemRequest> filterByDate(List<ItemRequest> items, LocalDate targetDate, boolean useApprovedAt) {
        if (items == null || targetDate == null) {
            return items;
        }
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(LocalTime.MAX);
        return items.stream()
                .filter(item -> {
                    LocalDateTime timestamp = useApprovedAt ? item.getApprovedAt() : item.getRejectedAt();
                    return timestamp != null && !timestamp.isBefore(start) && !timestamp.isAfter(end);
                })
                .toList();
    }

    private void validateTeamType(Integer teamId, TeamType requiredType) {
        if (teamId == null) {
            throw new IllegalArgumentException("teamId가 필요합니다.");
        }
        Team team = teamRepository.findById(teamId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다. teamId: " + teamId));
        TeamType mappedType = team.getType() != null
                ? TeamType.valueOf(team.getType().name())
                : TeamType.NETWORK;
        if (mappedType != requiredType) {
            throw new IllegalArgumentException("요청한 팀은 " + requiredType + " 팀이 아닙니다. teamId: " + teamId);
        }
    }

    private List<ItemRequest> findItemsByStatusAndDate(LocalDate targetDate, ItemStatus status, Integer teamId) {
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);
        List<ItemRequest> items;
        if (status == ItemStatus.APPROVED) {
            items = itemRequestRepository.findByApprovedAtBetween(startOfDay, endOfDay);
        } else if (status == ItemStatus.REJECTED) {
            items = itemRequestRepository.findByRejectedAtBetween(startOfDay, endOfDay);
        } else {
            items = Collections.emptyList();
        }
        return filterByTeamId(items, teamId);
    }

    private Integer resolveTeamIdFromName(String teamName) {
        if (teamName == null || teamName.isBlank()) {
            return null;
        }
        Team team = teamRepository.findFirstByNameOrderByIdAsc(teamName)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀을 찾을 수 없습니다: " + teamName));
        if (team.getId() == null) {
            throw new IllegalStateException("팀 ID가 없습니다: " + teamName);
        }
        return team.getId().intValue();
    }

    private Map<Integer, String> resolveTeamNames(List<ItemRequest> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Integer> teamIds = items.stream()
                .map(ItemRequest::getTeamId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (teamIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Integer, String> teamNameMap = new HashMap<>();
        List<Team> teams = teamRepository.findAllById(
                teamIds.stream()
                        .map(Integer::longValue)
                        .toList()
        );
        for (Team team : teams) {
            if (team.getId() != null) {
                teamNameMap.put(team.getId().intValue(), team.getName());
            }
        }
        return teamNameMap;
    }
}
