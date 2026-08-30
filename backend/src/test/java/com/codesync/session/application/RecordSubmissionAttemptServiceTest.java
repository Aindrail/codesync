package com.codesync.session.application;

import com.codesync.common.exception.DuplicateSubmissionException;
import com.codesync.common.exception.ResourceNotFoundException;
import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.entity.SubmissionAttempt;
import com.codesync.session.domain.entity.User;
import com.codesync.session.domain.enumtype.Platform;
import com.codesync.session.domain.enumtype.ProgrammingLanguage;
import com.codesync.session.domain.enumtype.SubmissionVerdict;
import com.codesync.session.domain.identifier.SessionId;
import com.codesync.session.domain.repository.CodingSessionRepository;
import com.codesync.session.domain.repository.SubmissionAttemptRepository;
import com.codesync.session.domain.service.CodeFingerprintGenerator;
import com.codesync.session.domain.valueobject.CodeFingerprint;
import com.codesync.session.domain.valueobject.ExecutionResult;
import com.codesync.session.domain.valueobject.PlatformProblem;
import com.codesync.session.domain.valueobject.Solution;
import com.codesync.session.domain.valueobject.SourceCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
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

    @Mock
    private CodeFingerprintGenerator codeFingerprintGenerator;

    @InjectMocks
    private RecordSubmissionAttemptService service;

    @Test
    void shouldRecordSubmissionAttemptForExistingSession() {

        String sessionId =
                SessionId.newId().value().toString();

        CodingSession session =
                createSession(
                        User.reconstitute(
                                1L,
                                "github-user-1"
                        )
                );

        SourceCode sourceCode =
                new SourceCode("return 1;");

        CodeFingerprint fingerprint =
                new CodeFingerprint("fingerprint-1");

        ExecutionResult executionResult =
                createExecutionResult(
                        SubmissionVerdict.WRONG_ANSWER
                );

        Solution solution =
                new Solution(
                        sourceCode,
                        ProgrammingLanguage.JAVA,
                        fingerprint
                );

        SubmissionAttempt savedAttempt =
                new SubmissionAttempt(
                        1,
                        "LC-1001",
                        solution,
                        executionResult,
                        Instant.now()
                );

        when(codingSessionRepository.findBySessionId(any()))
                .thenReturn(Optional.of(session));

        when(codeFingerprintGenerator.generate(
                sourceCode,
                ProgrammingLanguage.JAVA
        ))
                .thenReturn(fingerprint);

        when(submissionAttemptRepository
                .existsByUserIdAndSolutionFingerprint(
                        1L,
                        fingerprint.value()
                ))
                .thenReturn(false);

        when(submissionAttemptRepository.save(
                eq(new SessionId(UUID.fromString(sessionId))),
                any(SubmissionAttempt.class)
        ))
                .thenReturn(savedAttempt);

        SubmissionAttempt result =
                service.record(
                        new RecordSubmissionAttemptCommand(
                                1L,
                                sessionId,
                                1,
                                "LC-1001",
                                sourceCode,
                                ProgrammingLanguage.JAVA,
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
                .isEqualTo(
                        SubmissionVerdict.WRONG_ANSWER
                );

        verify(codeFingerprintGenerator)
                .generate(
                        sourceCode,
                        ProgrammingLanguage.JAVA
                );

        verify(submissionAttemptRepository)
                .existsByUserIdAndSolutionFingerprint(
                        1L,
                        fingerprint.value()
                );

        verify(submissionAttemptRepository)
                .save(
                        eq(
                                new SessionId(
                                        UUID.fromString(sessionId)
                                )
                        ),
                        any(SubmissionAttempt.class)
                );
    }

    @Test
    void shouldAllowMultipleAttemptsForSameSessionWithDifferentFingerprints() {

        String sessionId =
                SessionId.newId().value().toString();

        CodingSession session =
                createSession(
                        User.reconstitute(
                                1L,
                                "github-user-1"
                        )
                );

        SourceCode sourceCode1 =
                new SourceCode("return 1;");

        SourceCode sourceCode2 =
                new SourceCode("return 2;");

        CodeFingerprint fingerprint1 =
                new CodeFingerprint("fingerprint-1");

        CodeFingerprint fingerprint2 =
                new CodeFingerprint("fingerprint-2");

        ExecutionResult executionResult1 =
                createExecutionResult(
                        SubmissionVerdict.WRONG_ANSWER
                );

        ExecutionResult executionResult2 =
                createExecutionResult(
                        SubmissionVerdict.ACCEPTED
                );

        SubmissionAttempt attempt1 =
                new SubmissionAttempt(
                        1,
                        "LC-2001",
                        new Solution(
                                sourceCode1,
                                ProgrammingLanguage.JAVA,
                                fingerprint1
                        ),
                        executionResult1,
                        Instant.now()
                );

        SubmissionAttempt attempt2 =
                new SubmissionAttempt(
                        2,
                        "LC-2002",
                        new Solution(
                                sourceCode2,
                                ProgrammingLanguage.JAVA,
                                fingerprint2
                        ),
                        executionResult2,
                        Instant.now()
                );

        when(codingSessionRepository.findBySessionId(any()))
                .thenReturn(Optional.of(session));

        when(codeFingerprintGenerator.generate(
                sourceCode1,
                ProgrammingLanguage.JAVA
        ))
                .thenReturn(fingerprint1);

        when(codeFingerprintGenerator.generate(
                sourceCode2,
                ProgrammingLanguage.JAVA
        ))
                .thenReturn(fingerprint2);

        when(submissionAttemptRepository
                .existsByUserIdAndSolutionFingerprint(
                        1L,
                        fingerprint1.value()
                ))
                .thenReturn(false);

        when(submissionAttemptRepository
                .existsByUserIdAndSolutionFingerprint(
                        1L,
                        fingerprint2.value()
                ))
                .thenReturn(false);

        when(submissionAttemptRepository.save(
                any(),
                any(SubmissionAttempt.class)
        ))
                .thenReturn(attempt1)
                .thenReturn(attempt2);

        SubmissionAttempt first =
                service.record(
                        new RecordSubmissionAttemptCommand(
                                1L,
                                sessionId,
                                1,
                                "LC-2001",
                                sourceCode1,
                                ProgrammingLanguage.JAVA,
                                executionResult1
                        )
                );

        SubmissionAttempt second =
                service.record(
                        new RecordSubmissionAttemptCommand(
                                1L,
                                sessionId,
                                2,
                                "LC-2002",
                                sourceCode2,
                                ProgrammingLanguage.JAVA,
                                executionResult2
                        )
                );

        assertThat(first.attemptNumber())
                .isEqualTo(1);

        assertThat(second.attemptNumber())
                .isEqualTo(2);

        verify(submissionAttemptRepository, times(2))
                .save(
                        eq(
                                new SessionId(
                                        UUID.fromString(sessionId)
                                )
                        ),
                        any(SubmissionAttempt.class)
                );
    }

    @Test
    void shouldRejectSameFingerprintForSameUser() {

        String sessionId =
                SessionId.newId().value().toString();

        User user =
                User.reconstitute(
                        1L,
                        "github-user-1"
                );

        CodingSession session =
                createSession(user);

        SourceCode sourceCode =
                new SourceCode(
                        "int add(int a, int b) { return a + b; }"
                );

        CodeFingerprint fingerprint =
                new CodeFingerprint(
                        "duplicate-fingerprint"
                );

        ExecutionResult executionResult =
                createExecutionResult(
                        SubmissionVerdict.ACCEPTED
                );

        when(codingSessionRepository.findBySessionId(any()))
                .thenReturn(Optional.of(session));

        when(codeFingerprintGenerator.generate(
                sourceCode,
                ProgrammingLanguage.JAVA
        ))
                .thenReturn(fingerprint);

        when(submissionAttemptRepository
                .existsByUserIdAndSolutionFingerprint(
                        user.id(),
                        fingerprint.value()
                ))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.record(
                        new RecordSubmissionAttemptCommand(
                                1L,
                                sessionId,
                                1,
                                "LC-3001",
                                sourceCode,
                                ProgrammingLanguage.JAVA,
                                executionResult
                        )
                )
        )
                .isInstanceOf(
                        DuplicateSubmissionException.class
                )
                .hasMessage(
                        "This solution has already been submitted by this user."
                );

        verify(submissionAttemptRepository)
                .existsByUserIdAndSolutionFingerprint(
                        1L,
                        fingerprint.value()
                );

        verify(submissionAttemptRepository, never())
                .save(
                        any(),
                        any()
                );
    }

    @Test
    void shouldAllowSameFingerprintForDifferentUsers() {

        String sessionId =
                SessionId.newId().value().toString();

        User secondUser =
                User.reconstitute(
                        2L,
                        "github-user-2"
                );

        CodingSession secondUserSession =
                createSession(secondUser);

        SourceCode sourceCode =
                new SourceCode(
                        "int add(int a, int b) { return a + b; }"
                );

        CodeFingerprint fingerprint =
                new CodeFingerprint(
                        "shared-fingerprint"
                );

        ExecutionResult executionResult =
                createExecutionResult(
                        SubmissionVerdict.ACCEPTED
                );

        SubmissionAttempt savedAttempt =
                new SubmissionAttempt(
                        1,
                        "LC-4001",
                        new Solution(
                                sourceCode,
                                ProgrammingLanguage.JAVA,
                                fingerprint
                        ),
                        executionResult,
                        Instant.now()
                );

        when(codingSessionRepository.findBySessionId(any()))
                .thenReturn(
                        Optional.of(secondUserSession)
                );

        when(codeFingerprintGenerator.generate(
                sourceCode,
                ProgrammingLanguage.JAVA
        ))
                .thenReturn(fingerprint);

        when(submissionAttemptRepository
                .existsByUserIdAndSolutionFingerprint(
                        2L,
                        fingerprint.value()
                ))
                .thenReturn(false);

        when(submissionAttemptRepository.save(
                any(),
                any(SubmissionAttempt.class)
        ))
                .thenReturn(savedAttempt);

        SubmissionAttempt result =
                service.record(
                        new RecordSubmissionAttemptCommand(
                                2L,
                                sessionId,
                                1,
                                "LC-4001",
                                sourceCode,
                                ProgrammingLanguage.JAVA,
                                executionResult
                        )
                );

        assertThat(result)
                .isSameAs(savedAttempt);

        verify(submissionAttemptRepository)
                .existsByUserIdAndSolutionFingerprint(
                        2L,
                        fingerprint.value()
                );

        verify(submissionAttemptRepository)
                .save(
                        any(),
                        any(SubmissionAttempt.class)
                );
    }

    @Test
    void shouldRejectAttemptWhenSessionDoesNotExist() {

        String sessionId =
                SessionId.newId().value().toString();

        SourceCode sourceCode =
                new SourceCode("return 1;");

        ExecutionResult executionResult =
                createExecutionResult(
                        SubmissionVerdict.WRONG_ANSWER
                );

        when(codingSessionRepository.findBySessionId(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.record(
                        new RecordSubmissionAttemptCommand(
                                1L,
                                sessionId,
                                1,
                                "LC-5001",
                                sourceCode,
                                ProgrammingLanguage.JAVA,
                                executionResult
                        )
                )
        )
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage(
                        "Coding session not found."
                );

        verifyNoInteractions(
                codeFingerprintGenerator
        );

        verify(submissionAttemptRepository, never())
                .save(any(), any());
    }

    @Test
    void shouldRejectAttemptWhenSessionBelongsToDifferentUser() {

        String sessionId =
                SessionId.newId().value().toString();

        User sessionOwner =
                User.reconstitute(
                        2L,
                        "github-user-2"
                );

        CodingSession session =
                createSession(sessionOwner);

        SourceCode sourceCode =
                new SourceCode("return 1;");

        ExecutionResult executionResult =
                createExecutionResult(
                        SubmissionVerdict.ACCEPTED
                );

        when(codingSessionRepository.findBySessionId(any()))
                .thenReturn(Optional.of(session));

        assertThatThrownBy(() ->
                service.record(
                        new RecordSubmissionAttemptCommand(
                                1L,
                                sessionId,
                                1,
                                "LC-5002",
                                sourceCode,
                                ProgrammingLanguage.JAVA,
                                executionResult
                        )
                )
        )
                .isInstanceOf(
                        ResourceNotFoundException.class
                )
                .hasMessage(
                        "Coding session not found."
                );

        verifyNoInteractions(
                codeFingerprintGenerator
        );

        verify(submissionAttemptRepository, never())
                .save(any(), any());
    }

    @Test
    void shouldRejectBlankSessionId() {

        assertThatThrownBy(() ->
                service.record(
                        new RecordSubmissionAttemptCommand(
                                1L,
                                "",
                                1,
                                "LC-6001",
                                new SourceCode("return 1;"),
                                ProgrammingLanguage.JAVA,
                                createExecutionResult(
                                        SubmissionVerdict.ACCEPTED
                                )
                        )
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "Session ID cannot be blank."
                );

        verifyNoInteractions(
                codingSessionRepository,
                submissionAttemptRepository,
                codeFingerprintGenerator
        );
    }

    @Test
    void shouldRejectInvalidAttemptNumber() {

        assertThatThrownBy(() ->
                service.record(
                        new RecordSubmissionAttemptCommand(
                                1L,
                                SessionId.newId().value().toString(),
                                0,
                                "LC-7001",
                                new SourceCode("return 1;"),
                                ProgrammingLanguage.JAVA,
                                createExecutionResult(
                                        SubmissionVerdict.WRONG_ANSWER
                                )
                        )
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "Attempt number must be greater than zero."
                );

        verifyNoInteractions(
                codingSessionRepository,
                submissionAttemptRepository,
                codeFingerprintGenerator
        );
    }

    @Test
    void shouldRejectMissingSourceCode() {

        assertThatThrownBy(() ->
                service.record(
                        new RecordSubmissionAttemptCommand(
                                1L,
                                SessionId.newId().value().toString(),
                                1,
                                "LC-8001",
                                null,
                                ProgrammingLanguage.JAVA,
                                createExecutionResult(
                                        SubmissionVerdict.ACCEPTED
                                )
                        )
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "Source code cannot be null."
                );

        verifyNoInteractions(
                codingSessionRepository,
                submissionAttemptRepository,
                codeFingerprintGenerator
        );
    }

    @Test
    void shouldRejectMissingProgrammingLanguage() {

        assertThatThrownBy(() ->
                service.record(
                        new RecordSubmissionAttemptCommand(
                                1L,
                                SessionId.newId().value().toString(),
                                1,
                                "LC-9001",
                                new SourceCode("return 1;"),
                                null,
                                createExecutionResult(
                                        SubmissionVerdict.ACCEPTED
                                )
                        )
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "Programming language cannot be null."
                );

        verifyNoInteractions(
                codingSessionRepository,
                submissionAttemptRepository,
                codeFingerprintGenerator
        );
    }

    @Test
    void shouldRejectMissingExecutionResult() {

        assertThatThrownBy(() ->
                service.record(
                        new RecordSubmissionAttemptCommand(
                                1L,
                                SessionId.newId().value().toString(),
                                1,
                                "LC-10001",
                                new SourceCode("return 1;"),
                                ProgrammingLanguage.JAVA,
                                null
                        )
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "Execution result cannot be null."
                );

        verifyNoInteractions(
                codingSessionRepository,
                submissionAttemptRepository,
                codeFingerprintGenerator
        );
    }

    @Test
    void shouldSaveSolutionWithGeneratedFingerprint() {

        String sessionId =
                SessionId.newId().value().toString();

        User user =
                User.reconstitute(
                        1L,
                        "github-user-1"
                );

        CodingSession session =
                createSession(user);

        SourceCode sourceCode =
                new SourceCode(
                        "int add(int a, int b) { return a + b; }"
                );

        CodeFingerprint generatedFingerprint =
                new CodeFingerprint(
                        "generated-fingerprint"
                );

        ExecutionResult executionResult =
                createExecutionResult(
                        SubmissionVerdict.ACCEPTED
                );

        when(codingSessionRepository.findBySessionId(any()))
                .thenReturn(Optional.of(session));

        when(codeFingerprintGenerator.generate(
                sourceCode,
                ProgrammingLanguage.JAVA
        ))
                .thenReturn(generatedFingerprint);

        when(submissionAttemptRepository
                .existsByUserIdAndSolutionFingerprint(
                        user.id(),
                        generatedFingerprint.value()
                ))
                .thenReturn(false);

        when(submissionAttemptRepository.save(
                any(),
                any(SubmissionAttempt.class)
        ))
                .thenAnswer(invocation ->
                        invocation.getArgument(1)
                );

        service.record(
                new RecordSubmissionAttemptCommand(
                        1L,
                        sessionId,
                        1,
                        "LC-11001",
                        sourceCode,
                        ProgrammingLanguage.JAVA,
                        executionResult
                )
        );

        ArgumentCaptor<SubmissionAttempt> captor =
                ArgumentCaptor.forClass(
                        SubmissionAttempt.class
                );

        verify(submissionAttemptRepository)
                .save(
                        any(),
                        captor.capture()
                );

        SubmissionAttempt savedAttempt =
                captor.getValue();

        assertThat(
                savedAttempt.solution().fingerprint()
        )
                .isEqualTo(generatedFingerprint);

        assertThat(
                savedAttempt.solution().sourceCode()
        )
                .isEqualTo(sourceCode);

        assertThat(
                savedAttempt.solution().language()
        )
                .isEqualTo(ProgrammingLanguage.JAVA);
    }

    private CodingSession createSession(User user) {

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

        return CodingSession.start(
                user,
                problem
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