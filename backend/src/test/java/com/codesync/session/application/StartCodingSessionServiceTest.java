package com.codesync.session.application;

import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.repository.CodingSessionRepository;
import com.codesync.session.domain.repository.PlatformProblemRepository;
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

    @InjectMocks
    private StartCodingSessionService service;

    @Test
    void shouldCreateNewSessionWhenNoActiveSessionExists() {

        PlatformProblem problem =
                createProblem();

        when(platformProblemRepository
                .findByPlatformProblemId(
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.of(problem));

        when(codingSessionRepository
                .findActiveByProblem(
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.empty());

        CodingSession newSession =
                CodingSession.start(problem);

        when(codingSessionRepository.save(any()))
                .thenReturn(newSession);

        CodingSession result =
                service.start(
                        new StartCodingSessionCommand(
                                "LEETCODE",
                                "1"
                        )
                );

        assertThat(result).isNotNull();

        assertThat(result.problem())
                .isEqualTo(problem);

        assertThat(result.isActive())
                .isTrue();

        verify(codingSessionRepository)
                .save(any(CodingSession.class));
    }

    @Test
    void shouldReturnExistingActiveSession() {

        PlatformProblem problem =
                createProblem();

        CodingSession existingSession =
                CodingSession.start(problem);

        when(platformProblemRepository
                .findByPlatformProblemId(
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.of(problem));

        when(codingSessionRepository
                .findActiveByProblem(
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.of(existingSession));

        CodingSession result =
                service.start(
                        new StartCodingSessionCommand(
                                "LEETCODE",
                                "1"
                        )
                );

        assertThat(result)
                .isSameAs(existingSession);

        verify(codingSessionRepository, never())
                .save(any(CodingSession.class));
    }

    @Test
    void shouldCreateNewSessionAfterPreviousSessionIsCompleted() {

        PlatformProblem problem =
                createProblem();

        // Repository returns no ACTIVE session.
        // This means any previous session is completed/abandoned.
        when(platformProblemRepository
                .findByPlatformProblemId(
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.of(problem));

        when(codingSessionRepository
                .findActiveByProblem(
                        "LEETCODE",
                        "1"
                ))
                .thenReturn(Optional.empty());

        CodingSession newSession =
                CodingSession.start(problem);

        when(codingSessionRepository.save(any()))
                .thenReturn(newSession);

        CodingSession result =
                service.start(
                        new StartCodingSessionCommand(
                                "LEETCODE",
                                "1"
                        )
                );

        assertThat(result)
                .isSameAs(newSession);

        verify(codingSessionRepository)
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
                                "LEETCODE",
                                "999"
                        )
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Platform problem not found.");

        verify(codingSessionRepository, never())
                .findActiveByProblem(any(), any());

        verify(codingSessionRepository, never())
                .save(any());
    }

    @Test
    void shouldRejectBlankPlatform() {

        assertThatThrownBy(() ->
                service.start(
                        new StartCodingSessionCommand(
                                "",
                                "1"
                        )
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Platform cannot be blank.");

        verifyNoInteractions(platformProblemRepository);
        verifyNoInteractions(codingSessionRepository);
    }

    @Test
    void shouldRejectBlankProblemId() {

        assertThatThrownBy(() ->
                service.start(
                        new StartCodingSessionCommand(
                                "LEETCODE",
                                ""
                        )
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Platform problem ID cannot be blank."
                );

        verifyNoInteractions(platformProblemRepository);
        verifyNoInteractions(codingSessionRepository);
    }

    @Test
    void shouldNotReuseSessionWhenPlatformIsDifferent() {

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
                CodingSession.start(leetCodeProblem);

        when(platformProblemRepository
                .findByPlatformProblemId(
                        "GEEKS_FOR_GEEKS",
                        "1"
                ))
                .thenReturn(Optional.of(gfgProblem));

        when(codingSessionRepository
                .findActiveByProblem(
                        "GEEKS_FOR_GEEKS",
                        "1"
                ))
                .thenReturn(Optional.empty());

        when(codingSessionRepository.save(any()))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        CodingSession result =
                service.start(
                        new StartCodingSessionCommand(
                                "GEEKS_FOR_GEEKS",
                                "1"
                        )
                );

        assertThat(result)
                .isNotSameAs(leetCodeSession);

        assertThat(result.problem())
                .isEqualTo(gfgProblem);

        assertThat(result.isActive())
                .isTrue();

        verify(codingSessionRepository)
                .findActiveByProblem(
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