package com.example.lab14_java;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private TextView textView2;
    private int counter = 0;
    private int counter2 = 0;
    private static final String TAG = "LAB14_THREAD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button button1 = findViewById(R.id.button);
        button1.setOnClickListener(v -> {
            Log.v(TAG, "start timer");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Log.v(TAG, "timer finish");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setContentView(R.layout.page2);
                        }
                    });
                }
            }).start();
        });
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(v -> new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            runOnUiThread(() -> setContentView(R.layout.page2));
        }).start());
        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(v -> new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            handler.sendEmptyMessage(MAIN_THREAD_TO_PAGE2);
        }).start());
        textView = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView);
        runCounter();
        runCounter2();
    }

    private void runCounter2() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                handler.sendEmptyMessage(MAIN_THREAD_INCREATE_COUNTER2);
            }
        }).start();
    }

    private static final int MAIN_THREAD_TO_PAGE2 = 1234;
    private static final int MAIN_THREAD_INCREATE_COUNTER2 = 5678;

    private void runCounter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(String.format("counter=%d", counter++));
                        }
                    });
                    //counter += 1;
                }
            }
        }).start();
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MAIN_THREAD_TO_PAGE2:
                    setContentView(R.layout.page2);
                    break;
                case MAIN_THREAD_INCREATE_COUNTER2:
                    textView2.setText(String.format("counter2=%d", counter2++));
                    break;

            }
        }
    };
}