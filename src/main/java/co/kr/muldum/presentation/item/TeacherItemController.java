package co.kr.muldum.presentation.item;

import co.kr.muldum.domain.item.dto.*;
import co.kr.muldum.domain.item.service.TeacherItemService;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tch/items")
@Slf4j
@PreAuthorize("hasRole('TEACHER')")
public class TeacherItemController {

    private final TeacherItemService teacherItemService;

    @GetMapping("/xlsx")
    public ResponseEntity<InputStreamResource> getApprovedItemsAsXlsx(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws IOException {
        log.info("선생님 물품 중 승인된 물품 엑셀 다운로드 요청 - teacherId: {}", userDetails.getUserId());

        InputStreamResource resource = new InputStreamResource(teacherItemService.getApprovedItemsAsXlsx());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=approved_items.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @GetMapping
    public ResponseEntity<List<TeacherItemResponseDto>> getAllPendingItems(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 물품 전체 조회 요청 - teacherId: {}", userDetails.getUserId());

        List<TeacherItemResponseDto> items = teacherItemService.getAllPendingItems();

        return ResponseEntity.ok(items);
    }

    @GetMapping("/approved")
    public ResponseEntity<List<TeacherItemResponseDto>> getAllApprovedItems(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 물품 전체 조회 요청 - teacherId: {}", userDetails.getUserId());

        List<TeacherItemResponseDto> items = teacherItemService.getAllApprovedItems();

        return ResponseEntity.ok(items);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<List<TeacherItemResponseDto>> getItemsByTeamId(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 팀별 물품 조회 요청 - teacherId: {}, teamId: {}", userDetails.getUserId(), teamId);

        List<TeacherItemResponseDto> items = teacherItemService.getItemsByTeamId(teamId);

        return ResponseEntity.ok(items);
    }

    

    @GetMapping("/not-approved")
    public ResponseEntity<List<TeacherItemResponseDto>> getAllNotApprovedItems(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 물품 중 승인 필요 물품 조회 요청 - teacherId: {}", userDetails.getUserId());

        List<TeacherItemResponseDto> items = teacherItemService.getAllNotApprovedItems();

        return ResponseEntity.ok(items);
    }

    @GetMapping("/{teamId}/not-approved")
    public ResponseEntity<List<TeacherItemResponseDto>> getItemsByTeamIdNotApproved(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 팀별 물품 중 승인 필요 물품 조회 요청");

        List<TeacherItemResponseDto> items = teacherItemService.getItemsByTeamIdNotApproved(teamId);

        return ResponseEntity.ok(items);
    }

    @GetMapping("/{teamId}/approved")
    public ResponseEntity<List<TeacherItemResponseDto>> getItemsByTeamIdApproved(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 팀별 물품 중 승인된 물품 조회 요청");

        List<TeacherItemResponseDto> items = teacherItemService.getItemsByTeamIdApproved(teamId);

        return ResponseEntity.ok(items);
    }

    @PatchMapping("/reject")
    public ResponseEntity<ItemActionResponseDto> rejectItems(
            @RequestBody List<RejectItemRequestDto> rejectRequests,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("물품 거절 요청 - teacherId: {}, 거절할 물품 수: {}",
                userDetails.getUserId(), rejectRequests.size());

        ItemActionResponseDto response = teacherItemService.rejectItems(rejectRequests);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/submit")
    public ResponseEntity<ItemActionResponseDto> approveItems(
            @RequestBody List<ApproveItemRequestDto> approveRequests,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("물품 승인 요청 - teacherId: {}, 승인할 물품 수: {}",
                userDetails.getUserId(), approveRequests.size());

        ItemActionResponseDto response = teacherItemService.approveItems(approveRequests);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/delivery_num")
    public ResponseEntity<DeliveryNumberResponseDto> registerDeliveryNumber(
            @RequestBody DeliveryNumberRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("운송장 번호 등록 요청 - teacherId: {}, itemId: {}",
                userDetails.getUserId(), request.getItem_id());

        DeliveryNumberResponseDto response = teacherItemService.registerDeliveryNumber(request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{item_id}")
    public ResponseEntity<ItemActionResponseDto> updateItem(
            @PathVariable("item_id") Long itemId,
            @RequestBody UpdateItemRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("물품 최종 신청 수정 요청 - teacherId: {}, itemId: {}",
                userDetails.getUserId(), itemId);

        ItemActionResponseDto response = teacherItemService.updateItem(itemId, requestDto);

        return ResponseEntity.ok(response);
    }
}
