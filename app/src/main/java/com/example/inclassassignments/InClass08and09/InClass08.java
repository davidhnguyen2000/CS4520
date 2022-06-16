package com.example.inclassassignments.InClass08and09;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.inclassassignments.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class InClass08 extends AppCompatActivity implements InClass08Register.IFromRegisterFragment,
        InClass08Login.IFromLoginFragment, ChatAdapter.IFromAuthUserAdapter,
        InClass08Chat.IFromChatFragment, InClass09EditProfile.IFromEditProfileFragment, InClass08ChatList.IFromChatListFragment {

    private InClass08Chat chatView;
    private InClass08ChatList chatListView;
    private InClass08Login login;
    private InClass08Register register;
    private InClass09EditProfile editProfile;
    private static final int REQUEST_IMAGE_CAPTURE_EDIT = 1;
    private static final int REQUEST_IMAGE_CAPTURE_REGISTER = 2;
    private static final int REQUEST_SELECT_PICTURE = 3;
    private static final int REQUEST_IMAGE_CAPTURE_CHAT = 4;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Uri dataUri = data.getData();
            Bitmap imageBitmap = null;
            if (extras == null) {
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dataUri);
                } catch (Exception e) {
                    Toast.makeText(this, "Unable to load image", Toast.LENGTH_SHORT).show();
                }
            }
            if (extras != null) {
                imageBitmap = (Bitmap) extras.get("data");
            }
            if (requestCode == REQUEST_IMAGE_CAPTURE_EDIT) {
                editProfile.updateProfileImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE_REGISTER) {
                register.updateProfileImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_SELECT_PICTURE || requestCode == REQUEST_IMAGE_CAPTURE_CHAT) {
                chatView.sendImage(imageBitmap);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_class08);
        login = new InClass08Login();
        chatListView = new InClass08ChatList();
        chatView = new InClass08Chat();
        register = new InClass08Register();
        editProfile = new InClass09EditProfile();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainerViewInClass08, login)
                .addToBackStack("login")
                .commit();
    }

    @Override
    public void backToChatList(String currentUsername) {
        popAndStartChatListView(currentUsername);
    }

    @Override
    public void getImageFromAlbum() {
        Intent selectPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(selectPictureIntent, REQUEST_SELECT_PICTURE);
        } catch (Exception exp) {
            Toast.makeText(this, "Unable to load album", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getImageFromCamera() {
        Intent takePictureIntent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_CHAT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Unable to open camera app", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void selectNewProfilePicture() {
        Intent takePictureIntent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_EDIT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Unable to open camera app", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void loggedIn(String currentUsername) {
        popAndStartChatListView(currentUsername);
    }

    @Override
    public void registerSelected() {
        getSupportFragmentManager().popBackStackImmediate();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainerViewInClass08, register)
                .addToBackStack("register")
                .commit();
    }

    @Override
    public void registered() {
        popAndStartLogin();
    }

    @Override
    public void loginSelected() {
        popAndStartLogin();
    }

    @Override
    public void selectProfilePicture() {
        Intent takePictureIntent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_REGISTER);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Unable to open camera app", Toast.LENGTH_SHORT).show();
        }
    }

    private void popAndStartChatListView(String currentUsername) {
        getSupportFragmentManager().popBackStackImmediate();
        chatListView.setCurrentUsername(currentUsername);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainerViewInClass08, chatListView)
                .addToBackStack("chat list")
                .commit();
    }

    private void popAndStartLogin() {
        getSupportFragmentManager().popBackStackImmediate();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainerViewInClass08, login)
                .addToBackStack("login")
                .commit();
    }

    @Override
    public void chatSelected(AuthUser chatUser, String currentUsername) {
        getSupportFragmentManager().popBackStackImmediate();
        chatView = new InClass08Chat(currentUsername, chatUser);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainerViewInClass08, chatView)
                .addToBackStack("chat")
                .commit();
    }

    @Override
    public void toEditProfile(String currentUsername) {
        getSupportFragmentManager().popBackStackImmediate();
        editProfile.setCurrentUsername(currentUsername);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainerViewInClass08, editProfile)
                .addToBackStack("edit profile")
                .commit();
    }

    @Override
    public void logout() {
        popAndStartLogin();
    }
}