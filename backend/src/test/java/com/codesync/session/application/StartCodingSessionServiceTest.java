package com.codesync.session.application;

import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.entity.User;
import com.codesync.session.domain.repository.CodingSessionRepository;
import com.codesync.session.domain.repository.PlatformProblemRepository;
import com.codesync.session.domain.repository.UserRepository;
import com.codesync.session.domain.valueobject.PlatformProblem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartCodingSessionServiceTest {

    @Mock
    private PlatformProblemRepository platformProblemRepository;

    @Mock
    private CodingSessionRepository codingSessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StartCodingSessionService service;

    @Test
    void shouldCreateNewSessionWhenNoActiveSessionExists() {

        User user =
                User.reconstitute(
                        1L,
                        "github-user-1"
                );

        PlatformProblem problem =
                createProblem();

        when(platformProblemRepository
                .findByPlatformProblemId(
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.of(problem));

        when(codingSessionRepository
                .findActiveByUserAndProblem(
                        1L,
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.empty());

        when(userRepository
                .findById(1L))
                .thenReturn(Optional.of(user));

        CodingSession newSession =
                CodingSession.start(
                        user,
                        problem
                );

        when(codingSessionRepository.save(any()))
                .thenReturn(newSession);

        CodingSession result =
                service.start(
                        new StartCodingSessionCommand(
                                1L,
                                "LEETCODE",
                                "1"
                        )
                );

        assertThat(result).isNotNull();

        assertThat(result.problem())
                .isEqualTo(problem);

        assertThat(result.user())
                .isEqualTo(user);

        assertThat(result.isActive())
                .isTrue();

        verify(codingSessionRepository)
                .save(any(CodingSession.class));

        verify(userRepository)
                .findById(1L);
    }

    @Test
    void shouldReturnExistingActiveSession() {

        User user =
                User.reconstitute(
                        1L,
                        "github-user-1"
                );

        PlatformProblem problem =
                createProblem();

        CodingSession existingSession =
                CodingSession.start(
                        user,
                        problem
                );

        when(platformProblemRepository
                .findByPlatformProblemId(
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.of(problem));

        when(codingSessionRepository
                .findActiveByUserAndProblem(
                        1L,
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.of(existingSession));

        CodingSession result =
                service.start(
                        new StartCodingSessionCommand(
                                1L,
                                "LEETCODE",
                                "1"
                        )
                );

        assertThat(result)
                .isSameAs(existingSession);

        verify(codingSessionRepository, never())
                .save(any(CodingSession.class));

        /*
         * User lookup is unnecessary because an existing
         * session has already been found.
         */
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldCreateNewSessionAfterPreviousSessionIsCompleted() {

        User user =
                User.reconstitute(
                        1L,
                        "github-user-1"
                );

        PlatformProblem problem =
                createProblem();

        when(platformProblemRepository
                .findByPlatformProblemId(
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.of(problem));

        /*
         * No ACTIVE session means the previous session
         * was completed or abandoned.
         */
        when(codingSessionRepository
                .findActiveByUserAndProblem(
                        1L,
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.empty());

        when(userRepository
                .findById(1L))
                .thenReturn(Optional.of(user));

        CodingSession newSession =
                CodingSession.start(
                        user,
                        problem
                );

        when(codingSessionRepository.save(any()))
                .thenReturn(newSession);

        CodingSession result =
                service.start(
                        new StartCodingSessionCommand(
                                1L,
                                "LEETCODE",
                                "1"
                        )
                );

        assertThat(result)
                .isSameAs(newSession);

        assertThat(result.user())
                .isEqualTo(user);

        verify(codingSessionRepository)
                .save(any(CodingSession.class));
    }

    @Test
    void shouldAllowDifferentUsersToHaveSeparateActiveSessionsForSameProblem() {

        User userA =
                User.reconstitute(
                        1L,
                        "github-user-A"
                );

        User userB =
                User.reconstitute(
                        2L,
                        "github-user-B"
                );

        PlatformProblem problem =
                createProblem();

        CodingSession sessionA =
                CodingSession.start(
                        userA,
                        problem
                );

        CodingSession sessionB =
                CodingSession.start(
                        userB,
                        problem
                );

        when(platformProblemRepository
                .findByPlatformProblemId(
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.of(problem));

        /*
         * User A has no active session for the problem.
         */
        when(codingSessionRepository
                .findActiveByUserAndProblem(
                        1L,
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.empty());

        /*
         * User B also has no active session for the problem.
         *
         * The important point is that the lookup is scoped
         * by userId.
         */
        when(codingSessionRepository
                .findActiveByUserAndProblem(
                        2L,
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.empty());

        when(userRepository
                .findById(1L))
                .thenReturn(Optional.of(userA));

        when(userRepository
                .findById(2L))
                .thenReturn(Optional.of(userB));

        when(codingSessionRepository.save(any()))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        CodingSession resultA =
                service.start(
                        new StartCodingSessionCommand(
                                1L,
                                "LEETCODE",
                                "1"
                        )
                );

        CodingSession resultB =
                service.start(
                        new StartCodingSessionCommand(
                                2L,
                                "LEETCODE",
                                "1"
                        )
                );

        assertThat(resultA.user())
                .isEqualTo(userA);

        assertThat(resultB.user())
                .isEqualTo(userB);

        assertThat(resultA.sessionId())
                .isNotEqualTo(resultB.sessionId());

        assertThat(resultA.problem())
                .isEqualTo(resultB.problem());

        verify(codingSessionRepository)
                .findActiveByUserAndProblem(
                        1L,
                        "LEETCODE",
                        "1"
                );

        verify(codingSessionRepository)
                .findActiveByUserAndProblem(
                        2L,
                        "LEETCODE",
                        "1"
                );

        verify(codingSessionRepository, times(2))
                .save(any(CodingSession.class));
    }

    @Test
    void shouldRejectMissingProblem() {

        when(platformProblemRepository
                .findByPlatformProblemId(
                        "LEETCODE",
                        "999"
                ))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.start(
                        new StartCodingSessionCommand(
                                1L,
                                "LEETCODE",
                                "999"
                        )
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "Platform problem not found."
                );

        verify(codingSessionRepository, never())
                .findActiveByUserAndProblem(
                        any(),
                        any(),
                        any()
                );

        verify(codingSessionRepository, never())
                .save(any());

        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldRejectMissingUser() {

        PlatformProblem problem =
                createProblem();

        when(platformProblemRepository
                .findByPlatformProblemId(
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.of(problem));

        when(codingSessionRepository
                .findActiveByUserAndProblem(
                        999L,
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.empty());

        when(userRepository
                .findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.start(
                        new StartCodingSessionCommand(
                                999L,
                                "LEETCODE",
                                "1"
                        )
                )
        )
                .isInstanceOf(
                        IllegalStateException.class
                )
                .hasMessage(
                        "User not found."
                );

        verify(codingSessionRepository, never())
                .save(any());
    }

    @Test
    void shouldRejectBlankPlatform() {

        assertThatThrownBy(() ->
                service.start(
                        new StartCodingSessionCommand(
                                1L,
                                "",
                                "1"
                        )
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "Platform cannot be blank."
                );

        verifyNoInteractions(platformProblemRepository);
        verifyNoInteractions(codingSessionRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldRejectBlankProblemId() {

        assertThatThrownBy(() ->
                service.start(
                        new StartCodingSessionCommand(
                                1L,
                                "LEETCODE",
                                ""
                        )
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "Platform problem ID cannot be blank."
                );

        verifyNoInteractions(platformProblemRepository);
        verifyNoInteractions(codingSessionRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldRejectNullUserId() {

        assertThatThrownBy(() ->
                service.start(
                        new StartCodingSessionCommand(
                                null,
                                "LEETCODE",
                                "1"
                        )
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "User ID cannot be null."
                );

        verifyNoInteractions(platformProblemRepository);
        verifyNoInteractions(codingSessionRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldNotReuseSessionWhenPlatformIsDifferent() {

        User user =
                User.reconstitute(
                        1L,
                        "github-user-1"
                );

        PlatformProblem leetCodeProblem =
                createProblem();

        PlatformProblem gfgProblem =
                new PlatformProblem(
                        "1",
                        "1",
                        "GFG-1",
                        "Two Sum",
                        "two-sum",
                        "https://www.geeksforgeeks.org/two-sum/",
                        com.codesync.session.domain.enumtype.Platform.GEEKS_FOR_GEEKS,
                        "Easy",
                        java.util.Set.of("Array"),
                        false,
                        "1"
                );

        CodingSession leetCodeSession =
                CodingSession.start(
                        user,
                        leetCodeProblem
                );

        when(platformProblemRepository
                .findByPlatformProblemId(
                        "GEEKS_FOR_GEEKS",
                        "1"
                ))
                .thenReturn(Optional.of(gfgProblem));

        when(codingSessionRepository
                .findActiveByUserAndProblem(
                        1L,
                        "GEEKS_FOR_GEEKS",
                        "1"
                ))
                .thenReturn(Optional.empty());

        when(userRepository
                .findById(1L))
                .thenReturn(Optional.of(user));

        when(codingSessionRepository.save(any()))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        CodingSession result =
                service.start(
                        new StartCodingSessionCommand(
                                1L,
                                "GEEKS_FOR_GEEKS",
                                "1"
                        )
                );

        assertThat(result)
                .isNotSameAs(leetCodeSession);

        assertThat(result.problem())
                .isEqualTo(gfgProblem);

        assertThat(result.user())
                .isEqualTo(user);

        assertThat(result.isActive())
                .isTrue();

        verify(codingSessionRepository)
                .findActiveByUserAndProblem(
                        1L,
                        "GEEKS_FOR_GEEKS",
                        "1"
                );

        verify(codingSessionRepository)
                .save(any(CodingSession.class));
    }

    private PlatformProblem createProblem() {

        return new PlatformProblem(
                "1",
                "1",
                "LEETCODE-1",
                "Two Sum",
                "two-sum",
                "https://leetcode.com/problems/two-sum/",
                com.codesync.session.domain.enumtype.Platform.LEETCODE,
                "Easy",
                java.util.Set.of("Array"),
                false,
                "1"
        );
    }
}