package com.example.group4f24.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_management.db";
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_TRIPS = "trips";
    private static final String COLUMN_TRIP_ID = "trip_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_END_TIME = "end_time";
    private static final String COLUMN_AVERAGE_SPEED = "average_speed";
    private static final String COLUMN_SCORE_BRAKING = "score_braking";
    private static final String COLUMN_SCORE_SPEEDING = "score_speeding";
    private static final String COLUMN_SCORE_ACCELERATION = "score_acceleration";
    private static final String COLUMN_SCORE_CORNERING = "score_cornering";
    private static final String COLUMN_OVERALL_SCORE = "overall_score";

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
                COLUMN_START_TIME + " TEXT NOT NULL, " +
                COLUMN_END_TIME + " TEXT, " +
                COLUMN_AVERAGE_SPEED + " REAL, " +
                COLUMN_SCORE_BRAKING + " REAL, " +
                COLUMN_SCORE_SPEEDING + " REAL, " +
                COLUMN_SCORE_ACCELERATION + " REAL, " +
                COLUMN_SCORE_CORNERING + " REAL, " +
                COLUMN_OVERALL_SCORE + " REAL, " +
                "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
        db.execSQL(createTripsTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_TRIPS);
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

    public boolean addTrip(int userId, String startTime, String endTime, float avgSpeed, float brakingScore, float accelScore, float cornerScore, float overallScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_START_TIME, startTime);
        values.put(COLUMN_END_TIME, endTime);
        values.put(COLUMN_AVERAGE_SPEED, avgSpeed);
        values.put(COLUMN_SCORE_BRAKING, brakingScore);
        values.put(COLUMN_SCORE_ACCELERATION, accelScore);
        values.put(COLUMN_SCORE_CORNERING, cornerScore);
        values.put(COLUMN_OVERALL_SCORE, overallScore);

        long result = db.insert("trips", null, values);
        db.close();
        return result != -1;
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

    // Start a trip
    public long startTrip(int userId, String startTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_START_TIME, startTime);

        long result = db.insert(TABLE_TRIPS, null, values);
        db.close();
        return result; // Returns the trip_id
    }

    // End a trip
    public boolean endTrip(int tripId, String endTime, double avgSpeed, double braking, double speeding, double acceleration, double cornering) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_END_TIME, endTime);
        values.put(COLUMN_AVERAGE_SPEED, avgSpeed);
        values.put(COLUMN_SCORE_BRAKING, braking);
        values.put(COLUMN_SCORE_SPEEDING, speeding);
        values.put(COLUMN_SCORE_ACCELERATION, acceleration);
        values.put(COLUMN_SCORE_CORNERING, cornering);
        values.put(COLUMN_OVERALL_SCORE, (braking + speeding + acceleration + cornering) / 4.0);

        int result = db.update(TABLE_TRIPS, values, COLUMN_TRIP_ID + "=?", new String[]{String.valueOf(tripId)});
        db.close();
        return result > 0;
    }
    // Retrieve trip data for the dashboard
    public Cursor getDashboardData(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TRIPS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }
}

