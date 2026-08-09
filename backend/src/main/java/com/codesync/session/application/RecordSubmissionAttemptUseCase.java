package com.codesync.session.application;

import com.codesync.session.domain.entity.SubmissionAttempt;

public interface RecordSubmissionAttemptUseCase {

    SubmissionAttempt record(
            RecordSubmissionAttemptCommand command
    );
}