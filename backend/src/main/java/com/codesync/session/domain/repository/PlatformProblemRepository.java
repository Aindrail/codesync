package com.codesync.session.domain.repository;

import com.codesync.session.domain.valueobject.PlatformProblem;

import java.util.Optional;

public interface PlatformProblemRepository {

    PlatformProblem save(PlatformProblem problem);

    Optional<PlatformProblem> findByPlatformProblemId(
            String platform,
            String platformProblemId
    );

    boolean existsByPlatformProblemId(
            String platform,
            String platformProblemId
    );
}