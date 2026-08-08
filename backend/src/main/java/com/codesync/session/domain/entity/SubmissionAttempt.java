package com.codesync.session.domain.entity;

import com.codesync.session.domain.valueobject.ExecutionResult;
import com.codesync.session.domain.valueobject.Solution;

import java.time.Instant;

/**
 * Represents a single submission made during a coding session.
 */
public final class SubmissionAttempt {

    private final Integer attemptNumber;

    private final String platformSubmissionId;

    private final Solution solution;

    private final ExecutionResult executionResult;

    private final Instant submittedAt;

    public SubmissionAttempt(
            Integer attemptNumber,
            String platformSubmissionId,
            Solution solution,
            ExecutionResult executionResult,
            Instant submittedAt) {

        if (attemptNumber == null || attemptNumber <= 0) {
            throw new IllegalArgumentException("Attempt number must be greater than zero.");
        }

        if (solution == null) {
            throw new IllegalArgumentException("Solution cannot be null.");
        }

        if (executionResult == null) {
            throw new IllegalArgumentException("Execution result cannot be null.");
        }

        if (submittedAt == null) {
            throw new IllegalArgumentException("Submission time cannot be null.");
        }

        this.attemptNumber = attemptNumber;
        this.platformSubmissionId = platformSubmissionId;
        this.solution = solution;
        this.executionResult = executionResult;
        this.submittedAt = submittedAt;
    }

    public Integer attemptNumber() {
        return attemptNumber;
    }

    public String platformSubmissionId() {
        return platformSubmissionId;
    }

    public Solution solution() {
        return solution;
    }

    public ExecutionResult executionResult() {
        return executionResult;
    }

    public Instant submittedAt() {
        return submittedAt;
    }
}