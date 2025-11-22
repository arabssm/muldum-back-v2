package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.SimilarProductResponseDto;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.ProductInfo;
import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.model.UserInfo;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimilarProductService {

    private static final int MAX_SUGGESTIONS = 3;

    private final ItemRequestRepository itemRequestRepository;
    private final UserReader userReader;

    public List<SimilarProductResponseDto> getSimilarProducts(Long itemId, Long userId) {
        UserInfo userInfo = userReader.read(User.class, userId);
        ItemRequest baseItem = itemRequestRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (!baseItem.getTeamId().equals(userInfo.getTeamId().intValue())) {
            throw new CustomException(ErrorCode.FORBIDDEN_TEAM_ITEM);
        }

        ProductInfo productInfo = baseItem.getProductInfo();
        if (productInfo == null || productInfo.getLink() == null) {
            throw new CustomException(ErrorCode.INVALID_PRODUCT_LINK);
        }

        ItemSource source = productInfo.getItemSource();
        if (source == null || source == ItemSource.OTHER) {
            source = ItemSource.fromUrl(productInfo.getLink());
        }

        if (source != ItemSource.DEVICEMART && source != ItemSource.ELEVENMARKET) {
            throw new CustomException(ErrorCode.INVALID_PRODUCT_LINK);
        }

        String domain = source == ItemSource.DEVICEMART ? "devicemart.co.kr" : "11st.co.kr";
        final ItemSource finalSource = source;
        String keyword = buildKeyword(productInfo.getName());

        List<ItemRequest> candidates = new ArrayList<>(itemRequestRepository.findSimilarItems(
                domain,
                itemId,
                keyword,
                MAX_SUGGESTIONS
        ));

        if (candidates.size() < MAX_SUGGESTIONS) {
            List<ItemRequest> fallback = itemRequestRepository.findSimilarItems(domain, itemId, null, MAX_SUGGESTIONS * 2);
            for (ItemRequest candidate : fallback) {
                boolean alreadyIncluded = candidates.stream()
                        .anyMatch(existing -> existing.getId().equals(candidate.getId()));
                if (!alreadyIncluded) {
                    candidates.add(candidate);
                }
                if (candidates.size() >= MAX_SUGGESTIONS) {
                    break;
                }
            }
        }

        return candidates.stream()
                .limit(MAX_SUGGESTIONS)
                .map(item -> toResponse(item, finalSource))
                .toList();
    }

    private SimilarProductResponseDto toResponse(ItemRequest itemRequest, ItemSource source) {
        ProductInfo info = itemRequest.getProductInfo();
        return SimilarProductResponseDto.builder()
                .itemId(itemRequest.getId())
                .productName(info != null ? info.getName() : null)
                .productLink(info != null ? info.getLink() : null)
                .price(parsePrice(info != null ? info.getPrice() : null))
                .source(source.name())
                .teamId(itemRequest.getTeamId())
                .build();
    }

    private Long parsePrice(String rawPrice) {
        if (rawPrice == null || rawPrice.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(rawPrice.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            log.debug("가격 파싱 실패 - rawPrice={}", rawPrice);
            return null;
        }
    }

    private String buildKeyword(String productName) {
        if (productName == null) {
            return null;
        }
        String trimmed = productName.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String[] tokens = trimmed.split("\\s+");
        int length = Math.min(tokens.length, 3);
        return String.join(" ", Arrays.copyOf(tokens, length));
    }
}
