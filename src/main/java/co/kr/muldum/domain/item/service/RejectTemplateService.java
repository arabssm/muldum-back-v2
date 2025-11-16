package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.RejectTemplateResponseDto;
import co.kr.muldum.domain.item.dto.req.AddRejectTemplatesRequest;
import co.kr.muldum.domain.item.model.RejectTemplate;
import co.kr.muldum.domain.item.repository.RejectTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RejectTemplateService {

    private final RejectTemplateRepository rejectTemplateRepository;

    @Transactional
    public List<RejectTemplateResponseDto> addRejectTemplates(AddRejectTemplatesRequest request) {
        if (request.getTemplates() == null) {
            throw new IllegalArgumentException("템플릿 목록이 필요합니다.");
        }

        List<String> templates = request.getTemplates().stream()
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();

        if (templates.isEmpty()) {
            throw new IllegalArgumentException("유효한 템플릿 내용이 필요합니다.");
        }

        List<RejectTemplate> saved = rejectTemplateRepository.saveAll(
                templates.stream()
                        .map(content -> RejectTemplate.builder().content(content).build())
                        .toList()
        );

        log.info("거절 사유 템플릿 {}건 등록 완료", saved.size());

        return saved.stream()
                .map(this::toDto)
                .toList();
    }

    public List<RejectTemplateResponseDto> getAllTemplates() {
        return rejectTemplateRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void deleteTemplate(Long templateId) {
        if (!rejectTemplateRepository.existsById(templateId)) {
            throw new EntityNotFoundException("존재하지 않는 템플릿입니다. id=" + templateId);
        }
        rejectTemplateRepository.deleteById(templateId);
        log.info("거절 사유 템플릿 삭제 완료 - id: {}", templateId);
    }

    private RejectTemplateResponseDto toDto(RejectTemplate template) {
        return RejectTemplateResponseDto.builder()
                .id(template.getId())
                .content(template.getContent())
                .createdAt(template.getCreatedAt())
                .build();
    }
}
