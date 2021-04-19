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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shelter.data.PetContract;
import com.example.shelter.data.PetContract.PetEntry;
import com.example.shelter.data.PetHelperDb;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

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

        Cursor cursor = getContentResolver().query(PetContract.CONTENT_URI,projection,null,null,null);

            ListView listView = findViewById(R.id.list);
            PetCursorAdapter adapter = new PetCursorAdapter(this,cursor);
            listView.setAdapter(adapter);
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