package co.kr.muldum.domain.notice.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ContentDataConverter implements AttributeConverter<ContentData, String> {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(ContentData attribute) {
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new RuntimeException("Failed to convert ContentData to JSON string", e);
    }
  }

  @Override
  public ContentData convertToEntityAttribute(String dbData) {
    try {
      return objectMapper.readValue(dbData, ContentData.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to convert JSON string to ContentData", e);
    }
  }

}
