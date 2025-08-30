package co.kr.muldum.domain.item.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductLinkValidator {

    public ValidationResult validateProductLink(String productLink, Long userId) {
        if (productLink == null || productLink.isBlank()) {
            log.warn("물품 신청 실패 - productLink가 비었습니다. userId={}", userId);
            return ValidationResult.fail("상품 링크가 유효하지 않습니다.");
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