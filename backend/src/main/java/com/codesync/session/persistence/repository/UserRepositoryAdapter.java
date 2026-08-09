package com.codesync.session.persistence.repository;

import com.codesync.session.domain.entity.User;
import com.codesync.session.domain.repository.UserRepository;
import com.codesync.session.persistence.entity.UserEntity;
import com.codesync.session.persistence.mapper.UserMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class UserRepositoryAdapter
        implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    public UserRepositoryAdapter(
            UserJpaRepository jpaRepository,
            UserMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {

        UserEntity entity =
                user.id() == null
                        ? mapper.toEntity(user)
                        : jpaRepository
                        .findById(user.id())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "User not found."
                                )
                        );

        if (user.id() != null) {
            mapper.updateEntity(
                    user,
                    entity
            );
        }

        return mapper.toDomain(
                jpaRepository.save(entity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {

        return jpaRepository
                .findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByGithubUserId(
            String githubUserId) {

        return jpaRepository
                .findByGithubUserId(githubUserId)
                .map(mapper::toDomain);
    }
}