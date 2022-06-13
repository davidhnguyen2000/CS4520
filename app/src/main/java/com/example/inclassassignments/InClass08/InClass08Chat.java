package com.example.inclassassignments.InClass08;

import android.content.Context;
import android.graphics.Color;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InClass08Chat extends Fragment implements ChatGetter.IFromChatGetter {
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
    private Map<String, Object> sentMessages;
    private Map<String, Object> receivedMessages;

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
        threadpool = Executors.newFixedThreadPool(4);
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
                    MessagePoster messagePoster = new MessagePoster(messageText, chatUser.getUserName(), currentUsername, messageQueue);
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
                        displayMessages((ArrayList<String>) sentMessages.get("sentMessages"),
                                (ArrayList<Timestamp>) sentMessages.get("timestamps"),
                                (ArrayList<String>) receivedMessages.get("sentMessages"),
                                (ArrayList<Timestamp>) receivedMessages.get("timestamps"));
                        break;
                    case MessagePoster.STATUS_START:
                        break;
                    case MessagePoster.STATUS_FAIL:
                        break;
                    case MessagePoster.STATUS_SUCCESS:
                        editTextMessage.setText("");
                        String messsageText = msg.getData().getString(MessagePoster.KEY_MESSAGE);
                        TextView message = new TextView(getContext());
                        message.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        message.setText(messsageText);
                        message.setGravity(View.FOCUS_RIGHT);
                        linearLayoutChat.addView(message);
                        break;
                }
                return false;
            }
        });

        ChatGetter chatGetter = new ChatGetter(this, currentUsername, chatUser.getUserName(), messageQueue);
        threadpool.execute(chatGetter);
        super.onStart();
    }

    private void displayMessages(ArrayList<String> sentMessages, ArrayList<Timestamp> sendTimestamps, ArrayList<String> receivedMessages, ArrayList<Timestamp> receivedTimestamps) {
        int lastDisplayedIndex = 0;
        for (int i = 0; i < sentMessages.size(); i ++) {
            Timestamp currentTimestamp = sendTimestamps.get(i); // timestamp of sent message
            if (receivedTimestamps.size() != 0) {
                Timestamp currentReceivedTimestamp = receivedTimestamps.get(lastDisplayedIndex); // timestamp of most recent un printed received message
                while (currentReceivedTimestamp.compareTo(currentTimestamp) <= 0 && lastDisplayedIndex < receivedMessages.size()) {
                    TextView message = new TextView(getContext());
                    message.setText(chatUser.getFirstName() + ": " + receivedMessages.get(lastDisplayedIndex));
                    message.setTextColor(Color.BLACK);
                    message.setBackgroundColor(Color.rgb(255, 192, 192));
                    linearLayoutChat.addView(message);
                    lastDisplayedIndex ++;
                    if (lastDisplayedIndex < receivedMessages.size()) {
                        currentReceivedTimestamp = receivedTimestamps.get(lastDisplayedIndex); // next timestamp of received message
                    }
                }
            }
            TextView message = new TextView(getContext());
            message.setText("me: " + sentMessages.get(i));
            message.setBackgroundColor(Color.rgb(192, 255, 192));
            linearLayoutChat.addView(message);
        }

        for (int i = lastDisplayedIndex; i < receivedMessages.size(); i ++) {
            TextView message = new TextView(getContext());
            message.setText(chatUser.getFirstName() + ": " + receivedMessages.get(i));
            message.setTextColor(Color.BLACK);
            message.setBackgroundColor(Color.rgb(255, 192, 192));
            linearLayoutChat.addView(message);
        }
    }

    @Override
    public void gotData(String key, Map<String, Object> messages) {
        if (key.equals(ChatGetter.KEY_SENT_MESSAGES)) {
            sentMessages = messages;
        } else {
            receivedMessages = messages;
        }
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
    public static final String KEY_SENT_MESSAGES = "sent messages";
    public static final String KEY_RECEIVED_MESSAGES = "received messages";
    private IFromChatGetter sendData;
    private int sendCount = 0;

    public ChatGetter(Fragment parentFragment, String currentUsername, String chatUsername, Handler messageQueue) {
        if (parentFragment instanceof IFromChatGetter) {
            sendData = (IFromChatGetter) parentFragment;
        } else {
            throw new RuntimeException(parentFragment + " must implement IFromChatGetter");
        }
        this.currentUsername = currentUsername;
        this.chatUsername = chatUsername;
        this.messageQueue = messageQueue;
        this.database = FirebaseFirestore.getInstance();
    }

    @Override
    public void run() {
        Message startMessage = new Message();
        startMessage.what = STATUS_START;
        messageQueue.sendMessage(startMessage);
        getChat(currentUsername, chatUsername);
        getChat(chatUsername, currentUsername);
    }

    private void getChat(String currentUsername, String chatUsername) {
        DocumentReference docRef = database
                .collection("users")
                .document(currentUsername)
                .collection("chats")
                .document(chatUsername);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> data = new HashMap<>();
                ArrayList<String> sentMessages = new ArrayList<>();
                ArrayList<Timestamp> timestamps = new ArrayList<>();
                data.put("sentMessages", sentMessages);
                data.put("timestamps", timestamps);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        DocumentReference docRef = document.getReference();
                        docRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                            }
                        });
                    } else {
                        data = document.getData();
                    }
                    if (currentUsername.equals(getCurrentUsername())) {
                        sendData.gotData(KEY_SENT_MESSAGES, data);
                    } else {
                        sendData.gotData(KEY_RECEIVED_MESSAGES, data);
                    }
                    sendCount ++;
                    if (sendCount > 1) {
                        Message successMessage = new Message();
                        successMessage.what = STATUS_SUCCESS;
                        messageQueue.sendMessage(successMessage);
                    }
                } else {
                    Message failMessage = new Message();
                    failMessage.what = STATUS_FAIL;
                    messageQueue.sendMessage(failMessage);
                }
            }
        });
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public interface IFromChatGetter {
        void gotData(String key, Map<String, Object> messages);
    }
}

