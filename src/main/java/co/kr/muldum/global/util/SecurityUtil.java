package co.kr.muldum.global.util;

import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.global.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    
    /**
     * 현재 인증된 사용자의 ID를 안전하게 추출
     * @return 현재 사용자 ID
     * @throws CustomException 인증 정보가 없거나 올바르지 않은 경우
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNREGISTERED_USER);
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }
        
        throw new CustomException(ErrorCode.UNREGISTERED_USER);
    }

    public static String getCurrentUserType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNREGISTERED_USER);
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserType();
        }
        
        throw new CustomException(ErrorCode.UNREGISTERED_USER);
        }

    public static CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNREGISTERED_USER);
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        }
        
        throw new CustomException(ErrorCode.UNREGISTERED_USER);
    }
}