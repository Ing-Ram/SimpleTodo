package com.codepath.ingram.simpletodo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

//import static android.os.FileUtils.*;
import static org.apache.commons.io.FileUtils.readLines;
import static org.apache.commons.io.FileUtils.writeLines;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;  // this doesn't matter at the moment


    List<String> items;


    Button add;
    EditText etitem;
    RecyclerView rvitems;
    ItemsAdapter itemsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = findViewById(R.id.add);
        etitem = findViewById(R.id.etItem);
        rvitems = findViewById(R.id.rvitems);


        // Create Mock data in an Arraylist
        loadItems();
//        items = new ArrayList<>();
//        items.add("Vote in Election");
//        items.add("Do 10 pushups");
//        items.add("Drink 80 ounces of water");

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
                // delete the item from the model
                items.remove(position);
                // notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Your item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at postion" + position);
                //create the new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                //pass the data being edited
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                // display the activity
                startActivityForResult(i,EDIT_TEXT_CODE);
            }
        };
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvitems.setAdapter(itemsAdapter);
        rvitems.setLayoutManager(new LinearLayoutManager(this));

        // Add button fuctionality here
        add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String todoItem = etitem.getText().toString();
                /** Add item to the model
                 * Notify adaper that an item has been insert
                 * into the list */
                items.add(todoItem);
                itemsAdapter.notifyItemInserted(items.size() -1);
                etitem.setText("");
                // add toast :)
                Toast.makeText(getApplicationContext(), "Your item was added", Toast.LENGTH_SHORT).show();
                saveItems();

            }
        });
    }
    // handle the result of the edity activity
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            // Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // extract the origninal positon of the edited item from the position key
            int postion = data.getExtras().getInt(KEY_ITEM_POSITION);

            //update the model at the correct position with the new(edited) item
            items.set(postion, itemText);
            //notify the adapter
            itemsAdapter.notifyItemChanged(postion);
            //persist the model
            saveItems();
            Toast.makeText(getApplicationContext(), "Your item was changed", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile(){
        return new File(getFilesDir(), "data.txt");
     }
     // This function will reading in line by line from data from data.txt
    private void loadItems(){
        try {
            items = new ArrayList<>(readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity","Error reading items",e);
            items = new ArrayList<>();
        }
    }
    // This function saves items by writing them into the data file
    private void saveItems(){
        try {
            writeLines(getDataFile(), items);
        }   catch (IOException e){
                Log.e("MainActivity","Error Writing items", e);
        }

    }

}