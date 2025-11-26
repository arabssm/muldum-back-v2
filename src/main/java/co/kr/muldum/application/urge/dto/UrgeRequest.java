package co.kr.muldum.application.urge.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UrgeRequest {
    private final Long taskId;
}
