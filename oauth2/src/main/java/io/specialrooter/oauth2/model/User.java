package io.specialrooter.oauth2.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
@Data
public class User implements UserDetails {
    private String id;
    private String password;
    private String username;
    private String realName;
    private Set<String> authorities = new HashSet<>();
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    //账户是否生效
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //账户是否锁定
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //凭证是否生效
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //是否激活
    @Override
    public boolean isEnabled() {
        return true;
    }
}
