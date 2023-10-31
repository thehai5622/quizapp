package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizapp.database_handler.DbHelper;
import com.example.quizapp.service.OnClickEffectManager;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {
    private ImageView btnBack;
    private TextView btnSms;
    private TextView btnEmail;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        setWindowApp();
        defineVariable();
        setUpListener();
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
        btnSms = findViewById(R.id.btnSms);
        btnEmail = findViewById(R.id.btnEmail);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
    }

    private void shareTextViaSMS(String text, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("smsto:" + phoneNumber));
        intent.putExtra("sms_body", text);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Không tìm thấy Tin Nhắn", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareTextViaEmail(String subject, String text, String[] recipients) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Xử lý trường hợp không có ứng dụng email
            Toast.makeText(this, "Không tìm thấy Email.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpListener() {
        // Set onClick button back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Set on click button sms
        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean("bgmEnabled", true)) {
                    OnClickEffectManager.playClickSound(ContactActivity.this);
                }
                String myText = "";
                String phoneNumber = "0123456789";
                shareTextViaSMS(myText, phoneNumber);
            }
        });

        // Set on click button email
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean("bgmEnabled", true)) {
                    OnClickEffectManager.playClickSound(ContactActivity.this);
                }
                String emailSubject = "Quiz app";
                String emailText = "";
                String[] recipients = {"thehai@quiz.com"};
                shareTextViaEmail(emailSubject, emailText, recipients);
            }
        });
    }
}