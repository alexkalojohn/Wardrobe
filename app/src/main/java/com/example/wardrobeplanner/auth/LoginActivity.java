package com.example.wardrobeplanner.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wardrobeplanner.MainActivity;
import com.example.wardrobeplanner.databinding.ActivityLoginBinding;
import com.example.wardrobeplanner.database.DatabaseHelper;
import com.example.wardrobeplanner.models.User;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private DatabaseHelper dbHelper;

    private static final String PREFS_NAME = "WardrobePrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "logged_in_user_id";
    private static final String KEY_LOGGED_IN_USERNAME = "logged_in_username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Έλεγχος αν ο χρήστης είναι ήδη συνδεδεμένος
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.contains(KEY_LOGGED_IN_USER_ID)) {
            startMainActivity();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);

        binding.buttonLogin.setOnClickListener(view -> attemptLogin());

        binding.textViewRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String identifier = binding.editTextUsername.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();

        if (identifier.isEmpty()) {
            binding.editTextUsername.setError("Πληκτρολογήστε username ή email");
            return;
        }

        if (password.isEmpty()) {
            binding.editTextPassword.setError("Πληκτρολογήστε κωδικό");
            return;
        }

        User user = dbHelper.loginUser(identifier, password);
        if (user != null) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_LOGGED_IN_USER_ID, user.getId());
            editor.putString(KEY_LOGGED_IN_USERNAME, user.getUsername());
            editor.apply();

            Toast.makeText(this, "Καλώς ήρθες, " + user.getUsername() + "!", Toast.LENGTH_SHORT).show();
            startMainActivity();
        } else {
            Toast.makeText(this, "Λάθος username/email ή κωδικός", Toast.LENGTH_SHORT).show();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
