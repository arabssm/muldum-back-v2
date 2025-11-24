package co.kr.muldum.domain.item.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "item_approval_history")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_request_id", nullable = false)
    private ItemRequest itemRequest;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "approved_at", nullable = false)
    private LocalDateTime approvedAt;
}
