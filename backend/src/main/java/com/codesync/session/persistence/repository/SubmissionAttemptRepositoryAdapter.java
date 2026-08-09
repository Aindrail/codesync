package com.codesync.session.persistence.repository;

import com.codesync.session.domain.entity.SubmissionAttempt;
import com.codesync.session.domain.identifier.SessionId;
import com.codesync.session.domain.repository.SubmissionAttemptRepository;
import com.codesync.session.persistence.entity.CodingSessionEntity;
import com.codesync.session.persistence.entity.SubmissionAttemptEntity;
import com.codesync.session.persistence.mapper.SubmissionAttemptMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class SubmissionAttemptRepositoryAdapter
        implements SubmissionAttemptRepository {

    private final SubmissionAttemptJpaRepository jpaRepository;
    private final CodingSessionJpaRepository codingSessionJpaRepository;
    private final SubmissionAttemptMapper mapper;

    public SubmissionAttemptRepositoryAdapter(
            SubmissionAttemptJpaRepository jpaRepository,
            CodingSessionJpaRepository codingSessionJpaRepository,
            SubmissionAttemptMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.codingSessionJpaRepository =
                codingSessionJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public SubmissionAttempt save(
            SessionId sessionId,
            SubmissionAttempt attempt) {

        CodingSessionEntity sessionEntity =
                codingSessionJpaRepository
                        .findBySessionId(sessionId.value())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Coding session must exist before saving a submission attempt."
                                )
                        );

        SubmissionAttemptEntity entity =
                jpaRepository
                        .findBySession_SessionIdAndAttemptNumber(
                                sessionId.value(),
                                attempt.attemptNumber()
                        )
                        .orElse(null);

        if (entity == null) {

            entity = mapper.toEntity(
                    attempt,
                    sessionEntity
            );

        } else {

            mapper.updateEntity(
                    attempt,
                    entity
            );
        }

        SubmissionAttemptEntity savedEntity =
                jpaRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubmissionAttempt>
    findBySessionIdAndAttemptNumber(
            SessionId sessionId,
            Integer attemptNumber) {

        return jpaRepository
                .findBySession_SessionIdAndAttemptNumber(
                        sessionId.value(),
                        attemptNumber
                )
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionAttempt> findAllBySessionId(
            SessionId sessionId) {

        return jpaRepository
                .findAllBySession_SessionIdOrderByAttemptNumberAsc(
                        sessionId.value()
                )
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySessionIdAndAttemptNumber(
            SessionId sessionId,
            Integer attemptNumber) {

        return jpaRepository
                .existsBySession_SessionIdAndAttemptNumber(
                        sessionId.value(),
                        attemptNumber
                );
    }
}