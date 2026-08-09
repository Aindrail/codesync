package com.codesync.session.persistence.repository;

import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.entity.SubmissionAttempt;
import com.codesync.session.domain.enumtype.Platform;
import com.codesync.session.domain.enumtype.ProgrammingLanguage;
import com.codesync.session.domain.enumtype.SubmissionVerdict;
import com.codesync.session.domain.repository.CodingSessionRepository;
import com.codesync.session.domain.repository.PlatformProblemRepository;
import com.codesync.session.domain.repository.SubmissionAttemptRepository;
import com.codesync.session.domain.valueobject.CodeFingerprint;
import com.codesync.session.domain.valueobject.ExecutionResult;
import com.codesync.session.domain.valueobject.PlatformProblem;
import com.codesync.session.domain.valueobject.Solution;
import com.codesync.session.domain.valueobject.SourceCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SubmissionAttemptRepositoryIntegrationTest {

    @Autowired
    private SubmissionAttemptRepository submissionAttemptRepository;

    @Autowired
    private CodingSessionRepository codingSessionRepository;

    @Autowired
    private PlatformProblemRepository platformProblemRepository;

    @Test
    void shouldSaveAndRetrieveSubmissionAttempt() {

        CodingSession session =
                createCodingSession("3001");

        SubmissionAttempt attempt =
                createAttempt(
                        1,
                        "LC-SUB-001",
                        "return 1;",
                        "fingerprint-001",
                        SubmissionVerdict.WRONG_ANSWER
                );

        SubmissionAttempt saved =
                submissionAttemptRepository.save(
                        session.sessionId(),
                        attempt
                );

        assertThat(saved).isNotNull();
        assertThat(saved.attemptNumber())
                .isEqualTo(1);
        assertThat(saved.platformSubmissionId())
                .isEqualTo("LC-SUB-001");
        assertThat(saved.solution().sourceCode().value())
                .isEqualTo("return 1;");
        assertThat(saved.solution().fingerprint().value())
                .isEqualTo("fingerprint-001");
        assertThat(saved.executionResult().verdict())
                .isEqualTo(SubmissionVerdict.WRONG_ANSWER);

        SubmissionAttempt retrieved =
                submissionAttemptRepository
                        .findBySessionIdAndAttemptNumber(
                                session.sessionId(),
                                1
                        )
                        .orElseThrow();

        assertThat(retrieved.attemptNumber())
                .isEqualTo(1);
        assertThat(retrieved.platformSubmissionId())
                .isEqualTo("LC-SUB-001");
        assertThat(retrieved.solution().sourceCode().value())
                .isEqualTo("return 1;");
        assertThat(retrieved.executionResult().verdict())
                .isEqualTo(SubmissionVerdict.WRONG_ANSWER);
    }

    @Test
    void shouldPreserveMultipleAttemptsInOrder() {

        CodingSession session =
                createCodingSession("3002");

        SubmissionAttempt attempt1 =
                createAttempt(
                        1,
                        "LC-SUB-101",
                        "return 1;",
                        "fingerprint-101",
                        SubmissionVerdict.WRONG_ANSWER
                );

        SubmissionAttempt attempt2 =
                createAttempt(
                        2,
                        "LC-SUB-102",
                        "return 2;",
                        "fingerprint-102",
                        SubmissionVerdict.RUNTIME_ERROR
                );

        SubmissionAttempt attempt3 =
                createAttempt(
                        3,
                        "LC-SUB-103",
                        "return 3;",
                        "fingerprint-103",
                        SubmissionVerdict.ACCEPTED
                );

        submissionAttemptRepository.save(
                session.sessionId(),
                attempt1
        );

        submissionAttemptRepository.save(
                session.sessionId(),
                attempt2
        );

        submissionAttemptRepository.save(
                session.sessionId(),
                attempt3
        );

        List<SubmissionAttempt> attempts =
                submissionAttemptRepository
                        .findAllBySessionId(
                                session.sessionId()
                        );

        assertThat(attempts)
                .hasSize(3);

        assertThat(attempts.get(0).attemptNumber())
                .isEqualTo(1);

        assertThat(attempts.get(1).attemptNumber())
                .isEqualTo(2);

        assertThat(attempts.get(2).attemptNumber())
                .isEqualTo(3);

        assertThat(attempts.get(0).executionResult().verdict())
                .isEqualTo(SubmissionVerdict.WRONG_ANSWER);

        assertThat(attempts.get(1).executionResult().verdict())
                .isEqualTo(SubmissionVerdict.RUNTIME_ERROR);

        assertThat(attempts.get(2).executionResult().verdict())
                .isEqualTo(SubmissionVerdict.ACCEPTED);
    }

    @Test
    void shouldKeepAttemptsIndependent() {

        CodingSession session =
                createCodingSession("3003");

        SubmissionAttempt attempt1 =
                createAttempt(
                        1,
                        "LC-SUB-201",
                        "return wrong;",
                        "fingerprint-201",
                        SubmissionVerdict.WRONG_ANSWER
                );

        SubmissionAttempt attempt2 =
                createAttempt(
                        2,
                        "LC-SUB-202",
                        "return correct;",
                        "fingerprint-202",
                        SubmissionVerdict.ACCEPTED
                );

        submissionAttemptRepository.save(
                session.sessionId(),
                attempt1
        );

        submissionAttemptRepository.save(
                session.sessionId(),
                attempt2
        );

        SubmissionAttempt retrievedAttempt1 =
                submissionAttemptRepository
                        .findBySessionIdAndAttemptNumber(
                                session.sessionId(),
                                1
                        )
                        .orElseThrow();

        SubmissionAttempt retrievedAttempt2 =
                submissionAttemptRepository
                        .findBySessionIdAndAttemptNumber(
                                session.sessionId(),
                                2
                        )
                        .orElseThrow();

        assertThat(
                retrievedAttempt1.solution().fingerprint().value()
        ).isEqualTo("fingerprint-201");

        assertThat(
                retrievedAttempt2.solution().fingerprint().value()
        ).isEqualTo("fingerprint-202");

        assertThat(
                retrievedAttempt1.executionResult().verdict()
        ).isEqualTo(SubmissionVerdict.WRONG_ANSWER);

        assertThat(
                retrievedAttempt2.executionResult().verdict()
        ).isEqualTo(SubmissionVerdict.ACCEPTED);
    }

    @Test
    void shouldAllowSameAttemptNumberForDifferentSessions() {

        CodingSession session1 =
                createCodingSession("3004");

        CodingSession session2 =
                createCodingSession("3005");

        SubmissionAttempt attemptForSession1 =
                createAttempt(
                        1,
                        "LC-SUB-301",
                        "return 10;",
                        "fingerprint-301",
                        SubmissionVerdict.WRONG_ANSWER
                );

        SubmissionAttempt attemptForSession2 =
                createAttempt(
                        1,
                        "LC-SUB-302",
                        "return 20;",
                        "fingerprint-302",
                        SubmissionVerdict.ACCEPTED
                );

        submissionAttemptRepository.save(
                session1.sessionId(),
                attemptForSession1
        );

        submissionAttemptRepository.save(
                session2.sessionId(),
                attemptForSession2
        );

        assertThat(
                submissionAttemptRepository
                        .findBySessionIdAndAttemptNumber(
                                session1.sessionId(),
                                1
                        )
        ).isPresent();

        assertThat(
                submissionAttemptRepository
                        .findBySessionIdAndAttemptNumber(
                                session2.sessionId(),
                                1
                        )
        ).isPresent();
    }

    @Test
    void shouldRejectAttemptForNonExistingSession() {



        SubmissionAttempt attempt =
                createAttempt(
                        1,
                        "LC-SUB-401",
                        "return 1;",
                        "fingerprint-401",
                        SubmissionVerdict.WRONG_ANSWER
                );

        // Deliberately use a different session ID that was never persisted.
        var nonExistingSessionId =
                com.codesync.session.domain.identifier.SessionId.newId();

        assertThatThrownBy(() ->
                submissionAttemptRepository.save(
                        nonExistingSessionId,
                        attempt
                )
        )
                .isInstanceOf(
                        org.springframework.dao.InvalidDataAccessApiUsageException.class
                )
                .hasMessageContaining(
                        "Coding session must exist"
                );
    }

    @Test
    void shouldRejectInvalidAttemptNumber() {

        CodingSession session =
                createCodingSession("3007");

        assertThatThrownBy(() ->
                createAttempt(
                        0,
                        "LC-SUB-501",
                        "return 1;",
                        "fingerprint-501",
                        SubmissionVerdict.WRONG_ANSWER
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(
                        "Attempt number must be greater than zero"
                );
    }

    @Test
    void shouldUpdateExistingAttemptWithoutCreatingDuplicate() {

        CodingSession session =
                createCodingSession("3008");

        SubmissionAttempt firstAttempt =
                createAttempt(
                        1,
                        "LC-SUB-601",
                        "return wrong;",
                        "fingerprint-601",
                        SubmissionVerdict.WRONG_ANSWER
                );

        submissionAttemptRepository.save(
                session.sessionId(),
                firstAttempt
        );

        SubmissionAttempt updatedAttempt =
                createAttempt(
                        1,
                        "LC-SUB-601",
                        "return corrected;",
                        "fingerprint-602",
                        SubmissionVerdict.ACCEPTED
                );

        submissionAttemptRepository.save(
                session.sessionId(),
                updatedAttempt
        );

        List<SubmissionAttempt> attempts =
                submissionAttemptRepository
                        .findAllBySessionId(
                                session.sessionId()
                        );

        assertThat(attempts)
                .hasSize(1);

        SubmissionAttempt retrieved =
                attempts.get(0);

        assertThat(retrieved.attemptNumber())
                .isEqualTo(1);

        assertThat(
                retrieved.solution().sourceCode().value()
        ).isEqualTo("return corrected;");

        assertThat(
                retrieved.solution().fingerprint().value()
        ).isEqualTo("fingerprint-602");

        assertThat(
                retrieved.executionResult().verdict()
        ).isEqualTo(SubmissionVerdict.ACCEPTED);
    }

    private CodingSession createCodingSession(
            String problemId) {

        PlatformProblem problem =
                new PlatformProblem(
                        problemId,
                        problemId,
                        "LEETCODE-" + problemId,
                        "Test Problem " + problemId,
                        "test-problem-" + problemId,
                        "https://leetcode.com/problems/test-problem-" + problemId + "/",
                        Platform.LEETCODE,
                        "Medium",
                        Set.of("Array"),
                        false,
                        "1"
                );

        PlatformProblem savedProblem =
                platformProblemRepository.save(problem);

        CodingSession session =
                CodingSession.start(savedProblem);

        return codingSessionRepository.save(session);
    }

    private SubmissionAttempt createAttempt(
            int attemptNumber,
            String platformSubmissionId,
            String code,
            String fingerprint,
            SubmissionVerdict verdict) {

        Solution solution =
                new Solution(
                        new SourceCode(code),
                        ProgrammingLanguage.JAVA,
                        new CodeFingerprint(fingerprint)
                );

        ExecutionResult executionResult =
                new ExecutionResult(
                        verdict,
                        100,
                        90.0,
                        1024,
                        85.0,
                        10,
                        verdict == SubmissionVerdict.ACCEPTED ? 10 : 5
                );

        return new SubmissionAttempt(
                attemptNumber,
                platformSubmissionId,
                solution,
                executionResult,
                java.time.Instant.now()
        );
    }
}