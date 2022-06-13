package com.example.inclassassignments.InClass08;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.inclassassignments.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class InClass08Login extends Fragment {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private IFromLoginFragment sendData;
    private FirebaseFirestore database;

    public InClass08Login() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFromLoginFragment) {
            sendData = (IFromLoginFragment) context;
        } else {
            throw new RuntimeException(context + " must implement IFromLoginFragment");
        }
    }

    public static InClass08Login newInstance(String param1, String param2) {
        return new InClass08Login();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_in_class08_login, container, false);
        editTextUsername = rootView.findViewById(R.id.editTextUsernameLoginFragment);
        editTextPassword = rootView.findViewById(R.id.editTextPasswordLoginFragment);
        buttonLogin = rootView.findViewById(R.id.buttonLoginLoginFragment);
        buttonRegister = rootView.findViewById(R.id.buttonRegisterFromLogin);
        database = FirebaseFirestore.getInstance();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.registerSelected();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Some fields are missing", Toast.LENGTH_SHORT).show();
                } else {
                    login(username, password);
                }
            }
        });
    }

    private void login(String username, String password) {
        DocumentReference docRef = database.collection("users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> user = document.getData();
                    if (user != null) {
                        if (user.get("password").equals(password)) {
                            sendData.loggedIn(username);
                            return;
                        }
                    }
                    Toast.makeText(getContext(), "Invalid login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface IFromLoginFragment {
        void loggedIn(String currentUsername);
        void registerSelected();
    }
}