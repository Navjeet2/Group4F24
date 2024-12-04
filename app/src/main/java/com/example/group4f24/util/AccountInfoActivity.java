package com.example.group4f24.util;

import android.database.Cursor;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.database.Cursor;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group4f24.R;
import com.example.group4f24.data.DatabaseHelper;

public class AccountInfoActivity extends AppCompatActivity {
    private EditText editUsername, editEmail, editPassword;
    private TextView editTextView1, editTextView2, editTextView3;
    private DatabaseHelper dbHelper;
    private String userEmail;  // Assume this is the email of the logged-in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_info);

        ImageView backButton = findViewById(R.id.back_button);

        // Set click listener
        backButton.setOnClickListener(view -> {
            setContentView(R.layout.dashboard); // Switch back to the first_page layout
        });


        dbHelper = new DatabaseHelper(this);
        // Initialize views
        editUsername = findViewById(R.id.Change_Username);
        editEmail = findViewById(R.id.Email);
        editPassword = findViewById(R.id.Change_Password);
        editTextView1 = findViewById(R.id.edit_Text_View);
        editTextView2 = findViewById(R.id.edit_Text_View2);
        editTextView3 = findViewById(R.id.edit_Text_View3);
        dbHelper = new DatabaseHelper(this);

        // Get the email from the intent (passed from LoginActivity or UserRegistrationActivity)
        userEmail = getIntent().getStringExtra("USER_EMAIL");


        // If email is not found, show an error
        if (userEmail == null) {
            Toast.makeText(this, "Error: No logged-in user found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set the initial values from the database (optional)
        loadUserInfo();

        // Edit functionality
        editTextView1.setOnClickListener(v -> enableEditMode(editUsername));
        editTextView2.setOnClickListener(v -> enableEditMode(editEmail));
        editTextView3.setOnClickListener(v -> enableEditMode(editPassword));

        // Save changes to database
        findViewById(R.id.save_changes).setOnClickListener(v -> updateAccountInfo());
    }

    private void loadUserInfo() {
        // Retrieve user data from the database
        Cursor cursor = dbHelper.getUserInfo(userEmail); // Fetch user info (username, email, password)

        if (cursor != null && cursor.moveToFirst()) {
            // Get column indices
            int usernameIndex = cursor.getColumnIndex("username");
            int emailIndex = cursor.getColumnIndex("email");
            int passwordIndex = cursor.getColumnIndex("password");

            // Check if the columns are valid
            if (usernameIndex != -1 && emailIndex != -1 && passwordIndex != -1) {
                // Retrieve values from the cursor
                String username = cursor.getString(usernameIndex);
                String email = cursor.getString(emailIndex);
                String password = cursor.getString(passwordIndex);

                // Set the fetched data into the EditText fields
                editUsername.setText(username);
                editEmail.setText(email);
                editPassword.setText("••••••••");  // Show password as dots
                editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);  // Set password input type
            } else {
                Toast.makeText(this, "Error: Invalid column indices", Toast.LENGTH_SHORT).show();
            }

            cursor.close(); // Don't forget to close the cursor
        } else {
            Toast.makeText(this, "Error fetching user details.", Toast.LENGTH_SHORT).show();
        }
    }

    private void enableEditMode(EditText editText) {
        editText.setEnabled(true);  // Enable the EditText field for editing
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
    }

    private void updateAccountInfo() {
        String newUsername = editUsername.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String newPassword = editPassword.getText().toString().trim();

        // Validate inputs before updating
        if (newUsername.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = dbHelper.getUserIdByEmail(userEmail);

        // Check if userId is valid
        if (userId == -1) {
            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a timestamp (or use current time)
        String timestamp = String.valueOf(System.currentTimeMillis());
        // Update the user's info in the database
        boolean isUpdated = dbHelper.updateUserInfo(userEmail, newUsername, newEmail, newPassword);
        if (isUpdated) {
            // Record the update in the logs (or user updates table) using addUserUpdate
            dbHelper.addUserUpdate(userId,userEmail, newUsername, newEmail, newPassword);
            Toast.makeText(this, "Account information updated successfully", Toast.LENGTH_SHORT).show();

            loadUserInfo();
        } else {
            Toast.makeText(this, "Failed to update account information", Toast.LENGTH_SHORT).show();
        }

    }
}
