package co.kr.muldum.presentation.item;

import co.kr.muldum.domain.item.dto.*;
import co.kr.muldum.domain.item.dto.req.AddRejectTemplatesRequest;
import co.kr.muldum.domain.item.dto.req.ItemGuideRequest;
import co.kr.muldum.domain.item.dto.req.ItemOpenRequest;
import co.kr.muldum.domain.item.dto.res.ItemGuideResponse;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.service.RejectTemplateService;
import co.kr.muldum.domain.item.service.TeacherItemService;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
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
    private final RejectTemplateService rejectTemplateService;

    @PostMapping("/open")
    public ResponseEntity<ItemActionResponseDto> openNthItemRequestPeriod(
            @RequestParam Integer nth,
            @RequestBody ItemOpenRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        log.info("선생님 {}차 물품신청 기간 오픈 요청", nth);

        teacherItemService.openNthItemRequestPeriod(nth, req.projectType(), req.guide(), req.deadlineDate(), userDetails.getUserId());

        ItemActionResponseDto response = ItemActionResponseDto.builder()
                .status(ItemStatus.APPROVED)
                .message(nth + "차 물품 신청 기간이 열렸습니다.")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/open-status")
    public ResponseEntity<NthStatusResponseDto> getNthItemRequestPeriodStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 물품신청 기간 상태 조회 요청");

        NthStatusResponseDto nth = teacherItemService.getNthStatus();

        return ResponseEntity.ok(nth);
    }

    @GetMapping("/open-history")
    public ResponseEntity<List<NthStatusHistoryResponseDto>> getNthOpenHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 물품신청 오픈 이력 조회 요청 - teacherId: {}", userDetails.getUserId());
        List<NthStatusHistoryResponseDto> history = teacherItemService.getNthOpenHistory();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/open-count")
    public ResponseEntity<NthOpenCountResponseDto> getNthOpenCount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 물품신청 오픈 횟수 조회 요청 - teacherId: {}", userDetails.getUserId());
        NthOpenCountResponseDto response = teacherItemService.getNthOpenCount();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/open-nths")
    public ResponseEntity<NthOpenedListResponseDto> getOpenedNthValues(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 물품신청 오픈 차수 리스트 조회 요청 - teacherId: {}", userDetails.getUserId());
        return ResponseEntity.ok(teacherItemService.getOpenedNthValues());
    }

    //엑셀
    @GetMapping("/xlsx")
    public ResponseEntity<InputStreamResource> getApprovedItemsAsXlsx(
            @RequestParam(required = false) Integer nth,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws IOException {

        Integer targetNth = nth;
        if (targetNth == null) {
            NthStatusResponseDto currentStatus = teacherItemService.getNthStatus();
            targetNth = currentStatus.getNth();
            if (targetNth == null || targetNth == 0) {
                throw new IllegalArgumentException("현재 열린 물품 신청 차수가 없습니다. nth 파라미터를 지정해주세요.");
            }
        }

        log.info("{}차 승인된 물품 엑셀 다운로드 요청 - teacherId: {}", targetNth, userDetails.getUserId());
        InputStreamResource resource = new InputStreamResource(teacherItemService.getApprovedItemsAsXlsxWithNth(targetNth));
        String filename = "approved_items_" + targetNth + "차" + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @GetMapping
    public ResponseEntity<List<TeacherItemResponseDto>> getAllPendingItems(
            @RequestParam(required = false) Integer nth,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 물품 전체 조회 요청 - teacherId: {}, nth: {}", userDetails.getUserId(), nth);
        List<TeacherItemResponseDto> response = teacherItemService.getAllPendingItems(nth, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/approved")
    public ResponseEntity<List<TeacherItemResponseDto>> getAllApprovedItems(
            @RequestParam(required = false) Integer nth,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 승인 물품 조회 요청 - teacherId: {}, nth: {}", userDetails.getUserId(), nth);
        List<TeacherItemResponseDto> response = teacherItemService.getAllApprovedItems(nth, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<List<TeacherItemResponseDto>> getItemsByTeamId(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 팀별 물품 조회 요청 - teacherId: {}, teamId: {}", userDetails.getUserId(), teamId);

        List<TeacherItemResponseDto> response = teacherItemService.getItemsByTeamId(teamId, userDetails.getUserId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/not-approved")
    public ResponseEntity<List<TeacherItemResponseDto>> getAllNotApprovedItems(
            @RequestParam(required = false) Integer nth,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 물품 중 승인 필요 물품 조회 요청 - teacherId: {}, nth: {}", userDetails.getUserId(), nth);
        List<TeacherItemResponseDto> response = teacherItemService.getAllNotApprovedItems(nth, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{teamId}/not-approved")
    public ResponseEntity<List<TeacherItemResponseDto>> getItemsByTeamIdNotApproved(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 팀별 물품 중 승인 필요 물품 조회 요청");

        List<TeacherItemResponseDto> response = teacherItemService.getItemsByTeamIdNotApproved(teamId, userDetails.getUserId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{teamId}/approved")
    public ResponseEntity<List<TeacherItemResponseDto>> getItemsByTeamIdApproved(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 팀별 물품 중 승인된 물품 조회 요청");

        List<TeacherItemResponseDto> response = teacherItemService.getItemsByTeamIdApproved(teamId, userDetails.getUserId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{teamId}/rejected")
    public ResponseEntity<List<TeacherItemResponseDto>> getItemsByTeamIdRejected(
            @PathVariable Integer teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 팀별 물품 중 거절된 물품 조회 요청");

        List<TeacherItemResponseDto> response = teacherItemService.getItemsByTeamIdRejected(teamId, userDetails.getUserId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<TeacherItemResponseDto>> getAllRejectedItems(
            @RequestParam(required = false) Integer nth,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 물품 중 거절된 물품 조회 요청 - teacherId: {}, nth: {}", userDetails.getUserId(), nth);
        List<TeacherItemResponseDto> response = teacherItemService.getAllRejectedItems(nth, userDetails.getUserId());
        return ResponseEntity.ok(response);
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

    @PostMapping("/reject-templates")
    public ResponseEntity<List<RejectTemplateResponseDto>> addRejectTemplates(
            @Valid @RequestBody AddRejectTemplatesRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        int templateCount = request.getTemplates() != null ? request.getTemplates().size() : 0;
        log.info("거절 템플릿 등록 요청 - teacherId: {}, 템플릿 수: {}", userDetails.getUserId(), templateCount);
        List<RejectTemplateResponseDto> response = rejectTemplateService.addRejectTemplates(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reject-templates")
    public ResponseEntity<List<RejectTemplateResponseDto>> getRejectTemplates(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("거절 템플릿 목록 조회 요청 - teacherId: {}", userDetails.getUserId());
        List<RejectTemplateResponseDto> response = rejectTemplateService.getAllTemplates();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reject-templates/{templateId}")
    public ResponseEntity<Void> deleteRejectTemplate(
            @PathVariable Long templateId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("거절 템플릿 삭제 요청 - teacherId: {}, templateId: {}", userDetails.getUserId(), templateId);
        rejectTemplateService.deleteTemplate(templateId);
        return ResponseEntity.noContent().build();
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

    @PostMapping("/guide")
    public ResponseEntity<ItemGuideResponse> getItemGuide(
            @RequestBody ItemGuideRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("선생님 물품신청 가이드 작성 요청 - teacherId: {}", userDetails.getUserId());
        ItemGuideResponse response = teacherItemService.createItemGuide(request, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/guide/{guide_id}")
    public ResponseEntity<ItemGuideResponse> updateItemGuide(
            @RequestBody ItemGuideRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("guide_id") Long guideId
    ) {
        log.info("선생님 물품신청 가이드 수정 요청 - teacherId: {}", userDetails.getUserId());
        ItemGuideResponse response = teacherItemService.updateItemGuide(request, guideId);
        return ResponseEntity.ok(response);
    }
}
