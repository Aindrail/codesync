package com.codesync.session.domain.valueobject;

import com.codesync.session.domain.enumtype.ProgrammingLanguage;

/**
 * Represents an immutable programming solution.
 * It should be a Value Object.
 *
 * Why?
 *
 * Two solutions are the same if:
 *
 * Source code is the same.
 * Language is the same.
 */
public record Solution(

        SourceCode sourceCode,

        ProgrammingLanguage language,

        CodeFingerprint fingerprint

) {

    public Solution {
        if (sourceCode == null) {
            throw new IllegalArgumentException("Source code cannot be null.");
        }

        if (language == null) {
            throw new IllegalArgumentException("Programming language cannot be null.");
        }

        if (fingerprint == null) {
            throw new IllegalArgumentException("Fingerprint cannot be null.");
        }
    }

}