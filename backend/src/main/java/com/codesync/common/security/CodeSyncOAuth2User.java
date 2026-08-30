package com.codesync.common.security;

import com.codesync.session.domain.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public final class CodeSyncOAuth2User implements OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public CodeSyncOAuth2User(
            User user,
            Map<String, Object> attributes,
            Collection<? extends GrantedAuthority> authorities) {

        this.user = user;
        this.attributes = attributes;
        this.authorities = authorities;
    }

    public Long userId() {
        return user.id();
    }

    public String githubUserId() {
        return user.githubUserId();
    }

    public User user() {
        return user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return user.githubUserId();
    }
}