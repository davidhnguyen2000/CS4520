package com.example.inclassassignments.InClass07;

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
import android.widget.Toast;
import com.example.inclassassignments.R;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InClass07Register extends Fragment {

    private EditText userName;
    private EditText email;
    private EditText password;
    private Button register;
    private Button back;
    private IFromRegisterToMain sendData;
    private Handler messageQueue;
    private ExecutorService threadPool;

    public InClass07Register() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFromRegisterToMain) {
            sendData = (IFromRegisterToMain) context;
        } else {
            throw new RuntimeException(context + " must implement IFromRegisterToMain");
        }
    }

    public static InClass07Register newInstance(String param1, String param2) {
        return new InClass07Register();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_in_class07_register, container, false);
        userName = rootView.findViewById(R.id.editTextUserName);
        email = rootView.findViewById(R.id.editTextUserEmail);
        password = rootView.findViewById(R.id.editTextUserPassword);
        register = rootView.findViewById(R.id.buttonSendRegister);
        back = rootView.findViewById(R.id.buttonRegisterBack);
        threadPool = Executors.newFixedThreadPool(2);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.backFromRegister();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameData = userName.getText().toString();
                String emailData = email.getText().toString();
                String passwordData = password.getText().toString();
                boolean checkUser = !userNameData.isEmpty();
                boolean checkEmail = !emailData.isEmpty();
                boolean checkPass = !passwordData.isEmpty();
                if (checkUser && checkEmail && checkPass) {
                    PostRegister postRegister = new PostRegister(userNameData, emailData, passwordData, messageQueue);
                    threadPool.execute(postRegister);
                } else {
                    Toast.makeText(getContext(), "Missing fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        messageQueue = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case PostRegister.STATUS_START:
                        break;
                    case PostRegister.STATUS_FAIL:
                        Toast.makeText(getContext(), "Failed to register user", Toast.LENGTH_SHORT).show();
                        break;
                    case PostRegister.STATUS_SUCCESS:
                        Token token = (Token) msg.getData().getSerializable(PostRegister.KEY_TOKEN);
                        sendData.tokenFromRegister(token);
                }
                return false;
            }
        });
    }

    public interface IFromRegisterToMain {
        void backFromRegister();
        void tokenFromRegister(Token token);
    }
}

class PostRegister implements Runnable {
    private String userName;
    private String email;
    private String password;
    public static final int STATUS_START = 0x001;
    public static final int STATUS_FAIL = 0x002;
    public static final int STATUS_SUCCESS = 0x003;
    public static final String KEY_TOKEN = "token";
    private Handler messageQueue;

    public PostRegister(String userName, String email, String password, Handler messageQueue) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        // send start message
        Message startMessage = new Message();
        startMessage.what = STATUS_START;
        messageQueue.sendMessage(startMessage);
        // begin posting to API
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("name", userName)
                .add("email", email)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url("http://dev.sakibnm.space:3000/api/auth/register")
                .post(formBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                Gson gsonData = new Gson();
                Token token = gsonData.fromJson(response.body().charStream(), Token.class);
                // send token back to main thread
                Message successMessage = new Message();
                successMessage.what = STATUS_SUCCESS;
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_TOKEN, token);
                successMessage.setData(bundle);
                messageQueue.sendMessage(successMessage);
            } else {
                Message failMessage = new Message();
                failMessage.what = STATUS_FAIL;
                messageQueue.sendMessage(failMessage);
            }
        } catch (IOException e) {
            Message failMessage = new Message();
            failMessage.what = STATUS_FAIL;
            messageQueue.sendMessage(failMessage);
        }
    }
}