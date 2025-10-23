package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.ProductInfoResponseDto;
import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProductInfoService {

    public ProductInfoResponseDto getProductInfo(String productLink) {
        ItemSource source = ItemSource.fromUrl(productLink);

        return switch (source) {
            case DEVICEMART -> scrapeDeviceMart(productLink);
            case ELEVENMARKET -> scrape11st(productLink);
            default -> throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        };
    }

    private ProductInfoResponseDto scrapeDeviceMart(String productLink) {
        try {
            Document doc = Jsoup.connect(productLink)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(5000)
                    .get();

            Element nameEl = doc.selectFirst(".right_item_title");
            Element priceEl = doc.selectFirst(".right_item_price");

            if (nameEl == null || priceEl == null)
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);

            String name = nameEl.text().trim();
            String price = priceEl.text().trim();

            return new ProductInfoResponseDto(name, price);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private ProductInfoResponseDto scrape11st(String productLink) {
        try {
            Document doc = Jsoup.connect(productLink)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(5000)
                    .get();

            Element nameEl = doc.selectFirst("h1.title");
            Element priceEl = doc.selectFirst("strong span.value");

            if (nameEl == null || priceEl == null)
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);

            String name = nameEl.text().trim();
            String price = priceEl.text().trim();

            return new ProductInfoResponseDto(name, price);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
