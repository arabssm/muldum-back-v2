package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.TeacherItemResponseDto;
import co.kr.muldum.domain.item.model.ItemRequest;
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
}