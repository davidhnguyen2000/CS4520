/*
David Nguyen
In Class 05
 */

package com.example.inclassassignments.InClass05;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inclassassignments.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InClass05 extends AppCompatActivity {

    private EditText editTextSearch;
    private Button buttonSearch;
    private ImageView imageDisplay;
    private ProgressBar loading;
    private TextView loadingText;
    private ImageView backButton;
    private ImageView nextButton;
    private Handler messageQueue;
    private ArrayList<String> keywords;
    private ArrayList<String> imageURLs;
    private ImageDownloader imageDownloader;
    private ExecutorService threadPool;
    int currentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_class05);
        setTitle("Image Search");
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        imageDisplay = findViewById(R.id.imageViewDisplay);
        loading = findViewById(R.id.progressBarLoading);
        loadingText = findViewById(R.id.textViewLoading);
        backButton = findViewById(R.id.imageViewBackButton);
        nextButton = findViewById(R.id.imageViewNext);
        imageDisplay.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        threadPool = Executors.newFixedThreadPool(4);
        if (!isNetworkAvailable(InClass05.this)) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        } else {
            startMessageHandler();
            updateKeywords();
            startGoButtonListener();
            startNavButtonListeners();
        }
    }

    private void startGoButtonListener() {
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchEntree = editTextSearch.getText().toString();
                if (searchEntree.isEmpty()) {
                    Toast.makeText(InClass05.this, "Must input keyword to search", Toast.LENGTH_SHORT).show();
                } else if (!keywords.contains(searchEntree)) {
                    Toast.makeText(InClass05.this, "Invalid keyword search", Toast.LENGTH_SHORT).show();
                } else {
                    getImages(searchEntree);
                    imageDisplay.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    loadingText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void startNavButtonListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentImage > 0)
                    currentImage --;
                else
                    currentImage = imageURLs.size() - 1;
                imageDownloader.updateURL(imageURLs.get(currentImage));
                threadPool.execute(imageDownloader);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentImage < imageURLs.size() - 1)
                    currentImage ++;
                else
                    currentImage = 0;
                imageDownloader.updateURL(imageURLs.get(currentImage));
                threadPool.execute(imageDownloader);
            }
        });
    }

    private void startMessageHandler() {
        messageQueue = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case KeywordGetter.ON_FAILURE:
                        Toast.makeText(InClass05.this, "Unable to retrieve keywords", Toast.LENGTH_LONG).show();
                        break;
                    case KeywordGetter.ON_RESPONSE:
                        keywords = (ArrayList<String>) msg.getData().getSerializable(KeywordGetter.KEY_KEYWORDS);
                        break;
                    case ImageGetter.ON_FAILURE:
                        loading.setVisibility(View.GONE);
                        loadingText.setVisibility(View.GONE);
                        Toast.makeText(InClass05.this, "Unable to retrieve image data", Toast.LENGTH_LONG).show();
                        break;
                    case ImageGetter.ON_RESPONSE:
                        imageURLs = (ArrayList<String>) msg.getData().getSerializable(ImageGetter.KEY_IMAGE_URLS);
                        if (imageURLs.size() != 0) {
                            currentImage = 0;
                            imageDownloader = new ImageDownloader(messageQueue, imageURLs.get(currentImage));
                            threadPool.execute(imageDownloader);
                        } else {
                            loading.setVisibility(View.GONE);
                            loadingText.setVisibility(View.GONE);
                            Toast.makeText(InClass05.this, "No URLs loaded", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ImageDownloader.STARTED_LOADING:
                        loading.setVisibility(View.VISIBLE);
                        loadingText.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.GONE);
                        backButton.setVisibility(View.GONE);
                        imageDisplay.setVisibility(View.GONE);
                        break;
                    case ImageDownloader.LOADED_IMAGE:
                        loading.setVisibility(View.GONE);
                        loadingText.setVisibility(View.GONE);
                        Bitmap bitmap = msg.getData().getParcelable(ImageDownloader.IMAGE_KEY);
                        imageDisplay.setImageBitmap(bitmap);
                        imageDisplay.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                        backButton.setVisibility(View.VISIBLE);
                        break;
                    case ImageDownloader.FAILED:
                        Toast.makeText(InClass05.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }

    private void updateKeywords() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://dev.sakibnm.space/apis/images/keywords")
                .build();
        client.newCall(request).enqueue(new KeywordGetter(messageQueue));
    }

    private void getImages(String keyword) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://dev.sakibnm.space/apis/images/retrieve?keyword=" + keyword)
                .build();
        client.newCall(request).enqueue(new ImageGetter(messageQueue));
    }

    // code reference: https://stackoverflow.com/questions/40389163/check-internet-connection-okhttp
    private boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return (networkInfo != null && networkInfo.isConnected());
    }
}

