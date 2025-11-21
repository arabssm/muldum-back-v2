package co.kr.muldum.presentation.room;

import co.kr.muldum.application.room.dto.leave.LeaveRoomResponse;
import co.kr.muldum.application.room.dto.room.request.CreateRoomRequest;
import co.kr.muldum.application.room.dto.room.response.CreateRoomResponse;
import co.kr.muldum.application.room.dto.room.response.RoomDetailResponse;
import co.kr.muldum.application.room.dto.room.response.RoomSummaryResponse;
import co.kr.muldum.application.room.mapper.RoomDtoMapper;
import co.kr.muldum.application.room.port.in.CreateRoomCommand;
import co.kr.muldum.application.room.port.in.CreateRoomUseCase;
import co.kr.muldum.application.room.port.in.DeleteRoomUseCase;
import co.kr.muldum.application.room.port.in.FindRoomByIdUseCase;
import co.kr.muldum.application.room.port.in.FindRoomsByTeamUseCase;
import co.kr.muldum.application.room.port.in.LeaveRoomUseCase;
import co.kr.muldum.domain.room.model.Room;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.util.SecurityUtil;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/ara/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final CreateRoomUseCase createRoomUseCase;
    private final FindRoomsByTeamUseCase findRoomsByTeamUseCase;
    private final FindRoomByIdUseCase findRoomByIdUseCase;
    private final LeaveRoomUseCase leaveRoomUseCase;
    private final DeleteRoomUseCase deleteRoomUseCase;

    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom(
            @Valid @RequestBody CreateRoomRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId
    ) {
        Long userId = resolveUserId(headerUserId);
        CreateRoomCommand command = new CreateRoomCommand(
                request.getTitle(),
                request.getTeamId(),
                request.getMaxParticipants(),
                userId
        );

        Room createdRoom = createRoomUseCase.createRoom(command);
        CreateRoomResponse response = RoomDtoMapper.toCreateRoomResponse(createdRoom);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getRoomId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RoomSummaryResponse>> getRoomsByTeam(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId
    ) {
        Long userId = resolveUserId(headerUserId);
        List<RoomSummaryResponse> response = findRoomsByTeamUseCase.findRoomsByTeam(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId:^(?!health$).+}")
    public ResponseEntity<RoomDetailResponse> getRoomDetails(
            @PathVariable String roomId
    ) {
        RoomDetailResponse response = findRoomByIdUseCase.findRoomById(roomId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomId:^(?!health$).+}/leave")
    public ResponseEntity<LeaveRoomResponse> leaveRoom(
            @PathVariable String roomId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId
    ) {
        Long userId = resolveUserId(headerUserId);
        LeaveRoomResponse response = leaveRoomUseCase.leaveRoom(roomId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{roomId:^(?!health$).+}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable String roomId
    ) {
        deleteRoomUseCase.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(Long headerUserId) {
        try {
            return SecurityUtil.getCurrentUserId();
        } catch (CustomException ex) {
            if (headerUserId != null) {
                return headerUserId;
            }
            throw ex;
        }
    }
}
