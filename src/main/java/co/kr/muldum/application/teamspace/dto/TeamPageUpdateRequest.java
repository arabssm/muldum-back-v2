package co.kr.muldum.application.teamspace.dto;

import jakarta.validation.constraints.NotBlank;

public record TeamPageUpdateRequest(
        @NotBlank String content
) {}
