package com.example.depthoffieldcalculator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;


public class CalculatorActivity extends AppCompatActivity {

    private static final String EXTRA_LENSINDEX = "com.example.depthoffieldcalculator.CalculatorActivity - the lensIndex";
    private static final String EXTRA_LENSINFO = "com.example.depthoffieldcalculator.CalculatorActivity - the lensInfo";
    private static final int EDIT_REQUEST_CODE = 101;
    private static final int DELETE_REQUEST_CODE = 102;

    private LensManager manager;
    private double hyperfocalDistance;
    private double nearFocalPoint;
    private double farFocalPoint;
    private double depthOfField;

    private int lensIndex;
    private String lensInfo;

    private double circleOfConfusion;
    private double distance;
    private double aperture;

    EditText circleOfConfusionInput;
    EditText distanceInput;
    EditText apertureInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
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

        manager = LensManager.getInstance();

        extractDataFromIntent();
        prepopulateCOC();
        displayChosenLens();

        setupEditLensButton();
        setupDeleteButton();
        autoRecalculate();

    }

    private void setupEditLensButton() {
        Button btn = findViewById(R.id.editlensbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Launch the edit lens activity
                Intent intent = EditLensActivity.makeEditIntent(CalculatorActivity.this, lensIndex);
                //startActivity(intent);
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        });
    }

    private void setupDeleteButton() {
        Button btn = findViewById(R.id.removebtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.remove(manager.getValue(lensIndex));
                finish();
        }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case EDIT_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK){
                    //Get the message
                    int index = data.getIntExtra("Index", 0);
                    String editMake = data.getStringExtra("EditMake");
                    int editFocalLength = data.getIntExtra("EditFocalLength", 1);
                    double editAperture = data.getDoubleExtra("EditAperture", 1.0);

                    //Do something with it
                    manager.getValue(index).setMake(editMake);
                    manager.getValue(index).setMaxAperture(editAperture);
                    manager.getValue(index).setFocalLength(editFocalLength);

                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        lensInfo = lensIndex + ". " +
                manager.getValue(lensIndex).getMake() + " " +
                manager.getValue(lensIndex).getFocalLength() + "mm " + "F" +
                manager.getValue(lensIndex).getMaxAperture();
        displayChosenLens();
        autoRecalculate();
    }

    public static Intent makeIntent(Context context, int lensIndex, String lensInfo) {
        Intent intent = new Intent (context, CalculatorActivity.class);
        intent.putExtra(EXTRA_LENSINDEX, lensIndex);
        intent.putExtra(EXTRA_LENSINFO, lensInfo);

        return intent;
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        lensIndex = intent.getIntExtra(EXTRA_LENSINDEX, 0);
        lensInfo = intent.getStringExtra(EXTRA_LENSINFO);
    }

    private void prepopulateCOC(){
        EditText editText = findViewById(R.id.userCOC);
        editText.setText("" + 0.029);
    }

    private void displayChosenLens(){
        TextView textView = findViewById(R.id.lensChosen);
        textView.setText(lensInfo);
    }

    private void autoRecalculate(){
        circleOfConfusionInput = findViewById(R.id.userCOC);
        distanceInput = findViewById(R.id.userDistance);
        apertureInput = findViewById(R.id.userApertureEdit);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (circleOfConfusionInput.getText().length() > 0){
                    circleOfConfusion = Double.parseDouble(circleOfConfusionInput.getText().toString());
                    if (circleOfConfusion <= 0){
                        String errorMessage = "Circle of Confusion must be > 0";
                        Toast.makeText(CalculatorActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (distanceInput.getText().length() > 0){
                    distance = Double.parseDouble(distanceInput.getText().toString());
                    if (distance <= 0){
                        //TextView textView = (TextView) v;
                        String errorMessage = "Distance to subject must be > 0";
                        Toast.makeText(CalculatorActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                        return;
                    }
                }
                if (apertureInput.getText().length() > 0){
                    aperture = Double.parseDouble(apertureInput.getText().toString());
                    if (aperture < 1.4){
                        //TextView textView = (TextView) v;
                        String errorMessage = "Selected aperture must be >= 1.4";
                        Toast.makeText(CalculatorActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                        return;
                    }
                }

                if (circleOfConfusionInput.getText().length() > 0
                        && distanceInput.getText().length() > 0
                        && apertureInput.getText().length() > 0) {
                    Calc(circleOfConfusion, distance, aperture);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        circleOfConfusionInput.addTextChangedListener(textWatcher);
        distanceInput.addTextChangedListener(textWatcher);
        apertureInput.addTextChangedListener(textWatcher);

    }

    private void Calc(double circleOfConfusion, double distance, double aperture){
        TextView txtNearFocalPoint = findViewById(R.id.nearFocalDistanceTextView);
        TextView txtFarFocalPoint = findViewById(R.id.farFocalDistanceTextView);
        TextView txtHyperfocalDistance = findViewById(R.id.hyperfocalDistanceTextView);
        TextView txtDepthOfField = findViewById(R.id.depthOfFieldTextView);

        if (aperture < manager.getValue(lensIndex).getMaxAperture()){
            txtNearFocalPoint.setText("Invalid aperture");
            txtFarFocalPoint.setText("Invalid aperture");
            txtHyperfocalDistance.setText("Invalid aperture");
            txtDepthOfField.setText("Invalid aperture");

            return;
        }

        distance = distance * 1000; // convert to mm before calculation

        // Calculate
        hyperfocalDistance = Calculator.calcHyperfocalDistance
                (manager.getValue(lensIndex).getFocalLength(), aperture, circleOfConfusion);
        nearFocalPoint = Calculator.calcNearFocalPoint
                (hyperfocalDistance, distance, manager.getValue(lensIndex).getFocalLength());

        farFocalPoint = Calculator.calcFarFocalPoint
                (hyperfocalDistance, distance, manager.getValue(lensIndex).getFocalLength());

        hyperfocalDistance = convertToMeter(hyperfocalDistance);
        nearFocalPoint = convertToMeter(nearFocalPoint);
        farFocalPoint = convertToMeter(farFocalPoint);

        depthOfField = Calculator.calcDepthOfField(farFocalPoint, nearFocalPoint);

        txtNearFocalPoint.setText("" + formatM(nearFocalPoint) + "m");
        txtFarFocalPoint.setText("" + formatM(farFocalPoint) + "m");
        txtHyperfocalDistance.setText("" + formatM(hyperfocalDistance)+ "m");
        txtDepthOfField.setText("" + formatM(depthOfField) + "m");

    }

    private double convertToMeter(double value){
        return value / 1000;
    }

    private String formatM(double distanceInM) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(distanceInM);
    }

}
