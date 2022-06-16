package com.example.inclassassignments.InClass08and09;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.inclassassignments.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InClass08ChatList extends Fragment {
    private Button buttonEditProfile;
    private Button buttonLogout;
    private RecyclerView recyclerViewChats;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ChatAdapter chatAdapter;
    private String currentUsername;
    private Handler messageQueue;
    private ExecutorService threadPool;
    private IFromChatListFragment sendData;

    public InClass08ChatList() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFromChatListFragment) {
            sendData = (IFromChatListFragment) context;
        } else {
            throw new RuntimeException(context + " must implement IFromChatListFragment");
        }
    }

    public static InClass08ChatList newInstance(String param1, String param2) {
        return new InClass08ChatList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_in_class08_chat_list, container, false);
        buttonEditProfile = rootView.findViewById(R.id.buttonEditProfile);
        buttonLogout = rootView.findViewById(R.id.buttonLogoutChat);
        recyclerViewChats = rootView.findViewById(R.id.recylcerViewChats);
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewChats.setLayoutManager(recyclerViewLayoutManager);
        threadPool = Executors.newFixedThreadPool(2);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.toEditProfile(currentUsername);
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.logout();
            }
        });

        messageQueue = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case ChatListGetter.STATUS_START:
                        break;
                    case ChatListGetter.STATUS_FAIL:
                        break;
                    case ChatListGetter.STATUS_SUCCESS:
                        ArrayList<AuthUser> chats = (ArrayList<AuthUser>) msg.getData().getSerializable(ChatListGetter.KEY_CHATS);
                        chatAdapter = new ChatAdapter(getContext(), chats, currentUsername);
                        recyclerViewChats.setAdapter(chatAdapter);
                        break;
                }
                return false;
            }
        });
        ChatListGetter chatGetter = new ChatListGetter(currentUsername, messageQueue);
        threadPool.execute(chatGetter);
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    public interface IFromChatListFragment {
        void toEditProfile(String currentUsername);
        void logout();
    }

}

class ChatListGetter implements Runnable {
    private String currentUsername;
    public static final int STATUS_START = 0x001;
    public static final int STATUS_SUCCESS = 0x002;
    public static final int STATUS_FAIL = 0x003;
    public static final String KEY_CHATS = "chats";
    private Handler messageQueue;

    public ChatListGetter(String currentUsername, Handler messageQueue) {
        this.currentUsername = currentUsername;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        Message startMessage = new Message();
        startMessage.what = STATUS_START;
        messageQueue.sendMessage(startMessage);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("debug", "onComplete: found documents");
                            ArrayList<AuthUser> chats = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> docData = document.getData();
                                String userName = document.getId();
                                if (!userName.equals(currentUsername)) {
                                    Log.d("debug", "adding to list");
                                    String firstName = docData.get("first name").toString();
                                    String lastName = docData.get("last name").toString();
                                    chats.add(new AuthUser(firstName, lastName, userName));
                                }
                            }
                            Message successMessage = new Message();
                            successMessage.what = STATUS_SUCCESS;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(KEY_CHATS, chats);
                            successMessage.setData(bundle);
                            messageQueue.sendMessage(successMessage);
                        } else {
                            Message failMessage = new Message();
                            failMessage.what = STATUS_FAIL;
                            messageQueue.sendMessage(failMessage);
                        }
                    }
                });
    }
}