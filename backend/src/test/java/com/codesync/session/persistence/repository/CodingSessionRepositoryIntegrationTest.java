package com.codesync.session.persistence.repository;

import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.entity.User;
import com.codesync.session.domain.enumtype.Platform;
import com.codesync.session.domain.enumtype.SessionStatus;
import com.codesync.session.domain.repository.CodingSessionRepository;
import com.codesync.session.domain.repository.PlatformProblemRepository;
import com.codesync.session.domain.repository.UserRepository;
import com.codesync.session.domain.valueobject.PlatformProblem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CodingSessionRepositoryIntegrationTest {

    @Autowired
    private CodingSessionRepository codingSessionRepository;

    @Autowired
    private PlatformProblemRepository platformProblemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndRetrieveCodingSession() {

        User user =
                userRepository.save(
                        User.create("github-session-test-1001")
                );

        PlatformProblem problem = new PlatformProblem(
                "1001",
                "1001",
                "LEETCODE-1001",
                "Test Problem",
                "test-problem",
                "https://leetcode.com/problems/test-problem/",
                Platform.LEETCODE,
                "Easy",
                Set.of("Array"),
                false,
                "1"
        );

        PlatformProblem savedProblem =
                platformProblemRepository.save(problem);

        CodingSession session =
                CodingSession.start(
                        user,
                        savedProblem
                );

        CodingSession savedSession =
                codingSessionRepository.save(session);

        assertThat(savedSession).isNotNull();

        assertThat(savedSession.sessionId())
                .isEqualTo(session.sessionId());

        assertThat(savedSession.user().id())
                .isEqualTo(user.id());

        assertThat(savedSession.user().githubUserId())
                .isEqualTo("github-session-test-1001");

        assertThat(savedSession.problem().platformProblemId())
                .isEqualTo("1001");

        assertThat(savedSession.status())
                .isEqualTo(SessionStatus.ACTIVE);

        assertThat(savedSession.startedAt())
                .isEqualTo(session.startedAt());

        assertThat(savedSession.endedAt())
                .isNull();

        CodingSession retrieved =
                codingSessionRepository
                        .findBySessionId(session.sessionId())
                        .orElseThrow();

        assertThat(retrieved.sessionId())
                .isEqualTo(session.sessionId());

        assertThat(retrieved.user().id())
                .isEqualTo(user.id());

        assertThat(retrieved.user().githubUserId())
                .isEqualTo("github-session-test-1001");

        assertThat(retrieved.problem().title())
                .isEqualTo("Test Problem");

        assertThat(retrieved.status())
                .isEqualTo(SessionStatus.ACTIVE);

        assertThat(retrieved.endedAt())
                .isNull();
    }

    @Test
    void shouldUpdateSessionWhenCompleted() {

        User user =
                userRepository.save(
                        User.create("github-session-test-1002")
                );

        PlatformProblem problem = new PlatformProblem(
                "1002",
                "1002",
                "LEETCODE-1002",
                "Another Test Problem",
                "another-test-problem",
                "https://leetcode.com/problems/another-test-problem/",
                Platform.LEETCODE,
                "Medium",
                Set.of("Hash Table"),
                false,
                "1"
        );

        PlatformProblem savedProblem =
                platformProblemRepository.save(problem);

        CodingSession session =
                CodingSession.start(
                        user,
                        savedProblem
                );

        codingSessionRepository.save(session);

        session.complete();

        CodingSession updated =
                codingSessionRepository.save(session);

        assertThat(updated.status())
                .isEqualTo(SessionStatus.COMPLETED);

        assertThat(updated.endedAt())
                .isNotNull();

        assertThat(updated.user().id())
                .isEqualTo(user.id());

        CodingSession retrieved =
                codingSessionRepository
                        .findBySessionId(session.sessionId())
                        .orElseThrow();

        assertThat(retrieved.status())
                .isEqualTo(SessionStatus.COMPLETED);

        assertThat(retrieved.user().id())
                .isEqualTo(user.id());

        assertThat(retrieved.endedAt())
                .isNotNull();
    }

    @Test
    void shouldDetectExistingSession() {

        User user =
                userRepository.save(
                        User.create("github-session-test-1003")
                );

        PlatformProblem problem = new PlatformProblem(
                "1003",
                "1003",
                "LEETCODE-1003",
                "Existence Test Problem",
                "existence-test-problem",
                "https://leetcode.com/problems/existence-test-problem/",
                Platform.LEETCODE,
                "Hard",
                Set.of("Graph"),
                false,
                "1"
        );

        PlatformProblem savedProblem =
                platformProblemRepository.save(problem);

        CodingSession session =
                CodingSession.start(
                        user,
                        savedProblem
                );

        codingSessionRepository.save(session);

        assertThat(
                codingSessionRepository
                        .existsBySessionId(
                                session.sessionId()
                        )
        ).isTrue();
    }

    @Test
    void shouldAllowMultipleCodingSessionsForSameProblem() {

        User user =
                userRepository.save(
                        User.create("github-session-test-1004")
                );

        PlatformProblem problem = new PlatformProblem(
                "2001",
                "2001",
                "LEETCODE-2001",
                "Shared Problem",
                "shared-problem",
                "https://leetcode.com/problems/shared-problem/",
                Platform.LEETCODE,
                "Medium",
                Set.of("Array"),
                false,
                "1"
        );

        PlatformProblem savedProblem =
                platformProblemRepository.save(problem);

        // First coding session
        CodingSession firstSession =
                CodingSession.start(
                        user,
                        savedProblem
                );

        CodingSession savedFirstSession =
                codingSessionRepository.save(firstSession);

        // Second coding session for the SAME problem
        CodingSession secondSession =
                CodingSession.start(
                        user,
                        savedProblem
                );

        CodingSession savedSecondSession =
                codingSessionRepository.save(secondSession);

        // Each session must have its own identity
        assertThat(savedFirstSession.sessionId())
                .isNotEqualTo(savedSecondSession.sessionId());

        // Both sessions belong to the same user
        assertThat(savedFirstSession.user().id())
                .isEqualTo(savedSecondSession.user().id());

        // Both sessions must point to the same problem
        assertThat(savedFirstSession.problem().platformProblemId())
                .isEqualTo(
                        savedSecondSession
                                .problem()
                                .platformProblemId()
                );

        assertThat(savedFirstSession.problem().canonicalProblemKey())
                .isEqualTo(
                        savedSecondSession
                                .problem()
                                .canonicalProblemKey()
                );

        // Both sessions are independent
        assertThat(savedFirstSession.status())
                .isEqualTo(SessionStatus.ACTIVE);

        assertThat(savedSecondSession.status())
                .isEqualTo(SessionStatus.ACTIVE);

        // Complete only the first session
        firstSession.complete();

        CodingSession updatedFirstSession =
                codingSessionRepository.save(
                        firstSession
                );

        CodingSession retrievedSecondSession =
                codingSessionRepository
                        .findBySessionId(
                                secondSession.sessionId()
                        )
                        .orElseThrow();

        // First session changed
        assertThat(updatedFirstSession.status())
                .isEqualTo(SessionStatus.COMPLETED);

        // Second session remains independent
        assertThat(retrievedSecondSession.status())
                .isEqualTo(SessionStatus.ACTIVE);

        assertThat(retrievedSecondSession.endedAt())
                .isNull();

        assertThat(retrievedSecondSession.user().id())
                .isEqualTo(user.id());
    }

    @Test
    void shouldAbandonAndPersistCodingSession() {

        User user =
                userRepository.save(
                        User.create("github-session-test-1005")
                );

        PlatformProblem problem = new PlatformProblem(
                "1004",
                "1004",
                "LEETCODE-1004",
                "Abandon Test Problem",
                "abandon-test-problem",
                "https://leetcode.com/problems/abandon-test-problem/",
                Platform.LEETCODE,
                "Easy",
                Set.of("Array"),
                false,
                "1"
        );

        PlatformProblem savedProblem =
                platformProblemRepository.save(problem);

        CodingSession session =
                CodingSession.start(
                        user,
                        savedProblem
                );

        codingSessionRepository.save(session);

        session.abandon();

        CodingSession saved =
                codingSessionRepository.save(session);

        assertThat(saved.status())
                .isEqualTo(SessionStatus.ABANDONED);

        assertThat(saved.endedAt())
                .isNotNull();

        assertThat(saved.user().id())
                .isEqualTo(user.id());

        CodingSession retrieved =
                codingSessionRepository
                        .findBySessionId(
                                session.sessionId()
                        )
                        .orElseThrow();

        assertThat(retrieved.status())
                .isEqualTo(SessionStatus.ABANDONED);

        assertThat(retrieved.user().id())
                .isEqualTo(user.id());

        assertThat(retrieved.endedAt())
                .isNotNull();
    }

    @Test
    void shouldNotCompleteAlreadyCompletedSession() {

        User user =
                userRepository.save(
                        User.create("github-session-test-1006")
                );

        PlatformProblem problem = new PlatformProblem(
                "1005",
                "1005",
                "LEETCODE-1005",
                "Completed Test Problem",
                "completed-test-problem",
                "https://leetcode.com/problems/completed-test-problem/",
                Platform.LEETCODE,
                "Medium",
                Set.of("Hash Table"),
                false,
                "1"
        );

        PlatformProblem savedProblem =
                platformProblemRepository.save(problem);

        CodingSession session =
                CodingSession.start(
                        user,
                        savedProblem
                );

        session.complete();

        assertThatThrownBy(
                session::complete
        )
                .isInstanceOf(
                        IllegalStateException.class
                );

        assertThat(session.status())
                .isEqualTo(SessionStatus.COMPLETED);

        assertThat(session.user().id())
                .isEqualTo(user.id());
    }

    @Test
    void shouldNotAbandonAlreadyCompletedSession() {

        User user =
                userRepository.save(
                        User.create("github-session-test-1007")
                );

        PlatformProblem problem = new PlatformProblem(
                "1006",
                "1006",
                "LEETCODE-1006",
                "Invalid Transition Test Problem",
                "invalid-transition-test-problem",
                "https://leetcode.com/problems/invalid-transition-test-problem/",
                Platform.LEETCODE,
                "Hard",
                Set.of("Graph"),
                false,
                "1"
        );

        PlatformProblem savedProblem =
                platformProblemRepository.save(problem);

        CodingSession session =
                CodingSession.start(
                        user,
                        savedProblem
                );

        session.complete();

        assertThatThrownBy(
                session::abandon
        )
                .isInstanceOf(
                        IllegalStateException.class
                );

        assertThat(session.status())
                .isEqualTo(SessionStatus.COMPLETED);

        assertThat(session.user().id())
                .isEqualTo(user.id());
    }
}