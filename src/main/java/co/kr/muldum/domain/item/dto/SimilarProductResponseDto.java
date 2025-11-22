package co.kr.muldum.domain.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarProductResponseDto {
    private Long itemId;
    private String productName;
    private Long price;
    private String productLink;
    private String source;
    private Integer teamId;
}
