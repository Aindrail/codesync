package com.codesync.session.persistence.mapper;

import com.codesync.session.domain.entity.User;
import com.codesync.session.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {

        return User.reconstitute(
                entity.getId(),
                entity.getGithubUserId()
        );
    }

    public UserEntity toEntity(User user) {

        UserEntity entity =
                UserEntity.create();

        updateEntity(
                user,
                entity
        );

        return entity;
    }

    public void updateEntity(
            User user,
            UserEntity entity) {

        entity.setGithubUserId(
                user.githubUserId()
        );
    }
}