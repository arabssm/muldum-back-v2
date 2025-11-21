package co.kr.muldum.application.room.port.in;

import co.kr.muldum.application.room.dto.leave.LeaveRoomResponse;

public interface LeaveRoomUseCase {
    LeaveRoomResponse leaveRoom(String roomId, Long userId);
}
