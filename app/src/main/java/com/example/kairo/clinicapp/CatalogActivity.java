package com.example.kairo.clinicapp;

/**
 * Created by kairo on 09/05/18.
 */

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import android.content.ContentValues;

import com.example.kairo.clinicapp.data.PatientContract.PatientEntry;
import com.example.kairo.clinicapp.data.PatientDbHelper;

/**
 * Displays list of patients that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    // Identifier for the patient data loader
    private static final int PATIENT_LOADER = 0;

    // Adapter for the ListView
    PatientCursorAdapter mCursorAdapter;

    //private PatientDbHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_catalog );

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( CatalogActivity.this, EditorActivity.class );
                startActivity( intent );
            }
        } );
        //mDBHelper = new PatientDbHelper( this );
        // Find the ListView which will be populated with the patient data
        ListView patientListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        patientListView.setEmptyView(emptyView);

        // setup an Adapter to create a list item for each row of patient data in the Cursor.
        // There is no patient data yet (until the loader finishes) so pass in null for the Cursor
        mCursorAdapter = new PatientCursorAdapter(this, null);
        patientListView.setAdapter(mCursorAdapter);

        // Setup the click listener
        patientListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                /** Form the content URI that represents the specific patient that was clicked on,
                 * by appending the "id" (passed as input to this method) onto the
                 * {@link PatientEntry#CONTENT_URI}.
                 **/
                Uri currentPatientUri = ContentUris.withAppendedId(PatientEntry.CONTENT_URI, id);
                intent.setData(currentPatientUri);
                startActivity(intent);
            }
        } );

        // Kick off the loader
        getLoaderManager().initLoader(PATIENT_LOADER, null, this);

    }

    /**
     * Helper method to insert hardcoded patient data into the database. For debugging purposes only.
     */

    private void insertPatient() {

        //SQLiteDatabase db = mDBHelper.getWritableDatabase();
        // Create a ContentValues object where column names are the keys,
        // and Sara's patient attributes are the values.
        ContentValues values = new ContentValues();
        values.put( PatientEntry.COLUMN_PATIENT_NAME, "Sara" );
        values.put( PatientEntry.COLUMN_PATIENT_AGE, 20 );
        values.put( PatientEntry.COLUMN_PATIENT_ADDRESS, "Mansoura" );
        values.put( PatientEntry.COLUMN_PATIENT_DIEASE_DESC, "Flu" );
        values.put( PatientEntry.COLUMN_PATIENT_MEDICINE, "dd" );
        values.put( PatientEntry.COLUMN_PATIENT_GENDER, 2 );

        // Insert a new row for Sara into the provider using the ContentResolver.
        // Use the {@link PatientEntry#CONTENT_URI} to indicate that we want to insert
        // into the patients database table.
        // Receive the new content URI that will allow us to access Sara's data in the future.
        //Uri newUri = getContentResolver().insert(PatientEntry.CONTENT_URI, values);

        //db.insert( PatientEntry.TABLE_NAME, null, values );

    }

    /**
     * Helper method to delete all patients in the database.
     */
    private void deleteAllPatients() {
        int rowsDeleted = getContentResolver().delete(PatientEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from patient database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate( R.menu.menu_catalog, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPatient();
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPatients();
                return true;
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                PatientEntry._ID,
                PatientEntry.COLUMN_PATIENT_NAME,
                PatientEntry.COLUMN_PATIENT_AGE,
                PatientEntry.COLUMN_PATIENT_DIEASE_DESC};

        // This loader will execute the contentProvider's query method on a background thread
        return new CursorLoader(this,    // Parent activity context
                PatientEntry.CONTENT_URI,       // Provider content URI to query
                projection,                     // Columns to include in the resulting Cursor
                null,                  // No selection clause
                null,               // No selection arguments
                null);                // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /** Update {@link PatientCursorAdapter } with this new cursor containing updated patient data **/
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
