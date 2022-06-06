package com.example.inclassassignments.InClass07;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.inclassassignments.R;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InClass07ViewNotes extends Fragment implements NoteAdapter.IDeleteNote {

    private Handler messageQueue;
    private RecyclerView recyclerViewNotes;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private EditText editTextNote;
    private Button buttonAddNote;
    private Button buttonBack;
    private TextView textViewNotes;
    private ProgressBar progressBarLoadingNotes;
    private NoteAdapter noteAdapter;
    private UserNotes notes;
    private Token token;
    private ExecutorService threadPool;
    private IFromViewNotes sendData;

    public InClass07ViewNotes() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFromViewNotes) {
            sendData = (IFromViewNotes) context;
        } else {
            throw new RuntimeException(context + " must implement IFromViewNotes");
        }
    }

    public static InClass07ViewNotes newInstance(String param1, String param2) {
        return new InClass07ViewNotes();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_in_class07_view_notes, container, false);
        editTextNote = rootView.findViewById(R.id.editTextNote);
        buttonAddNote = rootView.findViewById(R.id.buttonAddNote);
        buttonBack = rootView.findViewById(R.id.buttonBackToViewProfile);
        progressBarLoadingNotes = rootView.findViewById(R.id.progressBarLoadingNotes);
        textViewNotes = rootView.findViewById(R.id.textViewNotes);
        recyclerViewNotes = rootView.findViewById(R.id.recyclerViewNotes);
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewNotes.setLayoutManager(recyclerViewLayoutManager);
        noteAdapter = new NoteAdapter(this);
        recyclerViewNotes.setAdapter(noteAdapter);
        // set visibility
        progressBarLoadingNotes.setVisibility(View.GONE);
        textViewNotes.setVisibility(View.VISIBLE);
        recyclerViewNotes.setVisibility(View.GONE);
        editTextNote.setVisibility(View.GONE);
        buttonAddNote.setVisibility(View.GONE);

        threadPool = Executors.newFixedThreadPool(2);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        messageQueue = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case NotesGetter.STATUS_START:
                    case PostNewNote.STATUS_START:
                    case PostDeleteNote.STATUS_START:
                        progressBarLoadingNotes.setVisibility(View.VISIBLE);
                        break;
                    case NotesGetter.STATUS_FAIL:
                        progressBarLoadingNotes.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Unable to load notes", Toast.LENGTH_SHORT).show();
                        break;
                    case NotesGetter.STATUS_SUCCESS:
                        notes = (UserNotes) msg.getData().getSerializable(NotesGetter.KEY_NOTES);
                        noteAdapter.updateNotes(notes.getNotes());
                        progressBarLoadingNotes.setVisibility(View.GONE);
                        recyclerViewNotes.setVisibility(View.VISIBLE);
                        editTextNote.setVisibility(View.VISIBLE);
                        buttonAddNote.setVisibility(View.VISIBLE);
                        break;
                    case PostNewNote.STATUS_FAIL:
                        progressBarLoadingNotes.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Unable to add note", Toast.LENGTH_SHORT).show();
                        break;
                    case PostNewNote.STATUS_SUCCESS:
                        progressBarLoadingNotes.setVisibility(View.GONE);
                        editTextNote.setText("");
                        Note newNote = (Note) msg.getData().getSerializable(PostNewNote.KEY_NOTE);
                        noteAdapter.addNote(newNote);
                        break;
                    case PostDeleteNote.STATUS_FAIL:
                        progressBarLoadingNotes.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Unable to delete note", Toast.LENGTH_SHORT).show();
                        break;
                    case PostDeleteNote.STATUS_SUCCESS:
                        String id = msg.getData().getString(PostDeleteNote.KEY_ID);
                        progressBarLoadingNotes.setVisibility(View.GONE);
                        noteAdapter.removeNote(id);
                        break;
                }
                return false;
            }
        });

        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextNote.getText().toString().isEmpty()) {
                    PostNewNote postNewNote = new PostNewNote(messageQueue, editTextNote.getText().toString(), token);
                    threadPool.execute(postNewNote);
                } else {
                    Toast.makeText(getContext(), "Must input text for note", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.fromViewNotes();
            }
        });

        NotesGetter notesGetter = new NotesGetter(messageQueue, token);
        threadPool.execute(notesGetter);
    }

    public void updateToken(Token token) {
        this.token = token;
    }

    @Override
    public void deleteNote(String _id) {
        PostDeleteNote deleteNote = new PostDeleteNote(messageQueue, _id, token);
        threadPool.execute(deleteNote);
    }

    public interface IFromViewNotes {
        void fromViewNotes();
    }
}

class NotesGetter implements Runnable {
    private Handler messageQueue;
    private Token token;
    public static final int STATUS_START = 0x001;
    public static final int STATUS_FAIL = 0x002;
    public static final int STATUS_SUCCESS = 0x003;
    public static final String KEY_NOTES = "notes";

    public NotesGetter(Handler messageQueue, Token token) {
        this.messageQueue = messageQueue;
        this.token = token;
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
                .url("http://dev.sakibnm.space:3000/api/note/getall")
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
                    UserNotes notes = gsonData.fromJson(response.body().charStream(), UserNotes.class);
                    Message successMessage = new Message();
                    successMessage.what = STATUS_SUCCESS;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KEY_NOTES, notes);
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

class PostNewNote implements Runnable {
    private Handler messageQueue;
    private String text;
    private Token token;
    public static final int STATUS_START = 0x004;
    public static final int STATUS_FAIL = 0x005;
    public static final int STATUS_SUCCESS = 0x006;
    public static final String KEY_NOTE = "note";

    public PostNewNote(Handler messageQueue, String text, Token token) {
        this.messageQueue = messageQueue;
        this.text = text;
        this.token = token;
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
                .add("text", text)
                .build();
        Request request = new Request.Builder()
                .url("http://dev.sakibnm.space:3000/api/note/post")
                .header("x-access-token", token.getToken())
                .post(formBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                Gson gsonData = new Gson();
                PostNote postNote = gsonData.fromJson(response.body().charStream(), PostNote.class);
                Note note = postNote.getNote();
                // send token back to main thread
                Message successMessage = new Message();
                successMessage.what = STATUS_SUCCESS;
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_NOTE, note);
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

class PostDeleteNote implements Runnable {
    private Handler messageQueue;
    private String _id;
    private Token token;
    public static final int STATUS_START = 0x007;
    public static final int STATUS_FAIL = 0x008;
    public static final int STATUS_SUCCESS = 0x009;
    public static final String KEY_ID = "id";

    public PostDeleteNote(Handler messageQueue, String _id, Token token) {
        this.messageQueue = messageQueue;
        this._id = _id;
        this.token = token;
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
                .add("id", _id)
                .build();
        Request request = new Request.Builder()
                .url("http://dev.sakibnm.space:3000/api/note/delete")
                .header("x-access-token", token.getToken())
                .post(formBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Message successMessage = new Message();
            successMessage.what = STATUS_SUCCESS;
            Bundle bundle = new Bundle();
            bundle.putString(KEY_ID, _id);
            successMessage.setData(bundle);
            messageQueue.sendMessage(successMessage);
        } catch (IOException e) {
            Message failMessage = new Message();
            failMessage.what = STATUS_FAIL;
            messageQueue.sendMessage(failMessage);
        }
    }
    }