package co.kr.muldum.presentation.history;

import co.kr.muldum.application.history.HistoryUseCase;
import co.kr.muldum.application.history.dto.HistoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ara/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryUseCase historyUseCase;

    @GetMapping
    public List<HistoryResponseDto> getHistories(
            @RequestParam(required = false) Integer generation
    ) {
        return historyUseCase.getHistories(generation);
    }
}
