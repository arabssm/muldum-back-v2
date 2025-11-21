package co.kr.muldum.application.room.port.in;

import co.kr.muldum.application.room.dto.room.response.RoomDetailResponse;

public interface FindRoomByIdUseCase {
    RoomDetailResponse findRoomById(String roomId);
}
