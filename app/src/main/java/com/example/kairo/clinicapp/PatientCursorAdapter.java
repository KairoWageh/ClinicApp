package com.example.kairo.clinicapp;

/**
 * Created by kairo on 25/05/18.
 */

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * {@link PatientCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of patient data as its data source. This adapter knows
 * how to create list items for each row of patient data in the {@link Cursor}.
 */
public class PatientCursorAdapter extends CursorAdapter {
    /**
     * Constructs a new {@link PatientCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public PatientCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the patient data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current patient can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView pName = (TextView) view.findViewById( R.id.pName );
        TextView pAge = (TextView) view.findViewById( R.id.pAge );
        TextView pDiease = (TextView) view.findViewById( R.id.pDiease );
        // Extract properties from cursor
        String patientName = cursor.getString( cursor.getColumnIndexOrThrow( "patient_name" ) );
        int patientAge = cursor.getInt( cursor.getColumnIndexOrThrow( "patient_age" ) );
        String patientDiease = cursor.getString( cursor.getColumnIndexOrThrow( "patient_diease_desc" ) );
        // Populate fields with extracted properties
        pName.setText( patientName );
        pAge.setText( String.valueOf( patientAge ) );
        pDiease.setText( patientDiease );
    }
}
