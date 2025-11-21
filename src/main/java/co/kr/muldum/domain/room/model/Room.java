package co.kr.muldum.domain.room.model;

import co.kr.muldum.domain.room.model.enums.RoomRole;
import co.kr.muldum.domain.room.model.enums.RoomStatus;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Room {

    @EqualsAndHashCode.Include
    private final String roomId;
    private final String title;
    private final Long teamId;
    private final String createdBy;
    private final LocalDateTime createdAt;
    private final Integer maxParticipants;
    private final RoomStatus status;
    private final List<Participant> participants;

    public Room(String roomId, String title, Long teamId, String createdBy,
                LocalDateTime createdAt, Integer maxParticipants,
                RoomStatus status, List<Participant> participants) {
        this.roomId = roomId;
        this.title = title;
        this.teamId = teamId;
        this.createdBy = createdBy;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.maxParticipants = maxParticipants;
        this.status = status != null ? status : RoomStatus.ACTIVE;
        this.participants = participants != null ? new ArrayList<>(participants) : new ArrayList<>();
    }

    public Room(String title, Long teamId, String createdBy, Integer maxParticipants) {
        this(null, title, teamId, createdBy, LocalDateTime.now(),
                maxParticipants, RoomStatus.ACTIVE, new ArrayList<>());
    }

    public Room withRoomId(String newRoomId) {
        return new Room(newRoomId, this.title, this.teamId, this.createdBy,
                this.createdAt, this.maxParticipants, this.status, this.participants);
    }

    public Room addParticipant(Participant participant) {
        if (this.participants.size() >= this.maxParticipants) {
            throw new CustomException(ErrorCode.ROOM_CAPACITY_EXCEEDED);
        }

        if (this.status == RoomStatus.DEACTIVE) {
            throw new CustomException(ErrorCode.ROOM_NOT_ACTIVE);
        }

        if (isParticipantExists(participant.userId())) {
            throw new CustomException(ErrorCode.PARTICIPANT_ALREADY_EXISTS);
        }

        List<Participant> updatedParticipants = new ArrayList<>(this.participants);
        updatedParticipants.add(participant);

        return new Room(this.roomId, this.title, this.teamId, this.createdBy,
                this.createdAt, this.maxParticipants, this.status, updatedParticipants);
    }

    public Room removeParticipant(Long userId) {
        List<Participant> updatedParticipants = new ArrayList<>(this.participants);
        updatedParticipants.removeIf(p -> p.userId().equals(userId));

        return new Room(this.roomId, this.title, this.teamId, this.createdBy,
                this.createdAt, this.maxParticipants, this.status, updatedParticipants);
    }

    public Room deactivate() {
        return new Room(this.roomId, this.title, this.teamId, this.createdBy,
                this.createdAt, this.maxParticipants, RoomStatus.DEACTIVE, this.participants);
    }

    public Room activate() {
        return new Room(this.roomId, this.title, this.teamId, this.createdBy,
                this.createdAt, this.maxParticipants, RoomStatus.ACTIVE, this.participants);
    }

    public Room updateTitle(String newTitle) {
        return new Room(this.roomId, newTitle, this.teamId, this.createdBy,
                this.createdAt, this.maxParticipants, this.status, this.participants);
    }

    public Room updateMaxParticipants(Integer newMaxParticipants) {
        if (newMaxParticipants < this.participants.size()) {
            throw new CustomException(ErrorCode.MAX_PARTICIPANTS_INVALID);
        }

        return new Room(this.roomId, this.title, this.teamId, this.createdBy,
                this.createdAt, newMaxParticipants, this.status, this.participants);
    }

    public Integer getParticipantCount() {
        return this.participants.size();
    }

    public boolean isFull() {
        return this.participants.size() >= this.maxParticipants;
    }

    public boolean isActive() {
        return this.status == RoomStatus.ACTIVE;
    }

    public boolean isParticipantExists(Long userId) {
        return this.participants.stream()
                .anyMatch(p -> p.userId().equals(userId));
    }

    public RoomRole getCreatorRole() {
        if (createdBy == null || !createdBy.contains("_")) {
            return null;
        }
        String roleValue = createdBy.split("_")[0];
        return RoomRole.fromValue(roleValue);
    }

    public Long getCreatorUserId() {
        if (createdBy == null || !createdBy.contains("_")) {
            return null;
        }
        try {
            return Long.parseLong(createdBy.split("_")[1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<Participant> getParticipants() {
        return new ArrayList<>(this.participants);
    }
}
