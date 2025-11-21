package co.kr.muldum.presentation.room;

import co.kr.muldum.application.room.dto.room.response.RoomSummaryResponse;
import co.kr.muldum.application.room.port.in.DeleteAllRoomsUseCase;
import co.kr.muldum.application.room.port.in.FindAllRoomsUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ara/sup/rooms")
@RequiredArgsConstructor
public class SupRoomController {

    private final FindAllRoomsUseCase findAllRoomsUseCase;
    private final DeleteAllRoomsUseCase deleteAllRoomsUseCase;

    @GetMapping("/all")
    public ResponseEntity<List<RoomSummaryResponse>> getAllRooms() {
        List<RoomSummaryResponse> response = findAllRoomsUseCase.findAllRooms();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllRooms() {
        deleteAllRoomsUseCase.deleteAllRooms();
        return ResponseEntity.noContent().build();
    }
}
