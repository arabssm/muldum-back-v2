package co.kr.muldum.application.room.service;

import co.kr.muldum.application.room.dto.room.response.RoomDetailResponse;
import co.kr.muldum.application.room.dto.room.response.RoomSummaryResponse;
import co.kr.muldum.application.room.mapper.RoomDtoMapper;
import co.kr.muldum.application.room.port.in.FindAllRoomsUseCase;
import co.kr.muldum.application.room.port.in.FindRoomByIdUseCase;
import co.kr.muldum.application.room.port.in.FindRoomsByTeamUseCase;
import co.kr.muldum.application.room.port.out.RoomRepository;
import co.kr.muldum.application.room.port.out.RoomUserPort;
import co.kr.muldum.domain.room.model.Room;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindRoomService implements FindAllRoomsUseCase, FindRoomsByTeamUseCase, FindRoomByIdUseCase {

    private final RoomRepository roomRepository;
    private final RoomUserPort roomUserPort;

    @Override
    public List<RoomSummaryResponse> findAllRooms() {
        return roomRepository.findAll().stream()
                .map(RoomDtoMapper::toRoomSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomSummaryResponse> findRoomsByTeam(Long userId) {
        Long teamId = roomUserPort.findTeamIdByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        return roomRepository.findByTeamId(teamId).stream()
                .map(RoomDtoMapper::toRoomSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoomDetailResponse findRoomById(String roomId) {
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        return RoomDtoMapper.toRoomDetailResponse(room);
    }
}
