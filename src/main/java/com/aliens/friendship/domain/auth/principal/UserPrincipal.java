package com.aliens.friendship.domain.auth.principal;

import com.aliens.friendship.domain.member.domain.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static com.aliens.friendship.domain.member.domain.Member.Status.APPLIED;

@Getter
public class UserPrincipal implements UserDetails {

    private final String email;
    private final Member.Status memberStatus;
    Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.memberStatus == APPLIED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.memberStatus == APPLIED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.memberStatus == APPLIED;
    }

    @Override
    public boolean isEnabled() {
        return this.memberStatus == APPLIED;
    }

    private UserPrincipal(
            String email,
            Member.Status memberStatus,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.email = email;
        this.memberStatus = memberStatus;
        this.authorities = authorities;
    }

    public static UserPrincipal from(Member member) {
        return new UserPrincipal(
                member.getEmail(),
                member.getStatus(),
                member.getAuthorities()
        );
    }

    public static UserPrincipal of(
            String email,
            Member.Status memberStatus,
            Collection<? extends GrantedAuthority> authorities
    ) {
        return new UserPrincipal(
                email,
                memberStatus,
                authorities
        );
    }
}