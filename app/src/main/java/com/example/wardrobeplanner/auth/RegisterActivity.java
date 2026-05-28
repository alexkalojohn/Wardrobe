package com.example.wardrobeplanner.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wardrobeplanner.databinding.ActivityRegisterBinding;
import com.example.wardrobeplanner.database.DatabaseHelper;
import com.example.wardrobeplanner.utils.EmailValidator;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        binding.buttonRegister.setOnClickListener(view -> attemptRegister());

        binding.textViewLogin.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void attemptRegister() {
        String username = binding.editTextUsername.getText().toString().trim();
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();
        String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();

        if (username.isEmpty()) {
            binding.editTextUsername.setError("Το username είναι υποχρεωτικό");
            return;
        }

        EmailValidator.ValidationResult emailResult = EmailValidator.validateWithAndroidPattern(email);
        if (!emailResult.isValid()) {
            binding.editTextEmail.setError(emailResult.getErrorMessage());
            return;
        }

        if (password.isEmpty()) {
            binding.editTextPassword.setError("Ο κωδικός είναι υποχρεωτικός");
            return;
        }

        if (password.length() < 6) {
            binding.editTextPassword.setError("Ο κωδικός πρέπει να είναι τουλάχιστον 6 χαρακτήρες");
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.editTextConfirmPassword.setError("Οι κωδικοί δεν ταιριάζουν");
            return;
        }

        if (dbHelper.usernameExists(username)) {
            binding.editTextUsername.setError("Το username χρησιμοποιείται ήδη");
            return;
        }

        if (dbHelper.emailExists(email)) {
            binding.editTextEmail.setError("Το email χρησιμοποιείται ήδη");
            return;
        }

        boolean success = dbHelper.registerUser(username, email, password);
        if (success) {
            Toast.makeText(this, "Επιτυχής εγγραφή! Παρακαλώ συνδεθείτε.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Αποτυχία εγγραφής. Δοκιμάστε ξανά.", Toast.LENGTH_SHORT).show();
        }
    }
}
