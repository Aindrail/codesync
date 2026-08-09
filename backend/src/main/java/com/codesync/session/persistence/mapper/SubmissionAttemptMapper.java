package com.codesync.session.persistence.mapper;

import com.codesync.session.domain.entity.SubmissionAttempt;
import com.codesync.session.domain.valueobject.ExecutionResult;
import com.codesync.session.domain.valueobject.Solution;
import com.codesync.session.persistence.entity.CodingSessionEntity;
import com.codesync.session.persistence.entity.ExecutionResultEntity;
import com.codesync.session.persistence.entity.SolutionEntity;
import com.codesync.session.persistence.entity.SubmissionAttemptEntity;
import org.springframework.stereotype.Component;

@Component
public class SubmissionAttemptMapper {

    private final SolutionMapper solutionMapper;
    private final ExecutionResultMapper executionResultMapper;


    public SubmissionAttemptMapper(
            SolutionMapper solutionMapper,
            ExecutionResultMapper executionResultMapper) {

        this.solutionMapper = solutionMapper;
        this.executionResultMapper = executionResultMapper;
    }

    public SubmissionAttemptEntity toEntity(
            SubmissionAttempt domain,
            CodingSessionEntity sessionEntity) {

        SubmissionAttemptEntity entity =
                SubmissionAttemptEntity.create();

        entity.setSession(sessionEntity);

        entity.setAttemptNumber(
                domain.attemptNumber()
        );

        entity.setPlatformSubmissionId(
                domain.platformSubmissionId()
        );

        SolutionEntity solutionEntity =
                solutionMapper.toEntity(
                        domain.solution()
                );

        entity.setSolution(solutionEntity);

        ExecutionResultEntity executionResultEntity =
                executionResultMapper.toEntity(
                        domain.executionResult()
                );

        entity.setExecutionResult(
                executionResultEntity
        );

        entity.setSubmittedAt(
                domain.submittedAt()
        );

        return entity;
    }
    public void updateEntity(
            SubmissionAttempt domain,
            SubmissionAttemptEntity entity) {

        entity.setAttemptNumber(
                domain.attemptNumber()
        );

        entity.setPlatformSubmissionId(
                domain.platformSubmissionId()
        );

        entity.setSolution(
                solutionMapper.toEntity(
                        domain.solution()
                )
        );

        entity.setExecutionResult(
                executionResultMapper.toEntity(
                        domain.executionResult()
                )
        );

        entity.setSubmittedAt(
                domain.submittedAt()
        );
    }

    public SubmissionAttempt toDomain(
            SubmissionAttemptEntity entity) {

        Solution solution =
                solutionMapper.toDomain(
                        entity.getSolution()
                );

        ExecutionResult executionResult =
                executionResultMapper.toDomain(
                        entity.getExecutionResult()
                );

        return new SubmissionAttempt(
                entity.getAttemptNumber(),
                entity.getPlatformSubmissionId(),
                solution,
                executionResult,
                entity.getSubmittedAt()
        );
    }
}