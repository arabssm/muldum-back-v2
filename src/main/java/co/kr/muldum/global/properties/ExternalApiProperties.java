package co.kr.muldum.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("external.api")
@Getter
@Setter
public class ExternalApiProperties {
    private String sweetTrackerApiKey;
    private String kakaoApiKey;
}
