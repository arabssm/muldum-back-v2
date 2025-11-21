package co.kr.muldum.application.room.service;

import co.kr.muldum.application.room.port.in.CreateRoomCommand;
import co.kr.muldum.application.room.port.in.CreateRoomUseCase;
import co.kr.muldum.application.room.port.out.RoomRepository;
import co.kr.muldum.application.room.port.out.RoomUserPort;
import co.kr.muldum.domain.room.model.Participant;
import co.kr.muldum.domain.room.model.Room;
import co.kr.muldum.domain.room.model.enums.RoomRole;
import co.kr.muldum.domain.room.model.enums.RoomStatus;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateRoomService implements CreateRoomUseCase {

    private final RoomRepository roomRepository;
    private final RoomUserPort roomUserPort;

    @Override
    public Room createRoom(CreateRoomCommand command) {
        RoomRole creatorRole = roomUserPort.findRoleByUserId(command.creatorUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREATOR_ROLE));

        String roomId = UUID.randomUUID().toString();
        String createdBy = creatorRole.getValue() + "_" + command.creatorUserId();

        Room roomWithoutParticipant = new Room(
                roomId,
                command.title(),
                command.teamId(),
                createdBy,
                LocalDateTime.now(),
                command.maxParticipants(),
                RoomStatus.ACTIVE,
                new ArrayList<>()
        );

        Room finalRoom = roomWithoutParticipant.addParticipant(
                Participant.of(command.creatorUserId(), creatorRole)
        );

        return roomRepository.save(finalRoom);
    }
}
