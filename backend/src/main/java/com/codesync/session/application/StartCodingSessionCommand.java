package com.codesync.session.application;

public record StartCodingSessionCommand(
        Long userId,
        String platform,
        String platformProblemId
) {
}