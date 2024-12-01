package com.example.group4f24.util;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group4f24.R;
import com.example.group4f24.UserRegistrationActivity;

public class LauncherPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launching_page); // Ensure this matches your XML filename

        // Delay of 5 seconds (5000 milliseconds)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Navigate to UserRegistrationActivity
                Intent intent = new Intent(LauncherPageActivity.this, UserRegistrationActivity.class);
                startActivity(intent);

                // Close the current activity to prevent going back to it
                finish();
            }
        }, 5000);
    }
}