class ImageGetter implements Callback {
    Handler messageQueue;
    public final static int ON_FAILURE = 0x001;
    public final static int ON_RESPONSE = 0x002;
    public final static String KEY_IMAGE_URLS = "imageURLs";

    public ImageGetter(Handler messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        Message progressMessage = new Message();
        progressMessage.what = ON_FAILURE;
        messageQueue.sendMessage(progressMessage);
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);
        String responseBody = Objects.requireNonNull(response.body()).string();
        ArrayList<String> imageURLs = new ArrayList<>();
        String currentString = responseBody;
        while (currentString.contains("\n")) {
            int splitIndex = currentString.indexOf('\n');
            imageURLs.add(currentString.substring(0, splitIndex));
            currentString = currentString.substring(splitIndex + 1);
        }
        imageURLs.add(currentString);
        Message progressMessage = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_IMAGE_URLS, imageURLs);
        progressMessage.what = ON_RESPONSE;
        progressMessage.setData(bundle);
        messageQueue.sendMessage(progressMessage);
    }
}

class KeywordGetter implements Callback {
    Handler messageQueue;
    public final static int ON_FAILURE = 0x003;
    public final static int ON_RESPONSE = 0x004;
    public final static String KEY_KEYWORDS = "keywords";

    public KeywordGetter(Handler messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        Message progressMessage = new Message();
        progressMessage.what = ON_FAILURE;
        messageQueue.sendMessage(progressMessage);
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);
        ArrayList<String> keywords = new ArrayList<>();
        String currentString = Objects.requireNonNull(response.body()).string();
        // add all the keywords separated by commas
        while (currentString.contains(",")) {
            int splitIndex = currentString.indexOf(',');
            keywords.add(currentString.substring(0, splitIndex));
            currentString = currentString.substring(splitIndex + 1);
        }
        keywords.add(currentString); // add the last keyword
        Message progressMessage = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_KEYWORDS, keywords);
        progressMessage.what = ON_RESPONSE;
        progressMessage.setData(bundle);
        messageQueue.sendMessage(progressMessage);
    }
}

class ImageDownloader implements Runnable {
    private String url;
    private final Handler messageQueue;
    public static final int LOADED_IMAGE = 0x006;
    public static final int STARTED_LOADING = 0x008;
    public static final int FAILED = 0x007;
    public static final String IMAGE_KEY = "image";

    public ImageDownloader(Handler messageQueue, String url) {
        this.messageQueue = messageQueue;
        this.url = url;
    }

    public void updateURL(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        Message startMessage = new Message();
        startMessage.what = STARTED_LOADING;
        messageQueue.sendMessage(startMessage);
        Message progressMessage = new Message();
        try {
            Bitmap bitmap = Picasso.get().load(url).get();
            progressMessage.what = LOADED_IMAGE;
            Bundle bundle = new Bundle();
            bundle.putParcelable(IMAGE_KEY, bitmap);
            progressMessage.setData(bundle);
            messageQueue.sendMessage(progressMessage);
        } catch (IOException e) {
            e.printStackTrace();
            progressMessage.what = FAILED;
            messageQueue.sendMessage(progressMessage);
        }
    }
}