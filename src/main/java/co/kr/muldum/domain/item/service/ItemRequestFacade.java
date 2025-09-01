package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.ItemResponseDto;
import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.Student;
import co.kr.muldum.domain.user.model.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemRequestFacade {

    private final UserReader userReader;
    private final ItemValidationService itemValidationService;
    private final ItemRequestCreator itemRequestCreator;
    private final ItemStatusDecisionService itemStatusDecisionService;
    private final ItemResponseFactory itemResponseFactory;

    public ItemResponseDto createTempItemRequest(TempItemRequestDto requestDto, Long userId) {
        try {
            UserInfo userInfo = userReader.read(Student.class, userId);
            
            log.debug("물품 신청 - 사용자 정보: userId={}, teamId={}, userType={}",
                    userInfo.getUserId(), userInfo.getTeamId(), userInfo.getUserType());

            // 검증
            itemValidationService.validateTeamInfo(userInfo);
            itemValidationService.validateProductLink(requestDto);

            // 상태 결정
            ItemSource itemSource = ItemSource.fromUrl(requestDto.getProductLink());
            ItemStatus status = itemStatusDecisionService.decideStatus(itemSource);
            String message = itemStatusDecisionService.getStatusMessage(status);

            // 거부된 경우 DB에 저장하지 않고 바로 응답
            if (status == ItemStatus.REJECTED) {
                return itemResponseFactory.createRejectedResponse(message);
            }

            // 승인된 경우 DB에 저장
            itemRequestCreator.createTempItemRequest(requestDto, userId, userInfo.getTeamId().intValue());
            return itemResponseFactory.createResponse(status, message);

        } catch (IllegalArgumentException e) {
            return itemResponseFactory.createRejectedResponse(e.getMessage());
        }
    }
}