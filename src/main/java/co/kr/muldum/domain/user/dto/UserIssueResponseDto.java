package co.kr.muldum.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserIssueResponseDto {
    private String message;
    private int updatedCount;
}