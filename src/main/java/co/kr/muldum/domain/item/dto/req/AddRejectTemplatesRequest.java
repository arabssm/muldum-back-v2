package co.kr.muldum.domain.item.dto.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AddRejectTemplatesRequest {

    @NotNull(message = "templates 항목이 필요합니다.")
    @Size(min = 1, message = "최소 1개 이상의 템플릿이 필요합니다.")
    private List<@NotBlank(message = "템플릿 내용은 비워둘 수 없습니다.") String> templates;
}
