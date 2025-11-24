package co.kr.muldum.domain.item.dto.res;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShippingPolicyResponse {
    private Long id;
    private int atLeastShippingMoney;
    private boolean youCantApplyForIgenship;
    private LocalDateTime updatedAt;
}
