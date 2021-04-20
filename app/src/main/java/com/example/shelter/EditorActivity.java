package com.example.shelter;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.shelter.data.PetContract;
import com.example.shelter.data.PetContract.PetEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    private boolean mPetHasChanged = false;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = PetEntry.GENDER_UNKNOWN;
    private static final int URI_LOADER_ID = 1;
    Uri intentUri;
    private static int mode_checker = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        intentUri = intent.getData();
        if(intentUri==null){
            setTitle(R.string.editor_activity_title_new_pet);
            mode_checker = 1;
            invalidateOptionsMenu();
        }else{
            setTitle(R.string.editor_activity_title_edit_pet);
            mode_checker =  2;
            LoaderManager.getInstance(this).initLoader(URI_LOADER_ID,null,this);
        }
        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_pet_name);
        mBreedEditText = findViewById(R.id.edit_pet_breed);
        mWeightEditText = findViewById(R.id.edit_pet_weight);
        mGenderSpinner = findViewById(R.id.spinner_gender);
        setupSpinner();

        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);
    }
    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };
    // showing dialog when up or back is hit :
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener)
    {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
//when user hit back or Up button  :
    @Override
    public void onBackPressed() {
        if(!mPetHasChanged){
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

    private void savePet()
    {
        int weight = 0;
        ContentValues values = new ContentValues();
        String pet_name = String.valueOf(mNameEditText.getText()).trim();
        String pet_breed = String.valueOf(mBreedEditText.getText()).trim();
        String pet_weight = String.valueOf(mWeightEditText.getText()).trim();
        if(TextUtils.isEmpty(pet_name)&&TextUtils.isEmpty(pet_breed)&&mGender==PetEntry.GENDER_UNKNOWN&&TextUtils.isEmpty(pet_weight)){
            return;
        }
        values.put(PetEntry.COLUMN_PET_NAME, pet_name);
        values.put(PetEntry.COLUMN_PET_BREED, pet_breed);
        values.put(PetEntry.COLUMN_PET_GENDER, mGender);
        if(!TextUtils.isEmpty(pet_weight)){
            weight = Integer.parseInt(pet_weight);
        }
        values.put(PetEntry.COLUMN_PET_WEIGHT, weight);
        Uri newUri = getContentResolver().insert(PetContract.CONTENT_URI,values);
        if(newUri==null){
            Toast.makeText(this,R.string.insertion_failed,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,R.string.insertion_success,Toast.LENGTH_SHORT).show();
        }
    }
    private void updatePet(){
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME,String.valueOf(mNameEditText.getText()).trim());
        values.put(PetEntry.COLUMN_PET_BREED,String.valueOf(mBreedEditText.getText()).trim());
        values.put(PetEntry.COLUMN_PET_GENDER,mGender);
        int weight = Integer.parseInt(mWeightEditText.getText().toString());
        values.put(PetEntry.COLUMN_PET_WEIGHT,weight);
        int updatedRow = getContentResolver().update(intentUri,values,null,null);
        if(updatedRow<0){
            Toast.makeText(this, "Pet is not updated", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Pet is updated",Toast.LENGTH_SHORT).show();
        }
    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deletePet() {
        int deletedRows = getContentResolver().delete(intentUri,null,null);
        if(deletedRows!=0) {
            Toast.makeText(this, "Pet deletion successful", Toast.LENGTH_SHORT).show();
            NavUtils.navigateUpFromSameTask(EditorActivity.this);
        }
        else {
            Toast.makeText(this, "Pet deletion unsuccessful", Toast.LENGTH_SHORT).show();
            NavUtils.navigateUpFromSameTask(EditorActivity.this);
        }
    }
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }

            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetEntry.GENDER_UNKNOWN; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(intentUri==null){
            MenuItem deleteMenu = menu.findItem(R.id.action_delete);
            deleteMenu.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                if(mode_checker==1) {
                    savePet();
                    finish();
                    Log.e("EditorActivity", "onOptionsItemSelected: " + "Insert");
                    break;
                }else if(mode_checker==2){
                    updatePet();
                    finish();
                    Log.e("EditorActivity", "onOptionsItemSelected: " + "Update");
                    break;
                }
//                displayDatabaseInfo();
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if (!mPetHasChanged) {
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
            default:
            throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String []projection = {PetEntry.COLUMN_PET_NAME,PetEntry.COLUMN_PET_BREED,PetEntry.COLUMN_PET_GENDER,PetEntry.COLUMN_PET_WEIGHT};
        return new CursorLoader(this,intentUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {
            mNameEditText.setText(data.getString(data.getColumnIndex(PetEntry.COLUMN_PET_NAME)));
            mBreedEditText.setText(data.getString(data.getColumnIndex(PetEntry.COLUMN_PET_BREED)));
            mWeightEditText.setText(data.getString(data.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT)));
            int genderDigit = data.getInt(data.getColumnIndex(PetEntry.COLUMN_PET_GENDER));
            if (genderDigit == PetEntry.GENDER_MALE) {
                mGenderSpinner.setSelection(1);
            } else if (genderDigit == PetEntry.GENDER_FEMALE) {
                mGenderSpinner.setSelection(2);
            } else {
                mGenderSpinner.setSelection(0);
            }
        }
    }
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
