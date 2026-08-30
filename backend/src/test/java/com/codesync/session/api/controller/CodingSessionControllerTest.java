package com.codesync.session.api.controller;

import com.codesync.common.exception.DuplicateSubmissionException;
import com.codesync.common.exception.ResourceNotFoundException;
import com.codesync.common.security.GitHubOAuth2UserService;
import com.codesync.common.security.SecurityConfig;
import com.codesync.session.application.RecordSubmissionAttemptUseCase;
import com.codesync.session.application.StartCodingSessionUseCase;
import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.entity.SubmissionAttempt;
import com.codesync.session.domain.enumtype.ProgrammingLanguage;
import com.codesync.session.domain.enumtype.SubmissionVerdict;
import com.codesync.session.domain.identifier.SessionId;
import com.codesync.session.domain.valueobject.ExecutionResult;
import com.codesync.session.domain.valueobject.Solution;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.codesync.common.security.CodeSyncOAuth2User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.mockito.ArgumentMatchers.argThat;

@WebMvcTest(CodingSessionController.class)
@Import(SecurityConfig.class)
class CodingSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StartCodingSessionUseCase startCodingSessionUseCase;

    @MockitoBean
    private RecordSubmissionAttemptUseCase recordSubmissionAttemptUseCase;

    @MockitoBean
    private GitHubOAuth2UserService gitHubOAuth2UserService;

    private CodeSyncOAuth2User authenticatedUser(Long userId) {
        CodeSyncOAuth2User user =
                org.mockito.Mockito.mock(CodeSyncOAuth2User.class);

        when(user.userId()).thenReturn(userId);

        return user;
    }


    private org.springframework.test.web.servlet.request.RequestPostProcessor authenticated(Long userId) {
        CodeSyncOAuth2User user = authenticatedUser(userId);
        return authentication(new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()));
    }

    @Test
    void shouldStartCodingSession() throws Exception {

        UUID sessionId = UUID.randomUUID();
        Instant startedAt = Instant.parse("2026-08-28T12:00:00Z");

        CodingSession session =
                org.mockito.Mockito.mock(CodingSession.class);

        when(session.sessionId()).thenReturn(new SessionId(sessionId));
        when(session.status()).thenReturn(
                com.codesync.session.domain.enumtype.SessionStatus.ACTIVE
        );
        when(session.startedAt()).thenReturn(startedAt);

        when(startCodingSessionUseCase.start(any()))
                .thenReturn(session);

        CodeSyncOAuth2User authenticatedUser =
                authenticatedUser(1L);

        String request = """
            {
              "platform": "LEETCODE",
              "platformProblemId": "1"
            }
            """;

        mockMvc.perform(
                        post("/api/v1/sessions")
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                authenticatedUser,
                                                null,
                                                authenticatedUser.getAuthorities()
                                        )
                                ))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId")
                        .value(sessionId.toString()))
                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"))
                .andExpect(jsonPath("$.startedAt")
                        .value(startedAt.toString()));

        verify(startCodingSessionUseCase).start(
                argThat(command ->
                        command.userId().equals(1L)
                                && command.platform().equals("LEETCODE")
                                && command.platformProblemId().equals("1")
                )
        );
    }

    @Test
    void shouldRecordSubmissionAttempt() throws Exception {
        UUID sessionId = UUID.randomUUID();
        Instant submittedAt = Instant.parse("2026-08-28T12:05:00Z");

        SubmissionAttempt attempt =
                org.mockito.Mockito.mock(SubmissionAttempt.class);

        Solution solution =
                org.mockito.Mockito.mock(Solution.class);

        ExecutionResult executionResult = new ExecutionResult(
                SubmissionVerdict.ACCEPTED,
                12,
                95.5,
                42000,
                88.2,
                50,
                50
        );

        when(attempt.attemptNumber()).thenReturn(1);
        when(attempt.platformSubmissionId()).thenReturn("123456789");
        when(attempt.solution()).thenReturn(solution);
        when(attempt.executionResult()).thenReturn(executionResult);
        when(attempt.submittedAt()).thenReturn(submittedAt);
        when(solution.language()).thenReturn(ProgrammingLanguage.JAVA);

        when(recordSubmissionAttemptUseCase.record(any()))
                .thenReturn(attempt);

        String request = """
                {
                  "attemptNumber": 1,
                  "platformSubmissionId": "123456789",
                  "sourceCode": "public class Solution {}",
                  "language": "JAVA",
                  "executionResult": {
                    "verdict": "ACCEPTED",
                    "runtimeInMillis": 12,
                    "runtimePercentile": 95.5,
                    "memoryInKb": 42000,
                    "memoryPercentile": 88.2,
                    "totalTestCases": 50,
                    "passedTestCases": 50
                  }
                }
                """;

        mockMvc.perform(
                        post("/api/v1/sessions/" + sessionId + "/attempts")
                                .with(authenticated(1L))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attemptNumber").value(1))
                .andExpect(jsonPath("$.platformSubmissionId").value("123456789"))
                .andExpect(jsonPath("$.language").value("JAVA"))
                .andExpect(jsonPath("$.executionResult.verdict").value("ACCEPTED"))
                .andExpect(jsonPath("$.executionResult.runtimeInMillis").value(12))
                .andExpect(jsonPath("$.executionResult.runtimePercentile").value(95.5))
                .andExpect(jsonPath("$.executionResult.memoryInKb").value(42000))
                .andExpect(jsonPath("$.executionResult.memoryPercentile").value(88.2))
                .andExpect(jsonPath("$.executionResult.totalTestCases").value(50))
                .andExpect(jsonPath("$.executionResult.passedTestCases").value(50))
                .andExpect(jsonPath("$.submittedAt").value(submittedAt.toString()));

        verify(recordSubmissionAttemptUseCase).record(
                argThat(command ->
                        command.userId().equals(1L)
                                && command.sessionId().equals(sessionId.toString())
                                && command.attemptNumber().equals(1)
                                && command.platformSubmissionId().equals("123456789")
                )
        );
    }

    @Test
    void shouldRejectUnauthenticatedSessionRequest() throws Exception {
        mockMvc.perform(
                        post("/api/v1/sessions")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "platform": "LEETCODE",
                                      "platformProblemId": "1"
                                    }
                                    """)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectSessionRequestWithoutCsrf() throws Exception {
        mockMvc.perform(
                        post("/api/v1/sessions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "platform": "LEETCODE",
                                          "platformProblemId": "1"
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectUnauthenticatedSubmissionRequest() throws Exception {
        UUID sessionId = UUID.randomUUID();

        mockMvc.perform(
                        post("/api/v1/sessions/" + sessionId + "/attempts")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "attemptNumber": 1,
                                      "platformSubmissionId": "123456789",
                                      "sourceCode": "public class Solution {}",
                                      "language": "JAVA",
                                      "executionResult": {
                                        "verdict": "ACCEPTED"
                                      }
                                    }
                                    """)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestForMalformedJson() throws Exception {
        mockMvc.perform(
                        post("/api/v1/sessions")
                                .with(authenticated(1L))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "platform":
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForMalformedSessionId() throws Exception {
        when(recordSubmissionAttemptUseCase.record(any()))
                .thenThrow(new IllegalArgumentException("Invalid session ID."));

        String request = """
                {
                  "attemptNumber": 1,
                  "platformSubmissionId": "123456789",
                  "sourceCode": "public class Solution {}",
                  "language": "JAVA",
                  "executionResult": {
                    "verdict": "ACCEPTED",
                    "runtimeInMillis": 12,
                    "runtimePercentile": 95.5,
                    "memoryInKb": 42000,
                    "memoryPercentile": 88.2,
                    "totalTestCases": 50,
                    "passedTestCases": 50
                  }
                }
                """;

        mockMvc.perform(
                        post("/api/v1/sessions/not-a-uuid/attempts")
                                .with(authenticated(1L))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictForDuplicateSubmission() throws Exception {
        when(recordSubmissionAttemptUseCase.record(any()))
                .thenThrow(new DuplicateSubmissionException(
                        "This solution has already been submitted by this user."
                ));

        String request = """
                {
                  "attemptNumber": 1,
                  "platformSubmissionId": "123456789",
                  "sourceCode": "public class Solution {}",
                  "language": "JAVA",
                  "executionResult": {
                    "verdict": "ACCEPTED",
                    "runtimeInMillis": 12,
                    "runtimePercentile": 95.5,
                    "memoryInKb": 42000,
                    "memoryPercentile": 88.2,
                    "totalTestCases": 50,
                    "passedTestCases": 50
                  }
                }
                """;

        mockMvc.perform(
                        post("/api/v1/sessions/" + UUID.randomUUID() + "/attempts")
                                .with(authenticated(1L))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail")
                        .value("This solution has already been submitted by this user."));
    }

    @Test
    void shouldReturnNotFoundWhenCodingSessionDoesNotExist() throws Exception {
        when(recordSubmissionAttemptUseCase.record(any()))
                .thenThrow(new ResourceNotFoundException(
                        "Coding session not found."
                ));

        String request = """
                {
                  "attemptNumber": 1,
                  "platformSubmissionId": "123456789",
                  "sourceCode": "public class Solution {}",
                  "language": "JAVA",
                  "executionResult": {
                    "verdict": "ACCEPTED",
                    "runtimeInMillis": 12,
                    "runtimePercentile": 95.5,
                    "memoryInKb": 42000,
                    "memoryPercentile": 88.2,
                    "totalTestCases": 50,
                    "passedTestCases": 50
                  }
                }
                """;

        mockMvc.perform(
                        post("/api/v1/sessions/" + UUID.randomUUID() + "/attempts")
                                .with(authenticated(1L))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail")
                        .value("Coding session not found."));
    }

    @Test
    void shouldUseAuthenticatedUserIdInsteadOfRequestUserId()
            throws Exception {

        CodingSession session =
                org.mockito.Mockito.mock(CodingSession.class);

        UUID sessionId = UUID.randomUUID();
        Instant startedAt = Instant.parse("2026-08-28T12:00:00Z");

        when(session.sessionId()).thenReturn(new SessionId(sessionId));
        when(session.status()).thenReturn(
                com.codesync.session.domain.enumtype.SessionStatus.ACTIVE
        );
        when(session.startedAt()).thenReturn(startedAt);

        when(startCodingSessionUseCase.start(any()))
                .thenReturn(session);

        CodeSyncOAuth2User authenticatedUser =
                authenticatedUser(42L);

        String request = """
            {
              "platform": "LEETCODE",
              "platformProblemId": "1"
            }
            """;

        mockMvc.perform(
                        post("/api/v1/sessions")
                                .with(authentication(
                                        new UsernamePasswordAuthenticationToken(
                                                authenticatedUser,
                                                null,
                                                authenticatedUser.getAuthorities()
                                        )
                                ))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated());

        verify(startCodingSessionUseCase).start(
                argThat(command ->
                        command.userId().equals(42L)
                )
        );
    }

    @Test
    void shouldReturnBadRequestWhenPlatformIsBlank() throws Exception {
        mockMvc.perform(
                        post("/api/v1/sessions")
                                .with(authenticated(1L))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "platform": "",
                                          "platformProblemId": "1"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenPlatformProblemIdIsBlank() throws Exception {
        mockMvc.perform(
                        post("/api/v1/sessions")
                                .with(authenticated(1L))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "platform": "LEETCODE",
                                          "platformProblemId": ""
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenAttemptNumberIsNotPositive() throws Exception {
        mockMvc.perform(
                        post("/api/v1/sessions/" + UUID.randomUUID() + "/attempts")
                                .with(authenticated(1L))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "attemptNumber": 0,
                                          "platformSubmissionId": "123",
                                          "sourceCode": "public class Solution {}",
                                          "language": "JAVA",
                                          "executionResult": {
                                            "verdict": "ACCEPTED"
                                          }
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenSourceCodeIsBlank() throws Exception {
        mockMvc.perform(
                        post("/api/v1/sessions/" + UUID.randomUUID() + "/attempts")
                                .with(authenticated(1L))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "attemptNumber": 1,
                                          "platformSubmissionId": "123",
                                          "sourceCode": "",
                                          "language": "JAVA",
                                          "executionResult": {
                                            "verdict": "ACCEPTED"
                                          }
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenLanguageIsMissing() throws Exception {
        mockMvc.perform(
                        post("/api/v1/sessions/" + UUID.randomUUID() + "/attempts")
                                .with(authenticated(1L))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "attemptNumber": 1,
                                          "platformSubmissionId": "123",
                                          "sourceCode": "public class Solution {}",
                                          "executionResult": {
                                            "verdict": "ACCEPTED"
                                          }
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenExecutionResultIsMissing() throws Exception {
        mockMvc.perform(
                        post("/api/v1/sessions/" + UUID.randomUUID() + "/attempts")
                                .with(authenticated(1L))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "attemptNumber": 1,
                                          "platformSubmissionId": "123",
                                          "sourceCode": "public class Solution {}",
                                          "language": "JAVA"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenRuntimeIsNegative() throws Exception {
        mockMvc.perform(
                        post("/api/v1/sessions/" + UUID.randomUUID() + "/attempts")
                                .with(authenticated(1L))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "attemptNumber": 1,
                                          "platformSubmissionId": "123",
                                          "sourceCode": "public class Solution {}",
                                          "language": "JAVA",
                                          "executionResult": {
                                            "verdict": "ACCEPTED",
                                            "runtimeInMillis": -1
                                          }
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenMemoryIsNegative() throws Exception {
        mockMvc.perform(
                        post("/api/v1/sessions/" + UUID.randomUUID() + "/attempts")
                                .with(authenticated(1L))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "attemptNumber": 1,
                                          "platformSubmissionId": "123",
                                          "sourceCode": "public class Solution {}",
                                          "language": "JAVA",
                                          "executionResult": {
                                            "verdict": "ACCEPTED",
                                            "memoryInKb": -1
                                          }
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenTestCaseCountIsNegative() throws Exception {
        mockMvc.perform(
                        post("/api/v1/sessions/" + UUID.randomUUID() + "/attempts")
                                .with(authenticated(1L))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "attemptNumber": 1,
                                          "platformSubmissionId": "123",
                                          "sourceCode": "public class Solution {}",
                                          "language": "JAVA",
                                          "executionResult": {
                                            "verdict": "ACCEPTED",
                                            "totalTestCases": -1
                                          }
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }
}