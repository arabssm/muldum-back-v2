package co.kr.muldum.presentation.calender;

import co.kr.muldum.application.calender.service.CalenderService;
import co.kr.muldum.application.calender.dto.CalenderResponseDto;
import co.kr.muldum.application.calender.dto.CreateCalenderRequestDto;
import co.kr.muldum.global.exception.TokenNotFoundException;
import co.kr.muldum.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/std/calender")
@RequiredArgsConstructor
public class CalenderController {

    private final CalenderService calenderService;

    @PostMapping("/{teamId}")
    public ResponseEntity<CalenderResponseDto> createCalender(
            @PathVariable Long teamId,
            @RequestBody CreateCalenderRequestDto createCalenderRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        if (customUserDetails == null) {
            throw new TokenNotFoundException("토큰이 없거나 유효하지 않습니다.");
        }

        CalenderResponseDto calenderResponse = calenderService.createCalender(teamId, createCalenderRequestDto);
        return ResponseEntity.ok(calenderResponse);
    }
}