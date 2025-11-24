package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.ItemResponseDto;
import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
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

    public ItemResponseDto updateItemRequest(Long itemId, Long userId, TempItemRequestDto requestDto) {
        try {
            UserInfo userInfo = userReader.read(User.class, userId);

            log.debug("임시 물품 수정 - 사용자 정보: userId={}, teamIds={}, userType={}",
                    userInfo.getUserId(), userInfo.getTeamIds(), userInfo.getUserType());

            itemValidationService.validateTeamInfo(userInfo);
            itemValidationService.validateProductLink(requestDto);

            // 팀 ID 결정
            Integer teamId = resolveTeamId(requestDto.getTeamId(), userInfo);

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

            itemRequestExecutor.updateItemRequest(itemId, requestDto, userId, teamId);
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

        if (!userInfo.getTeamIds().contains(itemRequest.getTeamId().longValue())) {
            throw new CustomException(ErrorCode.FORBIDDEN_TEAM_ITEM);
        }
        itemRequestExecutor.deleteItemRequest(itemRequestId);
        return itemResponseFactory.createResponse(ItemStatus.DELETED, "물품 신청이 정상적으로 삭제되었습니다.");
    }

    public ItemResponseDto deleteTempItemRequest(Long itemRequestId, Long userId) {
        UserInfo userInfo = userReader.read(User.class, userId);
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (!userInfo.getTeamIds().contains(itemRequest.getTeamId().longValue())) {
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
        // 일괄 삭제의 경우, 모든 요청이 사용자의 팀 중 하나에 속해야 함.
        // 하지만 여기서는 간단히 첫 번째 팀을 기준으로 하거나, 로직을 변경해야 함.
        // 기존 로직: findByTeamIdAndStatusAndIdIn
        // 변경 로직: ID로 조회 후 각각 권한 확인 또는 IN 절에 팀 목록 사용

        // 여기서는 간단하게 구현하기 위해, 조회된 모든 아이템의 팀 ID가 사용자의 팀 목록에 포함되는지 확인
        List<ItemRequest> requests = itemRequestRepository.findAllById(itemRequestIds);

        if (requests.size() != itemRequestIds.size()) {
            return itemResponseFactory.createRejectedResponse("존재하지 않는 물품이 포함되어 있습니다.");
        }

        for (ItemRequest req : requests) {
            if (req.getStatus() != ItemStatus.INTEMP) {
                return itemResponseFactory.createRejectedResponse("임시 상태가 아닌 물품이 포함되어 있습니다.");
            }
            if (!userInfo.getTeamIds().contains(req.getTeamId().longValue())) {
                return itemResponseFactory.createRejectedResponse("권한이 없는 물품이 포함되어 있습니다.");
            }
        }

        itemRequestExecutor.deleteTempItemRequests(itemRequestIds);
        return itemResponseFactory.createResponse(ItemStatus.INTEMP, "선택한 임시 신청이 정상적으로 취소되었습니다.");
    }

    public ItemResponseDto createTempItemRequest(TempItemRequestDto requestDto, Long userId) {
        try {
            UserInfo userInfo = userReader.read(User.class, userId);

            log.debug("물품 신청 - 사용자 정보: userId={}, teamIds={}, userType={}",
                    userInfo.getUserId(), userInfo.getTeamIds(), userInfo.getUserType());

            // 검증
            itemValidationService.validateTeamInfo(userInfo);
            itemValidationService.validateProductLink(requestDto);

            // 팀 ID 결정
            Integer teamId = resolveTeamId(requestDto.getTeamId(), userInfo);

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

            // 승인된 경우 DB에 저장
            itemRequestExecutor.createTempItemRequest(requestDto, userId, teamId);
            return itemResponseFactory.createResponse(status, message);

        } catch (IllegalArgumentException e) {
            return itemResponseFactory.createRejectedResponse(e.getMessage());
        }
    }

    public ItemResponseDto reapplyRejectedItem(Long itemId, Long userId) {
        UserInfo userInfo = userReader.read(User.class, userId);
        ItemRequest rejectedItem = itemRequestRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (!userInfo.getTeamIds().contains(rejectedItem.getTeamId().longValue())) {
            throw new CustomException(ErrorCode.FORBIDDEN_TEAM_ITEM);
        }

        if (!rejectedItem.getStatus().isRejected()) {
            log.warn("재신청 실패 - 거절 상태가 아닌 물품입니다. userId={}, itemId={}", userId, itemId);
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        itemRequestExecutor.duplicateRejectedItemAsTemp(rejectedItem);

        return itemResponseFactory.createResponse(
                ItemStatus.INTEMP,
                "거절된 물품을 임시 신청으로 이동했습니다. 수정 후 다시 제출하세요.");
    }

    private Integer resolveTeamId(Integer requestTeamId, UserInfo userInfo) {
        if (requestTeamId != null) {
            if (!userInfo.getTeamIds().contains(requestTeamId.longValue())) {
                throw new CustomException(ErrorCode.FORBIDDEN_TEAM_ITEM);
            }
            return requestTeamId;
        } else {
            if (userInfo.getTeamIds().isEmpty()) {
                throw new CustomException(ErrorCode.TEAM_NOT_FOUND);
            }
            if (userInfo.getTeamIds().size() == 1) {
                return userInfo.getTeamIds().get(0).intValue();
            }
            throw new IllegalArgumentException("팀 ID를 선택해주세요.");
        }
    }
}
