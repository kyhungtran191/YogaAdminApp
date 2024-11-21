package com.example.yogaadminapp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper



class AdminDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "universalYoga.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            """
            CREATE TABLE Course (
                id TEXT PRIMARY KEY,
                name TEXT,
                dayOfWeek TEXT,
                timeOfCourse TEXT,
                capacity INTEGER,
                duration INTEGER,
                price REAL,
                type TEXT,
                description TEXT
            )
            """
        )

        db?.execSQL(
            """
            CREATE TABLE ClassInstance (
                id TEXT PRIMARY KEY,
                courseId TEXT,
                date TEXT,
                teacher TEXT,
                comments TEXT,
                FOREIGN KEY(courseId) REFERENCES YogaCourse(id)
            )
            """
        )

        db?.execSQL(
            """
            CREATE TABLE Customer (
                id TEXT PRIMARY KEY,
                name TEXT,
                email TEXT,
                balance REAL DEFAULT 200
            )
            """
        )
        db?.execSQL(
            """
            CREATE TABLE Booking (
                id TEXT PRIMARY KEY,
                customerId TEXT,
                classInstaceId TEXT,
                bookingDate TEXT,
                bookingStatus TEXT,
                FOREIGN KEY(customerId) REFERENCES Customer(id),
                FOREIGN KEY(classInstaceId) REFERENCES ClassInstance(id)
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS YogaCourse")
        db?.execSQL("DROP TABLE IF EXISTS Schedule")
        db?.execSQL("DROP TABLE IF EXISTS Customer")
        db?.execSQL("DROP TABLE IF EXISTS Booking")
        onCreate(db)
    }
}