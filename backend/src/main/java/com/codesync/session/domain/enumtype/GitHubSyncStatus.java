package com.codesync.session.domain.enumtype;

import com.codesync.common.constants.Displayable;

/**
 * Represents the synchronization status of a GitHub upload.
 * This directly supports our retry queue and exponential backoff requirements
 */
public enum GitHubSyncStatus implements Displayable {

    PENDING("Pending"),

    IN_PROGRESS("In Progress"),

    SUCCESS("Success"),

    FAILED("Failed"),

    RETRY_SCHEDULED("Retry Scheduled");

    private final String displayName;

    GitHubSyncStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String displayName() {
        return displayName;
    }
}