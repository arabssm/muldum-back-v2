package co.kr.muldum.domain.item.service;

import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.user.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ItemValidationService {

    public void validateTeamInfo(UserInfo userInfo) {
        if (userInfo.getTeamId() == null) {
            log.warn("물품 신청 실패 - teamId가 null입니다. userId={}", userInfo.getUserId());
            throw new IllegalArgumentException("팀 정보가 없습니다. 팀에 소속되어야 물품을 신청할 수 있습니다.");
        }
    }

    public void validateProductLink(TempItemRequestDto requestDto) {
        if (requestDto.getProductLink() == null || requestDto.getProductLink().isBlank()) {
            log.warn("물품 신청 실패 - productLink가 비어있습니다");
            throw new IllegalArgumentException("상품 링크가 유효하지 않습니다.");
        }
    }
}