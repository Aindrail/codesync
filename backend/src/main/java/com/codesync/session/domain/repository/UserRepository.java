package com.codesync.session.domain.repository;

import com.codesync.session.domain.entity.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByGithubUserId(
            String githubUserId
    );
}