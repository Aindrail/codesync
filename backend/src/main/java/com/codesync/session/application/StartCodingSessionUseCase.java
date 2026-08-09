package com.codesync.session.application;

import com.codesync.session.domain.aggregate.CodingSession;

public interface StartCodingSessionUseCase {

    CodingSession start(StartCodingSessionCommand command);
}