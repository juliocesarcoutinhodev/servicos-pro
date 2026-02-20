package com.servicepro.auth.application.usecase.resetpassword;

public record ResetPasswordCommand(
        String email,
        String code,
        String newPassword
) {
}
