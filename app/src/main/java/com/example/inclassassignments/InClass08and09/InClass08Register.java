package com.example.inclassassignments.InClass08and09;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.inclassassignments.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InClass08Register extends Fragment {

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextDisplayName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegister;
    private Button buttonLogin;
    private ImageView imageViewProfile;
    private IFromRegisterFragment sendData;
    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private Bitmap profileImageBitmap;

    public InClass08Register() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFromRegisterFragment) {
            sendData = (IFromRegisterFragment) context;
        } else {
            throw new RuntimeException(context + " must implement IFromRegisterFragment");
        }
    }

    public static InClass08Register newInstance(String param1, String param2) {
        return new InClass08Register();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_in_class08_register, container, false);
        editTextFirstName = rootView.findViewById(R.id.editTextFirstName);
        editTextLastName = rootView.findViewById(R.id.editTextLastName);
        editTextDisplayName = rootView.findViewById(R.id.editTextDisplayName);
        editTextEmail = rootView.findViewById(R.id.editTextEmailAddress);
        editTextPassword = rootView.findViewById(R.id.editTextPassword);
        buttonRegister = rootView.findViewById(R.id.buttonRegisterNewUser);
        buttonLogin = rootView.findViewById(R.id.buttonLoginFromRegister);
        imageViewProfile = rootView.findViewById(R.id.imageViewProfileRegister);
        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        imageViewProfile.setImageResource(R.drawable.select_avatar);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = editTextFirstName.getText().toString();
                String lastName = editTextLastName.getText().toString();
                String displayName = editTextDisplayName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                if (firstName.isEmpty() || lastName.isEmpty() || displayName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Missing fields", Toast.LENGTH_SHORT).show();
                } else {
                    register(firstName, lastName, displayName, email, password);
                }
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.loginSelected();
            }
        });

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.selectProfilePicture();
            }
        });
    }

    public void updateProfileImageBitmap(Bitmap bmp) {
        profileImageBitmap = bmp;
        imageViewProfile.setImageBitmap(Bitmap.createScaledBitmap(profileImageBitmap, imageViewProfile.getWidth(), imageViewProfile.getHeight(), false));
    }

    private void register(String firstName, String lastName, String username, String email, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("first name", firstName);
        user.put("last name", lastName);
        user.put("email", email);
        user.put("password", password);

        DocumentReference docRef = database.collection("users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(getContext(), "User name already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                setupChats(docRef);
                                String accessPath = "profilePictures/" + username + ".JPG";
                                StorageReference storageRef = storage.getReference().child(accessPath);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                profileImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] data = baos.toByteArray();
                                UploadTask uploadTask = storageRef.putBytes(data);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(getContext(), "Unable to upload profile picture", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                sendData.registered();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Unable to register new user", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void setupChats(DocumentReference docRef) {
        database.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                        for (int i = 0; i < docs.size(); i++) {
                            DocumentSnapshot userDoc = docs.get(i);
                            DocumentReference collectionReference = database.collection("users").document(docRef.getId()).collection("chats").document(userDoc.getId());
                            collectionReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    DocumentReference docRef = documentSnapshot.getReference();
                                    Map<String, Object> blank = new HashMap<>();
                                    ArrayList<String> sentMessages = new ArrayList<>();
                                    ArrayList<Timestamp> timestamps = new ArrayList<>();
                                    blank.put("sentMessages", sentMessages);
                                    blank.put("timestamps", timestamps);
                                    docRef.set(blank).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {}
                                    });
                                }
                            });
                        }
                    }
                });
    }

    public interface IFromRegisterFragment {
        void registered();
        void loginSelected();
        void selectProfilePicture();
    }
}