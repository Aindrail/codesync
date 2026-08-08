package com.codesync.session.domain.repository;

import com.codesync.session.domain.valueobject.Solution;

import java.util.Optional;

public interface SolutionRepository {

    Solution save(Solution solution);

    Optional<Solution> findByFingerprint(String fingerprint);

    boolean existsByFingerprint(String fingerprint);
}