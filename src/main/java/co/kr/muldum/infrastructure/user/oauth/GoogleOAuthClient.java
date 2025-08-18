package co.kr.muldum.infrastructure.user.oauth;

import co.kr.muldum.infrastructure.user.oauth.dto.GoogleUserInfoDto;
import co.kr.muldum.infrastructure.user.oauth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {

    private final WebClient webClient = WebClient.builder().build();

    @Value("${google.client-id}")     private String clientId;
    @Value("${google.client-secret}") private String clientSecret;
    @Value("${google.redirect-uri}")  private String redirectUri;
    @Value("${google.token-url}")     private String tokenUrl;
    @Value("${google.user-info-url}") private String userInfoUrl;

    // PKCE 안 쓰면 codeVerifier는 null로 넘겨도 OK
    public TokenResponse exchangeCodeForToken(String authorizationCode) {
        return exchangeCodeForToken(authorizationCode, null);
    }

    public TokenResponse exchangeCodeForToken(String authorizationCode, String codeVerifier) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", authorizationCode);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");
        if (codeVerifier != null && !codeVerifier.isBlank()) {
            form.add("code_verifier", codeVerifier); // ← PKCE 사용 시 필수
        }

        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .exchangeToMono(res -> {
                    if (res.statusCode().is2xxSuccessful()) {
                        return res.bodyToMono(TokenResponse.class);
                    }
                    return res.bodyToMono(String.class).defaultIfEmpty("")
                            .flatMap(body -> {
                                log.error("Google /token error {} body={}", res.statusCode(), body);
                                return Mono.error(new IllegalStateException("Google token error: " + body));
                            });
                })
                .block();
    }

    public GoogleUserInfoDto getUserInfo(String accessToken) {
        return webClient.get()
                .uri(userInfoUrl)
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(GoogleUserInfoDto.class)
                .block();
    }
}
