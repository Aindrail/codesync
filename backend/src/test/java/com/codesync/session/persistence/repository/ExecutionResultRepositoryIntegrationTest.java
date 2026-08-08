package com.codesync.session.persistence.repository;

import com.codesync.session.domain.enumtype.SubmissionVerdict;
import com.codesync.session.domain.repository.ExecutionResultRepository;
import com.codesync.session.domain.valueobject.ExecutionResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExecutionResultRepositoryIntegrationTest {

    @Autowired
    private ExecutionResultRepository repository;

    @Test
    void shouldSaveAndRetrieveExecutionResult() {

        ExecutionResult result = new ExecutionResult(
                SubmissionVerdict.ACCEPTED,
                120,
                95.5,
                2048,
                90.2,
                100,
                100
        );

        ExecutionResult saved =
                repository.save(result);

        assertThat(saved).isNotNull();

        assertThat(saved.verdict())
                .isEqualTo(SubmissionVerdict.ACCEPTED);

        assertThat(saved.runtimeInMillis())
                .isEqualTo(120);

        assertThat(saved.runtimePercentile())
                .isEqualTo(95.5);

        assertThat(saved.memoryInKb())
                .isEqualTo(2048);

        assertThat(saved.memoryPercentile())
                .isEqualTo(90.2);

        assertThat(saved.totalTestCases())
                .isEqualTo(100);

        assertThat(saved.passedTestCases())
                .isEqualTo(100);
    }
}