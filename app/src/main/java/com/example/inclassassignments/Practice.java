package com.example.inclassassignments;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Practice extends AppCompatActivity {
    String TAG = "demo";
    Button buttonLogCat;
    Button buttonToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        setTitle("Practice In Class");
        buttonLogCat = findViewById(R.id.buttonLogCat);
        buttonToast = findViewById(R.id.buttonToast);

        buttonLogCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Practice!Practice!!Practice!!!");
            }
        });

        buttonToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Practice.this, "Now push to GitHub and Submit!", Toast.LENGTH_LONG).show();
            }
        });
    }
}