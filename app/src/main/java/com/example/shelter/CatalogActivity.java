package com.example.shelter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shelter.data.PetContract;
import com.example.shelter.data.PetContract.PetEntry;
import com.example.shelter.data.PetHelperDb;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CatalogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    public void displayDatabaseInfo() {
        String[] projection = {PetEntry._ID,PetEntry.COLUMN_PET_NAME,PetEntry.COLUMN_PET_BREED,PetEntry.COLUMN_PET_WEIGHT,PetEntry.COLUMN_PET_GENDER};

  //      String selection = PetEntry._ID+"=?";
//        String[] selectionArgs = new String[] {String.valueOf(PetEntry.GENDER_MALE)};
        Cursor cursor = getContentResolver().query(PetContract.CONTENT_URI,projection,null,null,null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("The pet table contains "+cursor.getCount()+" pets.\n\n");
            displayView.append(PetEntry._ID+" - "+PetEntry.COLUMN_PET_NAME+" - "+PetEntry.COLUMN_PET_BREED+" - "+PetEntry.COLUMN_PET_WEIGHT+" - "+PetEntry.COLUMN_PET_GENDER+"\n");

            int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);
            int genderColumnINdex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);

            while(cursor.moveToNext()){
                int petId = cursor.getInt(idColumnIndex);
                String petName = cursor.getString(nameColumnIndex);
                String petBreed = cursor.getString(breedColumnIndex);
                String petWeight = cursor.getString(weightColumnIndex);
                int genderInt = cursor.getInt(genderColumnINdex);
                String gender;
                if(genderInt==1) gender="MALE";
                else if(genderInt==2)gender="FEMALE";
                else gender="UNKNOWN";
                displayView.append(petId+" - "+petName+" - "+petBreed+" - "+petWeight+" - "+gender+"\n");
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
    private void insertPet(){
        ContentValues cv = new ContentValues();
        cv.put(PetEntry.COLUMN_PET_NAME,"Toto");
        cv.put(PetEntry.COLUMN_PET_BREED,"Terrier");
        cv.put(PetEntry.COLUMN_PET_GENDER,PetEntry.GENDER_MALE);
        cv.put(PetEntry.COLUMN_PET_WEIGHT,7);
        Uri newUri = getContentResolver().insert(PetContract.CONTENT_URI,cv);
        if(newUri==null){
            Toast.makeText(this,R.string.insertion_failed,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,R.string.insertion_success,Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_insert_dummy_data :
                insertPet();
                displayDatabaseInfo();
            case R.id.action_delete_all_entries :
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}