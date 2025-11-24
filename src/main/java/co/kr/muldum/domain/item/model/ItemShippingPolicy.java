package co.kr.muldum.domain.item.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_shipping_policies")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ItemShippingPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "atleastshippingmoney", nullable = false)
    private int atLeastShippingMoney;

    @Column(name = "youcantapplyforigenship", nullable = false)
    private boolean youCantApplyForIgenship;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updatePolicy(int atLeastShippingMoney, boolean youCantApplyForIgenship) {
        this.atLeastShippingMoney = atLeastShippingMoney;
        this.youCantApplyForIgenship = youCantApplyForIgenship;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
