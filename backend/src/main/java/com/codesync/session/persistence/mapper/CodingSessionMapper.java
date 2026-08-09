package com.codesync.session.persistence.mapper;

import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.enumtype.SessionStatus;
import com.codesync.session.domain.identifier.SessionId;
import com.codesync.session.persistence.entity.CodingSessionEntity;
import com.codesync.session.persistence.entity.PlatformProblemEntity;
import com.codesync.session.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class CodingSessionMapper {

    private final PlatformProblemMapper platformProblemMapper;
    private final UserMapper userMapper;

    public CodingSessionMapper(
            PlatformProblemMapper platformProblemMapper,
            UserMapper userMapper) {

        this.platformProblemMapper =
                platformProblemMapper;

        this.userMapper =
                userMapper;
    }

    public CodingSessionEntity toEntity(
            CodingSession domain,
            PlatformProblemEntity existingProblemEntity,
            UserEntity existingUserEntity) {

        CodingSessionEntity entity =
                CodingSessionEntity.create();

        entity.setSessionId(
                domain.sessionId().value()
        );

        entity.setUser(
                existingUserEntity
        );

        entity.setProblem(
                existingProblemEntity
        );

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
                new SessionId(
                        entity.getSessionId()
                ),

                userMapper.toDomain(
                        entity.getUser()
                ),

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