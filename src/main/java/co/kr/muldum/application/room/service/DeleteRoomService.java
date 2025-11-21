package co.kr.muldum.application.room.service;

import co.kr.muldum.application.room.port.in.DeleteRoomUseCase;
import co.kr.muldum.application.room.port.out.RoomRepository;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteRoomService implements DeleteRoomUseCase {

    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public void deleteRoom(String roomId) {
        roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        roomRepository.delete(roomId);
    }
}
