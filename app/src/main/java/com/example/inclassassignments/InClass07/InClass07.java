/*
David Nguyen
InClass07
 */

package com.example.inclassassignments.InClass07;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.inclassassignments.R;

public class InClass07 extends AppCompatActivity implements InClass07Register.IFromRegisterToMain,
        InClass07Login.IFromLoginToMain, InClass07ViewProfile.IFromViewProfileToMain, InClass07ViewNotes.IFromViewNotes {

    // declaring fragments
    private InClass07Login login;
    private InClass07Register register;
    private InClass07ViewProfile viewProfile;
    private InClass07ViewNotes viewNotes;

    // declaring UI
    private Button buttonLogIn;
    private Button buttonRegister;
    private TextView textViewLogin;
    private Button buttonViewNotes;

    Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_class07);

        login = new InClass07Login();
        register = new InClass07Register();
        viewProfile = new InClass07ViewProfile();
        viewNotes = new InClass07ViewNotes();
        buttonLogIn = findViewById(R.id.buttonLogIn);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogIn);
        buttonViewNotes = findViewById(R.id.buttonViewNotes);

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainerInClass07LogIn, login)
                        .addToBackStack("login")
                        .commit();
                buttonLogIn.setVisibility(View.GONE);
                buttonRegister.setVisibility(View.GONE);
                textViewLogin.setVisibility(View.GONE);
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainerInClass07LogIn, register)
                        .addToBackStack("register")
                        .commit();
                buttonLogIn.setVisibility(View.GONE);
                buttonRegister.setVisibility(View.GONE);
                textViewLogin.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void backFromRegister() {
        backToMain();
    }

    @Override
    public void tokenFromRegister(Token token) {
        setTokenAndStartViewProfile(token);
    }

    @Override
    public void backFromLogin() {
        backToMain();
    }

    @Override
    public void tokenFromLogin(Token token) {
        setTokenAndStartViewProfile(token);
    }

    @Override
    public void viewNotesFromViewProfile() {
        getSupportFragmentManager().popBackStackImmediate();
        viewNotes.updateToken(token);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainerInClass07LogIn, viewNotes)
                .addToBackStack("viewNotes")
                .commit();
        buttonLogIn.setVisibility(View.GONE);
        buttonRegister.setVisibility(View.GONE);
        textViewLogin.setVisibility(View.GONE);
    }

    @Override
    public void logoutFromViewProfile() {
        backToMain();
    }

    @Override
    public void fromViewNotes() {
        getSupportFragmentManager().popBackStackImmediate();
        setTokenAndStartViewProfile(token);
    }

    public void setTokenAndStartViewProfile(Token token) {
        this.token = token;
        getSupportFragmentManager().popBackStackImmediate();
        if (token.isAuth()) {
            viewProfile.updateToken(token);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainerInClass07LogIn, viewProfile)
                    .addToBackStack("viewProfile")
                    .commit();
            buttonLogIn.setVisibility(View.GONE);
            buttonRegister.setVisibility(View.GONE);
            textViewLogin.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Unable to authorize token", Toast.LENGTH_SHORT).show();
        }
    }

    public void backToMain() {
        getSupportFragmentManager().popBackStackImmediate();
        buttonLogIn.setVisibility(View.VISIBLE);
        buttonRegister.setVisibility(View.VISIBLE);
        textViewLogin.setVisibility(View.VISIBLE);
    }
}