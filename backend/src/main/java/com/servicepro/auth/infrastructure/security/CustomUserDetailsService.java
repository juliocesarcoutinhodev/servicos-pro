package com.servicepro.auth.infrastructure.security;

import com.servicepro.auth.domain.gateway.UserGateway;
import com.servicepro.auth.domain.model.User;
import com.servicepro.auth.domain.model.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserGateway userGateway;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedEmail;
        try {
            normalizedEmail = Email.of(username).value();
        } catch (RuntimeException exception) {
            throw new UsernameNotFoundException("Usuario nao encontrado.");
        }
        User user = userGateway.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado."));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                .disabled(!user.isActive())
                .build();
    }
}
