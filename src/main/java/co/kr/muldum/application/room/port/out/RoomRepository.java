package co.kr.muldum.application.room.port.out;

import co.kr.muldum.domain.room.model.Room;
import java.util.List;
import java.util.Optional;

public interface RoomRepository {
    Room save(Room room);
    Optional<Room> findByRoomId(String roomId);
    List<Room> findAll();
    List<Room> findByTeamId(Long teamId);
    void delete(String roomId);
    void deleteAll();
}
