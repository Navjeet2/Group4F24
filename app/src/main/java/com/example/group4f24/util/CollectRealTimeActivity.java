package com.example.group4f24.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.group4f24.R;
import com.example.group4f24.data.DatabaseHelper;
import com.example.group4f24.util.AccountInfoActivity;


public class CollectRealTimeActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private DatabaseHelper dbHelper;
    private double previousSpeed = 0;
    private long previousTime = 0;
    private double previousLatitude = 0;
    private double previousLongitude = 0;
    private double totalAcceleration = 0;
    private double totalBraking = 0;
    private double totalCornering = 0;
    private int tripCount = 0;
    private double totalScore = 0;
    private Button startTripButton;
    private Button endTripButton;
    private TextView scoreCircleTextView;
    private TextView averageScoreTextView, brakingTextView, speedingTextView, accelerationTextView, corneringTextView;
    private double totalSpeeding = 0;  // To accumulate speeding speed values
    private int speedingCount = 0;  // To count how many times the user was speeding
    private final double SPEEDING_THRESHOLD = 120.0;
    private boolean isTripActive = false;
    private int tripId;

    private int userId; // Replace with dynamically fetched userId from the login session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CollectRealTimeActivity", "Activity started successfully");
        // Set initial layout for the start trip
        setContentView(R.layout.first_page);  // This layout should have the Start Trip button

        // Initialize the Start Trip button
        startTripButton = findViewById(R.id.start_trip);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        ImageView profileButton = findViewById(R.id.profile);
        profileButton.setOnClickListener(view ->{
            setContentView(R.layout.account_info);
        });




        // Collect and save trip data

        // Set up the click listener for the "Start Trip" button
        startTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start collecting data when Start Trip is clicked
                startCollectingRealTimeData();

                // Switch to the homepage layout (with End Trip button)
                setContentView(R.layout.homepage);  // This layout should have the End Trip button

                // Find the score_circle TextView
                scoreCircleTextView = findViewById(R.id.score_circle);
                // Initialize the End Trip button
                endTripButton = findViewById(R.id.End_trip);

                ImageView backButton = findViewById(R.id.back_button);

                // Set click listener
                backButton.setOnClickListener(view -> {
                    setContentView(R.layout.first_page); // Switch back to the first_page layout
                });

                ImageView profileButton = findViewById(R.id.profile);
                profileButton.setOnClickListener(view ->{
                    setContentView(R.layout.account_info);
                });





                // Set up the click listener for the "End Trip" button
                endTripButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // End the trip and calculate the score
                        endTrip();
                    }
                });
            }
        });
    }

    private void startCollectingRealTimeData() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

       // Request location updates only if the trip has started
        if (!isTripActive) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
            isTripActive = true;  // Mark the trip as active
            Toast.makeText(CollectRealTimeActivity.this, "Trip Started", Toast.LENGTH_SHORT).show();
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // Real-time data collection
            double currentSpeed = location.getSpeed(); // speed in meters per second
            long currentTime = System.currentTimeMillis();
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();

            // Convert speed to km/h (multiply by 3.6)
            double speedInKmh = currentSpeed * 3.6;


            // Update the TextView with the current speed
            if (scoreCircleTextView != null) {
                scoreCircleTextView.setText(String.format("%.1f ", speedInKmh));  // Display speed in km/h
            }

            // Calculate speeding events (if the speed exceeds the threshold)
            if (speedInKmh > SPEEDING_THRESHOLD) {
                totalSpeeding += speedInKmh;
                speedingCount++;
            }

            // Calculate acceleration
            if (previousTime != 0) {
                double timeDiff = (currentTime - previousTime) / 1000.0; // time difference in seconds
                double speedDiff = currentSpeed - previousSpeed; // speed difference in m/s
                double acceleration = speedDiff / timeDiff; // acceleration in m/s²

                // Calculate braking (negative acceleration)
                if (acceleration < -0.5) {
                    totalBraking += acceleration;
                }

                // Calculate cornering (using change in direction and speed)
                if (currentLatitude != previousLatitude && currentLongitude != previousLongitude) {
                    double directionChange = Math.abs(currentLatitude - previousLatitude) + Math.abs(currentLongitude - previousLongitude);
                    double cornering = directionChange / timeDiff; // approximate cornering based on direction change
                    totalCornering += cornering;
                }

                totalAcceleration += acceleration;
                tripCount++;  // Increment trip count for each location update
            }

            // Update previous values for next calculation
            previousSpeed = currentSpeed;
            previousTime = currentTime;
            previousLatitude = currentLatitude;
            previousLongitude = currentLongitude;

            // Save location data to the database
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(currentTime));
            dbHelper.addLocation((int) tripId, currentLatitude, currentLongitude, timestamp);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    // Method to stop the trip and calculate the final score
    private void endTrip() {
        if (!isTripActive) {
            Toast.makeText(CollectRealTimeActivity.this, "No trip is active", Toast.LENGTH_SHORT).show();
            return;
        }

        // Stop collecting data
        locationManager.removeUpdates(locationListener);
        isTripActive = false;

        // Calculate the average acceleration, braking, and cornering
        double averageAcceleration = (tripCount > 0) ? (totalAcceleration / tripCount) : 0.0;
        double averageBraking = (tripCount > 0) ? (totalBraking / tripCount) : 0.0;
        double averageCornering = (tripCount > 0) ? (totalCornering / tripCount) : 0.0;

        double averageSpeeding = (speedingCount > 0) ? totalSpeeding / speedingCount : 0;

        // Calculate the overall score (this is just an example formula)
        double score = calculateScore(averageAcceleration, averageBraking, averageCornering);
        dbHelper.addScore(tripId, score);
        totalScore += score;

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        boolean tripSaved = dbHelper.addTrip(userId, averageSpeeding, averageAcceleration, averageBraking, averageCornering, score, timestamp);

        // Show the final score in a Toast (or navigate to the Dashboard page)
        Toast.makeText(CollectRealTimeActivity.this, "Trip Ended. Your Score: " + score, Toast.LENGTH_LONG).show();

        if (tripSaved) {
            Toast.makeText(this, "Trip Saved. Score: " + score, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error saving trip.", Toast.LENGTH_LONG).show();
        }

        setContentView(R.layout.dashboard);
        averageScoreTextView = findViewById(R.id.average_score_circle);
        brakingTextView = findViewById(R.id.braking_circle);
        speedingTextView = findViewById(R.id.speeding_circle);
        accelerationTextView = findViewById(R.id.accelerate_circle);
        corneringTextView = findViewById(R.id.cornering_circle);


        // Set the calculated values on the dashboard
        if (tripCount > 0) { // Ensure a trip was completed
            averageScoreTextView.setText(String.format("%.1f", score));
            brakingTextView.setText(String.format("%.1f", averageBraking));
            speedingTextView.setText(String.format("%.1f", averageSpeeding));
            accelerationTextView.setText(String.format("%.1f", averageAcceleration));
            corneringTextView.setText(String.format("%.1f", averageCornering));
        } else {
            // Set default or placeholder values when no trip data exists
            averageScoreTextView.setText("0.0");
            brakingTextView.setText("0.0");
            speedingTextView.setText("0.0");
            accelerationTextView.setText("0.0");
            corneringTextView.setText("0.0");
        }


        ImageView backButton = findViewById(R.id.back_button);

        // Set click listener
        backButton.setOnClickListener(view -> {
            setContentView(R.layout.homepage); // Switch back to the first_page layout
        });

        ImageView profileButton = findViewById(R.id.profile);
        profileButton.setOnClickListener(view ->{
            setContentView(R.layout.account_info);
        });



    }

    // Example algorithm to calculate the overall driving score
    private double calculateScore(double averageAcceleration, double averageBraking, double averageCornering) {
        double score = 100 - (Math.abs(averageAcceleration) * 10) - (Math.abs(averageBraking) * 10) - (averageCornering * 5);
        return Math.max(0, score); // Ensure score does not go below 0
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener); // Stop location updates when activity is destroyed
        }
    }
}