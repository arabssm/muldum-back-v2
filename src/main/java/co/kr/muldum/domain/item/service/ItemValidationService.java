package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.validator.TeamValidator;
import co.kr.muldum.domain.item.validator.ProductLinkValidator;
import co.kr.muldum.domain.user.model.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemValidationService {

    private final TeamValidator teamValidator;
    private final ProductLinkValidator productLinkValidator;

    public ValidationResult validateUserTeam(UserInfo userInfo) {
        TeamValidator.ValidationResult result = teamValidator.validateTeam(userInfo);
        return new ValidationResult(result.isValid(), result.getErrorMessage());
    }

    public ValidationResult validateProductLink(String productLink, Long userId) {
        ProductLinkValidator.ValidationResult result = productLinkValidator.validateProductLink(productLink, userId);
        return new ValidationResult(result.isValid(), result.getErrorMessage());
    }

    public static class ValidationResult {
        private final boolean isValid;
        private final String errorMessage;

        public ValidationResult(boolean isValid, String errorMessage) {
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