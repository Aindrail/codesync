package com.codesync.session.persistence.repository;

import com.codesync.session.domain.repository.SolutionRepository;
import com.codesync.session.domain.valueobject.Solution;
import com.codesync.session.persistence.entity.SolutionEntity;
import com.codesync.session.persistence.mapper.SolutionMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class SolutionRepositoryAdapter implements SolutionRepository {

    private final SolutionJpaRepository jpaRepository;
    private final SolutionMapper mapper;

    public SolutionRepositoryAdapter(
            SolutionJpaRepository jpaRepository,
            SolutionMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Solution save(Solution solution) {

        SolutionEntity entity = mapper.toEntity(solution);

        SolutionEntity savedEntity =
                jpaRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Solution> findByFingerprint(
            String fingerprint) {

        return jpaRepository
                .findByFingerprint(fingerprint)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByFingerprint(
            String fingerprint) {

        return jpaRepository.existsByFingerprint(fingerprint);
    }
}