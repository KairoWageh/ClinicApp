package com.example.kairo.clinicapp;

/**
 * Created by kairo on 09/05/18.
 */

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.content.Intent;
import android.widget.Toast;
import android.app.LoaderManager;
import android.database.Cursor;
import android.content.CursorLoader;

import com.example.kairo.clinicapp.data.PatientContract.PatientEntry;
import com.example.kairo.clinicapp.data.PatientDbHelper;

/**
 * Allows user to create a new patient or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    /**
     * EditText field to enter the patient's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the patient's age
     */
    private EditText mAgeEditText;

    /**
     * EditText field to enter the patient's address
     */
    private EditText mAddressEditText;

    /**
     * EditText field to enter the patient's diease description
     */
    private EditText mDieaseDescEditText;

    /**
     * EditText field to enter the patient's medicine
     */
    private EditText mMedicineEditText;


    /**
     * EditText field to enter the patient's gender
     */
    private Spinner mGenderSpinner;

    /**
     * Gender of the patient. The possible valid values are in the PatientContract.java file:
     * {@link PatientEntry#GENDER_UNKNOWN}, {@link PatientEntry#GENDER_MALE}, or
     * {@link PatientEntry#GENDER_FEMALE}.
     */
    private int mGender = PatientEntry.GENDER_UNKNOWN;

    /** Identifier for the patient data loader */
    private static final int EXISTING_PATIENT_LOADER = 0;

    /** Content URI for the existing patient (null if it's a new patient) */
    private Uri mCurrentPatientUri;

    /** Boolean flag that keeps track of whether the patient has been edited (true) or not (false) */
    private boolean mPatientHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPatientHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPatientHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_editor );
        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled( true );
        }


        //getLoaderManager().initLoader(EXISTING_PATIENT_LOADER, null, this);

        /** Examine the intent that was used to launch this activity
         * in order to figure out if we're creating a new patient or editing an existing one
         *
         * */
        Intent intent = getIntent();
        mCurrentPatientUri = intent.getData();

        /**
         * If the intent does not contain a patient content URI, then we know that we are
         * creating a new patient.
         *
         * */

        if (mCurrentPatientUri == null) {
            // This is a new patient, so change the app bar to say "Add a Patient"
            setTitle(getString(R.string.editor_activity_title_new_patient));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a patient that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing patient, so change app bar to say "Edit Patient"
            setTitle(getString(R.string.editor_activity_title_edit_patient));

            // Initialize a loader to read the patient data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PATIENT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById( R.id.edit_patient_name );
        mAgeEditText = (EditText) findViewById( R.id.edit_patient_age );
        mAddressEditText = (EditText) findViewById( R.id.edit_patient_address );
        mDieaseDescEditText =(EditText) findViewById(R.id.edit_diease_desc);
        mMedicineEditText = (EditText) findViewById(R.id.edit_medicine);
        mGenderSpinner = (Spinner) findViewById( R.id.spinner_gender );

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the patient.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource( this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item );

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource( android.R.layout.simple_dropdown_item_1line );

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter( genderSpinnerAdapter );

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition( position );
                if (!TextUtils.isEmpty( selection )) {
                    if (selection.equals( getString( R.string.gender_male ) )) {
                        mGender = PatientEntry.GENDER_MALE; // Male
                    } else if (selection.equals( getString( R.string.gender_female ) )) {
                        mGender = PatientEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PatientEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                mGender = PatientEntry.GENDER_UNKNOWN;
            }
        } );
    }

    /**
     * Get user input from editor and save new patient into database.
     * */

    private void savePatient(){
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String ageString = mAgeEditText.getText().toString().trim();
        int ageInteger = Integer.parseInt(ageString);


        String addressString = mAddressEditText.getText().toString().trim();
        String dieaseDescString = mDieaseDescEditText.getText().toString().trim();
        String medicineString = mMedicineEditText.getText().toString().trim();

        // Check if this is supposed to be a new patient
        // and check if all the fields in the editor are blank
        if (mCurrentPatientUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(ageString) &&
                TextUtils.isEmpty(addressString) && TextUtils.isEmpty(dieaseDescString) &&
                TextUtils.isEmpty(medicineString) &&  mGender == PatientEntry.GENDER_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new patient.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and patient attributes from the editor are the values.
        //PatientDbHelper mDbHelper = new PatientDbHelper(this);
        //SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PatientEntry.COLUMN_PATIENT_NAME, nameString);
        values.put(PatientEntry.COLUMN_PATIENT_AGE, ageInteger);
        values.put(PatientEntry.COLUMN_PATIENT_ADDRESS, addressString);
        values.put(PatientEntry.COLUMN_PATIENT_DIEASE_DESC, dieaseDescString);
        values.put(PatientEntry.COLUMN_PATIENT_MEDICINE, medicineString);
        values.put(PatientEntry.COLUMN_PATIENT_GENDER, mGender);


        // Insert a new row for patient in the database, returning the ID of that new row.

        //Uri newUri = getContentResolver().insert(PatientEntry.CONTENT_URI, values);

        /*long newRowId = db.insert(PatientEntry.TABLE_NAME, null, values);

        //Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
             Toast.makeText(this, getString(R.string.editor_insert_patient_failed), Toast.LENGTH_SHORT).show();
        } else {
             // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, getString(R.string.editor_insert_patient_successful), Toast.LENGTH_SHORT).show();
        }*/

        // Determine if this is a new or existing patient by checking if mCurrentPatientUri is null or not
        if (mCurrentPatientUri == null) {
            // This is a NEW patient, so insert a new patient into the provider,
            // returning the content URI for the new patient.
            Uri newUri = getContentResolver().insert(PatientEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_patient_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_patient_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING patient, so update the patient with content URI: mCurrentPatientUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPatientUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentPatientUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_patient_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_patient_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate( R.menu.menu_editor, menu );
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new patient, hide the "Delete" menu item.
        if (mCurrentPatientUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save patient to database
                savePatient();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the patient hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPatientHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected( item );
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the patient hasn't changed, continue with handling back button press
        if (!mPatientHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all patient attributes, define a projection that contains
        // all columns from the patient table
        String[] projection = {
                PatientEntry._ID,
                PatientEntry.COLUMN_PATIENT_NAME,
                PatientEntry.COLUMN_PATIENT_AGE,
                PatientEntry.COLUMN_PATIENT_ADDRESS,
                PatientEntry.COLUMN_PATIENT_DIEASE_DESC,
                PatientEntry.COLUMN_PATIENT_MEDICINE,
                PatientEntry.COLUMN_PATIENT_GENDER
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentPatientUri,         // Query the content URI for the current patient
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of patient attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(PatientEntry.COLUMN_PATIENT_NAME);
            int ageColumnIndex = cursor.getColumnIndex(PatientEntry.COLUMN_PATIENT_AGE);
            int addressColumnIndex = cursor.getColumnIndex(PatientEntry.COLUMN_PATIENT_ADDRESS);
            int dieaseDescColumnIndex = cursor.getColumnIndex(PatientEntry.COLUMN_PATIENT_DIEASE_DESC);
            int medicineColumnIndex = cursor.getColumnIndex(PatientEntry.COLUMN_PATIENT_MEDICINE);
            int genderColumnIndex = cursor.getColumnIndex(PatientEntry.COLUMN_PATIENT_GENDER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int age = cursor.getInt(ageColumnIndex);
            String address = cursor.getString(addressColumnIndex);
            String dieaseDesc = cursor.getString(dieaseDescColumnIndex);
            String medicine = cursor.getString(medicineColumnIndex);
            int  gender = cursor.getInt(genderColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mAgeEditText.setText(Integer.toString(age));
            mAddressEditText.setText(address);
            mDieaseDescEditText.setText(dieaseDesc);
            mMedicineEditText.setText(medicine);

            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection

            switch (gender) {
                case PatientEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case PatientEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mAgeEditText.setText("");
        mAddressEditText.setText("");
        mDieaseDescEditText.setText("");
        mMedicineEditText.setText("");
        mGenderSpinner.setSelection(0); // Select "Unknown" gender
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the patient.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this patient.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the patient.
                deletePatient();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the patient.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the patient in the database.
     */
    private void deletePatient() {
        // Only perform the delete if this is an existing patient.
        if (mCurrentPatientUri != null) {
            // Call the ContentResolver to delete the patient at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPatientUri
            // content URI already identifies the patient that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentPatientUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_patient_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_patient_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}