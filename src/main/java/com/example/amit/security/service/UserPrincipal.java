package com.example.amit.security.service;

import com.example.amit.common.constants.Role;
import com.example.amit.models.User;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public record UserPrincipal(User user, Set<Role> roles, Set<String> permissions) implements UserDetails {

    @NonNull
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r.name().toUpperCase())));
        permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p.toLowerCase())));
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @NonNull
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        Instant lockedUntil = user.getLockedUntil();
        return lockedUntil == null || lockedUntil.isBefore(Instant.now());
    }
}

