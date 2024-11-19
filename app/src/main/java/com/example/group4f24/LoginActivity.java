package com.example.group4f24;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        emailField = findViewById(R.id.Email);
        passwordField = findViewById(R.id.Enter_Password);
        loginButton = findViewById(R.id.Login);

        databaseHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        String storedPassword = databaseHelper.getPassword(email);

        if (storedPassword == null) {
            Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (HashingHelper.verifyPassword(password, storedPassword)) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            // Redirect to main screen
        } else {
            Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
        }
    }
}
