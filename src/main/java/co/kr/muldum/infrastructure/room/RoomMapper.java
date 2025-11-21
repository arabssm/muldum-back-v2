package co.kr.muldum.infrastructure.room;

import co.kr.muldum.domain.room.model.Participant;
import co.kr.muldum.domain.room.model.Room;
import co.kr.muldum.infrastructure.room.entity.ParticipantEntity;
import co.kr.muldum.infrastructure.room.entity.RoomEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomEntity mapToEntity(Room room) {
        RoomEntity roomEntity = RoomEntity.builder()
                .roomId(room.getRoomId())
                .title(room.getTitle())
                .teamId(room.getTeamId())
                .createdBy(room.getCreatedBy())
                .createdAt(room.getCreatedAt())
                .maxParticipants(room.getMaxParticipants())
                .status(room.getStatus())
                .build();

        List<ParticipantEntity> participantEntities = room.getParticipants().stream()
                .map(participant -> ParticipantEntity.builder()
                        .userId(participant.userId())
                        .role(participant.role())
                        .room(roomEntity)
                        .build())
                .collect(Collectors.toList());

        roomEntity.setParticipants(participantEntities);
        return roomEntity;
    }

    public Room mapToDomain(RoomEntity entity) {
        List<Participant> participants = entity.getParticipants().stream()
                .map(participantEntity -> Participant.of(
                        participantEntity.getUserId(),
                        participantEntity.getRole()
                ))
                .collect(Collectors.toList());

        return new Room(
                entity.getRoomId(),
                entity.getTitle(),
                entity.getTeamId(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getMaxParticipants(),
                entity.getStatus(),
                participants
        );
    }
}
