package com.example.inclassassignments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InClass04 extends AppCompatActivity {

    private TextView textViewComplexity;
    private SeekBar seekBarComplexity;
    private TextView min;
    private TextView max;
    private TextView average;
    private Button buttonGenerateNumber;
    private ProgressBar progressBarHeavyWork;
    private ExecutorService threadPool;
    private Handler messageQueue;
    private int complexity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_class04);
        setTitle("Number Generator");

        threadPool = Executors.newFixedThreadPool(2);

        textViewComplexity = findViewById(R.id.textViewComplexity);
        seekBarComplexity = findViewById(R.id.seekBarComplexity);
        min = findViewById(R.id.textViewMin);
        max = findViewById(R.id.textViewMax);
        average = findViewById(R.id.textViewAverage);
        buttonGenerateNumber = findViewById(R.id.buttonGenerateNumber);
        progressBarHeavyWork = findViewById(R.id.progressBarHeavyWork);
        progressBarHeavyWork.setVisibility(View.GONE);
        complexity = seekBarComplexity.getProgress();

        seekBarComplexity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                complexity = seekBarComplexity.getProgress();
                String complexityText = complexity + " times";
                textViewComplexity.setText(complexityText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        buttonGenerateNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (complexity < 1) {
                    Toast.makeText(InClass04.this, "Complexity must be greater than 0", Toast.LENGTH_SHORT).show();
                } else {
                    min.setText("");
                    max.setText("");
                    average.setText("");
                    progressBarHeavyWork.setProgress(0);
                    progressBarHeavyWork.setVisibility(View.VISIBLE);
                    HeavyWork heavyWorker = new HeavyWork(complexity, messageQueue);
                    threadPool.execute(heavyWorker);
                }
            }
        });

        messageQueue = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch(msg.what) {
                    case HeavyWork.STATUS_PROGRESS:
                        double progress = msg.getData().getDouble(HeavyWork.KEY_PROGRESS);
                        progressBarHeavyWork.setProgress((int) (progress * 100));
                        break;
                    case HeavyWork.STATUS_END:
                        progressBarHeavyWork.setVisibility(View.GONE);
                        ArrayList<Double> numbers = (ArrayList<Double>) msg.getData().getSerializable(HeavyWork.KEY_DATA);
                        double currentNum = numbers.get(0);
                        double maxNum = currentNum;
                        double minNum = currentNum;
                        double sum = 0;
                        for (int i = 0; i < numbers.size(); i ++) {
                            currentNum = numbers.get(i);
                            sum += currentNum;
                            if (currentNum > maxNum)
                                maxNum = currentNum;
                            if (currentNum < minNum)
                                minNum = currentNum;
                        }
                        double averageNum = sum / numbers.size();
                        averageNum = (double) Math.round(averageNum * 100) / 100;
                        minNum = (double) Math.round(minNum * 100) / 100;
                        maxNum = (double) Math.round(maxNum * 100) / 100;
                        String minText = "" + minNum;
                        String maxText = "" + maxNum;
                        String averageText = "" + averageNum;
                        min.setText(minText);
                        max.setText(maxText);
                        average.setText(averageText);
                        break;
                }
                return false;
            }
        });
    }
}