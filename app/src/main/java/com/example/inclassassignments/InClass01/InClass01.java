/*
David Nguyen
Assignment 01
 */

package com.example.inclassassignments.InClass01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inclassassignments.R;

public class InClass01 extends AppCompatActivity {

    EditText weightInput;
    EditText feetInput;
    EditText inchesInput;
    Button calculateButton;
    TextView results;
    double weight;
    double heightFeet;
    double heightInches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_class01);
        // declaring interface components
        setTitle("BMI Calculator");
        weightInput = findViewById(R.id.editTextWeight);
        feetInput = findViewById(R.id.editTextFeet);
        inchesInput = findViewById(R.id.editTextInches);
        calculateButton = findViewById(R.id.buttonCalculateBMI);
        results = findViewById(R.id.resultsText);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInputs()) {
                    // send message to user than negative inputs are not allowed
                    Toast.makeText(InClass01.this, "Invalid Inputs", Toast.LENGTH_LONG).show();
                } else {
                    // set the results text view to the bmi message
                    results.setText(makeResultMessage(calculateBMI()));
                }
            }
        });
    }

    private boolean checkInputs() {
        String inchesString = inchesInput.getText().toString();
        String feetString = feetInput.getText().toString();
        String weightString = weightInput.getText().toString();
        if (weightString.equals("") || feetString.equals("") || inchesString.equals(""))
            return false;
        else {
            weight = Double.parseDouble(weightInput.getText().toString());
            heightFeet = Double.parseDouble(feetInput.getText().toString());
            heightInches = Double.parseDouble(inchesInput.getText().toString());
            return weight >= 0 && heightFeet >= 0 && heightInches >= 0;
        }
    }

    private double calculateBMI() {
        // calculate the BMI
        double bmi;
        heightInches = heightInches + heightFeet * 12;
        bmi = (weight / (heightInches * heightInches)) * 703;
        // round the bmi value to the nearest tenth
        bmi = (double) Math.round(bmi * 10) / 10;
        return bmi;
    }

    private String makeResultMessage(double bmi) {
        String bmiStatus;
        // classify the bmi value in terms of bmi status
        if (bmi < 18.5)
            bmiStatus = "Underweight";
        else if (bmi < 24.9)
            bmiStatus = "Normal weight";
        else if (bmi < 29.9)
            bmiStatus = "Overweight";
        else
            bmiStatus = "Obese";
        // create the message to be sent to the user
        return "Your BMI " + bmi + "\nYou are " + bmiStatus;
    }
}