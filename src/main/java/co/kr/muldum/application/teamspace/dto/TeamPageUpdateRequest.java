package co.kr.muldum.application.teamspace.dto;

import jakarta.validation.constraints.NotBlank;

public record TeamPageUpdateRequest(
        @NotBlank String teamName,
        @NotBlank String content
) {}
