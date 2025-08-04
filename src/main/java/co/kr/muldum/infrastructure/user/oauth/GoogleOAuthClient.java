package co.kr.muldum.infrastructure.user.oauth;

import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.infrastructure.user.oauth.dto.GoogleUserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class GoogleOAuthClient {

    private final RestTemplate restTemplate;

    public GoogleOAuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GoogleUserInfoDto getUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<GoogleUserInfoDto> response = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v3/userinfo",
                    HttpMethod.GET,
                    request,
                    GoogleUserInfoDto.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.warn("Google OAuth 인증 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_AUTH_CODE);
        }
    }
}
