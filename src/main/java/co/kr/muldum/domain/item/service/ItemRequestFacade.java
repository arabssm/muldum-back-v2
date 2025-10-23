package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.ItemResponseDto;
import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
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
public class ItemRequestFacade {

    private final UserReader userReader;
    private final ItemValidationService itemValidationService;
    private final ItemRequestCreator itemRequestCreator;
    private final ItemStatusDecisionService itemStatusDecisionService;
    private final ItemResponseFactory itemResponseFactory;

    public ItemResponseDto createTempItemRequest(TempItemRequestDto requestDto, Long userId) {
        try {
            UserInfo userInfo = userReader.read(User.class, userId);
            
            log.debug("물품 신청 - 사용자 정보: userId={}, teamId={}, userType={}",
                    userInfo.getUserId(), userInfo.getTeamId(), userInfo.getUserType());

            // 검증
            itemValidationService.validateTeamInfo(userInfo);
            itemValidationService.validateProductLink(requestDto);

            // 상태 결정
            ItemSource itemSource = ItemSource.fromUrl(requestDto.getProductLink());
            ItemStatus status = itemStatusDecisionService.decideStatus(itemSource);
            String message = itemStatusDecisionService.getStatusMessage(status);

            // 거부된 경우 DB에 저장하지 않고 바로 예외 발생
            if (status == ItemStatus.REJECTED) {
                log.warn("물품 신청 실패 - 허용되지 않은 쇼핑몰: userId={}, productLink={}", userId, requestDto.getProductLink());
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE); // Throw exception here
            }

            // 가격 파싱 및 유효성 검사
            try {
                if (requestDto.getPrice() != null && !requestDto.getPrice().trim().isEmpty()) {
                    Long.parseLong(requestDto.getPrice());
                }
            } catch (NumberFormatException e) {
                log.warn("물품 신청 실패 - 가격 파싱 오류: userId={}, price={}", userId, requestDto.getPrice());
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            // 승인된 경우 DB에 저장
            itemRequestCreator.createTempItemRequest(requestDto, userId, userInfo.getTeamId().intValue());
            return itemResponseFactory.createResponse(status, message);

        } catch (IllegalArgumentException e) {
            return itemResponseFactory.createRejectedResponse(e.getMessage());
        } catch (CustomException e) { // Catch CustomException here
            throw e; // Re-throw to be handled by GlobalExceptionHandler
        }
    }
}