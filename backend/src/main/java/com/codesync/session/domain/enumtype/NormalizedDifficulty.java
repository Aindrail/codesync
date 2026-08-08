package com.codesync.session.domain.enumtype;
/*
Platform difficulty belongs in the platform adapter.

Example:

platform/leetcode/
    DifficultyMapper
platform/codeforces/
    DifficultyMapper

Each platform translates its own difficulty into NormalizedDifficulty.

This keeps the domain independent of any platform.
 */

import com.codesync.common.constants.Displayable;

public enum NormalizedDifficulty implements Displayable {

    BEGINNER("Beginner"),

    EASY("Easy"),

    MEDIUM("Medium"),

    HARD("Hard"),

    EXPERT("Expert");

    private final String displayName;

    NormalizedDifficulty(String displayName) {
        this.displayName = displayName;
    }
    @Override
    public String displayName() {
        return displayName;
    }

}