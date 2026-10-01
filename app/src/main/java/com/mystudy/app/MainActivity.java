package com.mystudy.app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView welcomeText = new TextView(this);

        welcomeText.setText("Welcome to MyStudy 👋");
        welcomeText.setTextSize(28);
        welcomeText.setGravity(android.view.Gravity.CENTER);

        setContentView(welcomeText);
    }
}
