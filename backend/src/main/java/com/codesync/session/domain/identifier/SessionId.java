package com.codesync.session.domain.identifier;

import java.util.UUID;

/**
 * Strongly typed identifier for a CodingSession.
 */
public record SessionId(UUID value) {

    public SessionId {
        if (value == null) {
            throw new IllegalArgumentException("Session ID cannot be null.");
        }
    }

    public static SessionId newId() {
        return new SessionId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}