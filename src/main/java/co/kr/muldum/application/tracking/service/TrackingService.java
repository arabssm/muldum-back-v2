package co.kr.muldum.application.tracking.service;

import co.kr.muldum.application.tracking.dto.TrackingInfo;
import co.kr.muldum.global.properties.ExternalApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final ExternalApiProperties apiProperties;
    private final WebClient webClient = WebClient.create();

    public Mono<TrackingInfo> getTrackingInfo(String trackingNumber) {
        return getTrackingFromSweetTracker(trackingNumber)
                .flatMap(this::getCoordinatesFromKakao);
    }

    private Mono<TrackingInfo> getTrackingFromSweetTracker(String trackingNumber) {
        String url = "http://info.sweettracker.co.kr/api/v1/trackingInfo?t_key=" + apiProperties.getSweetTrackerApiKey()
                + "&t_invoice=" + trackingNumber;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                    JsonArray trackingDetails = jsonObject.getAsJsonArray("trackingDetails");
                    if (trackingDetails != null && trackingDetails.size() > 0) {
                        JsonObject lastDetail = trackingDetails.get(trackingDetails.size() - 1).getAsJsonObject();
                        String status = lastDetail.get("kind").getAsString();
                        String location = lastDetail.get("where").getAsString();
                        return new TrackingInfo(status, location, null, null);
                    }
                    return new TrackingInfo("정보 없음", "정보 없음", null, null);
                });
    }

    private Mono<TrackingInfo> getCoordinatesFromKakao(TrackingInfo trackingInfo) {
        if (trackingInfo.getLocation().equals("정보 없음")) {
            return Mono.just(trackingInfo);
        }

        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + trackingInfo.getLocation();

        return webClient.get()
                .uri(url)
                .header("Authorization", "KakaoAK " + apiProperties.getKakaoApiKey())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                    JsonArray documents = jsonObject.getAsJsonArray("documents");
                    if (documents != null && documents.size() > 0) {
                        JsonObject firstDoc = documents.get(0).getAsJsonObject();
                        double latitude = firstDoc.get("y").getAsDouble();
                        double longitude = firstDoc.get("x").getAsDouble();
                        return new TrackingInfo(trackingInfo.getStatus(), trackingInfo.getLocation(), latitude, longitude);
                    }
                    return trackingInfo; // 좌표를 찾지 못한 경우 기존 정보만 반환
                });
    }
}
