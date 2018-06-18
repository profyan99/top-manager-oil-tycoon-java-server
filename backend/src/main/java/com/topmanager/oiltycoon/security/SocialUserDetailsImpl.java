package com.topmanager.oiltycoon.security;

import com.topmanager.oiltycoon.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.social.security.SocialUserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SocialUserDetailsImpl implements SocialUserDetails {

    private List<GrantedAuthority> list = new ArrayList<>();
    private User user;

    public SocialUserDetailsImpl(User user) {
        this.user = user;
        user.getRoles().forEach(r -> this.list.add(new SimpleGrantedAuthority(r.name())));
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getUserId() {
        return String.valueOf(user.getId());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return list;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
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
