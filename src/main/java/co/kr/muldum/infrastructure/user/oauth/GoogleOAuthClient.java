package co.kr.muldum.infrastructure.user.oauth;

import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.infrastructure.user.oauth.dto.GoogleUserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
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
        } catch (HttpClientErrorException e) {
            log.warn("Google OAuth 코드 교환 실패: {}", e.getResponseBodyAsString());
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
                "https://oauth2.googleapis.com/token",
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

    @lombok.Getter
    static class GoogleTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("expires_in")
        private Long expiresIn;

        @JsonProperty("id_token")
        private String idToken;

        @JsonProperty("refresh_token")
        private String refreshToken;

        @JsonProperty("token_type")
        private String tokenType;
    }
}
