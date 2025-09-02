package co.kr.muldum.domain.teamspace.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomSetting {

    @Column(columnDefinition = "BOOLEAN DEFAULT true", nullable = false)
    private boolean active;

}
