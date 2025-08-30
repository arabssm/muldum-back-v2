package co.kr.muldum.presentation.item;

import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.dto.TempItemResponseDto;
import co.kr.muldum.domain.item.dto.TempItemListResponseDto;
import co.kr.muldum.domain.item.service.ItemRequestService;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/std/items")
@Slf4j
@PreAuthorize("hasRole('STUDENT')")
public class ItemController {

    private final ItemRequestService itemRequestService;

    @GetMapping("/temp")
    public ResponseEntity<List<TempItemListResponseDto>> getTempItemRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<TempItemListResponseDto> items = itemRequestService.getTempItemRequests(userDetails.getUserId());
        
        return ResponseEntity.ok(items);
    }

    @PostMapping("/temp")
    public ResponseEntity<TempItemResponseDto> createTempItemRequest(
            @RequestBody TempItemRequestDto tempItemRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TempItemResponseDto response = itemRequestService.createTempItemRequest(
                tempItemRequestDto,
                userDetails.getUserId()
        );

        if ("REJECTED".equals(response.getStatus())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
