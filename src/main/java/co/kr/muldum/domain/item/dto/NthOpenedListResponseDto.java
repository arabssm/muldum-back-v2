package co.kr.muldum.domain.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NthOpenedListResponseDto {
    private List<Integer> openedNths;
}
