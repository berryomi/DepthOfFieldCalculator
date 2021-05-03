package com.example.depthoffieldcalculator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    public static final int ADD_REQUEST_CODE = 100;
    private LensManager manager;
    private ArrayAdapter<String> adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = LensManager.getInstance();

        populateListView();
        clickListView();
        setupAddLensButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateListView(){
        String[] listLens = new String[manager.listSize()];

        int i = 0;
        for (Lens l: manager
        ) {listLens[i] = i + ". " +
                l.getMake() + " " +
                l.getFocalLength() + "mm " + "F" +
                l.getMaxAperture();
            i++;
        }

        adapter = new ArrayAdapter<>(
                this,
                R.layout.list_lens,
                listLens);

        listView = findViewById(R.id.listViewMain);
        listView.setAdapter(adapter);

    }

    private void clickListView(){
        ListView list = findViewById(R.id.listViewMain);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = (TextView) view;
                String message = textView.getText().toString();

                Intent intent = CalculatorActivity.makeIntent(MainActivity.this, position, message);
                startActivity(intent);

            }
        });
    }

    private void setupAddLensButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Launch the addlens activity
                Intent intent = AddLensActivity.makeIntent(MainActivity.this);
                //startActivity(intent);
                startActivityForResult(intent, ADD_REQUEST_CODE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case ADD_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    //Get the message
                    String make = data.getStringExtra("Make");
                    int focalLength = data.getIntExtra("FocalLength", 1);
                    double aperture = data.getDoubleExtra("Aperture", 1.0);

                    Lens lens = new Lens(make, aperture, focalLength);
                    //Do something with it
                    manager.add(lens);

                }
        }
    }

    private void checkEmptyState(){
        TextView textView = findViewById(R.id.selectLensText);
        if (manager.listSize() == 0){
            textView.setText("There is no lens to display.\n " +
                    "Click the + button below to add new lens!");
        }
        else{
            textView.setText("Select a lens to use:");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkEmptyState();
        updateListView();
    }

    private void updateListView() {
        String[] listLens = new String[manager.listSize()];

        int i = 0;
        for (Lens l: manager
        ) { listLens[i] = i + ". " +
                l.getMake() + " " +
                l.getFocalLength() + "mm " + "F" +
                l.getMaxAperture();
            i++;
        }

        adapter = new ArrayAdapter<>(
                this,
                R.layout.list_lens,
                listLens);

        listView = findViewById(R.id.listViewMain);
        listView.setAdapter(adapter);
    }

}
