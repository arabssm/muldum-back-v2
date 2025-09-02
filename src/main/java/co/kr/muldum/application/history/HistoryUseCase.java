package co.kr.muldum.application.history;

import org.springframework.transaction.annotation.Transactional;
import co.kr.muldum.application.history.dto.HistoryResponseDto;
import co.kr.muldum.domain.history.model.History;
import co.kr.muldum.domain.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryUseCase {

    private final HistoryService historyService;

    // 리스트 조회
    @Transactional(readOnly = true)
    public List<HistoryResponseDto> getHistories(Integer generation) {
        List<History> histories = historyService.findHistories(generation);
        return histories.stream()
                .map(history -> HistoryResponseDto.fromEntity(history, null, null)) // detail/slogan은 제외
                .toList();
    }

    // 상세 조회 추가
    @Transactional(readOnly = true)
    public HistoryResponseDto getHistory(Long teamId) {
        return historyService.getHistory(teamId);
    }
}