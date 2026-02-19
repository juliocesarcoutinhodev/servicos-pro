package com.servicepro.auth.domain.gateway;

import com.servicepro.auth.domain.model.User;

public interface UserGateway {

    boolean existsByEmail(String email);

    User save(User user);
}
