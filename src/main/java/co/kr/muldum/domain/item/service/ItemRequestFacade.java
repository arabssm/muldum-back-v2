package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.dto.ItemResponseDto;
import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.User;
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
    private final ItemRequestFinalizer itemRequestFinalizer;
    private final ItemResponseFactory itemResponseFactory;

    public ItemResponseDto createTempItemRequest(TempItemRequestDto requestDto, Long userId) {
        UserInfo userInfo = readUserInfo(userId);
        logUserInfo(userInfo, "물품 신청");

        ItemValidationService.ValidationResult teamValidation =
                itemValidationService.validateUserTeam(userInfo);
        if (!teamValidation.isValid()) {
            return itemResponseFactory.createRejectedResponse(teamValidation.getErrorMessage());
        }

        ItemValidationService.ValidationResult linkValidation = 
                itemValidationService.validateProductLink(requestDto.getProductLink(), userId);
        if (!linkValidation.isValid()) {
            return itemResponseFactory.createRejectedResponse(linkValidation.getErrorMessage());
        }

        ItemRequestCreator.CreateResult createResult = itemRequestCreator.createItemRequest(requestDto, userInfo);
        
        return itemResponseFactory.createResponse(createResult.status(), createResult.message());
    }

    public ItemResponseDto finalizeItemRequest(Long userId) {
        UserInfo userInfo = readUserInfo(userId);

        ItemValidationService.ValidationResult teamValidation =
                itemValidationService.validateUserTeam(userInfo);
        if (!teamValidation.isValid()) {
            return itemResponseFactory.createRejectedResponse(teamValidation.getErrorMessage());
        }

        ItemRequestFinalizer.FinalizeResult finalizeResult =
                itemRequestFinalizer.finalizeRequest(userInfo);
        
        return itemResponseFactory.createResponse(finalizeResult.getStatus(), finalizeResult.getMessage());
    }

    private UserInfo readUserInfo(Long userId) {
        return userReader.read(User.class, userId);
    }

    private void logUserInfo(UserInfo userInfo, String operation) {
        log.debug("{} - 사용자 정보: userId={}, teamId={}, userType={}",
                operation, userInfo.getUserId(), userInfo.getTeamId(), userInfo.getUserType());
    }
}