package co.kr.muldum.infrastructure.report.persistence.adapter;

import co.kr.muldum.domain.report.model.MonthReport;
import co.kr.muldum.infrastructure.report.persistence.entity.MonthReportEntity;
import co.kr.muldum.infrastructure.report.persistence.entity.ReportContent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonthReportMapper {

    private final ObjectMapper objectMapper;

    public MonthReport toDomain(MonthReportEntity entity) {
        ReportContent content = deserializeContent(entity.getReportContent(), entity.getId());
        return MonthReport.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .teamId(entity.getTeamId())
                .topic(content.getTopic())
                .goal(content.getGoal())
                .tech(content.getTech())
                .problem(content.getProblem())
                .teacherFeedback(content.getTeacherFeedback())
                .mentorFeedback(content.getMentorFeedback())
                .status(entity.getStatus())
                .submittedAt(entity.getSubmittedAt())
                .feedback(entity.getFeedback())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public MonthReportEntity toEntity(MonthReport domain) {
        String serializedContent = serializeContent(domain);

        return MonthReportEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .teamId(domain.getTeamId())
                .reportContent(serializedContent)
                .status(domain.getStatus())
                .submittedAt(domain.getSubmittedAt())
                .feedback(domain.getFeedback())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    private ReportContent deserializeContent(String rawContent, Long entityId) {
        String normalized = normalizeLegacyContent(rawContent, entityId);
        if (normalized == null) {
            return ReportContent.builder().build();
        }
        try {
            return objectMapper.readValue(normalized, ReportContent.class);
        } catch (IOException e) {
            log.warn("Failed to deserialize report_content for month_report id {}. Raw value: {}", entityId, rawContent, e);
            return ReportContent.builder().build();
        }
    }

    private String serializeContent(MonthReport domain) {
        ReportContent content = ReportContent.builder()
                .topic(domain.getTopic())
                .goal(domain.getGoal())
                .tech(domain.getTech())
                .problem(domain.getProblem())
                .teacherFeedback(domain.getTeacherFeedback())
                .mentorFeedback(domain.getMentorFeedback())
                .build();
        try {
            return objectMapper.writeValueAsString(content);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize report content", e);
        }
    }

    private String normalizeLegacyContent(String rawContent, Long entityId) {
        if (isEmptyContent(rawContent)) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(rawContent);
            if (node instanceof ObjectNode objectNode) {
                sanitizeGoal(objectNode);
                return objectMapper.writeValueAsString(objectNode);
            }
            return rawContent;
        } catch (IOException e) {
            log.warn("Failed to normalize report_content for month_report id {}. Raw value: {}", entityId, rawContent, e);
            return null;
        }
    }

    private void sanitizeGoal(ObjectNode objectNode) {
        JsonNode goalNode = objectNode.get("goal");
        if (goalNode == null || goalNode.isArray()) {
            return;
        }
        ArrayNode arrayNode = objectMapper.createArrayNode();
        if (goalNode.isTextual()) {
            String text = goalNode.asText();
            if (text != null && !text.isBlank()) {
                arrayNode.add(text);
            }
        }
        objectNode.set("goal", arrayNode);
    }

    private boolean isEmptyContent(String rawContent) {
        if (rawContent == null) {
            return true;
        }
        String trimmed = rawContent.trim();
        return trimmed.isEmpty() || "\"\"".equals(trimmed);
    }
}
