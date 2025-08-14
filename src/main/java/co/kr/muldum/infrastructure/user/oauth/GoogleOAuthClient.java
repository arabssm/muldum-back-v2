package co.kr.muldum.infrastructure.user.oauth;

import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.infrastructure.user.oauth.dto.GoogleUserInfoDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestClientException;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Component
public class GoogleOAuthClient {

    private final RestTemplate restTemplate;

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.client-secret}")
    private String clientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.google.token-url:https://oauth2.googleapis.com/token}")
    private String tokenUrl;

    @Value("${oauth.google.user-info-url:https://www.googleapis.com/oauth2/v3/userinfo}")
    private String userInfoUrl;

    public GoogleOAuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GoogleUserInfoDto getUserInfoByCode(String authorizationCode) {
        try {
            GoogleTokenResponse token = exchangeCodeForToken(authorizationCode);
            if (token == null || token.getAccessToken() == null || token.getAccessToken().isBlank()) {
                throw new CustomException(ErrorCode.INVALID_AUTH_CODE);
            }
            return getUserInfo(token.getAccessToken());
        } catch (RestClientResponseException e) {
            String body = e.getResponseBodyAsString();
            log.warn("Google OAuth 코드 교환 실패: status={}, body-length={}",
                    e.getRawStatusCode(), body != null ? body.length() : 0);
            throw new CustomException(ErrorCode.INVALID_AUTH_CODE);
        } catch (RestClientException e) {
            log.warn("Google OAuth 코드 교환 중 통신 오류: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_AUTH_CODE);
        }
    }

    private GoogleTokenResponse exchangeCodeForToken(String authorizationCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", authorizationCode);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        ResponseEntity<GoogleTokenResponse> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                GoogleTokenResponse.class
        );

        return response.getBody();
    }

    public GoogleUserInfoDto getUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<GoogleUserInfoDto> response = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    request,
                    GoogleUserInfoDto.class
            );

            return response.getBody();
        } catch (RestClientResponseException e) {
            String body = e.getResponseBodyAsString();
            log.warn("Google OAuth 사용자 조회 실패: status={}, body-length={}",
                    e.getRawStatusCode(), body != null ? body.length() : 0);
            throw new CustomException(ErrorCode.INVALID_AUTH_CODE);
        } catch (RestClientException e) {
            log.warn("Google OAuth 사용자 조회 중 통신 오류: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_AUTH_CODE);
        }
    }

    @Getter
    static class GoogleTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("expires_in")
        private Long expiresIn;

        @JsonProperty("id_token")
        private String idToken;

        @JsonProperty("refresh_token")
        private String refreshToken;
    }
}
