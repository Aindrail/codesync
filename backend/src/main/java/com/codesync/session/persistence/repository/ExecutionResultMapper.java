package com.codesync.session.persistence.mapper;

import com.codesync.session.domain.enumtype.SubmissionVerdict;
import com.codesync.session.domain.valueobject.ExecutionResult;
import com.codesync.session.persistence.entity.ExecutionResultEntity;
import org.springframework.stereotype.Component;

@Component
public class ExecutionResultMapper {

    public ExecutionResultEntity toEntity(
            ExecutionResult domain) {

        ExecutionResultEntity entity =
                ExecutionResultEntity.create();

        entity.setVerdict(
                domain.verdict().name()
        );

        entity.setRuntimeInMillis(
                domain.runtimeInMillis()
        );

        entity.setRuntimePercentile(
                domain.runtimePercentile()
        );

        entity.setMemoryInKb(
                domain.memoryInKb()
        );

        entity.setMemoryPercentile(
                domain.memoryPercentile()
        );

        entity.setTotalTestCases(
                domain.totalTestCases()
        );

        entity.setPassedTestCases(
                domain.passedTestCases()
        );

        return entity;
    }

    public ExecutionResult toDomain(
            ExecutionResultEntity entity) {

        return new ExecutionResult(

                SubmissionVerdict.valueOf(
                        entity.getVerdict()
                ),

                entity.getRuntimeInMillis(),

                entity.getRuntimePercentile(),

                entity.getMemoryInKb(),

                entity.getMemoryPercentile(),

                entity.getTotalTestCases(),

                entity.getPassedTestCases()
        );
    }
}