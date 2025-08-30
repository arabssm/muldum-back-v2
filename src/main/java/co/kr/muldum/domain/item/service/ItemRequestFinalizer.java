package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.user.model.UserInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ItemRequestFinalizer {

    public FinalizeResult finalizeRequest(UserInfo userInfo) {
        log.debug("물품 최종 신청 - 사용자 정보: userId={}, teamId={}, userType={}",
                userInfo.getUserId(), userInfo.getTeamId(), userInfo.getUserType());

        return new FinalizeResult("PENDING", "물품이 성공적으로 신청되었습니다.");
    }

    @Getter
    public static class FinalizeResult {
        private final String status;
        private final String message;

        private FinalizeResult(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}