package co.kr.muldum.application.teamspace.service;

import co.kr.muldum.presentation.teamspace.dto.NotionPageResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class NotionService {

    @Value("${notion.api-version}")
    private String notionApiVersion;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();
    private final NotionOAuthService oauthService;

    @Autowired
    public NotionService(NotionOAuthService oauthService) {
        this.oauthService = oauthService;
    }

    public NotionPageResponse getNotionPageWithOAuth(String pageId, Long userId) throws IOException {
        String accessToken = oauthService.getUserAccessToken(userId);
        log.info("Notion API 호출 시작 - pageId: {}", pageId);

        Request pageRequest = new Request.Builder()
                .url("https://api.notion.com/v1/pages/" + pageId)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Notion-Version", notionApiVersion)
                .build();

        try (Response response = httpClient.newCall(pageRequest).execute()) {
            if (!response.isSuccessful()) {
                handleError(response);
            }

            String body = response.body().string();
            JsonObject pageData = gson.fromJson(body, JsonObject.class);
            String title = extractTitle(pageData);
            log.info("페이지 제목 추출 완료: {}", title);

            String content = getPageBlocks(pageId, accessToken);
            log.info("페이지 내용 추출 완료 - 길이: {}", content.length());

            return new NotionPageResponse(title, content, true);
        }
    }

    private void handleError(Response response) throws IOException {
        String errorBody = response.body() != null ? response.body().string() : "";
        log.error("Notion API 에러 - status: {}, body: {}", response.code(), errorBody);
        if (response.code() == 404) throw new RuntimeException("페이지를 찾을 수 없습니다");
        if (response.code() == 401) throw new RuntimeException("페이지에 접근 권한이 없습니다");
        throw new RuntimeException("Notion API 호출 실패: " + response.code());
    }

    private String getPageBlocks(String pageId, String accessToken) throws IOException {
        JsonArray blocks = getBlockChildren(pageId, accessToken);
        return convertBlocksToMarkdown(blocks, accessToken, 0);
    }

    private JsonArray getBlockChildren(String blockId, String accessToken) throws IOException {
        Request blocksRequest = new Request.Builder()
                .url("https://api.notion.com/v1/blocks/" + blockId + "/children")
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Notion-Version", notionApiVersion)
                .build();

        try (Response response = httpClient.newCall(blocksRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("블록을 가져올 수 없습니다: " + response.code());
            }
            String body = response.body().string();
            return gson.fromJson(body, JsonObject.class).getAsJsonArray("results");
        }
    }

    private String extractTitle(JsonObject pageData) {
        try {
            JsonObject properties = pageData.getAsJsonObject("properties");
            for (String key : properties.keySet()) {
                JsonObject prop = properties.getAsJsonObject(key);
                if (prop.has("title")) {
                    JsonArray titleArray = prop.getAsJsonArray("title");
                    if (titleArray.size() > 0) {
                        return titleArray.get(0).getAsJsonObject().get("plain_text").getAsString();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("제목 추출 실패", e);
        }
        return "제목 없음";
    }

    private String convertBlocksToMarkdown(JsonArray blocks, String accessToken, int depth) throws IOException {
        StringBuilder markdown = new StringBuilder();
        String indent = "  ".repeat(depth);

        for (JsonElement blockElement : blocks) {
            JsonObject block = blockElement.getAsJsonObject();
            String type = block.get("type").getAsString();
            JsonObject contentBlock = block.getAsJsonObject(type);

            try {
                switch (type) {
                    case "paragraph":
                        markdown.append(indent)
                                .append(extractRichText(contentBlock))
                                .append("\n\n");
                        break;
                    case "heading_1":
                        markdown.append("# ").append(extractRichText(contentBlock)).append("\n\n");
                        break;
                    case "heading_2":
                        markdown.append("## ").append(extractRichText(contentBlock)).append("\n\n");
                        break;
                    case "heading_3":
                        markdown.append("### ").append(extractRichText(contentBlock)).append("\n\n");
                        break;
                    case "bulleted_list_item":
                    case "numbered_list_item":
                        markdown.append(indent)
                                .append(type.equals("bulleted_list_item") ? "- " : "1. ")
                                .append(extractRichText(contentBlock))
                                .append("\n");
                        break;
                    case "code":
                        String language = contentBlock.get("language").getAsString();
                        markdown.append(indent).append("```").append(language).append("\n")
                                .append(extractRichText(contentBlock))
                                .append("\n").append(indent).append("```\n\n");
                        break;
                    case "quote":
                        markdown.append(indent).append("> ").append(extractRichText(contentBlock)).append("\n\n");
                        break;
                    case "image":
                        JsonObject file = contentBlock.getAsJsonObject("file");
                        String url = file.get("url").getAsString();
                        markdown.append(indent).append("![](").append(url).append(")\n\n");
                        break;
                    case "divider":
                        markdown.append(indent).append("---\n\n");
                        break;
                    case "callout":
                        String emoji = contentBlock.getAsJsonObject("icon").get("emoji").getAsString();
                        markdown.append(indent).append("> ").append(emoji).append(" ")
                                .append(extractRichText(contentBlock)).append("\n\n");
                        break;
                    case "toggle":
                         markdown.append(indent).append("> **").append(extractRichText(contentBlock)).append("**\n");
                         break;
                    default:
                        log.debug("지원하지 않는 블록 타입: {}", type);
                }

                if (block.get("has_children").getAsBoolean()) {
                    JsonArray children = getBlockChildren(block.get("id").getAsString(), accessToken);
                    markdown.append(convertBlocksToMarkdown(children, accessToken, depth + 1));
                }

            } catch (Exception e) {
                log.warn("블록 변환 실패 - type: {}", type, e);
            }
        }
        return markdown.toString();
    }

    private String extractRichText(JsonObject blockContent) {
        if (!blockContent.has("rich_text")) return "";
        StringBuilder text = new StringBuilder();
        for (JsonElement richTextElement : blockContent.getAsJsonArray("rich_text")) {
            JsonObject richText = richTextElement.getAsJsonObject();
            String plainText = richText.get("plain_text").getAsString();
            if (richText.has("annotations")) {
                JsonObject annotations = richText.getAsJsonObject("annotations");
                if (annotations.get("bold").getAsBoolean()) plainText = "**" + plainText + "**";
                if (annotations.get("italic").getAsBoolean()) plainText = "*" + plainText + "*";
                if (annotations.get("code").getAsBoolean()) plainText = "`" + plainText + "`";
            }
            text.append(plainText);
        }
        return text.toString();
    }
}