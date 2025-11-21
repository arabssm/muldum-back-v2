package co.kr.muldum.infrastructure.room;

import co.kr.muldum.infrastructure.room.entity.RoomEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataRoomRepository extends JpaRepository<RoomEntity, String> {
    Optional<RoomEntity> findByRoomId(String roomId);
    List<RoomEntity> findByTeamId(Long teamId);
}
