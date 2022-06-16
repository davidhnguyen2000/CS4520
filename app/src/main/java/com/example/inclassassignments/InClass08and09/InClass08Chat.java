package com.example.inclassassignments.InClass08and09;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InClass08Chat extends Fragment implements ChatGetter.IFromChatGetter, BitmapGetter.IFromBitmapGetter, TimestampGetter.IFromTimestampGetter {
    private TextView textViewChatUser;
    private LinearLayout linearLayoutChat;
    private EditText editTextMessage;
    private Button buttonSend;
    private Button buttonBackToChatList;
    private Button buttonSendImageGallery;
    private Button buttonSendImageCamera;

    private IFromChatFragment sendData;
    private String currentUsername;
    private AuthUser chatUser;
    private Handler messageQueue;
    private ExecutorService threadpool;
    private Map<String, Object> sentMessages;
    private Map<String, Object> receivedMessages;
    private Map<String, Object> sentImages;
    private Map<String, Object> receivedImages;
    private int userCount;

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
        buttonSendImageGallery = rootView.findViewById(R.id.buttonGallery);
        buttonSendImageCamera = rootView.findViewById(R.id.buttonCamera);
        threadpool = Executors.newFixedThreadPool(4);
        return rootView;
    }

    @Override
    public void onStart() {
        userCount = 0;
        sentImages = new HashMap<>();
        receivedImages = new HashMap<>();
        linearLayoutChat.removeAllViewsInLayout();
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

        buttonSendImageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.getImageFromAlbum();
            }
        });

        buttonSendImageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.getImageFromCamera();
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
                        getImages(currentUsername, chatUser.getUserName());
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
                        message.setText("me: " + messsageText);
                        message.setGravity(View.FOCUS_RIGHT);
                        linearLayoutChat.addView(message);
                        break;
                    case TimestampGetter.STATUS_END:
                        displayMessages((ArrayList<Object>) sentMessages.get("sentMessages"),
                                (ArrayList<Timestamp>) sentMessages.get("timestamps"),
                                (ArrayList<Object>) receivedMessages.get("sentMessages"),
                                (ArrayList<Timestamp>) receivedMessages.get("timestamps"),
                                (ArrayList<Object>) sentImages.get("images"),
                                (ArrayList<Timestamp>) sentImages.get("timestamps"),
                                (ArrayList<Object>) receivedImages.get("images"),
                                (ArrayList<Timestamp>) receivedImages.get("timestamps"));
                        break;
                }
                return false;
            }
        });

        ChatGetter chatGetter = new ChatGetter(this, currentUsername, chatUser.getUserName(), messageQueue);
        threadpool.execute(chatGetter);
        super.onStart();
    }

    private void displayMessages(ArrayList<Object> sentMessages, ArrayList<Timestamp> sendTimestamps,
                                 ArrayList<Object> receivedMessages, ArrayList<Timestamp> receivedTimestamps,
                                 ArrayList<Object> sentImages, ArrayList<Timestamp> sentImageTimestamps,
                                 ArrayList<Object> receivedImages, ArrayList<Timestamp> receivedImageTimestamps) {
        Map <String, Object> sortedMessagesMap = merge(sentMessages, receivedMessages, sendTimestamps, receivedTimestamps);
        Map <String, Object> sortedImagesMap = merge(sentImages, receivedImages, sentImageTimestamps, receivedImageTimestamps);
        ArrayList<String> sortedMessages = (ArrayList<String>) sortedMessagesMap.get("list");
        ArrayList<Timestamp> sortedMessagesTimestamps = (ArrayList<Timestamp>) sortedMessagesMap.get("timestamps");
        ArrayList<Integer> sortedMessagesWho = (ArrayList<Integer>) sortedMessagesMap.get("who");
        ArrayList<Bitmap> sortedImages = (ArrayList<Bitmap>) sortedImagesMap.get("list");
        ArrayList<Timestamp> sortedImagesTimestamps = (ArrayList<Timestamp>) sortedImagesMap.get("timestamps");
        ArrayList<Integer> sortedImagesWho = (ArrayList<Integer>) sortedImagesMap.get("who");
        while (!sortedMessages.isEmpty()) {
            if (!sortedImages.isEmpty()) {
                while (!sortedImages.isEmpty() && sortedImagesTimestamps.get(0).compareTo(sortedMessagesTimestamps.get(0)) < 0) {
                    // print image and remove from list
                    Bitmap imageBmp = sortedImages.get(0);
                    int whoId = sortedImagesWho.get(0);
                    String userText = "me: ";
                    TextView textView = new TextView(getContext());
                    if (whoId == 2) {
                        userText = chatUser.getFirstName() + ": ";
                        textView.setTextColor(Color.BLACK);
                    }
                    textView.setText(userText);
                    linearLayoutChat.addView(textView);
                    ImageView imageView = new ImageView(getContext());
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(imageBmp, linearLayoutChat.getWidth() / 2, linearLayoutChat.getWidth() / 2, false));
                    linearLayoutChat.addView(imageView);
                    // remove from list
                    sortedImages.remove(0);
                    sortedImagesTimestamps.remove(0);
                    sortedImagesWho.remove(0);
                }
            }
            String messageText = sortedMessages.get(0);
            int whoId = sortedMessagesWho.get(0);
            String who = "me: ";
            TextView message = new TextView(getContext());
            if (whoId == 2) {
                who = chatUser.getFirstName() + ": ";
                message.setTextColor(Color.BLACK);
            }
            message.setText(who + messageText);
            linearLayoutChat.addView(message);
            // remove from list
            sortedMessages.remove(0);
            sortedMessagesTimestamps.remove(0);
            sortedMessagesWho.remove(0);
        }

        while (!sortedImages.isEmpty()) {
            // print image and remove from list
            Bitmap imageBmp = sortedImages.get(0);
            int whoId = sortedImagesWho.get(0);
            String userText = "me: ";
            TextView textView = new TextView(getContext());
            if (whoId == 2) {
                userText = chatUser.getFirstName() + ": ";
                textView.setTextColor(Color.BLACK);
            }
            textView.setText(userText);
            linearLayoutChat.addView(textView);
            ImageView imageView = new ImageView(getContext());
            imageView.setImageBitmap(Bitmap.createScaledBitmap(imageBmp, linearLayoutChat.getWidth() / 2, linearLayoutChat.getWidth() / 2, false));
            linearLayoutChat.addView(imageView);
            // remove from list
            sortedImages.remove(0);
            sortedImagesTimestamps.remove(0);
            sortedImagesWho.remove(0);
        }
    }

    private Map<String, Object> merge(ArrayList<Object> list1, ArrayList<Object> list2,
                                      ArrayList<Timestamp> timestamps1, ArrayList<Timestamp> timestamps2) {
        Map<String, Object> merged = new HashMap<>();
        ArrayList<Object> sortedList = new ArrayList<>();
        ArrayList<Timestamp> sortedTimestamps = new ArrayList<>();
        ArrayList<Integer> whoList = new ArrayList<>();
        int index1;
        int index2;
        while (!list1.isEmpty() && !list2.isEmpty()) {
            index1 = 0;
            index2 = 0;
            if (timestamps1.get(index1).compareTo(timestamps2.get(index2)) < 1) {
                sortedList.add(list1.get(index1));
                sortedTimestamps.add(timestamps1.get(index1));
                list1.remove(index1);
                timestamps1.remove(index1);
                whoList.add(1);
            } else {
                sortedList.add(list2.get(index2));
                sortedTimestamps.add(timestamps2.get(index2));
                list2.remove(index2);
                timestamps2.remove(index2);
                whoList.add(2);
            }
        }

        while (!list1.isEmpty()) {
            index1 = 0;
            sortedList.add(list1.get(index1));
            sortedTimestamps.add(timestamps1.get(index1));
            list1.remove(index1);
            timestamps1.remove(index1);
            whoList.add(1);
        }

        while (!list2.isEmpty()) {
            index2 = 0;
            sortedList.add(list2.get(index2));
            sortedTimestamps.add(timestamps2.get(index2));
            list2.remove(index2);
            timestamps2.remove(index2);
            whoList.add(2);
        }
        merged.put("list", sortedList);
        merged.put("timestamps", sortedTimestamps);
        merged.put("who", whoList);

        for (int i = 0; i < whoList.size(); i ++) {
            Log.d("turtle", "list: " + sortedList.get(i) + " timestamp: " + sortedTimestamps.get(i) + " who: " + whoList.get(i));
        }

        return merged;
    }

    @Override
    public void gotData(String key, Map<String, Object> data) {
        switch (key) {
            case ChatGetter.KEY_SENT_MESSAGES:
                sentMessages = data;
                break;
            case ChatGetter.KEY_RECEIVED_MESSAGES:
                receivedMessages = data;
                break;
        }
    }

    public void sendImage(Bitmap bmp) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String accessPath = "chatPictures/" + currentUsername + "/"+ chatUser.getUserName() + "/" + Timestamp.now() + ".JPEG";
        StorageReference storageRef = storage.getReference().child(accessPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageRef.putBytes(data);
        Fragment parent = this;
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(), "Unable to upload image", Toast.LENGTH_SHORT).show();
            }
        });
        // cant get uploaded image to show without refresh
    }

    private void getImages(String username, String chatUsername) {
        Log.d("turtle", "getImages");
        userCount++;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String accessPath = "chatPictures/" + username + "/"+ chatUsername;
        StorageReference storageRef = storage.getReference().child(accessPath);
        Fragment parent = this;
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                Log.d("turtle", "onSuccess: got image reference list: " + listResult.getItems().size());
                ArrayList<Bitmap> imageBitmaps = new ArrayList<>();
                BitmapGetter bitmapGetter = new BitmapGetter(listResult, 0, imageBitmaps, parent);
                threadpool.execute(bitmapGetter);
            }
        });
    }

    @Override
    public void gotBitmap(ListResult results, int currentIndex, ArrayList<Bitmap> imageBitmaps) {
        if (currentIndex > results.getItems().size() - 1) {
            if (userCount == 1) {
                sentImages.put("images", imageBitmaps);
            } else {
                receivedImages.put("images", imageBitmaps);
            }
            ArrayList<Timestamp> timestamps = new ArrayList<>();
            TimestampGetter timestampGetter = new TimestampGetter(results, 0, timestamps, this);
            threadpool.execute(timestampGetter);
        } else {
            BitmapGetter bitmapGetter = new BitmapGetter(results, currentIndex, imageBitmaps, this);
            threadpool.execute(bitmapGetter);
        }
    }

    @Override
    public void gotTimestamp(ListResult results, int currentIndex, ArrayList<Timestamp> imageTimestamps) {
        if (currentIndex > results.getItems().size() - 1) {
            if (userCount == 1) {
                sentImages.put("timestamps", imageTimestamps);
                getImages(chatUser.getUserName(), currentUsername);
            } else {
                receivedImages.put("timestamps", imageTimestamps);
                TimestampGetter timestampGetter = new TimestampGetter(results, currentIndex, imageTimestamps, this, messageQueue);
                threadpool.execute(timestampGetter);
            }
        } else {
            TimestampGetter timestampGetter = new TimestampGetter(results, currentIndex, imageTimestamps, this);
            threadpool.execute(timestampGetter);
        }
    }

    public interface IFromChatFragment {
        void backToChatList(String currentUsername);
        void getImageFromAlbum();
        void getImageFromCamera();
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
        Message successMessage = new Message();
        successMessage.what = STATUS_SUCCESS;
        messageQueue.sendMessage(successMessage);
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

class BitmapGetter implements Runnable {
    private ListResult results;
    private int currentIndex;
    private ArrayList<Bitmap> imageBitmaps;
    private final int ONE_MEGABYTE = 1024 * 1024;
    private IFromBitmapGetter sendData;

    public BitmapGetter(ListResult results, int currentIndex, ArrayList<Bitmap> imageBitmaps, Fragment parent) {
        this.results = results;
        this.currentIndex = currentIndex;
        this.imageBitmaps = imageBitmaps;
        if (parent instanceof IFromBitmapGetter) {
            sendData = (IFromBitmapGetter) parent;
        } else {
            throw new RuntimeException(parent + " must implement IFromBitmapGetter");
        }
    }

    @Override
    public void run() {
        if (results.getItems().isEmpty()) {
            sendData.gotBitmap(results, currentIndex + 1, imageBitmaps);
        } else {
            StorageReference currentImage = results.getItems().get(currentIndex);
            currentImage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Log.d("turtle", "onSuccess: adding an image: ");
                    imageBitmaps.add(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    sendData.gotBitmap(results, currentIndex + 1, imageBitmaps);
                }
            });
        }
    }

    public interface IFromBitmapGetter {
        void gotBitmap(ListResult results, int currentIndex, ArrayList<Bitmap> imageBitmaps);
    }
}

