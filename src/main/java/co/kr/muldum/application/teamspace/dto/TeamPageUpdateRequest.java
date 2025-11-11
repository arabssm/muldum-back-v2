package co.kr.muldum.application.teamspace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record TeamPageUpdateRequest(
        @JsonProperty("name") String name,
        @NotBlank String content
) {}
