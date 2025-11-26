package co.kr.muldum.application.teamspace.service;

import co.kr.muldum.domain.user.model.UserNotionToken;
import co.kr.muldum.domain.user.repository.UserNotionTokenRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;

@Service
@Slf4j
public class NotionOAuthService {

    @Value("${notion.oauth.client-id}")
    private String clientId;

    @Value("${notion.oauth.client-secret}")
    private String clientSecret;

    @Value("${notion.oauth.redirect-uri}")
    private String redirectUri;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();
    private final UserNotionTokenRepository tokenRepository;

    @Autowired
    public NotionOAuthService(UserNotionTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * OAuth 콜백 처리 - Authorization Code를 Access Token으로 교환
     */
    @Transactional
    public void handleOAuthCallback(String code, Long userId) throws IOException {
        // 1. Access Token 요청
        String tokenUrl = "https://api.notion.com/v1/oauth/token";

        // Basic Auth 생성
        String credentials = clientId + ":" + clientSecret;
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

        // Request Body
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("grant_type", "authorization_code");
        requestBody.addProperty("code", code);
        requestBody.addProperty("redirect_uri", redirectUri);

        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(tokenUrl)
                .post(body)
                .addHeader("Authorization", basicAuth)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to exchange token: {}, response: {}", response.code(), response.body() != null ? response.body().string() : "null");
                throw new RuntimeException("토큰 교환 실패: " + response.code());
            }

            String responseBody = response.body().string();
            JsonObject tokenData = gson.fromJson(responseBody, JsonObject.class);

            // 2. 토큰 저장
            String accessToken = tokenData.get("access_token").getAsString();
            String workspaceId = tokenData.get("workspace_id").getAsString();
            String workspaceName = tokenData.get("workspace_name").getAsString();
            String botId = tokenData.get("bot_id").getAsString();

            UserNotionToken token = tokenRepository.findByUserId(userId)
                    .orElse(new UserNotionToken());

            token.setUserId(userId);
            token.setAccessToken(accessToken);
            token.setWorkspaceId(workspaceId);
            token.setWorkspaceName(workspaceName);
            token.setBotId(botId);

            tokenRepository.save(token);

            log.info("Notion OAuth 연동 완료 - userId: {}, workspace: {}", userId, workspaceName);
        }
    }

    /**
     * 사용자의 Notion Access Token 조회
     */
    public String getUserAccessToken(Long userId) {
        return tokenRepository.findByUserId(userId)
                .map(UserNotionToken::getAccessToken)
                .orElseThrow(() -> new RuntimeException("Notion 연동이 필요합니다"));
    }
}
