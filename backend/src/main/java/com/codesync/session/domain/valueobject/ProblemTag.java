package com.codesync.session.domain.valueobject;

public record ProblemTag(String value) {

    public ProblemTag {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Problem tag cannot be null or blank.");
        }
    }
}