package co.kr.muldum.infrastructure.room;

import co.kr.muldum.application.room.port.out.RoomRepository;
import co.kr.muldum.domain.room.model.Room;
import co.kr.muldum.infrastructure.room.entity.RoomEntity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepository {

    private final SpringDataRoomRepository springDataRoomRepository;
    private final RoomMapper roomMapper;

    @Override
    public Room save(Room room) {
        RoomEntity entity = roomMapper.mapToEntity(room);
        RoomEntity saved = springDataRoomRepository.save(entity);
        return roomMapper.mapToDomain(saved);
    }

    @Override
    public Optional<Room> findByRoomId(String roomId) {
        return springDataRoomRepository.findByRoomId(roomId)
                .map(roomMapper::mapToDomain);
    }

    @Override
    public List<Room> findAll() {
        return springDataRoomRepository.findAll().stream()
                .map(roomMapper::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Room> findByTeamId(Long teamId) {
        return springDataRoomRepository.findByTeamId(teamId).stream()
                .map(roomMapper::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String roomId) {
        springDataRoomRepository.deleteById(roomId);
    }

    @Override
    public void deleteAll() {
        springDataRoomRepository.deleteAll();
    }
}
