package com.example.group4f24;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.group4f24.data.DatabaseHelper;
import com.example.group4f24.util.CollectRealTimeActivity;
import com.example.group4f24.util.HashingHelper;

public class UserRegistrationActivity extends AppCompatActivity {

    private EditText usernameField, emailField, passwordField, confirmPasswordField;
    protected Button registerButton;
    private DatabaseHelper databaseHelper;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        usernameField = findViewById(R.id.Username);
        emailField = findViewById(R.id.Email);
        passwordField = findViewById(R.id.Enter_Password);
        confirmPasswordField = findViewById(R.id.Confirm_Password);
        registerButton = findViewById(R.id.btnRegister);
        TextView loginText = findViewById(R.id.Login);

        loginText.setOnClickListener(v -> navigateToLogin());



        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = usernameField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.checkUser(email)) {
            Toast.makeText(this, "Email already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = HashingHelper.hashPassword(password);
        boolean success = databaseHelper.addUser(username, email, hashedPassword);

        if (success) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserRegistrationActivity.this, CollectRealTimeActivity.class);
            startActivity(intent);
            finish(); // Redirect to login screen
        } else {
            Toast.makeText(this, "Registration failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToLogin(){
        Intent intent = new Intent(UserRegistrationActivity.this, com.example.group4f24.LoginActivity.class);
        startActivity(intent);
    }
}