package com.example.tfg.register;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tfg.R;

public class RegisterViewModel extends ViewModel {

    private final MutableLiveData<RegistrationFormState> registrationFormState = new MutableLiveData<>();

    LiveData<RegistrationFormState> getRegistrationFormState() {
        return registrationFormState;
    }

    public void registerDataChanged(String username, String email, String password, String confirmPassword) {
        if (!isUserNameValid(username)) {
            registrationFormState.setValue(new RegistrationFormState(R.string.invalid_username, null, null, null));
        } else if (!isEmailValid(email)) {
            registrationFormState.setValue(new RegistrationFormState(null, R.string.invalid_email, null, null));
        } else if (!isPasswordValid(password)) {
            registrationFormState.setValue(new RegistrationFormState(null, null, R.string.invalid_password_register, null));
        } else if (!isConfirmPasswordValid(password, confirmPassword)) {
            registrationFormState.setValue(new RegistrationFormState(null, null, null, R.string.passwords_not_matching));
        } else {
            registrationFormState.setValue(new RegistrationFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        return username != null && username.trim().length() > 5;
    }

    // A placeholder email validation check
    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    // A placeholder confirm password validation check
    private boolean isConfirmPasswordValid(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public boolean isRegistrationValid() {
        RegistrationFormState state = registrationFormState.getValue();
        return state != null && state.isDataValid();
    }
}
