package com.codesync.session.domain.entity;

public final class User {

    private final Long id;
    private final String githubUserId;

    private User(
            Long id,
            String githubUserId) {

        this.id = id;
        this.githubUserId = githubUserId;
    }

    public static User create(String githubUserId) {

        if (githubUserId == null
                || githubUserId.isBlank()) {

            throw new IllegalArgumentException(
                    "GitHub user ID cannot be blank."
            );
        }

        return new User(
                null,
                githubUserId
        );
    }

    public static User reconstitute(
            Long id,
            String githubUserId) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "User ID cannot be null."
            );
        }

        if (githubUserId == null
                || githubUserId.isBlank()) {

            throw new IllegalArgumentException(
                    "GitHub user ID cannot be blank."
            );
        }

        return new User(
                id,
                githubUserId
        );
    }

    public Long id() {
        return id;
    }

    public String githubUserId() {
        return githubUserId;
    }
}