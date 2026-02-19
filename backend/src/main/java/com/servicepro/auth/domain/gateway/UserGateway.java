package com.servicepro.auth.domain.gateway;

import com.servicepro.auth.domain.model.User;
import java.util.Optional;

public interface UserGateway {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    User save(User user);
}
