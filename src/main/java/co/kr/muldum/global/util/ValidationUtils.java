package co.kr.muldum.global.util;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 문자열이 비어있지 않은지 검증
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * 문자열 길이가 범위 내에 있는지 검증
     */
    public static boolean isLengthInRange(String value, int min, int max) {
        if (value == null) {
            return false;
        }
        int length = value.length();
        return length >= min && length <= max;
    }

    /**
     * Hex 색상 코드 형식 검증 (#RRGGBB 또는 #RGB)
     */
    public static boolean isValidHexColor(String color) {
        if (color == null) {
            return false;
        }
        return HEX_COLOR_PATTERN.matcher(color).matches();
    }
}

