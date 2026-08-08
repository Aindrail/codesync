package com.codesync.session.persistence.repository;

import com.codesync.session.persistence.entity.SolutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SolutionJpaRepository
        extends JpaRepository<SolutionEntity, Long> {

    Optional<SolutionEntity> findByFingerprint(String fingerprint);

    boolean existsByFingerprint(String fingerprint);
}