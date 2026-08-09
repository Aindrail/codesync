package com.codesync.session.application;

public record StartCodingSessionCommand(
        String platform,
        String platformProblemId
) {
}