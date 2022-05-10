package com.example.inclassassignments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button buttonPractice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPractice = findViewById(R.id.buttonPractice);

        buttonPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Activity population ...
                Intent toPractice = new Intent(MainActivity.this, Practice.class);
                startActivity(toPractice);
            }
        });
    }
}