package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.NthOpenCountResponseDto;
import co.kr.muldum.domain.item.dto.NthOpenedListResponseDto;
import co.kr.muldum.domain.item.dto.NthStatusHistoryResponseDto;
import co.kr.muldum.domain.item.dto.NthStatusResponseDto;
import co.kr.muldum.domain.item.model.NthStatus;
import co.kr.muldum.domain.item.model.NthStatusHistory;
import co.kr.muldum.domain.item.repository.NthStatusHistoryRepository;
import co.kr.muldum.domain.item.repository.NthStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NthStatusQueryService {

    private final NthStatusRepository nthStatusRepository;
    private final NthStatusHistoryRepository nthStatusHistoryRepository;

    public NthStatusResponseDto getCurrentStatus() {
        return nthStatusRepository.findByNthStatusId(1L)
                .map(this::convertToDto)
                .orElseGet(() -> NthStatusResponseDto.builder()
                        .nth(0)
                        .guide(Collections.emptyList())
                        .build());
    }

    public List<NthStatusHistoryResponseDto> getOpenHistory() {
        return nthStatusHistoryRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::convertHistoryToDto)
                .toList();
    }

    public NthOpenCountResponseDto getOpenCount() {
        long count = nthStatusHistoryRepository.count();
        return new NthOpenCountResponseDto(count);
    }

    public NthOpenedListResponseDto getOpenedNthValues() {
        List<Integer> nths = nthStatusHistoryRepository.findAll()
                .stream()
                .map(NthStatusHistory::getNthValue)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
        return new NthOpenedListResponseDto(nths);
    }

    private NthStatusResponseDto convertToDto(NthStatus nthStatus) {
        return NthStatusResponseDto.builder()
                .nth(nthStatus.getNthValue())
                .projectType(nthStatus.getProjectType())
                .guide(nthStatus.getGuide())
                .deadlineDate(nthStatus.getDeadlineDate())
                .teacherId(nthStatus.getTeacherId())
                .openedAt(nthStatus.getCreatedAt())
                .build();
    }

    private NthStatusHistoryResponseDto convertHistoryToDto(NthStatusHistory history) {
        return NthStatusHistoryResponseDto.builder()
                .nth(history.getNthValue())
                .projectType(history.getProjectType())
                .guide(history.getGuide())
                .deadlineDate(history.getDeadlineDate())
                .teacherId(history.getTeacherId())
                .openedAt(history.getCreatedAt())
                .build();
    }
}
