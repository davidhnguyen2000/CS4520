/*
By David Nguyen and Andrew Chin
In Class Assignment 06
 */

package com.example.inclassassignments.InClass06;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inclassassignments.R;
import com.squareup.picasso.Picasso;

public class InClass06ViewArticle extends Fragment {

    private TextView title;
    private TextView author;
    private TextView publishedAt;
    private TextView description;
    private ImageView image;
    private ImageView back;
    private ImageView next;
    private Button returnToSearch;

    private TopHeadLines headlines;
    private int articleIndex;

    IFromViewArticleToMain sendData;

    public InClass06ViewArticle() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof InClass06ViewArticle.IFromViewArticleToMain) {
            sendData = (InClass06ViewArticle.IFromViewArticleToMain) context;
        } else {
            throw new RuntimeException(context + " must implement IFromViewArticleToMain");
        }
    }

    public InClass06ViewArticle(TopHeadLines headlines) {
        this.headlines = headlines;
    }

    public static InClass06ViewArticle newInstance(String param1, String param2) {
        return new InClass06ViewArticle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_in_class06_view_article, container, false);
        title = rootView.findViewById(R.id.textViewTitle);
        author = rootView.findViewById(R.id.textViewAuthor);
        publishedAt = rootView.findViewById(R.id.textViewPublishedAt);
        description = rootView.findViewById(R.id.textViewDescription);
        image = rootView.findViewById(R.id.imageViewArticleImage);
        back = rootView.findViewById(R.id.imageViewBackButton);
        next = rootView.findViewById(R.id.imageViewNextButton);
        returnToSearch = rootView.findViewById(R.id.buttonReturnToSearch);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        articleIndex = 0;
        updateArticle(headlines.getArticles().get(articleIndex));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (articleIndex == 0) {
                    articleIndex = headlines.getArticles().size() - 1;
                } else {
                    articleIndex --;
                }
                updateArticle(headlines.getArticles().get(articleIndex));
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (articleIndex == headlines.getArticles().size() - 1) {
                    articleIndex = 0;
                } else {
                    articleIndex ++;
                }
                updateArticle(headlines.getArticles().get(articleIndex));
            }
        });

        returnToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData.fromViewArticle();
            }
        });
    }

    public void updateHeadlines(TopHeadLines headlines) {
        this.headlines = headlines;
    }

    private void updateArticle(Article currentArticle) {
        title.setText(currentArticle.getTitle());
        author.setText(currentArticle.getAuthor());
        publishedAt.setText(currentArticle.getPublishedAt());
        description.setText(currentArticle.getDescription());
        Picasso.get().load(currentArticle.getUrlToImage()).into(image);
    }

    public interface IFromViewArticleToMain {
        void fromViewArticle();
    }
}