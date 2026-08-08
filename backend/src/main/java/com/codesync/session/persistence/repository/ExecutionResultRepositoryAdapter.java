package com.codesync.session.persistence.repository;

import com.codesync.session.domain.repository.ExecutionResultRepository;
import com.codesync.session.domain.valueobject.ExecutionResult;
import com.codesync.session.persistence.entity.ExecutionResultEntity;
import com.codesync.session.persistence.mapper.ExecutionResultMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class ExecutionResultRepositoryAdapter
        implements ExecutionResultRepository {

    private final ExecutionResultJpaRepository jpaRepository;
    private final ExecutionResultMapper mapper;

    public ExecutionResultRepositoryAdapter(
            ExecutionResultJpaRepository jpaRepository,
            ExecutionResultMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ExecutionResult save(
            ExecutionResult executionResult) {

        ExecutionResultEntity entity =
                mapper.toEntity(executionResult);

        ExecutionResultEntity savedEntity =
                jpaRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExecutionResult> findById(Long id) {

        return jpaRepository
                .findById(id)
                .map(mapper::toDomain);
    }
}