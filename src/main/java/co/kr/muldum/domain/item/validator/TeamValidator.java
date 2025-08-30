package co.kr.muldum.domain.item.validator;

import co.kr.muldum.domain.user.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component

@Slf4j
public class TeamValidator {

    public ValidationResult validateTeam(UserInfo userInfo) {
        if (userInfo.getTeamId() == null) {
            log.warn("물품 신청 실패 - teamId가 null입니다. userId={}", userInfo.getUserId());
            return ValidationResult.fail("팀 정보가 없습니다. 팀에 소속되어야 물품을 신청할 수 있습니다.");
        }
        
        return ValidationResult.success();
    }

    public static class ValidationResult {
        private final boolean isValid;
        private final String errorMessage;

        private ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult fail(String errorMessage) {
            return new ValidationResult(false, errorMessage);
        }

        public boolean isValid() {
            return isValid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}