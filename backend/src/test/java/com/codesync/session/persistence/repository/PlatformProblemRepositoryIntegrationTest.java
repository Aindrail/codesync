package com.codesync.session.persistence.repository;

import com.codesync.session.domain.enumtype.Platform;
import com.codesync.session.domain.repository.PlatformProblemRepository;
import com.codesync.session.domain.valueobject.PlatformProblem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PlatformProblemRepositoryIntegrationTest {

    @Autowired
    private PlatformProblemRepository repository;

    @Test
    void shouldSaveAndRetrievePlatformProblem() {

        PlatformProblem problem = new PlatformProblem(
                "1",
                "1",
                "LEETCODE-1",
                "Two Sum",
                "two-sum",
                "https://leetcode.com/problems/two-sum/",
                Platform.LEETCODE,
                "Easy",
                Set.of(
                        "Array",
                        "Hash Table"
                ),
                false,
                "1"
        );

        PlatformProblem saved = repository.save(problem);

        assertThat(saved).isNotNull();
        assertThat(saved.platformProblemId()).isEqualTo("1");
        assertThat(saved.title()).isEqualTo("Two Sum");
        assertThat(saved.platform()).isEqualTo(Platform.LEETCODE);
        assertThat(saved.officialTags())
                .containsExactlyInAnyOrder(
                        "Array",
                        "Hash Table"
                );

        PlatformProblem retrieved =
                repository.findByPlatformProblemId(
                        "LEETCODE",
                        "1"
                ).orElseThrow();

        assertThat(retrieved.title())
                .isEqualTo("Two Sum");

        assertThat(retrieved.slug())
                .isEqualTo("two-sum");

        assertThat(retrieved.officialDifficulty())
                .isEqualTo("Easy");

        assertThat(retrieved.officialTags())
                .containsExactlyInAnyOrder(
                        "Array",
                        "Hash Table"
                );
    }

    @Test
    void shouldDetectExistingProblem() {

        PlatformProblem problem = new PlatformProblem(
                "2",
                "2",
                "LEETCODE-2",
                "Add Two Numbers",
                "add-two-numbers",
                "https://leetcode.com/problems/add-two-numbers/",
                Platform.LEETCODE,
                "Medium",
                Set.of("Linked List", "Math"),
                false,
                "1"
        );

        repository.save(problem);

        boolean exists =
                repository.existsByPlatformProblemId(
                        "LEETCODE",
                        "2"
                );

        assertThat(exists).isTrue();
    }
}