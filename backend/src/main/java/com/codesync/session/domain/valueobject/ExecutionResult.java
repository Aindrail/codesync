package com.codesync.session.domain.valueobject;

import com.codesync.session.domain.enumtype.SubmissionVerdict;

/**
 * Represents the execution result returned by the coding platform.
 */
public record ExecutionResult(

        SubmissionVerdict verdict,

        Integer runtimeInMillis,

        Double runtimePercentile,

        Integer memoryInKb,

        Double memoryPercentile,

        Integer totalTestCases,

        Integer passedTestCases

) {

    public ExecutionResult {

        if (verdict == null) {
            throw new IllegalArgumentException("Submission verdict cannot be null.");
        }

        if (runtimeInMillis != null && runtimeInMillis < 0) {
            throw new IllegalArgumentException("Runtime cannot be negative.");
        }

        if (memoryInKb != null && memoryInKb < 0) {
            throw new IllegalArgumentException("Memory cannot be negative.");
        }

        if (totalTestCases != null && totalTestCases < 0) {
            throw new IllegalArgumentException("Total test cases cannot be negative.");
        }

        if (passedTestCases != null && passedTestCases < 0) {
            throw new IllegalArgumentException("Passed test cases cannot be negative.");
        }

        if (totalTestCases != null &&
                passedTestCases != null &&
                passedTestCases > totalTestCases) {

            throw new IllegalArgumentException(
                    "Passed test cases cannot exceed total test cases."
            );
        }
    }
}