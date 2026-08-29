package com.codesync.session.api.request;

import com.codesync.session.domain.enumtype.ProgrammingLanguage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RecordSubmissionAttemptRequest(
        @NotNull
        @Positive
        Integer attemptNumber,

        String platformSubmissionId,

        @NotBlank
        String sourceCode,

        @NotNull
        ProgrammingLanguage language,

        @NotNull
        @Valid
        ExecutionResultRequest executionResult
) {
}