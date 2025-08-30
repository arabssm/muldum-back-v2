package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import co.kr.muldum.domain.user.model.UserInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestFinalizer {

    private final ItemRequestRepository itemRequestRepository;

    public FinalizeResult finalizeRequest(UserInfo userInfo) {
        log.debug("물품 최종 신청 - 사용자 정보: userId={}, teamId={}, userType={}",
                userInfo.getUserId(), userInfo.getTeamId(), userInfo.getUserType());

        List<ItemRequest> tempRequests = itemRequestRepository
                .findByRequesterUserIdAndStatus(userInfo.getUserId().intValue(), ItemStatus.INTEMP);

        if (tempRequests.isEmpty()) {
            return new FinalizeResult(ItemStatus.REJECTED, "임시 신청된 물품이 없습니다.");
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