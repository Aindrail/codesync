package com.codesync.session.api.controller;

import com.codesync.common.security.CodeSyncOAuth2User;
import com.codesync.session.api.request.ExecutionResultRequest;
import com.codesync.session.api.request.RecordSubmissionAttemptRequest;
import com.codesync.session.api.request.StartCodingSessionRequest;
import com.codesync.session.application.RecordSubmissionAttemptCommand;
import com.codesync.session.application.RecordSubmissionAttemptUseCase;
import com.codesync.session.application.StartCodingSessionCommand;
import com.codesync.session.application.StartCodingSessionUseCase;
import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.entity.SubmissionAttempt;
import com.codesync.session.domain.valueobject.ExecutionResult;
import com.codesync.session.domain.valueobject.SourceCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/sessions")
public class CodingSessionController {

    private final StartCodingSessionUseCase startCodingSessionUseCase;
    private final RecordSubmissionAttemptUseCase recordSubmissionAttemptUseCase;
    private ExecutionResult toExecutionResult(ExecutionResultRequest request) {
        return new ExecutionResult(
                request.verdict(),
                request.runtimeInMillis(),
                request.runtimePercentile(),
                request.memoryInKb(),
                request.memoryPercentile(),
                request.totalTestCases(),
                request.passedTestCases()
        );
    }
    public CodingSessionController(
            StartCodingSessionUseCase startCodingSessionUseCase,
            RecordSubmissionAttemptUseCase recordSubmissionAttemptUseCase
    ) {
        this.startCodingSessionUseCase = startCodingSessionUseCase;
        this.recordSubmissionAttemptUseCase = recordSubmissionAttemptUseCase;
    }

    @PostMapping
    public ResponseEntity<CodingSessionResponse> startSession(
            @AuthenticationPrincipal CodeSyncOAuth2User authenticatedUser,
            @Valid @RequestBody StartCodingSessionRequest request
    ) {
        StartCodingSessionCommand command = new StartCodingSessionCommand(
                authenticatedUser.userId(),
                request.platform(),
                request.platformProblemId()
        );

        CodingSession session = startCodingSessionUseCase.start(command);

        CodingSessionResponse response = new CodingSessionResponse(
                session.sessionId().value().toString(),
                session.status().name(),
                session.startedAt()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/{sessionId}/attempts")
    public ResponseEntity<SubmissionAttemptResponse> recordSubmissionAttempt(
            @AuthenticationPrincipal CodeSyncOAuth2User authenticatedUser,
            @PathVariable String sessionId,
            @Valid @RequestBody RecordSubmissionAttemptRequest request
    ) {
        RecordSubmissionAttemptCommand command =
                new RecordSubmissionAttemptCommand(
                        authenticatedUser.userId(),
                        sessionId,
                        request.attemptNumber(),
                        request.platformSubmissionId(),
                        new SourceCode(request.sourceCode()),
                        request.language(),
                        toExecutionResult(request.executionResult())
                );

        SubmissionAttempt attempt =
                recordSubmissionAttemptUseCase.record(command);

        SubmissionAttemptResponse response =
                new SubmissionAttemptResponse(
                        attempt.attemptNumber(),
                        attempt.platformSubmissionId(),
                        attempt.solution().language().name(),
                        new ExecutionResultResponse(
                                attempt.executionResult().verdict().name(),
                                attempt.executionResult().runtimeInMillis(),
                                attempt.executionResult().runtimePercentile(),
                                attempt.executionResult().memoryInKb(),
                                attempt.executionResult().memoryPercentile(),
                                attempt.executionResult().totalTestCases(),
                                attempt.executionResult().passedTestCases()
                        ),
                        attempt.submittedAt()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    public record CodingSessionResponse(
            String sessionId,
            String status,
            java.time.Instant startedAt
    ) {
    }

    public record SubmissionAttemptResponse(
            Integer attemptNumber,
            String platformSubmissionId,
            String language,
            ExecutionResultResponse executionResult,
            java.time.Instant submittedAt
    ) {
    }

    public record ExecutionResultResponse(
            String verdict,
            Integer runtimeInMillis,
            Double runtimePercentile,
            Integer memoryInKb,
            Double memoryPercentile,
            Integer totalTestCases,
            Integer passedTestCases
    ) {
    }
}