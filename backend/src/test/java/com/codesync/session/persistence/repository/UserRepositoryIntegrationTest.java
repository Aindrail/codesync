package com.codesync.session.persistence.repository;

import com.codesync.session.domain.entity.User;
import com.codesync.session.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateAndRetrieveUserByGithubUserId() {

        User user =
                userRepository.save(
                        User.create("github-user-1001")
                );

        assertThat(user.id())
                .isNotNull();

        assertThat(user.githubUserId())
                .isEqualTo("github-user-1001");

        User found =
                userRepository
                        .findByGithubUserId(
                                "github-user-1001"
                        )
                        .orElseThrow();

        assertThat(found.id())
                .isEqualTo(user.id());

        assertThat(found.githubUserId())
                .isEqualTo("github-user-1001");
    }

    @Test
    void shouldFindUserById() {

        User user =
                userRepository.save(
                        User.create("github-user-1002")
                );

        User found =
                userRepository
                        .findById(user.id())
                        .orElseThrow();

        assertThat(found.id())
                .isEqualTo(user.id());

        assertThat(found.githubUserId())
                .isEqualTo("github-user-1002");
    }

    @Test
    void shouldNotAllowDuplicateGithubUserId() {

        userRepository.save(
                User.create("github-user-1003")
        );

        assertThatThrownBy(() ->
                userRepository.save(
                        User.create("github-user-1003")
                )
        )
                .isInstanceOf(
                        JpaSystemException.class
                );
    }

    @Test
    void shouldAllowDifferentGithubUsers() {

        User userA =
                userRepository.save(
                        User.create("github-user-1004")
                );

        User userB =
                userRepository.save(
                        User.create("github-user-1005")
                );

        assertThat(userA.id())
                .isNotEqualTo(userB.id());

        assertThat(userA.githubUserId())
                .isEqualTo("github-user-1004");

        assertThat(userB.githubUserId())
                .isEqualTo("github-user-1005");
    }
}