package com.education.education.user.user.wrapper;

import com.education.education.user.user.entities.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    
    public final User user;

    public UserDetailsImpl(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        this.user = user;
    }
    
    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return user.getRole()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return user.getUsername();
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
        return user.getStatus() == com.education.education.user.user.enums.EStatus.ACTIVE;
    }

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(user);
    }
}
