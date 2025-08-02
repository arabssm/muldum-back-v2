package co.kr.muldum.domain.notice.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // JPA 기본 생성자
@AllArgsConstructor
public class ContentData {
  private String content;
  private List<FileData> files;
}
