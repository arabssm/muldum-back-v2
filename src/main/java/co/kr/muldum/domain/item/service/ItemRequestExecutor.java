package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.ProductInfo;
import co.kr.muldum.domain.item.model.RequestDetails;
import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.model.enums.TeamType;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;

import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.model.UserInfo;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemRequestExecutor {

    private final ItemRequestRepository itemRequestRepository;
    private final UserReader userReader;

    public void deleteTempItemRequest(Long itemRequestId, Long userId) {
        UserInfo userInfo = userReader.read(User.class, userId);
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (!itemRequest.getTeamId().equals(userInfo.getTeamId().intValue())) {
            log.error("임시 물품 삭제 실패 - 팀 불일치: itemRequestId={}, userId={}, itemTeamId={}, userTeamId={}",
                    itemRequestId, userId, itemRequest.getTeamId(), userInfo.getTeamId());
            throw new CustomException(ErrorCode.FORBIDDEN_TEAM_ITEM);
        }

        if (itemRequest.getStatus() != ItemStatus.INTEMP) {
            log.error("임시 물품 삭제 실패 - 잘못된 상태: itemRequestId={}, status={}", itemRequestId, itemRequest.getStatus());
            throw new CustomException(ErrorCode.ITEM_NOT_IN_TEMP_STATUS);
        }

        itemRequestRepository.delete(itemRequest);
        log.info("임시 물품 삭제 완료 - itemId={}, userId={}, teamId={}", itemRequestId, userId, userInfo.getTeamId());
    }

    public ItemRequest createTempItemRequest(TempItemRequestDto requestDto, Long userId, int teamId) {
        ItemSource itemSource = ItemSource.fromUrl(requestDto.getProductLink());

        ProductInfo productInfo = ProductInfo.builder()
                .name(requestDto.getProduct_name())
                .quantity(requestDto.getQuantity())
                .price(requestDto.getPrice())
                .link(requestDto.getProductLink())
                .itemSource(itemSource)
                .build();

        RequestDetails requestDetails = RequestDetails.builder()
                .reason(requestDto.getReason())
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .teamId(teamId)
                .requesterUserId(userId.intValue())
                .productInfo(productInfo)
                .status(ItemStatus.INTEMP)
                .teamType(TeamType.NETWORK)  // 이 줄 추가
                .requestDetails(requestDetails)
                .build();

        return itemRequestRepository.save(itemRequest);
    }
}