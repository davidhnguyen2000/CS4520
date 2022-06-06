/*
David Nguyen
Assignment 02
 */

package com.example.inclassassignments.InClass02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inclassassignments.R;

public class InClass02 extends AppCompatActivity {

    EditText editTextName;
    EditText editTextEmail;
    ImageView imageViewAvatar;
    RadioGroup radioGroupOS;
    TextView textViewMood;
    SeekBar seekBarMood;
    ImageView imageViewMood;
    Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int avatarNumber = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_class02);
        setTitle("Edit Profile Activity");
        // declare interface view components
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        imageViewAvatar = findViewById(R.id.imageViewAvatar);
        radioGroupOS = findViewById(R.id.radioGroupOS);
        textViewMood = findViewById(R.id.textViewMood);
        seekBarMood = findViewById(R.id.seekBarMood);
        imageViewMood = findViewById(R.id.imageViewMood);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        if (getIntent() != null && getIntent().getExtras() != null) {
            avatarNumber = getIntent().getIntExtra("avatarNumber", 0);
            switch (avatarNumber) {
                case 0:
                    Toast.makeText(this, "Error No Avatar Image",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 1: imageViewAvatar.setImageResource(R.drawable.avatar_f_1);
                    break;
                case 2: imageViewAvatar.setImageResource(R.drawable.avatar_f_2);
                    break;
                case 3: imageViewAvatar.setImageResource(R.drawable.avatar_f_3);
                    break;
                case 4: imageViewAvatar.setImageResource(R.drawable.avatar_m_1);
                    break;
                case 5: imageViewAvatar.setImageResource(R.drawable.avatar_m_2);
                    break;
                case 6: imageViewAvatar.setImageResource(R.drawable.avatar_m_3);
                    break;
            }
        }

        final int finalAvatarNumber = avatarNumber;
        buttonSubmit.setOnClickListener(v -> {
            int selectedRadioButton = radioGroupOS.getCheckedRadioButtonId();
            int selectedMood = seekBarMood.getProgress();
            String name = editTextName.getText().toString();
            String email = editTextEmail.getText().toString();
            boolean emailGood = checkEmail(email);
            boolean nameGood = !name.isEmpty();
            boolean OSGood = selectedRadioButton != -1;
            if (!emailGood)
                Toast.makeText(InClass02.this, "Invalid Email",
                        Toast.LENGTH_SHORT).show();
            if (!nameGood)
                Toast.makeText(InClass02.this, "Invalid Name",
                        Toast.LENGTH_SHORT).show();
            if (!OSGood)
                Toast.makeText(InClass02.this, "Must Select An OS",
                        Toast.LENGTH_SHORT).show();
            if (finalAvatarNumber == 0)
                Toast.makeText(InClass02.this, "Must Select An Avatar Image",
                        Toast.LENGTH_SHORT).show();
            if (emailGood && nameGood && OSGood && finalAvatarNumber != 0) {
                // bring the user to the display activity
                Profile profile = new Profile(name, email, finalAvatarNumber, selectedRadioButton, selectedMood);
                Intent toDisplayActivity = new Intent(InClass02.this,
                        InClass02DisplayActivity.class);
                toDisplayActivity.putExtra("profile", profile);
                startActivity(toDisplayActivity);
            }
        });

        imageViewAvatar.setOnClickListener(v -> {
            // bring the user to another activity to choose the avatar
            Intent toSelectAvatarActivity = new Intent(InClass02.this,
                    InClass02SelectAvatarActivity.class);
            startActivity(toSelectAvatarActivity);
        });

        seekBarMood.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int seekBarValue = seekBarMood.getProgress();
                String moodString = "Your Current Mood: ";
                switch (seekBarValue) {
                    case 0: imageViewMood.setImageResource(R.drawable.angry);
                        moodString = moodString + "Angry";
                    break;
                    case 1: imageViewMood.setImageResource(R.drawable.sad);
                        moodString = moodString + "Sad";
                    break;
                    case 2: imageViewMood.setImageResource(R.drawable.happy);
                        moodString = moodString + "Happy";
                    break;
                    case 3: imageViewMood.setImageResource(R.drawable.awesome);
                        moodString = moodString + "Awesome";
                    break;
                }
                textViewMood.setText(moodString);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    // return true if the inputted name is in the format abc@def.xyz
    private boolean checkEmail(String email) {
        boolean containsAt = email.contains("@");
        boolean containsDot = email.contains(".") && email.lastIndexOf(".") > email.indexOf("@");
        boolean containsSpace = email.contains(" ");
        String address;
        String server;
        String suffix;
        if (containsDot && containsAt && !containsSpace) {
            address = email.substring(0, email.lastIndexOf('@'));
            server = email.substring(email.indexOf('@') + 1, email.lastIndexOf("."));
            suffix = email.substring(email.lastIndexOf(".") + 1);
            return !address.isEmpty() && !server.isEmpty() && !suffix.isEmpty() && !address.contains("@");
        }
        return false;
    }
}