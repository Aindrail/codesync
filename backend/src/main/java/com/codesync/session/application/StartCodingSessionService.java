package com.codesync.session.application;

import com.codesync.session.domain.aggregate.CodingSession;
import com.codesync.session.domain.entity.User;
import com.codesync.session.domain.repository.CodingSessionRepository;
import com.codesync.session.domain.repository.PlatformProblemRepository;
import com.codesync.session.domain.repository.UserRepository;
import com.codesync.session.domain.valueobject.PlatformProblem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class StartCodingSessionService
        implements StartCodingSessionUseCase {

    private final PlatformProblemRepository platformProblemRepository;
    private final CodingSessionRepository codingSessionRepository;
    private final UserRepository userRepository;

    public StartCodingSessionService(
            PlatformProblemRepository platformProblemRepository,
            CodingSessionRepository codingSessionRepository,
            UserRepository userRepository) {

        this.platformProblemRepository =
                platformProblemRepository;

        this.codingSessionRepository =
                codingSessionRepository;

        this.userRepository =
                userRepository;
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

        /*
         * Only look for an active session belonging to
         * THIS user and THIS problem.
         *
         * User A + Problem X must not reuse
         * User B + Problem X.
         */
        Optional<CodingSession> existingSession =
                codingSessionRepository
                        .findActiveByUserAndProblem(
                                command.userId(),
                                command.platform(),
                                command.platformProblemId()
                        );

        if (existingSession.isPresent()) {
            return existingSession.get();
        }

        /*
         * We only need the User when we are actually
         * creating a new CodingSession.
         */
        User user =
                userRepository
                        .findById(command.userId())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "User not found."
                                )
                        );

        CodingSession newSession =
                CodingSession.start(
                        user,
                        problem
                );

        return codingSessionRepository.save(
                newSession
        );
    }

    private void validate(
            StartCodingSessionCommand command) {

        if (command == null) {
            throw new IllegalArgumentException(
                    "Command cannot be null."
            );
        }

        if (command.userId() == null) {
            throw new IllegalArgumentException(
                    "User ID cannot be null."
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