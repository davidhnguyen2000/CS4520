package com.example.inclassassignments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button buttonPractice;
    Button buttonInClass01;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPractice = findViewById(R.id.buttonPractice);
        buttonInClass01 = findViewById(R.id.buttonInClass01);

        buttonPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Activity population ...
                Intent toPractice = new Intent(MainActivity.this, Practice.class);
                startActivity(toPractice);
            }
        });

        buttonInClass01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Activity population ...
                Intent toInClass01 = new Intent(MainActivity.this, InClass01.class);
                startActivity(toInClass01);
            }
        });
    }
}