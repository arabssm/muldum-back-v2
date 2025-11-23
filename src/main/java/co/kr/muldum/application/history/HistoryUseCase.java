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

    private static final int GENERATION_BASE_YEAR = 2021;

    private final HistoryService historyService;

    // 리스트 조회
    @Transactional(readOnly = true)
    public List<HistoryResponseDto> getHistories(Integer generation) {
        // 프런트에서는 기준연도(2021)를 뺀 값을 전달하므로 다시 더해 실제 연도로 조회
        Integer actualGeneration = generation != null ? generation + GENERATION_BASE_YEAR : null;

        List<History> histories = historyService.findHistories(actualGeneration);
        return histories.stream()
                .map(history -> HistoryResponseDto.fromEntity(history, null, null)) // detail/slogan은 제외
                .toList();
    }

    // 상세 조회 추가
    @Transactional(readOnly = true)
    public HistoryResponseDto getHistory(Long teamId) {
        return historyService.getHistory(teamId);
    }

    @Transactional(readOnly = true)
    public Integer getCurrentGeneration() {
        Integer rawGeneration = historyService.getCurrentGeneration();
        if (rawGeneration == null) {
            return null;
        }
        // 프런트 요구: 연도 기준으로 기준연도를 뺀 값을 전달 (예: 2025 -> 4)
        return rawGeneration - GENERATION_BASE_YEAR;
    }
}
