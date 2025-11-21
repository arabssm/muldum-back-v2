package co.kr.muldum.application.room.service;

import co.kr.muldum.application.room.port.in.DeleteAllRoomsUseCase;
import co.kr.muldum.application.room.port.out.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteAllRoomsService implements DeleteAllRoomsUseCase {

    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public void deleteAllRooms() {
        roomRepository.deleteAll();
    }
}
