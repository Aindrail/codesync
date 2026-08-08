package com.codesync.session.domain.enumtype;

import com.codesync.common.constants.Displayable;

/**
 * Represents the lifecycle of a coding session.

 * Example:
 *
 * User opens problem -> Start Coding -> idle / struggle -> leaves laptop -> accepted -> GitHub Sync -> closed

 * The session has states.
 */
public enum SessionStatus implements Displayable {

    ACTIVE("Active"),

    PAUSED("Paused"),

    COMPLETED("Completed"),

    ABANDONED("Abandoned");

    private final String displayName;


    SessionStatus(String displayName) {
        this.displayName = displayName;
    }
    @Override
    public String displayName() {
        return displayName;
    }

}