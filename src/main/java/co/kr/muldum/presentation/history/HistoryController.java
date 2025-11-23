package co.kr.muldum.presentation.history;

import co.kr.muldum.application.history.HistoryUseCase;
import co.kr.muldum.application.history.dto.HistoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{teamId:\\d+}")
    public HistoryResponseDto getHistory(@PathVariable Long teamId) {
        return historyUseCase.getHistory(teamId);
    }

    @GetMapping("/current-generation")
    public Integer getCurrentGeneration() {
        return historyUseCase.getCurrentGeneration();
    }
}
