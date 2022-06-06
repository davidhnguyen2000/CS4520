/*
By David Nguyen and Andrew Chin
In Class Assignment 06
 */
package com.example.inclassassignments.InClass06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class InClass06 extends AppCompatActivity implements InClass06ViewArticle.IFromViewArticleToMain {

    private String[] countries = {"ae", "ar", "at", "au", "be", "bg", "br", "ca", "ch", "cn", "co",
            "cu", "cz", "de", "eg", "fr", "gb", "gr", "hk", "hu", "id", "ie", "il", "in", "it",
            "jp", "kr", "lt", "lv", "ma", "mx", "my", "ng", "nl", "no", "nz", "ph", "pl", "pt",
            "ro", "rs", "ru", "sa", "se", "sg", "si", "sk", "th", "tr", "tw", "ua", "us", "ve"};
    private ArrayAdapter<String> adapter;
    private TopHeadLines headlines;
    private String country;
    private String category;

    private ListView listView;
    private RadioGroup radioGroupCategory;
    private Button submit;
    private FragmentContainerView viewArticle;
    private ExecutorService threadPool;
    private Handler messageQueue;
    private TextView viewCountry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_class06);
        // declaring layout objects
        listView = findViewById(R.id.listViewCountry);
        submit = findViewById(R.id.buttonSubmitSearch);
        radioGroupCategory = findViewById(R.id.radioGroupCategory);
        viewArticle = findViewById(R.id.fragmentContainerViewArtilce);
        viewCountry = findViewById(R.id.textViewCountry);
        threadPool = Executors.newFixedThreadPool(2);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, countries);
        listView.setAdapter(adapter);
        // set default country value
        country = "us";

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                country = countries[position];
                viewCountry.setText("Selected Country: " + country);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check radio group
                switch(radioGroupCategory.getCheckedRadioButtonId()) {
                    case R.id.radioButtonBusiness:
                        category = "business";
                        break;
                    case R.id.radioButtonEntertainment:
                        category = "entertainment";
                        break;
                    case R.id.radioButtonGeneral:
                        category = "general";
                        break;
                    case R.id.radioButtonHealth:
                        category = "health";
                        break;
                    case R.id.radioButtonScience:
                        category = "science";
                        break;
                    case R.id.radioButtonSports:
                        category = "sports";
                        break;
                    case R.id.radioButtonTehcnology:
                        category = "technology";
                        break;
                    default:
                        category = "";
                }
                ArticleGetter articleGetter = new ArticleGetter(country, category, messageQueue);
                threadPool.execute(articleGetter);
            }
        });

        messageQueue = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case ArticleGetter.STATUS_ERROR:
                        Toast.makeText(InClass06.this, "Unable to contact server.", Toast.LENGTH_SHORT).show();
                        break;
                    case ArticleGetter.STATUS_END:
                        headlines = (TopHeadLines) msg.getData().getSerializable(ArticleGetter.KEY_HEADLINES);
                        InClass06ViewArticle viewArticles = new InClass06ViewArticle(headlines);
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.fragmentContainerViewArtilce, viewArticles)
                                .addToBackStack("viewArticle")
                                .commit();
                        submit.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void fromViewArticle() {
        getSupportFragmentManager().popBackStackImmediate();
        submit.setVisibility(View.VISIBLE);
    }
}

class ArticleGetter implements Runnable {
    public final static int STATUS_START = 0x001;
    public final static int STATUS_END = 0x002;
    public final static int STATUS_ERROR = 0x003;
    public final static String KEY_HEADLINES = "headlines";
    private TopHeadLines headlines;
    private String category;
    private String country;
    private final Handler messageQueue;

    public ArticleGetter(String country, String category, Handler messageQueue) {
        this.category = category;
        this.country = country;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        Message startMessage = new Message();
        startMessage.what = STATUS_START;
        messageQueue.sendMessage(startMessage);
        getTopHeadlines();
    }

    private void getTopHeadlines() {
        OkHttpClient client = new OkHttpClient();
        String url;
        if (!country.isEmpty() && category.isEmpty()) {
            url = "https://newsapi.org/v2/top-headlines?country="
                    + country
                    + "&apiKey=7a389c10538d49b68c1a76ad08322e19";
        } else if (country.isEmpty() && !category.isEmpty()) {
            url = "https://newsapi.org/v2/top-headlines?category="
                    + category
                    + "&apiKey=7a389c10538d49b68c1a76ad08322e19";
        } else if (!country.isEmpty()) {
            url = "https://newsapi.org/v2/top-headlines?country="
                    + country
                    + "&category="
                    + category
                    + "&apiKey=7a389c10538d49b68c1a76ad08322e19";
        } else {
            url = "https://newsapi.org/v2/top-headlines?apiKey=7a389c10538d49b68c1a76ad08322e19";
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message errorMessage = new Message();
                errorMessage.what = STATUS_ERROR;
                messageQueue.sendMessage(errorMessage);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Gson gsonData = new Gson();
                    headlines = gsonData.fromJson(response.body().charStream(), TopHeadLines.class);
                    Message endMessage = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KEY_HEADLINES, headlines);
                    endMessage.setData(bundle);
                    endMessage.what = STATUS_END;
                    messageQueue.sendMessage(endMessage);
                }
            }
        });
    }
}