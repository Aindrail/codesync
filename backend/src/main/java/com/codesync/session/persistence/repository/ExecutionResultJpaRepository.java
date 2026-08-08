package com.codesync.session.persistence.repository;

import com.codesync.session.persistence.entity.ExecutionResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionResultJpaRepository
        extends JpaRepository<ExecutionResultEntity, Long> {
}