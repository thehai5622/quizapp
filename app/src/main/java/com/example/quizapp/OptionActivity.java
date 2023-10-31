package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.quizapp.service.BackgroundMusicService;
import com.example.quizapp.service.OnClickEffectManager;

public class OptionActivity extends AppCompatActivity {
    private ImageView btnBack;
    private CheckBox cbMusic;
    private CheckBox cbBgm;

    boolean musicEnabled;
    boolean bgmEnabled;
    SharedPreferences sharedPreferences;
    Intent intentAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        setWindowApp();
        defineVariable();
        setUpListener();
        getSharedPreferences();
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
        cbMusic = findViewById(R.id.cbMusic);
        cbBgm = findViewById(R.id.cbBgm);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        intentAudio = new Intent(OptionActivity.this, BackgroundMusicService.class);
        musicEnabled = sharedPreferences.getBoolean("musicEnabled", true);
        bgmEnabled = sharedPreferences.getBoolean("bgmEnabled", true);
    }

    private void getSharedPreferences() {
        cbMusic.setChecked(musicEnabled);
        cbBgm.setChecked(bgmEnabled);
    }

    private void changedPlayMusic() {
        if (musicEnabled) {
            stopService(intentAudio);
        } else {
            startService(intentAudio);
        }

        musicEnabled = !musicEnabled;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("musicEnabled", musicEnabled);
        editor.apply();
    }

    private void setUpListener() {
        // Set on click checkbox music
        cbMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changedPlayMusic();
            }
        });

        // Set on click checkbox bgm
        cbBgm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sharedPreferences.getBoolean("bgmEnabled", true)) {
                    OnClickEffectManager.playClickSound(OptionActivity.this);
                }
                bgmEnabled = !bgmEnabled;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("bgmEnabled", bgmEnabled);
                editor.apply();
            }
        });

        // Set onClick button back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}