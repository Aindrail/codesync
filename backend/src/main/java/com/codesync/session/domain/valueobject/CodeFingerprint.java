package com.codesync.session.domain.valueobject;

/**
 * Represents the normalized fingerprint of a solution.
 * Used for duplicate detection and solution comparison.
 */
public record CodeFingerprint(String value) {

    public CodeFingerprint {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Fingerprint cannot be null or blank.");
        }
    }

}