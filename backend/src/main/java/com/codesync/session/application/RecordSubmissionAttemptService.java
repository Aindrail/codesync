package com.codesync.session.application;

import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.entity.SubmissionAttempt;
import com.codesync.session.domain.identifier.SessionId;
import com.codesync.session.domain.repository.CodingSessionRepository;
import com.codesync.session.domain.repository.SubmissionAttemptRepository;
import com.codesync.session.domain.service.CodeFingerprintGenerator;
import com.codesync.session.domain.valueobject.CodeFingerprint;
import com.codesync.session.domain.valueobject.Solution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class RecordSubmissionAttemptService
        implements RecordSubmissionAttemptUseCase {

    private final CodingSessionRepository codingSessionRepository;
    private final SubmissionAttemptRepository submissionAttemptRepository;
    private final CodeFingerprintGenerator codeFingerprintGenerator;

    public RecordSubmissionAttemptService(
            CodingSessionRepository codingSessionRepository,
            SubmissionAttemptRepository submissionAttemptRepository,
            CodeFingerprintGenerator codeFingerprintGenerator) {

        this.codingSessionRepository =
                codingSessionRepository;

        this.submissionAttemptRepository =
                submissionAttemptRepository;

        this.codeFingerprintGenerator =
                codeFingerprintGenerator;
    }

    @Override
    public SubmissionAttempt record(
            RecordSubmissionAttemptCommand command) {

        validate(command);

        SessionId sessionId =
                new SessionId(
                        UUID.fromString(command.sessionId())
                );

        CodingSession session =
                codingSessionRepository
                        .findBySessionId(sessionId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Coding session not found."
                                )
                        );

        CodeFingerprint fingerprint =
                codeFingerprintGenerator.generate(
                        command.sourceCode(),
                        command.language()
                );

        Solution solution =
                new Solution(
                        command.sourceCode(),
                        command.language(),
                        fingerprint
                );

        boolean duplicate =
                submissionAttemptRepository
                        .existsByUserIdAndSolutionFingerprint(
                                session.user().id(),
                                fingerprint.value()
                        );

        if (duplicate) {
            throw new IllegalStateException(
                    "This solution has already been submitted by this user."
            );
        }

        SubmissionAttempt attempt =
                new SubmissionAttempt(
                        command.attemptNumber(),
                        command.platformSubmissionId(),
                        solution,
                        command.executionResult(),
                        Instant.now()
                );

        return submissionAttemptRepository.save(
                sessionId,
                attempt
        );
    }

    private void validate(
            RecordSubmissionAttemptCommand command) {

        if (command == null) {
            throw new IllegalArgumentException(
                    "Command cannot be null."
            );
        }

        if (command.sessionId() == null
                || command.sessionId().isBlank()) {

            throw new IllegalArgumentException(
                    "Session ID cannot be blank."
            );
        }

        if (command.attemptNumber() == null
                || command.attemptNumber() <= 0) {

            throw new IllegalArgumentException(
                    "Attempt number must be greater than zero."
            );
        }

        if (command.sourceCode() == null) {
            throw new IllegalArgumentException(
                    "Source code cannot be null."
            );
        }

        if (command.language() == null) {
            throw new IllegalArgumentException(
                    "Programming language cannot be null."
            );
        }

        if (command.executionResult() == null) {
            throw new IllegalArgumentException(
                    "Execution result cannot be null."
            );
        }
    }
}