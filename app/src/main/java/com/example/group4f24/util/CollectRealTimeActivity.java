package com.example.group4f24.util;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group4f24.R;
import com.example.group4f24.data.DatabaseHelper;


public class CollectRealTimeActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private TextView averageSpeedTextView;
    private TextView brakingScoreTextView;
    private TextView speedingScoreTextView;
    private TextView accelerationScoreTextView;
    private TextView corneringScoreTextView;
    private TextView overallScoreTextView;

    private int currentUserId;
    private long currentTripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        setContentView(R.layout.homepage);
        setContentView(R.layout.first_page);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        startTimeTextView = findViewById(R.id.start_time_text);
        endTimeTextView = findViewById(R.id.end_time_text);
        averageSpeedTextView = findViewById(R.id.average_speed_text);
        brakingScoreTextView = findViewById(R.id.braking_score_text);
        speedingScoreTextView = findViewById(R.id.speeding_score_text);
        accelerationScoreTextView = findViewById(R.id.acceleration_score_text);
        corneringScoreTextView = findViewById(R.id.cornering_score_text);
        overallScoreTextView = findViewById(R.id.overall_score_text);

        // Assuming `currentUserId` is retrieved when the user logs in
        currentUserId = 1; // Example user id, replace with dynamic login logic

        // Start a new trip
        startTrip();
    }

    // Method to start a new trip
    private void startTrip() {
        String startTime = "2024-11-21 10:00:00"; // Use current time in real case
        currentTripId = dbHelper.startTrip(currentUserId, startTime);
        startTimeTextView.setText(startTime);
    }

    // Method to end the current trip and calculate scores
    private void endTrip() {
        // Example data for ending the trip
        String endTime = "2024-11-21 11:00:00"; // Use current time in real case
        double avgSpeed = 60.5;
        double brakingScore = 75.0;
        double speedingScore = 85.0;
        double accelerationScore = 80.0;
        double corneringScore = 70.0;

        boolean success = dbHelper.endTrip((int) currentTripId, endTime, avgSpeed, brakingScore, speedingScore, accelerationScore, corneringScore);

        if (success) {
            // Update the UI to reflect the trip's results
            endTimeTextView.setText(endTime);
            averageSpeedTextView.setText(String.valueOf(avgSpeed));
            brakingScoreTextView.setText(String.valueOf(brakingScore));
            speedingScoreTextView.setText(String.valueOf(speedingScore));
            accelerationScoreTextView.setText(String.valueOf(accelerationScore));
            corneringScoreTextView.setText(String.valueOf(corneringScore));

            double overallScore = (brakingScore + speedingScore + accelerationScore + corneringScore) / 4.0;
            overallScoreTextView.setText(String.valueOf(overallScore));

        } else {
            // Handle the error if the trip could not be ended
        }
    }

    // Method to retrieve trip data for the dashboard
    private void loadTripData() {
        Cursor cursor = dbHelper.getDashboardData(currentUserId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String startTime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_START_TIME));
                String endTime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_END_TIME));
                double avgSpeed = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AVERAGE_SPEED));
                double braking = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCORE_BRAKING));
                double speeding = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCORE_SPEEDING));
                double acceleration = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCORE_ACCELERATION));
                double cornering = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCORE_CORNERING));
                double overallScore = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_OVERALL_SCORE));

                // Display trip data on the dashboard (you can update UI here)
                // Example for displaying trip data in a simple TextView or RecyclerView

            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    // Call this method when you want to end the trip
    @Override
    protected void onStop() {
        super.onStop();
        endTrip();
    }

}
