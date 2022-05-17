/*
David Nguyen
Assignment 02
 */

package com.example.inclassassignments;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class InClass02SelectAvatarActivity extends AppCompatActivity {

    ImageView imageViewAvatar1;
    ImageView imageViewAvatar2;
    ImageView imageViewAvatar3;
    ImageView imageViewAvatar4;
    ImageView imageViewAvatar5;
    ImageView imageViewAvatar6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_class02_select_avatar);
        setTitle("Select Avatar Activity");
        imageViewAvatar1 = findViewById(R.id.imageViewAvatar_f_1);
        imageViewAvatar2 = findViewById(R.id.imageViewAvatar_f_2);
        imageViewAvatar3 = findViewById(R.id.imageViewAvatar_f_3);
        imageViewAvatar4 = findViewById(R.id.imageViewAvatar_m_1);
        imageViewAvatar5 = findViewById(R.id.imageViewAvatar_m_2);
        imageViewAvatar6 = findViewById(R.id.imageViewAvatar_m_3);

        imageViewAvatar1.setOnClickListener(v -> sendToMain(1));
        imageViewAvatar2.setOnClickListener(v -> sendToMain(2));
        imageViewAvatar3.setOnClickListener(v -> sendToMain(3));
        imageViewAvatar4.setOnClickListener(v -> sendToMain(4));
        imageViewAvatar5.setOnClickListener(v -> sendToMain(5));
        imageViewAvatar6.setOnClickListener(v -> sendToMain(6));
    }

    private void sendToMain(int avatarNumber) {
        Intent toInClass02 = new Intent(InClass02SelectAvatarActivity.this,
                InClass02.class);
        toInClass02.putExtra("avatarNumber", avatarNumber);
        startActivity(toInClass02);
    }
}