package com.example.group4f24.util;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group4f24.R;

public class HomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Button btnStartTrip = findViewById(R.id.btnStartTrip);
        Button btnViewDashboard = findViewById(R.id.btnViewDashboard);

        btnStartTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to First Page
                Intent intent = new Intent(HomepageActivity.this, FirstPageActivity.class);
                startActivity(intent);
            }
        });

        btnViewDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Dashboard Page
                Intent intent = new Intent(HomepageActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });
    }
}