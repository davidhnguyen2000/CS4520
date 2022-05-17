/*
David Nguyen
Assignment 02
 */

package com.example.inclassassignments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class InClass02DisplayActivity extends AppCompatActivity {

    ImageView avatar;
    TextView email;
    TextView name;
    TextView OS;
    TextView mood;
    ImageView moodImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_class02_display);
        setTitle("Display Activity");
        avatar = findViewById(R.id.imageViewDisplayAvatar);
        email = findViewById(R.id.textViewEmail);
        name = findViewById(R.id.textViewName);
        OS = findViewById(R.id.textViewDispayOS);
        mood = findViewById(R.id.textViewDisplayMood);
        moodImage = findViewById(R.id.imageViewDisplayMood);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Profile profile = (Profile) getIntent().getSerializableExtra("profile");
            String displayOS = "I use " + profile.operatingSystem;
            String displayMood = "I am " + profile.getMood();
            email.setText(profile.email);
            name.setText(profile.name);
            switch (profile.avatarId) {
                case 1: avatar.setImageResource(R.drawable.avatar_f_1);
                    break;
                case 2: avatar.setImageResource(R.drawable.avatar_f_2);
                    break;
                case 3: avatar.setImageResource(R.drawable.avatar_f_3);
                    break;
                case 4: avatar.setImageResource(R.drawable.avatar_m_1);
                    break;
                case 5: avatar.setImageResource(R.drawable.avatar_m_2);
                    break;
                case 6: avatar.setImageResource(R.drawable.avatar_m_3);
                    break;
            }
            OS.setText(displayOS);
            mood.setText(displayMood);
            switch (profile.mood) {
                case 0: moodImage.setImageResource(R.drawable.angry);
                    break;
                case 1: moodImage.setImageResource(R.drawable.sad);
                    break;
                case 2: moodImage.setImageResource(R.drawable.happy);
                    break;
                case 3: moodImage.setImageResource(R.drawable.awesome);
                    break;
            }
        }

    }
}