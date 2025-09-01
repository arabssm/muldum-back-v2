package co.kr.muldum.domain.item.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsedBudgetResponseDto {
    private Long usedBudget;
}