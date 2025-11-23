package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.ItemResponseDto;
import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.NthStatus;
import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import co.kr.muldum.domain.item.repository.NthStatusRepository;
import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.domain.user.model.UserInfo;
import co.kr.muldum.global.exception.CustomException;
import co.kr.muldum.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private final NthStatusRepository nthStatusRepository;

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
        return itemResponseFactory.createResponse(ItemStatus.DELETED, "물품 신청이 정상적으로 삭제되었습니다.");
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

    public ItemResponseDto deleteTempItemRequests(List<Long> itemRequestIds, Long userId) {
        if (itemRequestIds == null || itemRequestIds.isEmpty()) {
            return itemResponseFactory.createRejectedResponse("삭제할 임시 물품 ID를 전달해주세요.");
        }

        UserInfo userInfo = userReader.read(User.class, userId);
        Integer teamId = userInfo.getTeamId().intValue();

        // 팀 소속 & INTEMP 상태 검증
        List<ItemRequest> requests = itemRequestRepository.findByTeamIdAndStatusAndIdIn(
                teamId,
                ItemStatus.INTEMP,
                itemRequestIds
        );

        if (requests.size() != itemRequestIds.size()) {
            return itemResponseFactory.createRejectedResponse("선택한 항목 중 임시 상태가 아니거나 팀에 속하지 않는 물품이 있습니다.");
        }

        itemRequestExecutor.deleteTempItemRequests(itemRequestIds);
        return itemResponseFactory.createResponse(ItemStatus.INTEMP, "선택한 임시 신청이 정상적으로 취소되었습니다.");
    }

    public ItemResponseDto createTempItemRequest(TempItemRequestDto requestDto, Long userId) {
        try {
            UserInfo userInfo = userReader.read(User.class, userId);
            NthStatus nthStatus = nthStatusRepository.findNthStatusById(1L);
            Integer nth = (nthStatus != null && nthStatus.getNthValue() != null) ? nthStatus.getNthValue() : 1;

            log.debug("물품 신청 - 사용자 정보: userId={}, teamId={}, userType={}, nth={}",
                    userInfo.getUserId(), userInfo.getTeamId(), userInfo.getUserType(), nth);

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
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
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

            // 승인된 경우 DB에 저장 (nth 파라미터 추가)
            itemRequestExecutor.createTempItemRequest(requestDto, userId, userInfo.getTeamId().intValue(), nth);
            return itemResponseFactory.createResponse(status, message);

        } catch (IllegalArgumentException e) {
            return itemResponseFactory.createRejectedResponse(e.getMessage());
        } catch (CustomException e) {
            throw e;
        }
    }

    public ItemResponseDto reapplyRejectedItem(Long itemId, Long userId) {
        UserInfo userInfo = userReader.read(User.class, userId);
        ItemRequest rejectedItem = itemRequestRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (!rejectedItem.getTeamId().equals(userInfo.getTeamId().intValue())) {
            throw new CustomException(ErrorCode.FORBIDDEN_TEAM_ITEM);
        }

        if (rejectedItem.getStatus() != ItemStatus.REJECTED) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        NthStatus nthStatus = nthStatusRepository.findNthStatusById(1L);
        Integer nth = (nthStatus != null && nthStatus.getNthValue() != null)
                ? nthStatus.getNthValue()
                : rejectedItem.getNth();

        itemRequestExecutor.duplicateRejectedItemAsTemp(rejectedItem, nth);

        return itemResponseFactory.createResponse(
                ItemStatus.INTEMP,
                "거절된 물품을 임시 신청으로 이동했습니다. 수정 후 다시 제출하세요."
        );
    }
}
