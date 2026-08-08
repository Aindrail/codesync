package com.codesync.session.domain.valueobject;

/**
 * Represents immutable source code.
 */
public record SourceCode(String value) {

    public SourceCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Source code cannot be null or blank.");
        }
    }

}
