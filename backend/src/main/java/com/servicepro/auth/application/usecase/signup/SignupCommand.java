package com.servicepro.auth.application.usecase.signup;

import com.servicepro.auth.domain.model.Role;

public record SignupCommand(
        String name,
        String email,
        String phone,
        String password,
        Role role
) {
}
