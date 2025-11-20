package co.kr.muldum.domain.item.dto.req;

import java.util.List;

public record ItemOpenRequest(
        String projectType,
        List<ItemMinPriceRequest> guide,
        String deadlineDate
) {}
