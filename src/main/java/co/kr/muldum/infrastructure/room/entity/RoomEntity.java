package co.kr.muldum.infrastructure.room.entity;

import co.kr.muldum.domain.room.model.enums.RoomStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomEntity {

    @Id
    @Column(name = "room_id")
    private String roomId;

    private String title;
    private Long teamId;
    private String createdBy;
    private LocalDateTime createdAt;
    private Integer maxParticipants;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ParticipantEntity> participants = new ArrayList<>();

    public void addParticipant(ParticipantEntity participant) {
        this.participants.add(participant);
        participant.setRoom(this);
    }

    public void removeParticipant(ParticipantEntity participant) {
        this.participants.remove(participant);
        participant.setRoom(null);
    }
}
