package com.example.quizapp.service;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.quizapp.R;

public class OnClickEffectManager {
    private static MediaPlayer mediaPlayer;

    public static void playClickSound(Context context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.click);
        mediaPlayer.start();
    }

    public static void stopClickSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
