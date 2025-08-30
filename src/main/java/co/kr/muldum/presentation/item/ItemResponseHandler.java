package co.kr.muldum.presentation.item;

import co.kr.muldum.domain.item.dto.ItemResponseDto;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ItemResponseHandler {

    public ResponseEntity<ItemResponseDto> handleItemResponse(ItemResponseDto response) {
        if (ItemStatus.REJECTED.name().equals(response.getStatus())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}