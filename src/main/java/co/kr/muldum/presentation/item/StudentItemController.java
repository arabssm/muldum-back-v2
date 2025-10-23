package co.kr.muldum.presentation.item;

import co.kr.muldum.domain.item.dto.*;
import co.kr.muldum.domain.item.service.*;
import co.kr.muldum.global.security.CustomUserDetails;
import co.kr.muldum.domain.item.dto.TempItemListResponseDto;
import co.kr.muldum.domain.item.service.ItemRequestService;
import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.model.UserInfo;
import co.kr.muldum.presentation.dto.item.ItemStatusResponseDto;
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
public class StudentItemController {

    private final ItemRequestService itemRequestService;
    private final ItemListService itemListService;
    private final ItemRequestFinalizer itemRequestFinalizer;
    private final UserReader userReader;
    private final ProductInfoService productInfoService;

    @GetMapping
    public ResponseEntity<List<ItemListResponseDto>> getTeamItems(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserInfo userInfo = userReader.read(User.class, userDetails.getUserId());
        List<ItemListResponseDto> items = itemListService.getTeamItemRequests(userInfo);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/preview")
    public ResponseEntity<ProductInfoResponseDto> previewProduct(
            @RequestBody ProductInfoRequestDto dto
    ) {
        String productLink = dto.getProductLink();
        ProductInfoResponseDto productInfo = productInfoService.getProductInfo(productLink);
        return ResponseEntity.ok(productInfo);
    }

    @GetMapping("/temp")
    public ResponseEntity<List<TempItemListResponseDto>> getTempItemRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<TempItemListResponseDto> items = itemRequestService.getTempItemRequests(userDetails.getUserId());
        return ResponseEntity.ok(items);
    }

    @PostMapping("/temp")
    public ResponseEntity<ItemResponseDto> createTempItemRequest(
            @RequestBody TempItemRequestDto tempItemRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ItemResponseDto response = itemRequestService.createTempItemRequest(
                tempItemRequestDto,
                userDetails.getUserId()
        );
        return handleItemResponse(response);
    }

    @PatchMapping
    public ResponseEntity<ItemResponseDto> finalizeItemRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserInfo userInfo = userReader.read(User.class, userDetails.getUserId());
        ItemRequestFinalizer.FinalizeResult result = itemRequestFinalizer.finalizeRequest(userInfo);

        ItemResponseDto response = ItemResponseDto.builder()
                .status(result.getStatus().name())
                .message(result.getMessage())
                .build();

        return handleItemResponse(response);
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<ItemStatusResponseDto>> getApprovedItemStatuses(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ItemStatusResponseDto> items = itemRequestService.getApprovedItemStatuses(userDetails.getUserId());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/money")
    public ResponseEntity<UsedBudgetResponseDto> getUsedBudget(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UsedBudgetResponseDto response = itemRequestService.getUsedBudget(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<ItemResponseDto> handleItemResponse(ItemResponseDto response) {
        if ("REJECTED".equals(response.getStatus())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
        return ResponseEntity.ok(response);
    }
}
