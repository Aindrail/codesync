package com.codesync.session.persistence.repository;

import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.identifier.SessionId;
import com.codesync.session.domain.repository.CodingSessionRepository;
import com.codesync.session.persistence.entity.CodingSessionEntity;
import com.codesync.session.persistence.entity.PlatformProblemEntity;
import com.codesync.session.persistence.mapper.CodingSessionMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class CodingSessionRepositoryAdapter
        implements CodingSessionRepository {

    private final CodingSessionJpaRepository jpaRepository;
    private final CodingSessionMapper mapper;
    private final PlatformProblemJpaRepository platformProblemJpaRepository;
    @Override
    @Transactional(readOnly = true)
    public Optional<CodingSession> findActiveByProblem(
            String platform,
            String platformProblemId) {

        return jpaRepository
                .findByProblem_PlatformAndProblem_PlatformProblemIdAndStatus(
                        platform,
                        platformProblemId,
                        "ACTIVE"
                )
                .map(mapper::toDomain);
    }

    public CodingSessionRepositoryAdapter(
            CodingSessionJpaRepository jpaRepository,
            CodingSessionMapper mapper,
            PlatformProblemJpaRepository platformProblemJpaRepository) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.platformProblemJpaRepository =
                platformProblemJpaRepository;
    }

    @Override
    public CodingSession save(CodingSession session) {

        CodingSessionEntity entity =
                jpaRepository
                        .findBySessionId(session.sessionId().value())
                        .orElse(null);

        PlatformProblemEntity problemEntity =
                platformProblemJpaRepository
                        .findByPlatformProblemIdAndPlatform(
                                session.problem().platformProblemId(),
                                session.problem().platform().name()
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Platform problem must exist before saving a coding session."
                                )
                        );

        if (entity == null) {

            entity = mapper.toEntity(
                    session,
                    problemEntity
            );

        } else {

            entity.setProblem(problemEntity);

            entity.setStatus(
                    session.status().name()
            );

            entity.setStartedAt(
                    session.startedAt()
            );

            entity.setEndedAt(
                    session.endedAt()
            );
        }

        CodingSessionEntity savedEntity =
                jpaRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CodingSession> findBySessionId(
            SessionId sessionId) {

        return jpaRepository
                .findBySessionId(sessionId.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySessionId(
            SessionId sessionId) {

        return jpaRepository.existsBySessionId(
                sessionId.value()
        );
    }
}