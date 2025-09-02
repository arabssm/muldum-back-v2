package co.kr.muldum.domain.teamspace.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamConfig {

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_setting", columnDefinition = "jsonb")
    private CustomSetting customSetting;

}