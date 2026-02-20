package com.servicepro.auth.application.usecase.resetpassword;

public record ResetPasswordCommand(
        String token,
        String newPassword
) {
}
