package co.kr.muldum.domain.room.model;

import co.kr.muldum.domain.room.model.enums.RoomRole;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;

public record Participant(Long userId, RoomRole role) {

    public static Participant of(Long userId, RoomRole role) {
        return new Participant(userId, role);
    }

    public static Participant fromCreatedBy(String createdBy) {
        if (createdBy == null || !createdBy.contains("_")) {
            throw new CustomException(ErrorCode.INVALID_CREATED_BY_FORMAT);
        }

        String[] parts = createdBy.split("_");
        if (parts.length != 2) {
            throw new CustomException(ErrorCode.INVALID_CREATED_BY_FORMAT);
        }

        RoomRole role = RoomRole.fromValue(parts[0]);
        if (role == null) {
            throw new CustomException(ErrorCode.INVALID_CREATED_BY_FORMAT);
        }

        try {
            Long userId = Long.parseLong(parts[1]);
            return new Participant(userId, role);
        } catch (NumberFormatException e) {
            throw new CustomException(ErrorCode.INVALID_CREATED_BY_FORMAT);
        }
    }

    public String toCreatedByFormat() {
        return role.getValue() + "_" + userId;
    }
}
