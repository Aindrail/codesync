package com.codesync.session.domain.repository;

import com.codesync.session.domain.entity.SubmissionAttempt;
import com.codesync.session.domain.identifier.SessionId;

import java.util.List;
import java.util.Optional;

public interface SubmissionAttemptRepository {

    SubmissionAttempt save(
            SessionId sessionId,
            SubmissionAttempt attempt
    );

    Optional<SubmissionAttempt> findBySessionIdAndAttemptNumber(
            SessionId sessionId,
            Integer attemptNumber
    );

    List<SubmissionAttempt> findAllBySessionId(
            SessionId sessionId
    );

    boolean existsBySessionIdAndAttemptNumber(
            SessionId sessionId,
            Integer attemptNumber
    );
}