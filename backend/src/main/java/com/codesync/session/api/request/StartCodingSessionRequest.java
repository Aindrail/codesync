package com.codesync.session.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StartCodingSessionRequest(
        @NotNull
        Long userId,

        @NotBlank
        String platform,

        @NotBlank
        String platformProblemId
) {
}