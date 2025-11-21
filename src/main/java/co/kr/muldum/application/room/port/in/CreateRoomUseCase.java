package co.kr.muldum.application.room.port.in;

import co.kr.muldum.domain.room.model.Room;

public interface CreateRoomUseCase {
    Room createRoom(CreateRoomCommand command);
}
