package org.zerhusen.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.zerhusen.model.ams.AmsPatientAuthority;
import org.zerhusen.model.ams.Ams_patient_users;
import org.zerhusen.model.security.Authority;
import org.zerhusen.model.security.User;

public final class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getMobilenumber(),
                user.getEmail(),
                user.getPassword(),
                mapToGrantedAuthorities(user.getAuthorities()),
                user.getEnabled(),
                user.getLastPasswordResetDate()
        );
    }

    
    
    
    private static List<GrantedAuthority> mapToGrantedAuthorities(Collection<Authority> collection) {
        return collection.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());
    }
    
    
}
