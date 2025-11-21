package co.kr.muldum.domain.room.model.enums;

import co.kr.muldum.domain.user.model.UserType;
import java.util.Arrays;

public enum RoomRole {
    TEACHER("teacher"),
    STUDENT("student"),
    MENTOR("mentor");

    private final String value;

    RoomRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RoomRole fromValue(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(RoomRole.values())
                .filter(role -> role.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

    public static RoomRole fromUserType(UserType userType) {
        if (userType == null) {
            return null;
        }
        return switch (userType) {
            case TEACHER, SUPER -> TEACHER;
            case STUDENT -> STUDENT;
            case MENTOR -> MENTOR;
        };
    }
}
