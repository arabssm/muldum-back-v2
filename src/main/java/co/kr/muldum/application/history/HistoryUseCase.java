package co.kr.muldum.application.history;

import org.springframework.transaction.annotation.Transactional;
import co.kr.muldum.application.history.dto.HistoryResponseDto;
import co.kr.muldum.domain.history.model.History;
import co.kr.muldum.domain.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryUseCase {

    private final HistoryService historyService;

    @Transactional(readOnly = true)
    public List<HistoryResponseDto> getHistories(Integer generation) {
        List<History> histories = historyService.findHistories(generation);
        return histories.stream()
                .map(HistoryResponseDto::fromEntity)
                .toList();
    }
}
