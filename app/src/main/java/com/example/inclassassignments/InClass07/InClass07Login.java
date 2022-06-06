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

public class InClass07Login extends Fragment {

    EditText email;
    EditText password;
    Button login;
    Button back;
    IFromLoginToMain sendData;
    private Handler messageQueue;
    private ExecutorService threadPool;

    public InClass07Login() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof InClass07Login.IFromLoginToMain) {
            sendData = (InClass07Login.IFromLoginToMain) context;
        } else {
            throw new RuntimeException(context + " must implement IFromRegisterToMain");
        }
    }

    public static InClass07Login newInstance(String param1, String param2) {
        return new InClass07Login();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_in_class07_login, container, false);
        email = rootView.findViewById(R.id.editTextLoginEmail);
        password = rootView.findViewById(R.id.editTextLoginPassword);
        login = rootView.findViewById(R.id.buttonSendLogIn);
        back = rootView.findViewById(R.id.buttonLogInBack);
        threadPool = Executors.newFixedThreadPool(2);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.backFromLogin();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailData = email.getText().toString();
                String passwordData = password.getText().toString();
                boolean checkEmail = !emailData.isEmpty();
                boolean checkPass = !passwordData.isEmpty();
                if (checkEmail && checkPass) {
                    PostLogin postLogin = new PostLogin(emailData, passwordData, messageQueue);
                    threadPool.execute(postLogin);
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
                        Toast.makeText(getContext(), "Invalid login", Toast.LENGTH_SHORT).show();
                        break;
                    case PostRegister.STATUS_SUCCESS:
                        Token token = (Token) msg.getData().getSerializable(PostRegister.KEY_TOKEN);
                        sendData.tokenFromLogin(token);
                }
                return false;
            }
        });
    }

    public interface IFromLoginToMain {
        void backFromLogin();
        void tokenFromLogin(Token token);
    }
}

class PostLogin implements Runnable {
    private String email;
    private String password;
    public static final int STATUS_START = 0x001;
    public static final int STATUS_FAIL = 0x002;
    public static final int STATUS_SUCCESS = 0x003;
    public static final String KEY_TOKEN = "token";
    private Handler messageQueue;

    public PostLogin(String email, String password, Handler messageQueue) {
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
                .add("email", email)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url("http://dev.sakibnm.space:3000/api/auth/login")
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