package com.example.depthoffieldcalculator;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditLensActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    int lensIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lens);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        extractDatafromIntent();
        setupSaveButton();
    }

    private void extractDatafromIntent() {
        Intent intent = getIntent();
        lensIndex = intent.getIntExtra("lensIndex", 0);
    }

    private void setupSaveButton() {
        Button btn = findViewById(R.id.save);

        btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                //Extract data from UI
                EditText userMake = findViewById(R.id.userMakeEdit);
                EditText userFocalLength = findViewById(R.id.userFocalLengthEdit);
                EditText userAperture = findViewById(R.id.userApertureEdit);

                String make = userMake.getText().toString();
                String focalStr =  userFocalLength.getText().toString();
                String apertureStr = userAperture.getText().toString();

                int focalLength;
                double aperture;

                // Error checking input
                if (make.length() <= 0){ // if user does not input make
                    String errorMessage = "Please enter a make";
                    Toast.makeText(EditLensActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (focalStr.length() <= 0){
                    String errorMessage = "Please enter a focal length";
                    Toast.makeText(EditLensActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (apertureStr.length() <= 0){
                    String errorMessage = "Please enter an aperture";
                    Toast.makeText(EditLensActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                }

                focalLength = Integer.parseInt(focalStr);
                aperture = Double.parseDouble(apertureStr);

                if (make.length() > 0 && focalStr.length() > 0 && apertureStr.length() > 0) {
                    if (focalLength <= 0) {
                        String errorMessage = "Focal length must be > 0";
                        Toast.makeText(EditLensActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (aperture < 1.4) {
                        String errorMessage = "Selected aperture must be >= 1.4";
                        Toast.makeText(EditLensActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //Pass data back
                Intent intent = new Intent(EditLensActivity.this, CalculatorActivity.class);
                                intent.putExtra("Index", lensIndex);
                                intent.putExtra("EditMake", make);
                                intent.putExtra("EditFocalLength", focalLength);
                                intent.putExtra("EditAperture", aperture);
                                setResult(EditLensActivity.RESULT_OK, intent);

                                finish();
            }
        });
    }

    public static Intent makeEditIntent(Context context, int lensIndex){
        Intent intent = new Intent(context, EditLensActivity.class);
        intent.putExtra("lensIndex", lensIndex);
        return intent;
    }


}
