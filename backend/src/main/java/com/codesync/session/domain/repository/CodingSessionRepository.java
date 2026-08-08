package com.codesync.session.domain.repository;

import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.identifier.SessionId;

import java.util.Optional;

public interface CodingSessionRepository {

    CodingSession save(CodingSession session);

    Optional<CodingSession> findBySessionId(SessionId sessionId);

    boolean existsBySessionId(SessionId sessionId);
}