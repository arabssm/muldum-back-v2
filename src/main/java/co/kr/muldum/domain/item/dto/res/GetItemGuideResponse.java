package co.kr.muldum.domain.item.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetItemGuideResponse {
    private Long id;
    private String content;
}
