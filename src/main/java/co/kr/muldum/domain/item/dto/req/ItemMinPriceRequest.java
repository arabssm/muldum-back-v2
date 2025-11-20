package co.kr.muldum.domain.item.dto.req;

public record ItemMinPriceRequest(
        String minPrice,
        boolean shipping
) {}