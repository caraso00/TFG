package com.example.tfg.register;

import androidx.annotation.Nullable;

public class RegistrationFormState {

    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer confirmPasswordError;
    private boolean isDataValid;

    RegistrationFormState(@Nullable Integer usernameError, @Nullable Integer emailError,
                          @Nullable Integer passwordError, @Nullable Integer confirmPasswordError) {
        this.usernameError = usernameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.confirmPasswordError = confirmPasswordError;
        this.isDataValid = false;
    }

    RegistrationFormState(boolean isDataValid) {
        this.usernameError = null;
        this.emailError = null;
        this.passwordError = null;
        this.confirmPasswordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getConfirmPasswordError() {
        return confirmPasswordError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
