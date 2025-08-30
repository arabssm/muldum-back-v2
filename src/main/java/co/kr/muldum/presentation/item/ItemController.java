package co.kr.muldum.presentation.item;

import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.dto.ItemResponseDto;
import co.kr.muldum.domain.item.service.ItemRequestService;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/std/items")
@Slf4j
@PreAuthorize("hasRole('STUDENT')")
public class ItemController {

    private final ItemRequestService itemRequestService;
    private final ItemResponseHandler itemResponseHandler;

    @PostMapping("/temp")
    public ResponseEntity<ItemResponseDto> createTempItemRequest(
            @RequestBody TempItemRequestDto tempItemRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ItemResponseDto response = itemRequestService.createTempItemRequest(
                tempItemRequestDto,
                userDetails.getUserId()
        );

        return itemResponseHandler.handleItemResponse(response);
    }

    @PatchMapping
    public ResponseEntity<ItemResponseDto> finalizeItemRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ItemResponseDto response = itemRequestService.finalizeItemRequest(
                userDetails.getUserId()
        );

        return itemResponseHandler.handleItemResponse(response);
    }
}
