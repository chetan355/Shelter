package com.example.shelter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.PeriodicSync;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.shelter.data.PetContract.PetEntry;
import com.example.shelter.data.PetHelperDb;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CatalogActivity extends AppCompatActivity {
    PetHelperDb mDbHelper;
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
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new PetHelperDb(this);
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        String[] projection = {PetEntry._ID,PetEntry.COLUMN_PET_NAME};
  //      String selection = PetEntry._ID+"=?";
//        String[] selectionArgs = new String[] {String.valueOf(PetEntry.GENDER_MALE)};
        Cursor cursor = db.query(PetEntry.TABLE_NAME,projection,null,null,null,null,null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("The pet table contains "+cursor.getCount()+" pets.\n\n");
            displayView.append(PetEntry._ID+" - "+PetEntry.COLUMN_PET_NAME+"\n");
            int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            while(cursor.moveToNext()){
                int petId = cursor.getInt(idColumnIndex);
                String petName = cursor.getString(nameColumnIndex);
                displayView.append(petId+" - "+petName+"\n");
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
    private void insertPet(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PetEntry.COLUMN_PET_NAME,"Toto");
        cv.put(PetEntry.COLUMN_PET_BREED,"Terrier");
        cv.put(PetEntry.COLUMN_PET_GENDER,PetEntry.GENDER_MALE);
        cv.put(PetEntry.COLUMN_PET_WEIGHT,7);
        long rowId = db.insert(PetEntry.TABLE_NAME,null,cv);
        Log.e("CatalogActivity", "New row ID: "+rowId);
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