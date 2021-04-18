package com.example.shelter.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class PetProvider extends ContentProvider {
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private PetHelperDb dbHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PETS = 100;
    private static final int PETS_ID = 101;
    static
    {
        uriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS , PETS);
        uriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#" , PETS_ID);
    }
    @Override
    public boolean onCreate() {
          dbHelper = new PetHelperDb(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int match = uriMatcher.match(uri);
        Cursor cursor;
        switch (match){
            case PETS:
                cursor = db.query(PetContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PETS_ID:
                selection = PetContract.PetEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PetContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown found"+uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        int match = uriMatcher.match(uri);
        switch(match){
            case PETS:
                return PetContract.PetEntry.CONTENT_LIST_TYPE;
            case PETS_ID:
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        final int match = uriMatcher.match(uri);
        switch (match)
        {
            case PETS:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertPet(Uri uri, ContentValues values) {
        //Sanity Checking :
        String petName = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        if(petName==null)
            throw new IllegalArgumentException("Pet name should not be null");

        String petBreed = values.getAsString(PetContract.PetEntry.COLUMN_PET_BREED);
        if(petBreed==null)
            throw new IllegalArgumentException("Pet breed should not be null");

        int petWeight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if(petWeight < 0)
            throw new IllegalArgumentException("Pet weight should not be less than 0");

        Integer petGender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        if(petGender==null|| !PetContract.PetEntry.isValidGender(petGender))
            throw new IllegalArgumentException("Pet name should not be null");

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(PetContract.PetEntry.TABLE_NAME,null,values);
        if(rowId==-1){
            Log.e(LOG_TAG, "Failed to insert row for"+uri);
        }
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, rowId);
    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        int noDeletedRows = deletePet(uri,selection,selectionArgs);
        return noDeletedRows;
    }
    private int deletePet(Uri uri, String selection, String[] selectionArgs){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int noOfDeletedRows;
        switch (match){
            case PETS:
                noOfDeletedRows = db.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);
                return noOfDeletedRows;
            case PETS_ID:
                selection = PetContract.PetEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                noOfDeletedRows = db.delete(PetContract.PetEntry.TABLE_NAME,selection,selectionArgs);
                return noOfDeletedRows;
            default:
                throw new IllegalStateException("Deletion is not supported for" + uri);
        }
    }
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, values, selection, selectionArgs);
            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(values.size()==0)
            return 0;
        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)){
            String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
            if(name==null){
                throw new IllegalArgumentException("Pet name should not be null");
            }
        }
        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_BREED)){
            String breed = values.getAsString(PetContract.PetEntry.COLUMN_PET_BREED);
            if(breed==null){
                throw new IllegalArgumentException("Pet breed should not be null");
            }
        }
        if(values.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT)){
            Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
            if(weight == null|| weight < 0){
                throw new IllegalArgumentException("Pet weight should not be less than 0/null");
            }
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowUpdated;
        rowUpdated = db.update(PetContract.PetEntry.TABLE_NAME,values,selection,selectionArgs);
        return rowUpdated;
    }

}
