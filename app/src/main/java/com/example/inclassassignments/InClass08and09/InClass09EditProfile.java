package com.example.inclassassignments.InClass08and09;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.inclassassignments.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InClass09EditProfile extends Fragment {

    private ImageView imageViewProfilePicture;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private Button buttonSave;
    private Button buttonCancel;

    private String currentUsername;
    private IFromEditProfileFragment sendData;
    private ExecutorService threadpool;
    private Handler messageQueue;
    private FirebaseStorage storage;
    private Bitmap profileImageBitmap;

    public InClass09EditProfile() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFromEditProfileFragment) {
            sendData = (IFromEditProfileFragment) context;
        } else {
            throw new RuntimeException(context + " must implement IFromEditProfileFragment");
        }
    }

    public static InClass09EditProfile newInstance(String param1, String param2) {
        return new InClass09EditProfile();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        threadpool = Executors.newFixedThreadPool(3);

        messageQueue = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case ProfileGetter.STATUS_START:
                        break;
                    case ProfileGetter.STATUS_FAIL:
                        Toast.makeText(getContext(), "Unable to retrieve profile data", Toast.LENGTH_SHORT).show();
                        break;
                    case ProfileGetter.STATUS_SUCCESS:
                        updateText(msg);
                        profileImageBitmap = (Bitmap) msg.getData().getParcelable(ProfileGetter.KEY_PROFILE_IMAGE);
                        imageViewProfilePicture.setImageBitmap(profileImageBitmap);
                        break;
                    case ProfileGetter.STATUS_FAIL_GET_IMAGE:
                        updateText(msg);
                        imageViewProfilePicture.setImageResource(R.drawable.select_avatar);
                        break;
                    case ProfileSaver.STATUS_START:
                        break;
                    case ProfileSaver.STATUS_FAIL:
                        Toast.makeText(getContext(), "Unable to save profile data", Toast.LENGTH_SHORT).show();
                        break;
                    case ProfileSaver.STATUS_SUCCESS:
                        sendData.backToChatList(currentUsername);
                        break;
                }
                return false;
            }
        });



        ProfileGetter getProfile = new ProfileGetter(messageQueue, currentUsername);
        threadpool.execute(getProfile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_in_class09_edit_profile, container, false);
        imageViewProfilePicture = rootView.findViewById(R.id.imageViewProfilePicture);
        editTextFirstName = rootView.findViewById(R.id.editTextProfileFirstName);
        editTextLastName = rootView.findViewById(R.id.editTextProfileLastName);
        buttonSave = rootView.findViewById(R.id.buttonSaveProfile);
        buttonCancel = rootView.findViewById(R.id.buttonCancel);
        storage = FirebaseStorage.getInstance();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFirstName = editTextFirstName.getText().toString();
                String newLastName = editTextLastName.getText().toString();
                ProfileSaver save = new ProfileSaver(messageQueue, currentUsername, newFirstName, newLastName, profileImageBitmap);
                threadpool.execute(save);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.backToChatList(currentUsername);
            }
        });

        imageViewProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open new fragment
                sendData.selectNewProfilePicture();
            }
        });
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    private void updateText(Message msg) {
        String firstName = msg.getData().getString(ProfileGetter.KEY_FIRST);
        String lastName = msg.getData().getString(ProfileGetter.KEY_LAST);
        editTextFirstName.setText(firstName);
        editTextLastName.setText(lastName);
    }

    public void updateProfileImageBitmap(Bitmap bmp) {
        profileImageBitmap = bmp;
        imageViewProfilePicture.setImageBitmap(Bitmap.createScaledBitmap(profileImageBitmap, imageViewProfilePicture.getWidth(), imageViewProfilePicture.getHeight(), false));
    }

    public interface IFromEditProfileFragment {
        void backToChatList(String currentUsername);
        void selectNewProfilePicture();
    }
}

class ProfileGetter implements Runnable {
    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private Handler messageQueue;
    private String currentUsername;
    private Bitmap profileImageBitmap;
    public static final int STATUS_SUCCESS = 0x001;
    public static final int STATUS_FAIL = 0x002;
    public static final int STATUS_START = 0x003;
    public static final int STATUS_FAIL_GET_IMAGE = 0x007;
    public static final String KEY_FIRST = "first name";
    public static final String KEY_LAST = "last name";
    public static final String KEY_PROFILE_IMAGE = "profile image";

    public ProfileGetter(Handler messageQueue, String currentUsername) {
        this.messageQueue = messageQueue;
        this.currentUsername = currentUsername;
        this.database = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    @Override
    public void run() {
        Message startMessage = new Message();
        startMessage.what = STATUS_START;
        messageQueue.sendMessage(startMessage);
        DocumentReference docRef = database.collection("users").document(currentUsername);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    String firstName = (String) data.get("first name");
                    String lastName = (String) data.get("last name");
                    Message endMessage = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_FIRST, firstName);
                    bundle.putString(KEY_LAST, lastName);
                    String accessPath = "profilePictures/" + currentUsername + ".JPG";
                    StorageReference storageRef = storage.getReference().child(accessPath);
                    final long ONE_MEGABYTE = 1024 * 1024;
                    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            profileImageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            endMessage.what = STATUS_SUCCESS;
                            bundle.putParcelable(KEY_PROFILE_IMAGE, profileImageBitmap);
                            endMessage.setData(bundle);
                            messageQueue.sendMessage(endMessage);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            endMessage.what = STATUS_FAIL_GET_IMAGE;
                            messageQueue.sendMessage(endMessage);
                        }
                    });

                } else {
                    Message failMessage = new Message();
                    failMessage.what = STATUS_FAIL;
                    messageQueue.sendMessage(failMessage);
                }
            }
        });
    }
}

class ProfileSaver implements Runnable {
    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private Handler messageQueue;
    private String currentUsername;
    private String firstName;
    private String lastName;
    private Bitmap profileImageBitmap;
    public static final int STATUS_SUCCESS = 0x004;
    public static final int STATUS_FAIL = 0x005;
    public static final int STATUS_START = 0x006;

    public ProfileSaver(Handler messageQueue, String currentUsername, String firstName, String lastName, Bitmap profileImageBitmap) {
        this.messageQueue = messageQueue;
        this.currentUsername = currentUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileImageBitmap = profileImageBitmap;
        this.database = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    @Override
    public void run() {
        Message startMessage = new Message();
        startMessage.what = STATUS_START;
        messageQueue.sendMessage(startMessage);
        DocumentReference docRef = database.collection("users").document(currentUsername);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    data.put("first name", firstName);
                    data.put("last name", lastName);
                    docRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            String accessPath = "profilePictures/" + currentUsername + ".JPG";
                            StorageReference storageRef = storage.getReference().child(accessPath);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            profileImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();
                            UploadTask uploadTask = storageRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Message failMessage = new Message();
                                    failMessage.what = STATUS_FAIL;
                                    messageQueue.sendMessage(failMessage);
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Message successMessage = new Message();
                                    successMessage.what = STATUS_SUCCESS;
                                    messageQueue.sendMessage(successMessage);
                                }
                            });
                        }
                    });
                } else {
                    Message failMessage = new Message();
                    failMessage.what = STATUS_FAIL;
                    messageQueue.sendMessage(failMessage);
                }
            }
        });
    }
}