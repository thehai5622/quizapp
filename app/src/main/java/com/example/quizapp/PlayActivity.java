package com.example.quizapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.quizapp.database_handler.DbHelper;
import com.example.quizapp.model.Answer;
import com.example.quizapp.model.Question;
import com.example.quizapp.service.OnClickEffectManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PlayActivity extends AppCompatActivity {
    private ImageView btnBack;
    private TextView tvTitle;
    private LinearLayout btnAnswerA;
    private TextView contentA;
    private LinearLayout btnAnswerB;
    private TextView contentB;
    private LinearLayout btnAnswerC;
    private TextView contentC;
    private LinearLayout btnAnswerD;
    private TextView contentD;
    private TextView btnSubmit;
    private TextView tvNumAnswerCorrect;
    private TextView tvTime;
    private TextView tvQuestion;

    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;
    DbHelper dbHelper;
    List<Question> questionList;
    List<Answer> answerList;
    Question currentQuestion;
    int answerSelected;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        setWindowApp();
        defineVariable();
        setUpListener();
        getSetDataActivity();
        initTurnPlay();
        randomQuestion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerTask.cancel();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void getSetDataActivity() {
        Intent intent = getIntent();
        tvTitle.setText(intent.getStringExtra("title"));
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
        tvTitle = findViewById(R.id.tvTitle);
        btnAnswerA = findViewById(R.id.btnAnswerA);
        contentA = findViewById(R.id.contentA);
        btnAnswerB = findViewById(R.id.btnAnswerB);
        contentB = findViewById(R.id.contentB);
        btnAnswerC = findViewById(R.id.btnAnswerC);
        contentC = findViewById(R.id.contentC);
        btnAnswerD = findViewById(R.id.btnAnswerD);
        contentD = findViewById(R.id.contentD);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvNumAnswerCorrect = findViewById(R.id.tvNumAnswerCorrect);
        tvTime = findViewById(R.id.tvTime);
        tvQuestion = findViewById(R.id.tvQuestion);

        dbHelper = new DbHelper(PlayActivity.this);
        questionList = dbHelper.getAllQuestions();
        answerList = new ArrayList<>();
        answerSelected = 0;
        timer = new Timer();
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
    }

    private void initTurnPlay() {
        tvNumAnswerCorrect.setText("0");
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    time++;
                    tvTime.setText(getTimerText());
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private void randomQuestion() {
        if (questionList.size() != 0) {
            Random random = new Random();
            answerList.clear();
            int index = random.nextInt(questionList.size());
            currentQuestion = questionList.remove(index);
            List<Answer> answerListTemp = dbHelper.getAnswersForQuestion(currentQuestion.getId());

            // Reset answer selected
            answerSelected = 0;
            btnSubmit.setBackground(this.getDrawable(R.drawable.submit_bg_not_ready));
            contentA.setTextColor(this.getColor(R.color.content_answer));
            contentB.setTextColor(this.getColor(R.color.content_answer));
            contentC.setTextColor(this.getColor(R.color.content_answer));
            contentD.setTextColor(this.getColor(R.color.content_answer));
            btnAnswerA.setBackground(this.getDrawable(R.drawable.answer_bg));
            btnAnswerB.setBackground(this.getDrawable(R.drawable.answer_bg));
            btnAnswerC.setBackground(this.getDrawable(R.drawable.answer_bg));
            btnAnswerD.setBackground(this.getDrawable(R.drawable.answer_bg));

            // Random answer
            while(answerListTemp.size() != 0) {
                index = random.nextInt(answerListTemp.size());
                answerList.add(answerListTemp.remove(index));
            }

            // Get set content
            tvQuestion.setText(currentQuestion.getContent());
            contentA.setText(answerList.get(0).getContent());
            contentB.setText(answerList.get(1).getContent());
            contentC.setText(answerList.get(2).getContent());
            contentD.setText(answerList.get(3).getContent());
        } else {
            endTurn();
        }
    }

    private String getTimerText() {
        int rounded = (int) Math.round(time);
        int second = ((rounded % 86400) % 3600) % 60;
        int minute = ((rounded % 86400) % 3600) / 60;

        return String.format("%02d", minute) + ":" + String.format("%02d", second);
    }

    private void endTurn() {
        timerTask.cancel();
        finish();

        String result = questionList.size() == 0 ? "allDone" : "not";

        // Add record
        dbHelper.insertTurn(Integer.parseInt(tvNumAnswerCorrect.getText().toString()), tvTime.getText().toString());

        // Move to result activity
        Intent intent = new Intent(PlayActivity.this, CongratulationActivity.class);
        intent.putExtra("answerCorrect", tvNumAnswerCorrect.getText().toString());
        intent.putExtra("time", tvTime.getText().toString());
        intent.putExtra("result", result);
        startActivity(intent);
    }

    private void back() {
        if (Integer.parseInt(tvNumAnswerCorrect.getText().toString()) == 0) {
            timerTask.cancel();
            finish();
            return;
        }
        final Dialog dialog = new Dialog(PlayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        // Set blur background dialog
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Set dialog gravity Center screen
        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowAttribute);

        // Close Dialog when click over dialog layout
        dialog.setCancelable(true);

        // Define variable Dialog
        ConstraintLayout btnCancel = dialog.findViewById(R.id.btnCancel);
        ConstraintLayout btnConfirm = dialog.findViewById(R.id.btnConfirm);
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);

        // Set text Dialog
        tvTitle.setText("Kết thúc lượt chơi");
        tvMessage.setText("Bạn có muốn kết thúc lượt chơi không ?");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                setWindowApp();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean("bgmEnabled", true)) {
                    OnClickEffectManager.playClickSound(PlayActivity.this);
                }
                endTurn();
            }
        });

        dialog.show();
    }

    private void setUpListener() {
        // Set onClick btn answer A
        btnAnswerA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchBtnAnswer(1);
            }
        });

        // Set onClick btn answer B
        btnAnswerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchBtnAnswer(2);
            }
        });

        // Set onClick btn answer C
        btnAnswerC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchBtnAnswer(3);
            }
        });

        // Set onClick btn answer D
        btnAnswerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchBtnAnswer(4);
            }
        });

        // Set onClick btn Submit
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerSelected != 0) {
                    if (sharedPreferences.getBoolean("bgmEnabled", true)) {
                        OnClickEffectManager.playClickSound(PlayActivity.this);
                    }
                    if(answerList.get(answerSelected-1).getId() == currentQuestion.getAnswer_id()) {
                        int num = Integer.parseInt(tvNumAnswerCorrect.getText().toString());
                        tvNumAnswerCorrect.setText(String.valueOf(num + 1));
                        randomQuestion();
                    }
                    else {
                        endTurn();
                    }
                }
            }
        });

        // Set onClick back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
    }

    private void switchBtnAnswer(int index) {
        btnSubmit.setBackground(this.getDrawable(R.drawable.submit_bg));
        answerSelected = index;
        switch (index) {
            case 1:
                contentA.setTextColor(this.getColor(R.color.white));
                contentB.setTextColor(this.getColor(R.color.content_answer));
                contentC.setTextColor(this.getColor(R.color.content_answer));
                contentD.setTextColor(this.getColor(R.color.content_answer));
                btnAnswerA.setBackground(this.getDrawable(R.drawable.answer_bg_selected));
                btnAnswerB.setBackground(this.getDrawable(R.drawable.answer_bg));
                btnAnswerC.setBackground(this.getDrawable(R.drawable.answer_bg));
                btnAnswerD.setBackground(this.getDrawable(R.drawable.answer_bg));
                return;
            case 2:
                contentA.setTextColor(this.getColor(R.color.content_answer));
                contentB.setTextColor(this.getColor(R.color.white));
                contentC.setTextColor(this.getColor(R.color.content_answer));
                contentD.setTextColor(this.getColor(R.color.content_answer));
                btnAnswerA.setBackground(this.getDrawable(R.drawable.answer_bg));
                btnAnswerB.setBackground(this.getDrawable(R.drawable.answer_bg_selected));
                btnAnswerC.setBackground(this.getDrawable(R.drawable.answer_bg));
                btnAnswerD.setBackground(this.getDrawable(R.drawable.answer_bg));
                return;
            case 3:
                contentA.setTextColor(this.getColor(R.color.content_answer));
                contentB.setTextColor(this.getColor(R.color.content_answer));
                contentC.setTextColor(this.getColor(R.color.white));
                contentD.setTextColor(this.getColor(R.color.content_answer));
                btnAnswerA.setBackground(this.getDrawable(R.drawable.answer_bg));
                btnAnswerB.setBackground(this.getDrawable(R.drawable.answer_bg));
                btnAnswerC.setBackground(this.getDrawable(R.drawable.answer_bg_selected));
                btnAnswerD.setBackground(this.getDrawable(R.drawable.answer_bg));
                return;
            case 4:
                contentA.setTextColor(this.getColor(R.color.content_answer));
                contentB.setTextColor(this.getColor(R.color.content_answer));
                contentC.setTextColor(this.getColor(R.color.content_answer));
                contentD.setTextColor(this.getColor(R.color.white));
                btnAnswerA.setBackground(this.getDrawable(R.drawable.answer_bg));
                btnAnswerB.setBackground(this.getDrawable(R.drawable.answer_bg));
                btnAnswerC.setBackground(this.getDrawable(R.drawable.answer_bg));
                btnAnswerD.setBackground(this.getDrawable(R.drawable.answer_bg_selected));
                return;
            default:
                throw new IllegalStateException("Unexpected value: " + index);
        }
    }
}
