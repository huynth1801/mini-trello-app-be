package com.huydev.skipli_be.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huydev.skipli_be.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Value
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    String id;
    String email;

    @JsonIgnore
    String password;

    Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(Users user) {
        List<GrantedAuthority> authorities = Collections.emptyList();

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                null,
                authorities
        );
    }

    public String getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public String getPassword() {
        return password;
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
