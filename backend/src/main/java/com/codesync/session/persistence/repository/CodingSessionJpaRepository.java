package com.codesync.session.persistence.repository;

import com.codesync.session.persistence.entity.CodingSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CodingSessionJpaRepository
        extends JpaRepository<CodingSessionEntity, Long> {

    Optional<CodingSessionEntity> findBySessionId(UUID sessionId);
    Optional<CodingSessionEntity>
    findByProblem_PlatformAndProblem_PlatformProblemIdAndStatus(
            String platform,
            String platformProblemId,
            String status
    );

    boolean existsBySessionId(UUID sessionId);
}