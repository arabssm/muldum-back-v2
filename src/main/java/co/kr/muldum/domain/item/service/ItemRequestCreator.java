package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.ProductInfo;
import co.kr.muldum.domain.item.model.RequestDetails;
import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.domain.item.model.enums.TeamType;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.user.model.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestCreator {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemStatusDecisionService itemStatusDecisionService;

    public CreateResult createItemRequest(TempItemRequestDto requestDto, UserInfo userInfo) {
        ItemSource itemSource = ItemSource.fromUrl(requestDto.getProductLink());
        ItemStatusDecisionService.StatusDecision statusDecision = itemStatusDecisionService.decideStatus(itemSource);

        ProductInfo productInfo = ProductInfo.builder()
                .name(requestDto.getProductName())
                .quantity(requestDto.getQuantity())
                .price(requestDto.getPrice())
                .link(requestDto.getProductLink())
                .itemSource(itemSource)
                .build();

        RequestDetails requestDetails = RequestDetails.builder()
                .reason(requestDto.getReason())
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .teamId(userInfo.getTeamId().intValue())
                .requesterUserId(userInfo.getUserId().intValue())
                .productInfo(productInfo)
                .status(statusDecision.status())
                .teamType(TeamType.NETWORK)  // 이 줄 추가
                .requestDetails(requestDetails)
                .build();

        itemRequestRepository.save(itemRequest);

        return new CreateResult(statusDecision.status(), statusDecision.message());
    }


    public record CreateResult(ItemStatus status, String message) {
    }
}