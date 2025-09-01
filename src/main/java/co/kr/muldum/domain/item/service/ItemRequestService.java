package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.dto.ItemResponseDto;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestFacade itemRequestFacade;

    public ItemResponseDto createTempItemRequest(TempItemRequestDto requestDto, Long userId) {
        return itemRequestFacade.createTempItemRequest(requestDto, userId);
    }

    public ItemResponseDto finalizeItemRequest(Long userId) {
        return itemRequestFacade.finalizeItemRequest(userId);
    }
}
