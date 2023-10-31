package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CongratulationActivity extends AppCompatActivity {
    private ImageView btnBack;
    private TextView tvAnswerCorrect;
    private TextView tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulation);

        setWindowApp();
        defineVariable();
        setUpListener();
        getSetDataActivity();
    }

    private void getSetDataActivity() {
        Intent intent = getIntent();
        tvAnswerCorrect.setText(intent.getStringExtra("answerCorrect"));
        tvTime.setText(intent.getStringExtra("time"));
        if (intent.getStringExtra("result").toString().equals("allDone")) {
            Toast toast = Toast.makeText(CongratulationActivity.this, "Bạn đã hoàn thành tất cả các câu hỏi", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void setWindowApp() {
        Window w = getWindow();
        // Make status bar transparent
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        // Hide navigation bar
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void defineVariable() {
        btnBack = findViewById(R.id.btnBack);
        tvAnswerCorrect = findViewById(R.id.tvAnswerCorrect);
        tvTime = findViewById(R.id.tvTime);
    }

    private void setUpListener() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}