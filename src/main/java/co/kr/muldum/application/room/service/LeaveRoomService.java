package co.kr.muldum.application.room.service;

import co.kr.muldum.application.room.dto.leave.LeaveRoomResponse;
import co.kr.muldum.application.room.dto.leave.LeaveRoomStatus;
import co.kr.muldum.application.room.port.in.LeaveRoomUseCase;
import co.kr.muldum.application.room.port.out.RoomRepository;
import co.kr.muldum.domain.room.model.Room;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveRoomService implements LeaveRoomUseCase {

    private final RoomRepository roomRepository;

    @Override
    public LeaveRoomResponse leaveRoom(String roomId, Long userId) {
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        Room updatedRoom = room.removeParticipant(userId);
        roomRepository.save(updatedRoom);

        return LeaveRoomResponse.builder()
                .message("방에서 성공적으로 퇴장되었습니다")
                .status(LeaveRoomStatus.builder()
                        .userId(userId)
                        .roomId(roomId)
                        .executedAt(LocalDateTime.now())
                        .build())
                .build();
    }
}
