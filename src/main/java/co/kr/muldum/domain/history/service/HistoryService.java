package co.kr.muldum.domain.history.service;

import co.kr.muldum.domain.history.model.History;
import co.kr.muldum.domain.history.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    public List<History> findHistories(Integer generation){
        if (generation == null) {
            return historyRepository.findAll();
        }
        return historyRepository.findByGeneration(generation);
    }
}
