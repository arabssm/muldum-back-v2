package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.ProductInfoResponseDto;
import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProductInfoService {

    public ProductInfoResponseDto getProductInfo(String productLink) {
        ItemSource source = ItemSource.fromUrl(productLink);

        return switch (source) {
            case DEVICEMART -> scrapeDeviceMart(productLink);
            case ELEVENMARKET -> scrape11st(productLink);
            default -> throw new CustomException(ErrorCode.INVALID_PRODUCT_LINK);
        };
    }

    private ProductInfoResponseDto scrapeDeviceMart(String productLink) {
        try {
            Document doc = Jsoup.connect(productLink)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(5000)
                    .get();

            // 상품명
            Element titleEl = doc.selectFirst("li.goods_name > h3");
            // 판매가
            Element priceEl = doc.selectFirst("strong.sell_price");

            if (priceEl == null)
                throw new CustomException(ErrorCode.INVALID_PRODUCT_LINK);

            // 정가
            Element wrapEl = priceEl.parent();

            if (titleEl == null || wrapEl == null)
                throw new CustomException(ErrorCode.INVALID_PRODUCT_LINK);

            String title = titleEl.text();
            String price = normalizePrice(priceEl.text());
            String regularPrice = normalizePrice(wrapEl.ownText());

            return new ProductInfoResponseDto(title, price, regularPrice);

        } catch (IOException e) {
            throw new CustomException(ErrorCode.INVALID_PRODUCT_LINK);
        }
    }

    private ProductInfoResponseDto scrape11st(String productLink) {
        if (productLink.contains("www.11st.co.kr")) {
            productLink = productLink.replace("www.11st.co.kr", "m.11st.co.kr").replace("/products/", "/products/ma/");
        }

        try {
            Document doc = Jsoup.connect(productLink)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(8000)
                    .get();

            // 모든 script 가져오기
            Elements scripts = doc.select("script");

            String name = null;
            String price = null;

            // fbq ViewContent 스크립트에서 content_name과 value 추출
            Pattern namePattern = Pattern.compile("content_name\\s*:\\s*decodeURI\\('(.+?)'\\)");
            Pattern pricePattern = Pattern.compile("value\\s*:\\s*(\\d+)");

            for (Element script : scripts) {
                String scriptText = script.html();

                if (scriptText.contains("fbq('track', 'ViewContent'")) {
                    Matcher nameMatcher = namePattern.matcher(scriptText);
                    Matcher priceMatcher = pricePattern.matcher(scriptText);

                    if (nameMatcher.find())
                        name = nameMatcher.group(1).trim();

                    if (priceMatcher.find())
                        price = priceMatcher.group(1).trim();

                    if (name != null && price != null)
                        break;
                }
            }

            if (name == null || price == null)
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);

            // 정가 없으면 판매가로 대체
            String regularPrice = price;

            return new ProductInfoResponseDto(name, price, regularPrice);

        } catch (IOException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }


    private String normalizePrice(String rawPrice) {
        if (rawPrice == null) return "0";
        return rawPrice.replaceAll("[^0-9]", "");
    }
}
