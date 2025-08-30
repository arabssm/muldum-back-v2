package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import org.springframework.stereotype.Service;

@Service
public class ItemStatusDecisionService {

    public StatusDecision decideStatus(ItemSource itemSource) {
        if (itemSource == ItemSource.DEVICEMART || itemSource == ItemSource.ELEVENMARKET) {
            return new StatusDecision(ItemStatus.INTEMP, "임시 신청이 완료되었습니다.");
        } else {
            return new StatusDecision(
                    ItemStatus.REJECTED, 
                    "허용되지 않은 쇼핑몰입니다. 디바이스마트(devicemart.co.kr) 또는 11번가(11st.co.kr)에서만 신청 가능합니다."
            );
        }
    }

    public record StatusDecision(ItemStatus status, String message) {
    }
}