class MessagePoster implements Runnable {
    private FirebaseFirestore database;
    private String messageText;
    private String chatUsername;
    private String currentUsername;
    public static final int STATUS_START = 0x004;
    public static final int STATUS_FAIL = 0x005;
    public static final int STATUS_SUCCESS = 0x006;
    public static final String KEY_MESSAGE = "message text";
    private Handler messageQueue;

    public MessagePoster(String messageText, String chatUsername, String currentUsername, Handler messageQueue) {
        this.messageText = messageText;
        this.chatUsername = chatUsername;
        this.currentUsername = currentUsername;
        this.database = FirebaseFirestore.getInstance();
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        Message startMessage = new Message();
        startMessage.what = STATUS_START;
        messageQueue.sendMessage(startMessage);
        DocumentReference chatUserDoc = database
                .collection("users")
                .document(currentUsername)
                .collection("chats")
                .document(chatUsername);
        chatUserDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Map<String, Object> data = documentSnapshot.getData();
                    ArrayList<String> sentMessages = (ArrayList<String>) data.get("sentMessages");
                    ArrayList<Timestamp> timestamps = (ArrayList<Timestamp>) data.get("timestamps");
                    sentMessages.add(messageText);
                    timestamps.add(Timestamp.now());
                    Map<String, Object> newData = new HashMap<>();
                    newData.put("sentMessages", sentMessages);
                    newData.put("timestamps", timestamps);
                    chatUserDoc.update(newData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Message successMessage = new Message();
                            successMessage.what = STATUS_SUCCESS;
                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_MESSAGE, messageText);
                            successMessage.setData(bundle);
                            messageQueue.sendMessage(successMessage);
                        }
                    });
                }
            }
        });
    }
}