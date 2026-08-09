package com.codesync.session.persistence.repository;

import com.codesync.session.persistence.entity.SubmissionAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionAttemptJpaRepository
        extends JpaRepository<SubmissionAttemptEntity, Long> {

    Optional<SubmissionAttemptEntity>
    findBySession_SessionIdAndAttemptNumber(
            UUID sessionId,
            Integer attemptNumber
    );

    List<SubmissionAttemptEntity>
    findAllBySession_SessionIdOrderByAttemptNumberAsc(
            UUID sessionId
    );

    boolean existsBySession_SessionIdAndAttemptNumber(
            UUID sessionId,
            Integer attemptNumber
    );
}