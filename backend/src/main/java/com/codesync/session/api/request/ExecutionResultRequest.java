package com.codesync.session.api.request;

import com.codesync.session.domain.enumtype.SubmissionVerdict;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ExecutionResultRequest(
        @NotNull
        SubmissionVerdict verdict,

        @PositiveOrZero
        Integer runtimeInMillis,

        Double runtimePercentile,

        @PositiveOrZero
        Integer memoryInKb,

        Double memoryPercentile,

        @PositiveOrZero
        Integer totalTestCases,

        @PositiveOrZero
        Integer passedTestCases
) {
}