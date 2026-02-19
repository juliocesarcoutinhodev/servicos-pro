package com.servicepro.auth.domain.gateway;

public interface PasswordHasher {

    String hash(String rawPassword);
}
