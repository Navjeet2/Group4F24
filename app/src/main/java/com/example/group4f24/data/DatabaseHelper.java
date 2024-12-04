package com.example.group4f24.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_management.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // Trips table
    private static final String TABLE_TRIPS = "trips";
    private static final String COLUMN_TRIP_ID = "trip_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_SPEED = "speed";
    private static final String COLUMN_ACCELERATION = "acceleration";
    private static final String COLUMN_BRAKING = "braking";
    private static final String COLUMN_CORNERING = "cornering";
    private static final String COLUMN_TRIP_SCORE = "trip_score";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    // scores table
    private static final String TABLE_SCORES = "scores";
    private static final String COLUMN_SCORE_ID = "score_id";
    private static final String COLUMN_SCORE_TRIP_ID = "trip_id";
    private static final String COLUMN_SCORE = "score";

    // New locations table
    private static final String TABLE_LOCATIONS = "locations";
    private static final String COLUMN_LOCATION_ID = "location_id";
    private static final String COLUMN_LOCATION_TRIP_ID = "trip_id";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_LOCATION_TIMESTAMP = "location_timestamp";

    //  User Updates table
    private static final String TABLE_USER_UPDATES = "user_updates";
    private static final String COLUMN_UPDATE_ID = "update_id";
    private static final String COLUMN_UPDATE_USER_ID = "user_id";
    private static final String COLUMN_UPDATED_USERNAME = "updated_username";
    private static final String COLUMN_UPDATED_EMAIL = "updated_email";
    private static final String COLUMN_UPDATED_PASSWORD = "updated_password";
    private static final String COLUMN_UPDATE_TIMESTAMP = "update_timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT NOT NULL, " +
                COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(createTable);

        // Create Trips table
        String createTripsTable = "CREATE TABLE " + TABLE_TRIPS + " (" +
                COLUMN_TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_SPEED + " REAL NOT NULL, " +
                COLUMN_ACCELERATION + " REAL NOT NULL, " +
                COLUMN_BRAKING + " REAL NOT NULL, " +
                COLUMN_CORNERING + " REAL NOT NULL, " +
                COLUMN_TRIP_SCORE + " REAL NOT NULL, " +
                COLUMN_TIMESTAMP + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
        db.execSQL(createTripsTable);

        // Create scores table
        String createScoresTable = "CREATE TABLE " + TABLE_SCORES + " (" +
                COLUMN_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SCORE_TRIP_ID + " INTEGER NOT NULL, " +
                COLUMN_SCORE + " REAL NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_SCORE_TRIP_ID + ") REFERENCES " + TABLE_TRIPS + "(" + COLUMN_TRIP_ID + "))";
        db.execSQL(createScoresTable);

        // Create locations table
        String createLocationsTable = "CREATE TABLE " + TABLE_LOCATIONS + " (" +
                COLUMN_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LOCATION_TRIP_ID + " INTEGER NOT NULL, " +
                COLUMN_LATITUDE + " REAL NOT NULL, " +
                COLUMN_LONGITUDE + " REAL NOT NULL, " +
                COLUMN_LOCATION_TIMESTAMP + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_LOCATION_TRIP_ID + ") REFERENCES " + TABLE_TRIPS + "(" + COLUMN_TRIP_ID + "))";
        db.execSQL(createLocationsTable);

        // Create User Updates table
        String createUserUpdatesTable = "CREATE TABLE " + TABLE_USER_UPDATES + " (" +
                COLUMN_UPDATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_UPDATE_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_UPDATED_USERNAME + " TEXT NOT NULL, " +
                COLUMN_UPDATED_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COLUMN_UPDATED_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_UPDATE_TIMESTAMP + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_UPDATE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
        db.execSQL(createUserUpdatesTable);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_UPDATES);
            onCreate(db);

    }

    public boolean addUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public void addUserUpdate(int userId, String updatedUsername, String updatedEmail, String updatedPassword, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UPDATE_USER_ID, userId);
        values.put(COLUMN_UPDATED_USERNAME, updatedUsername);
        values.put(COLUMN_UPDATED_EMAIL, updatedEmail);
        values.put(COLUMN_UPDATED_PASSWORD, updatedPassword);
        values.put(COLUMN_UPDATE_TIMESTAMP, timestamp);

        long result = db.insert(TABLE_USER_UPDATES, null, values);
        db.close();
    }

    // Method to get user update history by user ID
    public Cursor getUserUpdates(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USER_UPDATES, null, COLUMN_UPDATE_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, COLUMN_UPDATE_TIMESTAMP + " DESC");
    }

    public boolean checkUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_EMAIL}, COLUMN_EMAIL + " = ?", new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public String getPassword(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_PASSWORD}, COLUMN_EMAIL + " = ?", new String[]{email}, null, null, null);

        if (cursor.moveToFirst()) {
            String password = cursor.getString(0);
            cursor.close();
            db.close();
            return password;
        }
        cursor.close();
        db.close();
        return null;
    }
    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM User WHERE email = ?", new String[]{email});

        if (cursor != null) {
            // Check if the column exists
            int columnIndex = cursor.getColumnIndex("id");
            if (columnIndex != -1 && cursor.moveToFirst()) {
                // If the column is found and the cursor is not empty, return the userId
                int userId = cursor.getInt(columnIndex);
                cursor.close();
                return userId;
            }
            cursor.close();
        }
        return -1;  // Return -1 if no user is found
    }

    public Cursor getUserInfo(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Query the database for the user's information
        Cursor cursor = db.query("users", new String[]{"username", "email", "password"}, "email = ?", new String[]{email}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Initialize the user info array
            String[] userInfo = new String[3];

            // Get column indices safely, ensuring they are >= 0
            int usernameIndex = cursor.getColumnIndex("username");
            int emailIndex = cursor.getColumnIndex("email");
            int passwordIndex = cursor.getColumnIndex("password");

            // Check if all columns are found
            if (usernameIndex != -1 && emailIndex != -1 && passwordIndex != -1) {
                userInfo[0] = cursor.getString(usernameIndex);
                userInfo[1] = cursor.getString(emailIndex);
                userInfo[2] = cursor.getString(passwordIndex);
                cursor.close();
                return db.query("users", new String[]{"username", "email", "password"}, "email = ?", new String[]{email}, null, null, null);
            } else {
                // Column not found, handle the error or log it
                cursor.close();
                return null;
            }
        }

        // If no data was found, return null
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
    public void addLocation(int tripId, double latitude, double longitude, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOCATION_TRIP_ID, tripId);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_LOCATION_TIMESTAMP, timestamp);

        long result = db.insert(TABLE_LOCATIONS, null, values);
        db.close();

    }

    public void addScore(int tripId, double score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE_TRIP_ID, tripId);
        values.put(COLUMN_SCORE, score);

        long result = db.insert(TABLE_SCORES, null, values);
        db.close();
    }

    // Method to get locations for a specific trip
    public Cursor getLocationsForTrip(int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_LOCATIONS, null, COLUMN_LOCATION_TRIP_ID + " = ?", new String[]{String.valueOf(tripId)}, null, null, COLUMN_LOCATION_TIMESTAMP + " ASC");
    }

    // Method to get score for a specific trip
    public Cursor getScoreForTrip(int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_SCORES, null, COLUMN_SCORE_TRIP_ID + " = ?", new String[]{String.valueOf(tripId)}, null, null, null);
    }

    public boolean updateUserInfo(String oldEmail, String newUsername, String newEmail, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", newUsername);
        contentValues.put("email", newEmail);
        contentValues.put("password", newPassword);

        int rowsAffected = db.update("users", contentValues, "email = ?", new String[]{oldEmail});
        return rowsAffected > 0;
    }


    // Add a method to insert trip data
    public boolean addTrip(int userId, double speed, double acceleration, double braking, double cornering, double tripScore, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_SPEED, speed);
        values.put(COLUMN_ACCELERATION, acceleration);
        values.put(COLUMN_BRAKING, braking);
        values.put(COLUMN_CORNERING, cornering);
        values.put(COLUMN_TRIP_SCORE, tripScore);
        values.put(COLUMN_TIMESTAMP, timestamp);

        long result = db.insert(TABLE_TRIPS, null, values);
        db.close();
        return result != -1;
    }

    // Add a method to fetch trip data for a user
    public Cursor getTripsForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TRIPS, null, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, COLUMN_TIMESTAMP + " DESC");
    }
}   





