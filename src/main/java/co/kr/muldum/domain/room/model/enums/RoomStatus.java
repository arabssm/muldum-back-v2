package co.kr.muldum.domain.room.model.enums;

import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum RoomStatus {
    ACTIVE("active", "활성"),
    DEACTIVE("deactive", "비활성");

    private final String value;
    private final String description;

    RoomStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static RoomStatus fromValue(String value) {
        if (value == null) {
            throw new CustomException(ErrorCode.INVALID_ROOM_STATUS);
        }
        for (RoomStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new CustomException(ErrorCode.INVALID_ROOM_STATUS);
    }
}
