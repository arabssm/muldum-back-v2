package co.kr.muldum.domain.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ItemFinalizeRequestDto {
    @JsonProperty("item_ids")
    private List<Long> itemIds;
}
