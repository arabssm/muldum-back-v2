package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import co.kr.muldum.domain.user.model.UserInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestFinalizer {

    private final ItemRequestRepository itemRequestRepository;

    public FinalizeResult finalizeRequest(UserInfo userInfo, List<Long> itemIds) {
        log.debug("물품 최종 신청 - 사용자 정보: userId={}, teamId={}, userType={}",
                userInfo.getUserId(), userInfo.getTeamId(), userInfo.getUserType());

        List<ItemRequest> tempRequests;
        try {
            tempRequests = resolveTargetItems(userInfo, itemIds);
        } catch (IllegalArgumentException e) {
            return new FinalizeResult(ItemStatus.REJECTED, e.getMessage());
        }

        if (tempRequests.isEmpty()) {
            return new FinalizeResult(ItemStatus.REJECTED, "최종 신청할 임시 물품이 없습니다.");
        }

        // 모든 INTEMP 상태의 물품을 PENDING으로 변경
        for (ItemRequest itemRequest : tempRequests) {
            itemRequest.updateStatus(ItemStatus.PENDING);
            itemRequestRepository.save(itemRequest);
            
            log.info("물품 상태 변경: INTEMP -> PENDING, userId={}, itemId={}", 
                    userInfo.getUserId(), itemRequest.getId());
        }

        return new FinalizeResult(ItemStatus.PENDING, 
                String.format("총 %d개 물품이 성공적으로 신청되었습니다.", tempRequests.size()));
    }

    private List<ItemRequest> resolveTargetItems(UserInfo userInfo, List<Long> itemIds) {
        Integer teamId = userInfo.getTeamId().intValue();
        if (itemIds == null || itemIds.isEmpty()) {
            return itemRequestRepository.findByTeamIdAndStatus(teamId, ItemStatus.INTEMP);
        }

        List<ItemRequest> requests = itemRequestRepository.findByTeamIdAndStatusAndIdIn(
                teamId,
                ItemStatus.INTEMP,
                itemIds
        );

        Set<Long> foundIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toSet());

        if (foundIds.size() != itemIds.size()) {
            List<Long> missing = itemIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            log.warn("최종 신청 대상 검증 실패 - userId={}, teamId={}, missing={}", userInfo.getUserId(), teamId, missing);
            throw new IllegalArgumentException(
                    String.format("선택한 항목 중 임시 상태가 아니거나 팀에 속하지 않는 물품이 있습니다: %s", missing)
            );
        }

        return requests;
    }

    @Getter
    public static class FinalizeResult {
        private final ItemStatus status;
        private final String message;

        private FinalizeResult(ItemStatus status, String message) {
            this.status = status;
            this.message = message;
        }
        
        public static FinalizeResult of(ItemStatus status, String message) {
            return new FinalizeResult(status, message);
        }
    }
}
