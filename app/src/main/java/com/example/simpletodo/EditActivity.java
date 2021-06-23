package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {

    // Create instance variables of views
    EditText edtItem;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Create references to views
        edtItem = findViewById(R.id.edtItem);
        btnSave = findViewById(R.id.btnSave);

        // Display activity title
        getSupportActionBar().setTitle("Edit item");

        // Retrieve data that we passed in
        edtItem.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_TEXT));

        // Add click listener on the button (when user is done editing, they click the save button)
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent which will contain the results
                Intent intent = new Intent();
                // Pass the data (results of editing)
                intent.putExtra(MainActivity.KEY_ITEM_TEXT, edtItem.getText().toString());
                intent.putExtra(MainActivity.KEY_ITEM_POSITION, getIntent().getExtras().getInt(MainActivity.KEY_ITEM_POSITION));
                // Set the result of the intent
                setResult(RESULT_OK, intent);
                // Finish activity, close the screen and go back
                finish();
            }
        });

    }
}