package com.example.shelter.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class PetContract {
    public static final String CONTENT_AUTHORITY = "com.example.shelter";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PETS = "Pets";

    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

    public static final class PetEntry implements BaseColumns {

        public static final String TABLE_NAME = "Pets";


        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_WEIGHT = "weight";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_UNKNOWN = 0;

        public static boolean isValidGender(int gender){
            if(gender==GENDER_MALE||gender==GENDER_FEMALE||gender==GENDER_UNKNOWN){
                return true;
            }
            return false;
        }


    }
}
