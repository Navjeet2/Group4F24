package com.example.group4f24;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.group4f24.data.DatabaseHelper;
import com.example.group4f24.util.AccountInfoActivity;
import com.example.group4f24.util.CollectRealTimeActivity;
import com.example.group4f24.util.HashingHelper;

public class LoginActivity extends AppCompatActivity {
    private EditText emailField, passwordField;
    protected TextView signupField;
    protected Button loginButton;
    private DatabaseHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Log.d("LoginActivity", "Navigating to CollectRealTimeActivity");
        databaseHelper = new DatabaseHelper(this);


        emailField = findViewById(R.id.Email);
        passwordField = findViewById(R.id.Enter_Password);
        loginButton = findViewById(R.id.login_screen);
        signupField = findViewById(R.id.Signup);

        signupField.setOnClickListener(v-> navigateToSignup());

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
                Intent intent = new Intent(LoginActivity.this, CollectRealTimeActivity.class);
                startActivity(intent);
                finish(); // Close the current activity to prevent going back to it
        } else {
            Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
        }

    }
    private  void navigateToSignup(){
        Intent intent= new Intent(LoginActivity.this, UserRegistrationActivity.class);
        startActivity(intent);
    }
}
