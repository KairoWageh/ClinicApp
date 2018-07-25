package com.example.kairo.clinicapp.data;

/**
 * Created by kairo on 10/05/18.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kairo.clinicapp.data.PatientContract.PatientEntry;

/**
 * Database helper for Patients app. Manages database creation and version management.
 */
public class PatientDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = PatientDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "clinic.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link PatientDbHelper}.
     *
     * @param context of the app
     */
    public PatientDbHelper(Context context) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the patients table
        String SQL_CREATE_PATIENT_TABLE = "CREATE TABLE " + PatientEntry.TABLE_NAME + " ("
        + PatientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + PatientEntry.COLUMN_PATIENT_NAME + " TEXT NOT NULL, "
        + PatientEntry.COLUMN_PATIENT_AGE + " INTEGER NOT NULL, "
        + PatientEntry.COLUMN_PATIENT_ADDRESS + " TEXT, "
        + PatientEntry.COLUMN_PATIENT_DIEASE_DESC + " INTEGER, "
        + PatientEntry.COLUMN_PATIENT_MEDICINE + " TEXT, "
        + PatientEntry.COLUMN_PATIENT_GENDER + " INTEGER NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PATIENT_TABLE);
}

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}