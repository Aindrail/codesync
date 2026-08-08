package com.codesync.session.persistence.repository;

import com.codesync.session.domain.enumtype.ProgrammingLanguage;
import com.codesync.session.domain.repository.SolutionRepository;
import com.codesync.session.domain.valueobject.CodeFingerprint;
import com.codesync.session.domain.valueobject.Solution;
import com.codesync.session.domain.valueobject.SourceCode;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SolutionRepositoryIntegrationTest {

    @Autowired
    private SolutionRepository repository;

    @Test
    void shouldSaveAndRetrieveSolutionByFingerprint() {

        Solution solution = new Solution(
                new SourceCode("class Solution { }"),
                ProgrammingLanguage.JAVA,
                new CodeFingerprint("test-fingerprint-001")
        );

        Solution saved = repository.save(solution);

        assertThat(saved).isNotNull();
        assertThat(saved.sourceCode().value())
                .isEqualTo("class Solution { }");

        assertThat(saved.language())
                .isEqualTo(ProgrammingLanguage.JAVA);

        assertThat(saved.fingerprint().value())
                .isEqualTo("test-fingerprint-001");

        Solution retrieved =
                repository.findByFingerprint(
                        "test-fingerprint-001"
                ).orElseThrow();

        assertThat(retrieved.sourceCode().value())
                .isEqualTo("class Solution { }");

        assertThat(retrieved.language())
                .isEqualTo(ProgrammingLanguage.JAVA);

        assertThat(retrieved.fingerprint().value())
                .isEqualTo("test-fingerprint-001");
    }

    @Test
    void shouldDetectExistingSolutionByFingerprint() {

        Solution solution = new Solution(
                new SourceCode("public class Test {}"),
                ProgrammingLanguage.JAVA,
                new CodeFingerprint("test-fingerprint-002")
        );

        repository.save(solution);

        assertThat(
                repository.existsByFingerprint(
                        "test-fingerprint-002"
                )
        ).isTrue();
    }
}