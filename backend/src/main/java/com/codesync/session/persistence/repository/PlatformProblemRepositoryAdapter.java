package com.codesync.session.persistence.repository;

import com.codesync.session.domain.repository.PlatformProblemRepository;
import com.codesync.session.domain.valueobject.PlatformProblem;
import com.codesync.session.persistence.entity.PlatformProblemEntity;
import com.codesync.session.persistence.mapper.PlatformProblemMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class PlatformProblemRepositoryAdapter
        implements PlatformProblemRepository {

    private final PlatformProblemJpaRepository jpaRepository;
    private final PlatformProblemMapper mapper;

    public PlatformProblemRepositoryAdapter(
            PlatformProblemJpaRepository jpaRepository,
            PlatformProblemMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public PlatformProblem save(PlatformProblem problem) {

        PlatformProblemEntity entity = mapper.toEntity(problem);

        PlatformProblemEntity savedEntity =
                jpaRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlatformProblem> findByPlatformProblemId(
            String platform,
            String platformProblemId) {

        return jpaRepository
                .findByPlatformProblemIdAndPlatform(
                        platformProblemId,
                        platform
                )
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPlatformProblemId(
            String platform,
            String platformProblemId) {

        return jpaRepository.existsByPlatformProblemIdAndPlatform(
                platformProblemId,
                platform
        );
    }
}