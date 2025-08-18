package co.kr.muldum.infrastructure.user.oauth;

import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import co.kr.muldum.infrastructure.user.oauth.dto.GoogleUserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {

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

    private final RestTemplate restTemplate;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String idToken;
    }

    public TokenResponse exchangeCodeForToken(String authorizationCode) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("code", authorizationCode);
            form.add("client_id", clientId);
            form.add("client_secret", clientSecret);
            form.add("redirect_uri", redirectUri);
            form.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);
            Map<?, ?> body = response.getBody();

            String accessToken = body == null ? null : (String) body.get("access_token");
            String idToken = body == null ? null : (String) body.get("id_token");

            if (accessToken == null) {
                throw new CustomException(ErrorCode.INVALID_AUTH_CODE);
            }
            return new TokenResponse(accessToken, idToken);

        } catch (HttpClientErrorException e) {
            log.warn("Google OAuth 토큰 교환 실패: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.INVALID_AUTH_CODE);
        } catch (Exception e) {
            log.warn("Google OAuth 토큰 교환 중 알 수 없는 오류", e);
            throw new CustomException(ErrorCode.EXTERNAL_PROVIDER_ERROR);
        }
    }

    public GoogleUserInfoDto getUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<?, ?> body = response.getBody();
            if (body == null || body.get("email") == null) {
                throw new CustomException(ErrorCode.INVALID_AUTH_CODE);
            }

            // Map to DTO (minimal fields used; expand as needed)
            String email = (String) body.get("email");
            String name = (String) body.get("name");
            return new GoogleUserInfoDto(email, name);
        } catch (HttpClientErrorException e) {
            log.warn("Google OAuth 인증 실패: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.INVALID_AUTH_CODE);
        } catch (Exception e) {
            log.warn("Google OAuth 사용자 정보 조회 중 알 수 없는 오류", e);
            throw new CustomException(ErrorCode.EXTERNAL_PROVIDER_ERROR);
        }
    }
}
