package com.example.shelter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shelter.data.PetContract;
import com.example.shelter.data.PetContract.PetEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int URI_LOADER_ID = 1;
    PetCursorAdapter adapter;
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
        ListView listView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        adapter = new PetCursorAdapter(this,null);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currUri = ContentUris.withAppendedId(PetContract.CONTENT_URI,id);
                intent.setData(currUri);
                startActivity(intent);
            }
        });
        LoaderManager.getInstance(this).initLoader(URI_LOADER_ID,null,this);
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
/*    public void displayDatabaseInfo() {

        Cursor cursor = getContentResolver().query(PetContract.CONTENT_URI,projection,null,null,null);

            ListView listView = findViewById(R.id.list);
            adapter = new PetCursorAdapter(this,cursor);
            listView.setAdapter(adapter);

            View emptyView = findViewById(R.id.empty_view);
            listView.setEmptyView(emptyView);
    }
  */  private void insertPet(){
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
            case R.id.action_delete_all_entries :
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {PetEntry._ID,PetEntry.COLUMN_PET_NAME,PetEntry.COLUMN_PET_BREED,PetEntry.COLUMN_PET_WEIGHT,PetEntry.COLUMN_PET_GENDER};

        return new CursorLoader(this, PetContract.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}