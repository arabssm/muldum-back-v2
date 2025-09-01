package co.kr.muldum.domain.item.model;

import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.model.enums.TeamType;
import co.kr.muldum.global.util.SnowflakeIdGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "item_requests")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ItemRequest {

    @Id
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Integer teamId;

    @Column(name = "requester_user_id", nullable = false)
    private Integer requesterUserId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "product_info", columnDefinition = "jsonb", nullable = false)
    private ProductInfo productInfo;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ItemStatus status = ItemStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "team_type", nullable = false)
    @Builder.Default
    private TeamType teamType = TeamType.NETWORK;

    @Column(name = "reject_id")
    private Integer rejectId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_details", columnDefinition = "jsonb")
    private RequestDetails requestDetails;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "delivery_number")
    private String deliveryNumber;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            SnowflakeIdGenerator generator = new SnowflakeIdGenerator();
            this.id = generator.generateId(this.teamId);
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(ItemStatus status) {
        this.status = status;
    }

}
