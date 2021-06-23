package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Create member variables for UI views and data
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    List<String> items;
    ItemsAdapter itemsAdapter;

    // Create keys to reference data we are receiving
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    // onCreate called by Android system and informs developer that MainActivity was created (but not visible to user)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Rule of thumb is to call superclass first during initialization
        super.onCreate(savedInstanceState);
        // This line sets activity main xml file (layout) as content of activity (we see UI)
        setContentView(R.layout.activity_main);

        // Get a reference of each view (component of UI) in our Java file
        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.edtItem);
        rvItems = findViewById(R.id.rvItems);

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model
                items.remove(position);
                // Notify the adapter at which position we deleted it
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        // Construct onClick listener
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at position " + position);
                // Create the new activity using an intent (request to Android system)
                // MainActivity.this refers to current instance of class while EditActivity.class refers to the class of the activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                // Pass the data being edited
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                // Display the activity (request code uniquely identifies request for another activity)
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };
        // Construct adapter and pass items in
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        // Place UI components vertically by default
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        // Be notified everytime user clicks on button
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etItem.getText().toString();
                // Add item to the model
                items.add(todoItem);
                // Notify adapter that an item was inserted
                itemsAdapter.notifyItemInserted(items.size()-1);
                // Clear text bar
                etItem.setText("");
                //Show toast that item was added
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    // Handle result of EditActivity by overriding
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Check if the actual request code matches with Edit Text Code
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            // Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // Extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            // Update the model at the right position with the new item text
            items.set(position, itemText);
            // Notify the adapted so RecyclyView knows something changed
            itemsAdapter.notifyItemChanged(position);
            // Persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    // Persistence related methods are private because only called within this file
    // Return the file in which we store our list of items
    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    // Load items by reading every line of the data file
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            // Utilize logging to log errors to logcat
            Log.e("MainActivity", "Error reading items", e);
            // Set items to be empty
            items = new ArrayList<>();
        }
    }

    // Save items by writing them into the data file
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }
}