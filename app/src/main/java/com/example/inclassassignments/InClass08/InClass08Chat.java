package com.example.inclassassignments.InClass08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inclassassignments.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InClass08Chat extends Fragment {

    private TextView textViewChatUser;
    private LinearLayout linearLayoutChat;
    private EditText editTextMessage;
    private Button buttonSend;
    private Button buttonBackToChatList;

    private IFromChatFragment sendData;
    private String currentUsername;
    private AuthUser chatUser;
    private Handler messageQueue;
    private ExecutorService threadpool;

    public InClass08Chat() {
        // Required empty public constructor
    }

    public InClass08Chat(String currentUsername, AuthUser chatUser) {
        this.currentUsername = currentUsername;
        this.chatUser = chatUser;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFromChatFragment) {
            sendData = (IFromChatFragment) context;
        } else {
            throw new RuntimeException(context + " must implement IFromChatFragment");
        }
    }

    public static InClass08Chat newInstance(String param1, String param2) {
        return new InClass08Chat();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_in_class08_chat, container, false);
        textViewChatUser = rootView.findViewById(R.id.textViewChatUser);
        linearLayoutChat = rootView.findViewById(R.id.linearLayoutChat);
        editTextMessage = rootView.findViewById(R.id.editTextMessage);
        buttonSend = rootView.findViewById(R.id.buttonSend);
        buttonBackToChatList = rootView.findViewById(R.id.buttonBackToChatList);
        threadpool = Executors.newFixedThreadPool(2);
        return rootView;
    }

    @Override
    public void onStart() {
        String chatUserText = chatUser.getFirstName() + " " + chatUser.getLastName();
        textViewChatUser.setText(chatUserText);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextMessage.getText().toString();
                if (messageText.isEmpty()) {
                    Toast.makeText(getContext(), "Message must have content", Toast.LENGTH_SHORT).show();
                } else {
                    MessagePoster messagePoster = new MessagePoster(messageText, currentUsername, chatUser.getUserName());
                    threadpool.execute(messagePoster);
                }
            }
        });

        buttonBackToChatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.backToChatList(currentUsername);
            }
        });

        messageQueue = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case ChatGetter.STATUS_START:
                        break;
                    case ChatGetter.STATUS_FAIL:
                        break;
                    case ChatGetter.STATUS_SUCCESS:
                        ArrayList<Message> currentUserMessages = (ArrayList<Message>) msg.getData().getSerializable(ChatGetter.KEY_CURRENT_USER_MESSAGES);
                        ArrayList<Message> chatUserMessages = (ArrayList<Message>) msg.getData().getSerializable(ChatGetter.KEY_CHAT_USER_MESSAGES);
                        break;
                    case MessagePoster.STATUS_START:
                        break;
                    case MessagePoster.STATUS_FAIL:
                        break;
                    case MessagePoster.STATUS_SUCCESS:
                        editTextMessage.setText("");
                        break;
                }
                return false;
            }
        });

        ChatGetter chatGetter = new ChatGetter(currentUsername, chatUser.getUserName(), messageQueue);
        threadpool.execute(chatGetter);
        super.onStart();
    }

    public interface IFromChatFragment {
        void backToChatList(String currentUsername);
    }
}

class ChatGetter implements Runnable {
    private FirebaseFirestore database;
    private String currentUsername;
    private String chatUsername;
    private Handler messageQueue;
    public static final int STATUS_START = 0x001;
    public static final int STATUS_FAIL = 0x002;
    public static final int STATUS_SUCCESS = 0x003;
    public static final String KEY_CURRENT_USER_MESSAGES = "current user messages";
    public static final String KEY_CHAT_USER_MESSAGES = "chat user messages";

    public ChatGetter(String currentUsername, String chatUsername, Handler messageQueue) {
        this.currentUsername = currentUsername;
        this.chatUsername = chatUsername;
        this.messageQueue = messageQueue;
        this.database = FirebaseFirestore.getInstance();
    }

    @Override
    public void run() {
        DocumentReference chatUserDoc = database.collection("users").document(chatUsername);
        DocumentReference currentUserDoc = database.collection("users").document(currentUsername);
        CollectionReference chatUserChats = chatUserDoc.collection("chats");
        CollectionReference currentUserChats = currentUserDoc.collection("chats");
        DocumentReference chatUserChatDoc = currentUserChats.document(chatUsername);
        DocumentReference currentUserChatDoc = chatUserChats.document(currentUsername);

        chatUserDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                    }
                }
            }
        });
        // look for chats collection, if it doesn't exist, create it
        // look for userName collection,
    }
}

class MessagePoster implements Runnable {
    private FirebaseFirestore database;
    private String messageText;
    private String getChatUsername;
    private String chatUsername;
    public static final int STATUS_START = 0x004;
    public static final int STATUS_FAIL = 0x005;
    public static final int STATUS_SUCCESS = 0x006;

    public MessagePoster(String messageText, String getChatUsername, String chatUsername) {
        this.messageText = messageText;
        this.getChatUsername = getChatUsername;
        this.chatUsername = chatUsername;
    }

    @Override
    public void run() {

    }
}