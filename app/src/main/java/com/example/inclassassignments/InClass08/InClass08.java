package com.example.inclassassignments.InClass08;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.inclassassignments.R;

public class InClass08 extends AppCompatActivity implements InClass08Register.IFromRegisterFragment, InClass08Login.IFromLoginFragment, ChatAdapter.IFromAuthUserAdapter, InClass08Chat.IFromChatFragment {

    private InClass08Chat chatView;
    private InClass08ChatList chatListView;
    private InClass08Login login;
    private InClass08Register register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_class08);
        login = new InClass08Login();
        chatListView = new InClass08ChatList();
        chatView = new InClass08Chat();
        register = new InClass08Register();
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
}