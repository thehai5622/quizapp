package com.example.quizapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.quizapp.database_handler.DbHelper;
import com.example.quizapp.service.BackgroundMusicService;
import com.example.quizapp.service.OnClickEffectManager;
import com.google.android.material.color.utilities.Score;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageView drawerImageView;
    private LinearLayout btnHome;
    private LinearLayout btnInsertQuestion;
    private LinearLayout btnHighCorrect;
    private LinearLayout btnFriend;
    private LinearLayout btnContact;
    private ConstraintLayout btnPlay;
    private ConstraintLayout btnOption;
    private ConstraintLayout btnExit;
    private TextView tvNumberQuestion;
    private TextView highCorrectQuestion;
    private ImageView avatar;

    DbHelper dbHelper;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setWindowApp();
        defineVariable();
        setUpListener();
        getSetDataActivity();
        playAudio();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);

        setWindowApp();
        defineVariable();
        setUpListener();
        getSetDataActivity();
    }

    void setWindowApp() {
        Window w = getWindow();
        // Make status bar transparent
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        // Hide navigation bar
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    void defineVariable() {
        drawerImageView = findViewById(R.id.drawer);
        drawerLayout = findViewById(R.id.drawerLayout);
        btnHome = findViewById(R.id.btnHome);
        btnInsertQuestion = findViewById(R.id.btnInsertQuestion);
        btnHighCorrect = findViewById(R.id.btnHighCorrect);
        btnFriend = findViewById(R.id.btnFriend);
        btnContact = findViewById(R.id.btnContact);
        btnPlay = findViewById(R.id.btnPlay);
        btnOption = findViewById(R.id.btnOption);
        btnExit = findViewById(R.id.btnExit);
        tvNumberQuestion = findViewById(R.id.tvNumberQuestion);
        highCorrectQuestion = findViewById(R.id.highCorrectQuestion);
        avatar = findViewById(R.id.avatar);

        dbHelper = new DbHelper(MainActivity.this);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
    }

    private void getSetDataActivity() {
        dbHelper.initQuestionAndAnswer();
        tvNumberQuestion.setText(String.valueOf(dbHelper.getQuestionCount()));
        highCorrectQuestion.setText(String.valueOf(dbHelper.getMaxCorrectAnswer()));
    }

    private void playAudio() {
        boolean musicEnabled = sharedPreferences.getBoolean("musicEnabled", true);
        if (musicEnabled) {
            Intent intent = new Intent(this, BackgroundMusicService.class);
            startService(intent);
        }
    }

    private void soon() {
        Toast toast = Toast.makeText(MainActivity.this, "Chức năng này sẽ sớm ra mắt", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void setUpListener() {
        // Set onClick drawer
        drawerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Set onClick button home
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getBoolean("bgmEnabled", true)) {
                    OnClickEffectManager.playClickSound(MainActivity.this);
                }
                drawerLayout.close();
            }
        });

        // Set onClick button insert question
        btnInsertQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {soon();}
        });

        // Set onClick button high soccer
        btnHighCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getBoolean("bgmEnabled", true)) {
                    OnClickEffectManager.playClickSound(MainActivity.this);
                }
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                startActivity(intent);
            }
        });

        // Set onClick button friend
        btnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {soon();}
        });

        // Set onClick button contact
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getBoolean("bgmEnabled", true)) {
                    OnClickEffectManager.playClickSound(MainActivity.this);
                }
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });

        // Set onCLick button play
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean("bgmEnabled", true)) {
                    OnClickEffectManager.playClickSound(MainActivity.this);
                }
                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                intent.putExtra("title", "Quiz");
                startActivity(intent);
            }
        });

        // Set onClick button option
        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean("bgmEnabled", true)) {
                    OnClickEffectManager.playClickSound(MainActivity.this);
                }
                Intent intent = new Intent(MainActivity.this, OptionActivity.class);
                startActivity(intent);
            }
        });

        // Set onClick avatar
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soon();
            }
        });

        // Set onClick button exit
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
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
                        finish();
                        System.exit(0);
                    }
                });

                dialog.show();
            }
        });
    }
}