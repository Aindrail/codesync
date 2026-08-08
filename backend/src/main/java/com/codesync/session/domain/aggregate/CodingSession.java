package com.codesync.session.domain.aggregate;

import com.codesync.session.domain.enumtype.SessionStatus;
import com.codesync.session.domain.identifier.SessionId;
import com.codesync.session.domain.valueobject.PlatformProblem;

import java.time.Duration;
import java.time.Instant;

/**
 * Aggregate root representing a user's coding session.
 */
public final class CodingSession {

    private final SessionId sessionId;

    private final PlatformProblem problem;

    private SessionStatus status;

    private final Instant startedAt;

    private Instant endedAt;

    private CodingSession(
            SessionId sessionId,
            PlatformProblem problem,
            SessionStatus status,
            Instant startedAt,
            Instant endedAt) {

        this.sessionId = sessionId;
        this.problem = problem;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    public static CodingSession start(PlatformProblem problem) {

        if (problem == null) {
            throw new IllegalArgumentException("Problem cannot be null.");
        }

        return new CodingSession(
                SessionId.newId(),
                problem,
                SessionStatus.ACTIVE,
                Instant.now(),
                null
        );
    }

    public static CodingSession reconstitute(
            SessionId sessionId,
            PlatformProblem problem,
            SessionStatus status,
            Instant startedAt,
            Instant endedAt) {

        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID cannot be null.");
        }

        if (problem == null) {
            throw new IllegalArgumentException("Problem cannot be null.");
        }

        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }

        if (startedAt == null) {
            throw new IllegalArgumentException("Started time cannot be null.");
        }

        return new CodingSession(
                sessionId,
                problem,
                status,
                startedAt,
                endedAt
        );
    }

    public void complete() {

        if (status != SessionStatus.ACTIVE) {
            throw new IllegalStateException("Only active sessions can be completed.");
        }

        status = SessionStatus.COMPLETED;
        endedAt = Instant.now();
    }

    public void abandon() {

        if (status != SessionStatus.ACTIVE) {
            throw new IllegalStateException("Only active sessions can be abandoned.");
        }

        status = SessionStatus.ABANDONED;
        endedAt = Instant.now();
    }

    public boolean isActive() {
        return status == SessionStatus.ACTIVE;
    }

    public Duration duration() {

        Instant end = endedAt == null
                ? Instant.now()
                : endedAt;

        return Duration.between(startedAt, end);
    }

    public SessionId sessionId() {
        return sessionId;
    }

    public PlatformProblem problem() {
        return problem;
    }

    public SessionStatus status() {
        return status;
    }

    public Instant startedAt() {
        return startedAt;
    }

    public Instant endedAt() {
        return endedAt;
    }

}