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
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
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
    private final ItemRequestExecutor itemRequestExecutor;
    private final ItemStatusDecisionService itemStatusDecisionService;
    private final ItemResponseFactory itemResponseFactory;
    private final ItemRequestRepository itemRequestRepository;

    public ItemResponseDto updateItemRequest(Long itemId, Long userId, TempItemRequestDto requestDto) {
        try {
            UserInfo userInfo = userReader.read(User.class, userId);

            log.debug("임시 물품 수정 - 사용자 정보: userId={}, teamId={}, userType={}",
                    userInfo.getUserId(), userInfo.getTeamId(), userInfo.getUserType());

            itemValidationService.validateTeamInfo(userInfo);
            itemValidationService.validateProductLink(requestDto);

            ItemSource itemSource = ItemSource.fromUrl(requestDto.getProductLink());
            ItemStatus status = itemStatusDecisionService.decideStatus(itemSource);
            String message = itemStatusDecisionService.getStatusMessage(status);

            if (status == ItemStatus.REJECTED) {
                log.warn("임시 물품 수정 실패 - 허용되지 않은 쇼핑몰: userId={}, productLink={}", userId, requestDto.getProductLink());
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            try {
                if (requestDto.getPrice() != null && !requestDto.getPrice().trim().isEmpty()) {
                    Long.parseLong(requestDto.getPrice());
                }
            } catch (NumberFormatException e) {
                log.warn("임시 물품 수정 실패 - 가격 파싱 오류: userId={}, price={}", userId, requestDto.getPrice());
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            itemRequestExecutor.updateItemRequest(itemId, requestDto, userId, userInfo.getTeamId().intValue());
            return itemResponseFactory.createResponse(status, message);

        } catch (IllegalArgumentException e) {
            return itemResponseFactory.createRejectedResponse(e.getMessage());
        } catch (CustomException e) {
            throw e;
        }
    }

    public ItemResponseDto deleteItemRequest(Long itemRequestId, Long userId) {
        UserInfo userInfo = userReader.read(User.class, userId);
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
        if (!itemRequest.getTeamId().equals(userInfo.getTeamId().intValue())) {
            throw new CustomException(ErrorCode.FORBIDDEN_TEAM_ITEM);
        }
        itemRequestExecutor.deleteItemRequest(itemRequestId);
        return itemResponseFactory.createResponse(ItemStatus.REJECTED, "물품 신청이 정상적으로 삭제되었습니다.");
    }

    public ItemResponseDto deleteTempItemRequest(Long itemRequestId, Long userId) {
        UserInfo userInfo = userReader.read(User.class, userId);
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (!itemRequest.getTeamId().equals(userInfo.getTeamId().intValue())) {
            throw new CustomException(ErrorCode.FORBIDDEN_TEAM_ITEM);
        }

        if (itemRequest.getStatus() != ItemStatus.INTEMP) {
            throw new CustomException(ErrorCode.ITEM_NOT_IN_TEMP_STATUS);
        }

        itemRequestExecutor.deleteTempItemRequest(itemRequestId);
        return itemResponseFactory.createResponse(ItemStatus.INTEMP, "임시 신청이 정상적으로 취소되었습니다.");
    }

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
            itemRequestExecutor.createTempItemRequest(requestDto, userId, userInfo.getTeamId().intValue());
            return itemResponseFactory.createResponse(status, message);

        } catch (IllegalArgumentException e) {
            return itemResponseFactory.createRejectedResponse(e.getMessage());
        } catch (CustomException e) { // Catch CustomException here
            throw e; // Re-throw to be handled by GlobalExceptionHandler
        }
    }
}