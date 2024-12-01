package com.example.group4f24.util;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class AccountInfoActivity {
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private EditText usernameEditText, emailEditText, passwordEditText;
    private TextView editUsername, editEmail, editPassword, logout, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_info);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Initialize views
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.Email);
        passwordEditText = findViewById(R.id.password);
        editUsername = findViewById(R.id.edit_Text_View);
        editEmail = findViewById(R.id.edit_Text_View2);
        editPassword = findViewById(R.id.edit_Text_View3);
        logout = findViewById(R.id.logout);
        delete = findViewById(R.id.delete);

        // Populate fields with user data
        loadUserData();

        // Set up listeners for edit actions
        editUsername.setOnClickListener(v -> editUsername());
        editEmail.setOnClickListener(v -> editEmail());
        editPassword.setOnClickListener(v -> editPassword());

        // Set up listeners for logout and delete
        logout.setOnClickListener(v -> logoutUser());
        delete.setOnClickListener(v -> deleteAccount());
    }

    private void loadUserData() {
        if (currentUser != null) {
            emailEditText.setText(currentUser.getEmail());
            // Assume username is stored in the user's display name
            usernameEditText.setText(currentUser.getDisplayName());
        } else {
            Toast.makeText(this, "No user signed in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void editUsername() {
        String newUsername = usernameEditText.getText().toString().trim();
        if (!newUsername.isEmpty()) {
            currentUser.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(newUsername)
                            .build())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Username updated successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update username: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
        }
    }

    private void editEmail() {
        String newEmail = emailEditText.getText().toString().trim();
        if (!newEmail.isEmpty()) {
            currentUser.updateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Email updated successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Email cannot be empty.", Toast.LENGTH_SHORT).show();
        }
    }

    private void editPassword() {
        String newPassword = passwordEditText.getText().toString().trim();
        if (!newPassword.isEmpty()) {
            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to update password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
        }
    }

    private void logoutUser() {
        auth.signOut();
        Intent intent = new Intent(AccountInfoActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void deleteAccount() {
        currentUser.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AccountInfoActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
