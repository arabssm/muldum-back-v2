package co.kr.muldum.domain.item.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ShippingPolicyRequest(
        @NotNull
        @Min(0)
        Integer atLeastShippingMoney,

        @NotNull
        Boolean youCantApplyForIgenship
) {
}
