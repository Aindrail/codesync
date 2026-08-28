package com.codesync.session.application;

import com.codesync.session.domain.enumtype.ProgrammingLanguage;
import com.codesync.session.domain.valueobject.ExecutionResult;
import com.codesync.session.domain.valueobject.SourceCode;

public record RecordSubmissionAttemptCommand(
        String sessionId,
        Integer attemptNumber,
        String platformSubmissionId,
        SourceCode sourceCode,
        ProgrammingLanguage language,
        ExecutionResult executionResult
) {
}