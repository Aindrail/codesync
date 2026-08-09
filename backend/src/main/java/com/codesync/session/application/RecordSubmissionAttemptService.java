package com.codesync.session.application;

import com.codesync.session.domain.entity.SubmissionAttempt;
import com.codesync.session.domain.identifier.SessionId;
import com.codesync.session.domain.repository.CodingSessionRepository;
import com.codesync.session.domain.repository.SubmissionAttemptRepository;
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

    public RecordSubmissionAttemptService(
            CodingSessionRepository codingSessionRepository,
            SubmissionAttemptRepository submissionAttemptRepository) {

        this.codingSessionRepository =
                codingSessionRepository;

        this.submissionAttemptRepository =
                submissionAttemptRepository;
    }

    @Override
    public SubmissionAttempt record(
            RecordSubmissionAttemptCommand command) {

        validate(command);

        SessionId sessionId =
                new SessionId(
                        UUID.fromString(command.sessionId())
                );

        codingSessionRepository
                .findBySessionId(sessionId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Coding session not found."
                        )
                );

        SubmissionAttempt attempt =
                new SubmissionAttempt(
                        command.attemptNumber(),
                        command.platformSubmissionId(),
                        command.solution(),
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

        if (command.solution() == null) {
            throw new IllegalArgumentException(
                    "Solution cannot be null."
            );
        }

        if (command.executionResult() == null) {
            throw new IllegalArgumentException(
                    "Execution result cannot be null."
            );
        }
    }
}