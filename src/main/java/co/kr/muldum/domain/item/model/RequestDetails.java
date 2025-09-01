package co.kr.muldum.domain.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDetails {
    private String reason;
    private String deliveryInfo;
    private String approvalNotes;

    public void updateReason(String reason) {
        this.reason = reason;
    }
}