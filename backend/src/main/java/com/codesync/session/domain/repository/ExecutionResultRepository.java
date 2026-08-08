package com.codesync.session.domain.repository;

import com.codesync.session.domain.valueobject.ExecutionResult;

import java.util.Optional;

public interface ExecutionResultRepository {

    ExecutionResult save(ExecutionResult executionResult);

    Optional<ExecutionResult> findById(Long id);
}