package co.kr.muldum.presentation.item;

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
}