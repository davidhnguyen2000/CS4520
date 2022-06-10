package com.example.inclassassignments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.inclassassignments.InClass01.InClass01;
import com.example.inclassassignments.InClass02.InClass02;
import com.example.inclassassignments.InClass03.InClass03;
import com.example.inclassassignments.InClass04.InClass04;
import com.example.inclassassignments.InClass05.InClass05;
import com.example.inclassassignments.InClass06.InClass06;
import com.example.inclassassignments.InClass07.InClass07;
import com.example.inclassassignments.InClass08.InClass08;
import com.example.inclassassignments.Practice.Practice;

public class MainActivity extends AppCompatActivity {
    Button buttonPractice;
    Button buttonInClass01;
    Button buttonInClass02;
    Button buttonInClass03;
    Button buttonInClass04;
    Button buttonInClass05;
    Button buttonInClass06;
    Button buttonInClass07;
    Button buttonInClass08;
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
        buttonInClass07 = findViewById(R.id.buttonInClass07);
        buttonInClass08 = findViewById(R.id.buttonInClass08);

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

        buttonInClass07.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toInClass07 = new Intent(MainActivity.this, InClass07.class);
                startActivity(toInClass07);
            }
        });

        buttonInClass08.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toInClass08 = new Intent(MainActivity.this, InClass08.class);
                startActivity(toInClass08);
            }
        });
    }
}