package co.kr.muldum.presentation.item;

import co.kr.muldum.domain.item.dto.ApproveItemRequestDto;
import co.kr.muldum.domain.item.dto.ItemActionResponseDto;
import co.kr.muldum.domain.item.dto.RejectItemRequestDto;
import co.kr.muldum.domain.item.dto.TeacherItemResponseDto;
import co.kr.muldum.domain.item.service.TeacherItemService;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tch/items")
@Slf4j
@PreAuthorize("hasRole('TEACHER')")
public class TeacherItemController {

    private final TeacherItemService teacherItemService;

    @GetMapping
    public ResponseEntity<List<TeacherItemResponseDto>> getAllPendingAndApprovedItems(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 물품 전체 조회 요청 - teacherId: {}", userDetails.getUserId());

        List<TeacherItemResponseDto> items = teacherItemService.getAllPendingAndApprovedItems();

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
}