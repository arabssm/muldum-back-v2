package co.kr.muldum.domain.history.service;

import co.kr.muldum.application.history.dto.HistoryResponseDto;
import co.kr.muldum.domain.history.model.History;
import co.kr.muldum.domain.history.repository.HistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final ObjectMapper objectMapper;

    public List<History> findHistories(Integer generation){

        return historyRepository.findByGeneration(generation);
    }

    public HistoryResponseDto getHistory(Long teamId) {
        History history = historyRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("해당 동아리를 찾을 수 없습니다. id=" + teamId));

        // DB에서 가져온 slogan
        String slogan = history.getSlogan();

        // DB detail(jsonb)을 DTO.Detail로 변환
        HistoryResponseDto.Detail detail = null;
        try {
            if (history.getDetail() != null) {
                detail = objectMapper.readValue(history.getDetail(), HistoryResponseDto.Detail.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("detail 파싱 실패", e);
        }

        return HistoryResponseDto.fromEntity(history, slogan, detail);
    }
}