class TimestampGetter implements Runnable {
    private ListResult results;
    private int currentIndex;
    private ArrayList<Timestamp> imageTimestamps;
    private IFromTimestampGetter sendData;
    private Handler messageQueue;
    public static final int STATUS_END = 0x012;

    public TimestampGetter(ListResult results, int currentIndex, ArrayList<Timestamp> imageTimestamps, Fragment parent) {
        this.results = results;
        this.currentIndex = currentIndex;
        this.imageTimestamps = imageTimestamps;
        if (parent instanceof IFromTimestampGetter) {
            sendData = (IFromTimestampGetter) parent;
        } else {
            throw new RuntimeException(parent + " must implement IFromBitmapGetter");
        }
    }

    public TimestampGetter(ListResult results, int currentIndex, ArrayList<Timestamp> imageTimestamps, Fragment parent, Handler messageQueue) {
        this.results = results;
        this.currentIndex = currentIndex;
        this.imageTimestamps = imageTimestamps;
        if (parent instanceof IFromTimestampGetter) {
            sendData = (IFromTimestampGetter) parent;
        } else {
            throw new RuntimeException(parent + " must implement IFromBitmapGetter");
        }
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        if (messageQueue != null) {
            Message message = new Message();
            message.what = STATUS_END;
            messageQueue.sendMessage(message);
        } else {
            if (results.getItems().isEmpty()) {
                sendData.gotTimestamp(results, currentIndex + 1, imageTimestamps);
            } else {
                StorageReference currentImage = results.getItems().get(currentIndex);
                currentImage.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        Log.d("turtle", "onSuccess: adding a timestamp: ");
                        Timestamp timestamp = new Timestamp(new Date(storageMetadata.getCreationTimeMillis()));
                        imageTimestamps.add(timestamp);
                        sendData.gotTimestamp(results, currentIndex + 1, imageTimestamps);
                    }
                });
            }
        }
    }

    public interface IFromTimestampGetter {
        void gotTimestamp(ListResult results, int currentIndex, ArrayList<Timestamp> imageTimestamps);
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