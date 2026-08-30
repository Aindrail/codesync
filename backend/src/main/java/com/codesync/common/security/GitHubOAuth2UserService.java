package com.codesync.common.security;

import com.codesync.session.domain.entity.User;
import com.codesync.session.domain.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class GitHubOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final DefaultOAuth2UserService delegate =
            new DefaultOAuth2UserService();

    public GitHubOAuth2UserService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(
            OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User githubUser = delegate.loadUser(userRequest);

        Object githubId = githubUser.getAttributes().get("id");

        if (githubId == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("github_identity_missing"),
                    "GitHub user ID was not provided."
            );
        }

        String githubUserId = githubId.toString();

        User user = userRepository
                .findByGithubUserId(githubUserId)
                .orElseGet(() ->
                        userRepository.save(
                                User.create(githubUserId)
                        )
                );

        Collection<? extends GrantedAuthority> authorities =
                githubUser.getAuthorities();

        return new CodeSyncOAuth2User(
                user,
                githubUser.getAttributes(),
                authorities
        );
    }
}