package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.ItemResponseDto;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import org.springframework.stereotype.Component;

@Component
public class ItemResponseFactory {

    public ItemResponseDto createResponse(ItemStatus status, String message) {
        return ItemResponseDto.builder()
                .status(status.name())
                .message(message)
                .build();
    }

    public ItemResponseDto createRejectedResponse(String message) {
        return ItemResponseDto.builder()
                .status("REJECTED")
                .message(message)
                .build();
    }

    public ItemResponseDto createResponse(String status, String message) {
        return ItemResponseDto.builder()
                .status(status)
                .message(message)
                .build();
    }
}