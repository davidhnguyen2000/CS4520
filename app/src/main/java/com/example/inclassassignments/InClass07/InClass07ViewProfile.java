package com.example.inclassassignments.InClass07;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inclassassignments.InClass06.TopHeadLines;
import com.example.inclassassignments.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InClass07ViewProfile extends Fragment {

    private TextView textViewUsername;
    private TextView textViewEmail;
    private Button logout;
    private Button viewNotes;
    private IFromViewProfileToMain sendData;
    private Token token;
    private Handler messageQueue;
    private ExecutorService threadPool;

    public InClass07ViewProfile() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFromViewProfileToMain) {
            sendData = (IFromViewProfileToMain) context;
        } else {
            throw new RuntimeException(context + " must implement IFromViewProfileToMain");
        }
    }

    public static InClass07ViewProfile newInstance(String param1, String param2) {
        return new InClass07ViewProfile();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_in_class07_view_profile, container, false);
        textViewUsername = rootView.findViewById(R.id.textViewProfileName);
        textViewEmail = rootView.findViewById(R.id.textViewProfileEmail);
        logout = rootView.findViewById(R.id.buttonLogout);
        viewNotes = rootView.findViewById(R.id.buttonViewNotes);
        threadPool = Executors.newFixedThreadPool(3);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout logout = new Logout(messageQueue);
                threadPool.execute(logout);
                sendData.logoutFromViewProfile();
            }
        });

        viewNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.viewNotesFromViewProfile();
            }
        });

        messageQueue = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case UserDataGetter.STATUS_START:
                        break;
                    case UserDataGetter.STATUS_SUCCESS:
                        User user = (User) msg.getData().getSerializable(UserDataGetter.KEY_USER);
                        textViewUsername.setText(user.getName());
                        textViewEmail.setText(user.getEmail());
                        break;
                    case UserDataGetter.STATUS_FAIL:
                        Toast.makeText(getContext(), "Unable to get user data", Toast.LENGTH_SHORT).show();
                        break;
                    case Logout.STATUS_FAIL:
                        Toast.makeText(getContext(), "Unable to log out", Toast.LENGTH_SHORT).show();
                        break;
                    case Logout.STATUS_SUCCESS:
                        sendData.logoutFromViewProfile();
                        break;
                }
                return false;
            }
        });

        // start getting user data
        UserDataGetter userDataGetter = new UserDataGetter(token, messageQueue);
        threadPool.execute(userDataGetter);
    }

    public void updateToken(Token token) {
        this.token = token;
    }

    public interface IFromViewProfileToMain {
        void viewNotesFromViewProfile();
        void logoutFromViewProfile();
    }
}

class UserDataGetter implements Runnable {
    private Token token;
    private Handler messageQueue;
    public static final int STATUS_START = 0x001;
    public static final int STATUS_FAIL = 0x002;
    public static final int STATUS_SUCCESS = 0x003;
    public static final String KEY_USER = "user";

    public UserDataGetter(Token token, Handler messageQueue) {
        this.token = token;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        // send start message
        Message startMessage = new Message();
        startMessage.what = STATUS_START;
        messageQueue.sendMessage(startMessage);
        // begin getting user data with token
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://dev.sakibnm.space:3000/api/auth/me")
                .header("x-access-token", token.getToken())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message failMessage = new Message();
                failMessage.what = STATUS_FAIL;
                messageQueue.sendMessage(failMessage);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Gson gsonData = new Gson();
                    User user = gsonData.fromJson(response.body().charStream(), User.class);
                    Message successMessage = new Message();
                    successMessage.what = STATUS_SUCCESS;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KEY_USER, user);
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

class Logout implements Runnable {
    private Handler messageQueue;
    public static final int STATUS_SUCCESS = 0x004;
    public static final int STATUS_FAIL = 0x005;

    public Logout(Handler messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://dev.sakibnm.space:3000/api/auth/logout")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message failMessage = new Message();
                failMessage.what = STATUS_FAIL;
                messageQueue.sendMessage(failMessage);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Message successMessage = new Message();
                    successMessage.what = STATUS_SUCCESS;
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
