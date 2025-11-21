package co.kr.muldum.application.room.port.in;

public record CreateRoomCommand(String title, Long teamId, Integer maxParticipants,
                                Long creatorUserId) {
}
