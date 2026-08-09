package com.codesync.session.application;

import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.repository.CodingSessionRepository;
import com.codesync.session.domain.repository.PlatformProblemRepository;
import com.codesync.session.domain.valueobject.PlatformProblem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StartCodingSessionService
        implements StartCodingSessionUseCase {

    private final PlatformProblemRepository platformProblemRepository;
    private final CodingSessionRepository codingSessionRepository;

    public StartCodingSessionService(
            PlatformProblemRepository platformProblemRepository,
            CodingSessionRepository codingSessionRepository) {

        this.platformProblemRepository =
                platformProblemRepository;

        this.codingSessionRepository =
                codingSessionRepository;
    }

    @Override
    public CodingSession start(
            StartCodingSessionCommand command) {

        validate(command);

        PlatformProblem problem =
                platformProblemRepository
                        .findByPlatformProblemId(
                                command.platform(),
                                command.platformProblemId()
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Platform problem not found."
                                )
                        );

        return codingSessionRepository
                .findActiveByProblem(
                        command.platform(),
                        command.platformProblemId()
                )
                .orElseGet(() -> {

                    CodingSession newSession =
                            CodingSession.start(problem);

                    return codingSessionRepository.save(
                            newSession
                    );
                });
    }

    private void validate(
            StartCodingSessionCommand command) {

        if (command == null) {
            throw new IllegalArgumentException(
                    "Command cannot be null."
            );
        }

        if (command.platform() == null
                || command.platform().isBlank()) {

            throw new IllegalArgumentException(
                    "Platform cannot be blank."
            );
        }

        if (command.platformProblemId() == null
                || command.platformProblemId().isBlank()) {

            throw new IllegalArgumentException(
                    "Platform problem ID cannot be blank."
            );
        }
    }
}