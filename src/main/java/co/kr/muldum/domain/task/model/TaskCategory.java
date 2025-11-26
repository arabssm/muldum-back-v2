package co.kr.muldum.domain.task.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskCategory {
    FRONTEND("프론트엔드"),
    BACKEND("백엔드"),
    DESIGN("디자인"),
    AI("AI");

    private final String description;
}
