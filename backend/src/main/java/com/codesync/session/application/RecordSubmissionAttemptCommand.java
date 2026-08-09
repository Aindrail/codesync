package com.codesync.session.application;

import com.codesync.session.domain.valueobject.ExecutionResult;
import com.codesync.session.domain.valueobject.Solution;

public record RecordSubmissionAttemptCommand(
        String sessionId,
        Integer attemptNumber,
        String platformSubmissionId,
        Solution solution,
        ExecutionResult executionResult
) {
}
