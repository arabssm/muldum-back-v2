package co.kr.muldum.application.teamspace.dto;

import jakarta.validation.constraints.NotBlank;

public record TeamPageUpdateRequest(
        String name,
        @NotBlank String content
) {}
