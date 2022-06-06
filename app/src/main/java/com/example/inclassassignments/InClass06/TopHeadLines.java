package com.example.inclassassignments.InClass06;

import java.io.Serializable;
import java.util.ArrayList;

public class TopHeadLines implements Serializable {
    private String status;
    private String code;
    private String message;
    private int totalResults;
    private ArrayList<Article> articles;

    public TopHeadLines(String status, int totalResults, ArrayList<Article> articles) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = articles;
    }

    public TopHeadLines(String status, int totalResults, ArrayList<Article> articles, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.totalResults = totalResults;
        this.articles = articles;
    }

    public String getStatus() {
        return status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public ArrayList<Article> getArticles() {
        return articles;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "TopHeadLines{" +
                "status='" + status + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", totalResults=" + totalResults +
                ", articles=" + articles +
                '}';
    }
}
