package com.example.kairo.clinicapp.data;

import android.provider.BaseColumns;
import android.net.Uri;
import android.content.ContentResolver;
import android.util.Log;

/**
 * Created by kairo on 09/05/18.
 */

public final class PatientContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private PatientContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */

    public static final String CONTENT_AUTHORITY = "com.example.kairo.clinicapp";


    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.patient/patient/ is a valid path for
     * looking at pet data. content://com.example.android.patient/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */

    public static final String PATH_PATIENTS = "clinicapp";

    /* Inner class that defines the table contents of patients table */

    public static final class PatientEntry implements BaseColumns{

        /** The content URI to access the patient data in the provider */

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PATIENTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of patiets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PATIENTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PATIENTS;

        // Table name
        public static final String TABLE_NAME = "patient";

        // Patient id
        public final static String _ID = BaseColumns._ID;

        // Patient name
        public static final String COLUMN_PATIENT_NAME = "patient_name";

        // Patient age
        public static final String COLUMN_PATIENT_AGE = "patient_age";

        // Patient address
        public static final String COLUMN_PATIENT_ADDRESS = "patient_address";

        // Patient diease description
        public static final String COLUMN_PATIENT_DIEASE_DESC = "patient_diease_desc";

        // Patient medicine
        public static final String COLUMN_PATIENT_MEDICINE = "patient_medicine";

        // Patient gender
        public static final String COLUMN_PATIENT_GENDER = "gender";

        public final static int GENDER_UNKNOWN = 0;

        public final static int GENDER_MALE = 1;

        public final static int GENDER_FEMALE = 2;


        /**
         * Returns whether or not the given gender is {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         */
        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }


        public static boolean isValidAge(int age) {
            if (age > 0) {
                return true;
            }
            return false;
        }

    }
}
