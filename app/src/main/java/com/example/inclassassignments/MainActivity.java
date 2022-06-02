package com.example.inclassassignments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button buttonPractice;
    Button buttonInClass01;
    Button buttonInClass02;
    Button buttonInClass03;
    Button buttonInClass04;
    Button buttonInClass05;
    Button buttonInClass06;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPractice = findViewById(R.id.buttonPractice);
        buttonInClass01 = findViewById(R.id.buttonInClass01);
        buttonInClass02 = findViewById(R.id.buttonInClass02);
        buttonInClass03 = findViewById(R.id.buttonInClass03);
        buttonInClass04 = findViewById(R.id.buttonInClass04);
        buttonInClass05 = findViewById(R.id.buttonInClass05);
        buttonInClass06 = findViewById(R.id.buttonInClass06);

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

        buttonInClass02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Activity population ...
                Intent toInClass02 = new Intent(MainActivity.this, InClass02.class);
                startActivity(toInClass02);
            }
        });

        buttonInClass03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Activity population ...
                Intent toInClass03 = new Intent(MainActivity.this, InClass03.class);
                startActivity(toInClass03);
            }
        });

        buttonInClass04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toInClass04 = new Intent(MainActivity.this, InClass04.class);
                startActivity(toInClass04);
            }
        });

        buttonInClass05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toInClass05 = new Intent(MainActivity.this, InClass05.class);
                startActivity(toInClass05);
            }
        });

        buttonInClass06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toInClass06 = new Intent(MainActivity.this, InClass06.class);
                startActivity(toInClass06);
            }
        });
    }
}