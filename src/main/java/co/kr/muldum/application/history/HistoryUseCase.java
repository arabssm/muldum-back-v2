package co.kr.muldum.application.history;

import co.kr.muldum.domain.dto.HistoryDetailResponseDto;
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

    @Transactional(readOnly = true)
    public List<HistoryResponseDto> getHistories(Integer generation) {
        List<History> histories = historyService.findHistories(generation);
        return histories.stream()
                .map(HistoryResponseDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public HistoryDetailResponseDto getHistoryDetail(Long teamId) {
        History history = historyService.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 동아리가 없습니다."));
        return HistoryDetailResponseDto.fromEntity(history);
    }
}
