package com.codesync.session.application;

import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.entity.SubmissionAttempt;
import com.codesync.session.domain.enumtype.Platform;
import com.codesync.session.domain.enumtype.ProgrammingLanguage;
import com.codesync.session.domain.enumtype.SubmissionVerdict;
import com.codesync.session.domain.identifier.SessionId;
import com.codesync.session.domain.repository.CodingSessionRepository;
import com.codesync.session.domain.repository.SubmissionAttemptRepository;
import com.codesync.session.domain.valueobject.CodeFingerprint;
import com.codesync.session.domain.valueobject.ExecutionResult;
import com.codesync.session.domain.valueobject.PlatformProblem;
import com.codesync.session.domain.valueobject.Solution;
import com.codesync.session.domain.valueobject.SourceCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordSubmissionAttemptServiceTest {

    @Mock
    private CodingSessionRepository codingSessionRepository;

    @Mock
    private SubmissionAttemptRepository submissionAttemptRepository;

    @InjectMocks
    private RecordSubmissionAttemptService service;

    @Test
    void shouldRecordSubmissionAttemptForExistingSession() {

        String sessionId =
                SessionId.newId().value().toString().toString();

        CodingSession session =
                createSession();

        Solution solution =
                createSolution("return 1;");

        ExecutionResult executionResult =
                createExecutionResult(
                        SubmissionVerdict.WRONG_ANSWER
                );

        SubmissionAttempt savedAttempt =
                new SubmissionAttempt(
                        1,
                        "LC-1001",
                        solution,
                        executionResult,
                        java.time.Instant.now()
                );

        when(codingSessionRepository.findBySessionId(any()))
                .thenReturn(Optional.of(session));

        when(submissionAttemptRepository.save(
                eq(new SessionId(UUID.fromString(sessionId))),
                any(SubmissionAttempt.class)
        ))
                .thenReturn(savedAttempt);

        SubmissionAttempt result =
                service.record(
                        new RecordSubmissionAttemptCommand(
                                sessionId,
                                1,
                                "LC-1001",
                                solution,
                                executionResult
                        )
                );

        assertThat(result)
                .isSameAs(savedAttempt);

        assertThat(result.attemptNumber())
                .isEqualTo(1);

        assertThat(result.platformSubmissionId())
                .isEqualTo("LC-1001");

        assertThat(result.executionResult().verdict())
                .isEqualTo(SubmissionVerdict.WRONG_ANSWER);

        verify(codingSessionRepository)
                .findBySessionId(
                        eq(new SessionId(UUID.fromString(sessionId)))
                );

        verify(submissionAttemptRepository)
                .save(
                        eq(new SessionId(UUID.fromString(sessionId))),
                        any(SubmissionAttempt.class)
                );
    }

    @Test
    void shouldAllowMultipleAttemptsForSameSession() {

        String sessionId =
                SessionId.newId().value().toString().toString();

        CodingSession session =
                createSession();

        when(codingSessionRepository.findBySessionId(any()))
                .thenReturn(Optional.of(session));

        SubmissionAttempt attempt1 =
                createAttempt(
                        1,
                        "LC-2001",
                        SubmissionVerdict.WRONG_ANSWER
                );

        SubmissionAttempt attempt2 =
                createAttempt(
                        2,
                        "LC-2002",
                        SubmissionVerdict.ACCEPTED
                );

        when(submissionAttemptRepository.save(
                any(),
                any(SubmissionAttempt.class)
        ))
                .thenReturn(attempt1)
                .thenReturn(attempt2);

        SubmissionAttempt first =
                service.record(
                        new RecordSubmissionAttemptCommand(
                                sessionId,
                                1,
                                "LC-2001",
                                attempt1.solution(),
                                attempt1.executionResult()
                        )
                );

        SubmissionAttempt second =
                service.record(
                        new RecordSubmissionAttemptCommand(
                                sessionId,
                                2,
                                "LC-2002",
                                attempt2.solution(),
                                attempt2.executionResult()
                        )
                );

        assertThat(first.attemptNumber())
                .isEqualTo(1);

        assertThat(first.executionResult().verdict())
                .isEqualTo(SubmissionVerdict.WRONG_ANSWER);

        assertThat(second.attemptNumber())
                .isEqualTo(2);

        assertThat(second.executionResult().verdict())
                .isEqualTo(SubmissionVerdict.ACCEPTED);

        verify(submissionAttemptRepository, times(2))
                .save(
                        eq(new SessionId(UUID.fromString(sessionId))),
                        any(SubmissionAttempt.class)
                );
    }

    @Test
    void shouldRejectAttemptWhenSessionDoesNotExist() {

        String sessionId =
                SessionId.newId().value().toString().toString();

        when(codingSessionRepository.findBySessionId(any()))
                .thenReturn(Optional.empty());

        Solution solution =
                createSolution("return 1;");

        ExecutionResult executionResult =
                createExecutionResult(
                        SubmissionVerdict.WRONG_ANSWER
                );

        assertThatThrownBy(() ->
                service.record(
                        new RecordSubmissionAttemptCommand(
                                sessionId,
                                1,
                                "LC-3001",
                                solution,
                                executionResult
                        )
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "Coding session not found."
                );

        verify(submissionAttemptRepository, never())
                .save(any(), any());
    }

    @Test
    void shouldRejectBlankSessionId() {

        assertThatThrownBy(() ->
                service.record(
                        new RecordSubmissionAttemptCommand(
                                "",
                                1,
                                "LC-4001",
                                createSolution("return 1;"),
                                createExecutionResult(
                                        SubmissionVerdict.ACCEPTED
                                )
                        )
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Session ID cannot be blank."
                );

        verifyNoInteractions(codingSessionRepository);
        verifyNoInteractions(submissionAttemptRepository);
    }

    @Test
    void shouldRejectInvalidAttemptNumber() {

        assertThatThrownBy(() ->
                service.record(
                        new RecordSubmissionAttemptCommand(
                                SessionId.newId().value().toString(),
                                0,
                                "LC-5001",
                                createSolution("return 1;"),
                                createExecutionResult(
                                        SubmissionVerdict.WRONG_ANSWER
                                )
                        )
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Attempt number must be greater than zero."
                );

        verifyNoInteractions(codingSessionRepository);
        verifyNoInteractions(submissionAttemptRepository);
    }

    @Test
    void shouldRejectMissingSolution() {

        assertThatThrownBy(() ->
                service.record(
                        new RecordSubmissionAttemptCommand(
                                SessionId.newId().value().toString(),
                                1,
                                "LC-6001",
                                null,
                                createExecutionResult(
                                        SubmissionVerdict.ACCEPTED
                                )
                        )
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Solution cannot be null."
                );

        verifyNoInteractions(codingSessionRepository);
        verifyNoInteractions(submissionAttemptRepository);
    }

    @Test
    void shouldRejectMissingExecutionResult() {

        assertThatThrownBy(() ->
                service.record(
                        new RecordSubmissionAttemptCommand(
                                SessionId.newId().value().toString(),
                                1,
                                "LC-7001",
                                createSolution("return 1;"),
                                null
                        )
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Execution result cannot be null."
                );

        verifyNoInteractions(codingSessionRepository);
        verifyNoInteractions(submissionAttemptRepository);
    }

    private CodingSession createSession() {

        PlatformProblem problem =
                new PlatformProblem(
                        "1",
                        "1",
                        "LEETCODE-1",
                        "Two Sum",
                        "two-sum",
                        "https://leetcode.com/problems/two-sum/",
                        Platform.LEETCODE,
                        "Easy",
                        java.util.Set.of("Array"),
                        false,
                        "1"
                );

        return CodingSession.start(problem);
    }

    private SubmissionAttempt createAttempt(
            int attemptNumber,
            String platformSubmissionId,
            SubmissionVerdict verdict) {

        return new SubmissionAttempt(
                attemptNumber,
                platformSubmissionId,
                createSolution(
                        "return " + attemptNumber + ";"
                ),
                createExecutionResult(verdict),
                java.time.Instant.now()
        );
    }

    private Solution createSolution(
            String sourceCode) {

        return new Solution(
                new SourceCode(sourceCode),
                ProgrammingLanguage.JAVA,
                new CodeFingerprint(
                        "fingerprint-" + sourceCode
                )
        );
    }

    private ExecutionResult createExecutionResult(
            SubmissionVerdict verdict) {

        return new ExecutionResult(
                verdict,
                100,
                90.0,
                1024,
                85.0,
                10,
                verdict == SubmissionVerdict.ACCEPTED
                        ? 10
                        : 5
        );
    }
}