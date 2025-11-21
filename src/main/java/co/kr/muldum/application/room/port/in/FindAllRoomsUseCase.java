package co.kr.muldum.application.room.port.in;

import co.kr.muldum.application.room.dto.room.response.RoomSummaryResponse;
import java.util.List;

public interface FindAllRoomsUseCase {
    List<RoomSummaryResponse> findAllRooms();
}
