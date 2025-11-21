package co.kr.muldum.application.room.mapper;

import co.kr.muldum.application.room.dto.participant.ParticipantInfo;
import co.kr.muldum.application.room.dto.participant.ParticipantResponse;
import co.kr.muldum.application.room.dto.room.response.CreateRoomResponse;
import co.kr.muldum.application.room.dto.room.response.RoomDetailResponse;
import co.kr.muldum.application.room.dto.room.response.RoomSummaryResponse;
import co.kr.muldum.domain.room.model.Room;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.text.StringEscapeUtils;

public final class RoomDtoMapper {

    private RoomDtoMapper() {
    }

    public static CreateRoomResponse toCreateRoomResponse(Room room) {
        return CreateRoomResponse.builder()
                .roomId(room.getRoomId())
                .title(StringEscapeUtils.escapeHtml4(room.getTitle()))
                .teamId(room.getTeamId())
                .createdBy(StringEscapeUtils.escapeHtml4(room.getCreatedBy()))
                .createdAt(room.getCreatedAt())
                .build();
    }

    public static RoomSummaryResponse toRoomSummaryResponse(Room room) {
        return RoomSummaryResponse.builder()
                .roomId(room.getRoomId())
                .title(StringEscapeUtils.escapeHtml4(room.getTitle()))
                .teamId(room.getTeamId())
                .createdBy(StringEscapeUtils.escapeHtml4(room.getCreatedBy()))
                .createdAt(room.getCreatedAt())
                .participants(room.getParticipantCount())
                .maxParticipants(room.getMaxParticipants())
                .status(StringEscapeUtils.escapeHtml4(room.getStatus().getValue()))
                .build();
    }

    public static RoomDetailResponse toRoomDetailResponse(Room room) {
        List<ParticipantResponse> participantResponses = room.getParticipants().stream()
                .map(participant -> ParticipantResponse.builder()
                        .userId(participant.userId())
                        .role(participant.role())
                        .build())
                .collect(Collectors.toList());

        return RoomDetailResponse.builder()
                .roomId(room.getRoomId())
                .title(StringEscapeUtils.escapeHtml4(room.getTitle()))
                .teamId(room.getTeamId())
                .createdBy(StringEscapeUtils.escapeHtml4(room.getCreatedBy()))
                .createdAt(room.getCreatedAt())
                .participants(ParticipantInfo.builder()
                        .amount(room.getParticipantCount())
                        .list(participantResponses)
                        .build())
                .maxParticipants(room.getMaxParticipants())
                .status(StringEscapeUtils.escapeHtml4(room.getStatus().getValue()))
                .build();
    }
}
