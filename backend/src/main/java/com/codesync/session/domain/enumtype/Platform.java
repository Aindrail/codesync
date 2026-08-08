package com.codesync.session.domain.enumtype;

import com.codesync.common.constants.Displayable;

public enum Platform implements Displayable {

    LEETCODE("LeetCode"),

    GEEKS_FOR_GEEKS("GeeksForGeeks");

    private final String displayName;

    Platform(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String displayName() {
        return displayName;
    }
}