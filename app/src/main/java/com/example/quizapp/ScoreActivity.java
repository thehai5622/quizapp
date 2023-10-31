package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quizapp.database_handler.DbHelper;
import com.example.quizapp.model.Turn;

import java.util.ArrayList;
import java.util.List;

public class ScoreActivity extends AppCompatActivity {
    private ImageView btnBack;
    private TextView tvCorrect1;
    private TextView tvTime1;
    private TextView tvCorrect2;
    private TextView tvTime2;
    private TextView tvCorrect3;
    private TextView tvTime3;
    private TextView tvCorrect4;
    private TextView tvTime4;
    private TextView tvCorrect5;
    private TextView tvTime5;

    DbHelper dbHelper;
    List<Turn> turnList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        setWindowApp();
        defineVariable();
        setUpListener();
        getSetDataTurn();
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
        tvCorrect1 =findViewById(R.id.tvCorrect1);
        tvTime1 =findViewById(R.id.tvTime1);
        tvCorrect2 =findViewById(R.id.tvCorrect2);
        tvTime2 =findViewById(R.id.tvTime2);
        tvCorrect3 =findViewById(R.id.tvCorrect3);
        tvTime3 =findViewById(R.id.tvTime3);
        tvCorrect4 =findViewById(R.id.tvCorrect4);
        tvTime4 =findViewById(R.id.tvTime4);
        tvCorrect5 =findViewById(R.id.tvCorrect5);
        tvTime5 =findViewById(R.id.tvTime5);

        dbHelper = new DbHelper(ScoreActivity.this);
        turnList = new ArrayList<>();
    }

    private void getSetDataTurn() {
        turnList = dbHelper.getAllTurns();
        for(int i = 0; i < turnList.size(); i++) {
            System.out.println(turnList.get(i).getCorrect() + " - " + turnList.get(i).getTime());
            switchTvCorrect(i).setText(String.valueOf(turnList.get(i).getCorrect()));
            switchTvTime(i).setText(turnList.get(i).getTime());
        }
    }

    private TextView switchTvCorrect(int index) {
        switch (index) {
            case 0:
                return tvCorrect1;
            case 1:
                return tvCorrect2;
            case 2:
                return tvCorrect3;
            case 3:
                return tvCorrect4;
            case 4:
                return tvCorrect5;
        }
        return tvCorrect1;
    }

    private TextView switchTvTime(int index) {
        switch (index) {
            case 0:
                return tvTime1;
            case 1:
                return tvTime2;
            case 2:
                return tvTime3;
            case 3:
                return tvTime4;
            case 4:
                return tvTime5;
        }
        return tvTime1;
    }

    private void setUpListener() {
        // Set onClick button back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}