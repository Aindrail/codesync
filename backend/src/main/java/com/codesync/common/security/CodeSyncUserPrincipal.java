package com.codesync.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CodeSyncUserPrincipal implements UserDetails {

    private final Long userId;
    private final String githubUserId;

    public CodeSyncUserPrincipal(
            Long userId,
            String githubUserId) {

        this.userId = userId;
        this.githubUserId = githubUserId;
    }

    public Long userId() {
        return userId;
    }

    public String githubUserId() {
        return githubUserId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return githubUserId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}