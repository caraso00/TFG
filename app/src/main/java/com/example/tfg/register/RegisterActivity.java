package com.example.tfg.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tfg.R;
import com.example.tfg.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;
    private EditText editTextUsername, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonCancel, buttonConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerViewModel = new ViewModelProvider(this, new RegisterViewModelFactory()).get(RegisterViewModel.class);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonConfirm = findViewById(R.id.buttonConfirm);

        registerViewModel.getRegistrationFormState().observe(this, registrationFormState -> {
            if (registrationFormState == null) {
                return;
            }
            if (registrationFormState.getUsernameError() != null) {
                editTextUsername.setError(getString(registrationFormState.getUsernameError()));
            }
            if (registrationFormState.getEmailError() != null) {
                editTextEmail.setError(getString(registrationFormState.getEmailError()));
            }
            if (registrationFormState.getPasswordError() != null) {
                editTextPassword.setError(getString(registrationFormState.getPasswordError()));
            }
            if (registrationFormState.getConfirmPasswordError() != null) {
                editTextConfirmPassword.setError(getString(registrationFormState.getConfirmPasswordError()));
            }

            if(registrationFormState.isDataValid()) {
                // User registration logic here (save the user data to your database)
                Toast.makeText(RegisterActivity.this, "Usuario registrado!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        buttonConfirm.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            registerViewModel.registerDataChanged(username, email, password, confirmPassword);
        });
    }
}


