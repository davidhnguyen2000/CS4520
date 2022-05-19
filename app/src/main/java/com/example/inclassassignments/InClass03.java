package com.example.inclassassignments;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class InClass03 extends AppCompatActivity implements InClass03Main.IFromMainToActivity, InClass03SelectAvatar.IFromSelectAvatarToActivity {

    private InClass03Main main;
    private InClass03Display display;
    private InClass03SelectAvatar selectAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_class03);
        setTitle("Edit Profile Activity");
        main = new InClass03Main();
        display = new InClass03Display();
        selectAvatar = new InClass03SelectAvatar();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, main)
                .commit();
    }

    @Override
    public void fromMainFragment(boolean submitClick, boolean avatarClick, Profile profile) {
        if (submitClick && profile != null) {
            setTitle("Display Activity");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, display)
                    .commit();
            display.updateValues(profile);
        } else if (avatarClick) {
            setTitle("Select Avatar");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, selectAvatar)
                    .commit();
        }
    }

    @Override
    public void fromSelectAvatarFragment(int avatarNumber) {
        setTitle("Edit Profile Activity");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, main)
                .commit();
        main.updateValues(avatarNumber);
    }
}