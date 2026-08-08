package com.codesync.session.persistence.mapper;

import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.enumtype.SessionStatus;
import com.codesync.session.domain.identifier.SessionId;
import com.codesync.session.persistence.entity.CodingSessionEntity;
import com.codesync.session.persistence.entity.PlatformProblemEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CodingSessionMapper {

    private final PlatformProblemMapper platformProblemMapper;

    public CodingSessionMapper(
            PlatformProblemMapper platformProblemMapper) {

        this.platformProblemMapper = platformProblemMapper;
    }

    public CodingSessionEntity toEntity(
            CodingSession domain,
            PlatformProblemEntity existingProblemEntity) {

        CodingSessionEntity entity =
                CodingSessionEntity.create();

        entity.setSessionId(
                domain.sessionId().value()
        );

        entity.setProblem(existingProblemEntity);

        entity.setStatus(
                domain.status().name()
        );

        entity.setStartedAt(
                domain.startedAt()
        );

        entity.setEndedAt(
                domain.endedAt()
        );

        return entity;
    }

    public CodingSession toDomain(
            CodingSessionEntity entity) {

        return CodingSession.reconstitute(
                new SessionId(entity.getSessionId()),
                platformProblemMapper.toDomain(
                        entity.getProblem()
                ),
                SessionStatus.valueOf(
                        entity.getStatus()
                ),
                entity.getStartedAt(),
                entity.getEndedAt()
        );
    }
}