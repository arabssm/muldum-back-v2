package co.kr.muldum.domain.item.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemGuideResponse {
    private Long id;
    private String message;
}
