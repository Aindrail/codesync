package com.codesync.session.persistence.repository;

import com.codesync.session.persistence.entity.PlatformProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlatformProblemJpaRepository
        extends JpaRepository<PlatformProblemEntity, Long> {

    Optional<PlatformProblemEntity> findByPlatformProblemIdAndPlatform(
            String platformProblemId,
            String platform
    );

    boolean existsByPlatformProblemIdAndPlatform(
            String platformProblemId,
            String platform
    );
}