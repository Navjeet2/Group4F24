package com.example.group4f24.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_management.db";
    private static final int DATABASE_VERSION = 2;
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
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
            onCreate(db);
        }
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





