package com.mystudy.app;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(40, 40, 40, 40);

        TextView title = new TextView(this);
        title.setText("MyStudy");
        title.setTextSize(36);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText("Learn • Practice • Grow");
        subtitle.setTextSize(18);
        subtitle.setGravity(Gravity.CENTER);

        Button startButton = new Button(this);
        startButton.setText("Get Started");

        root.addView(title);
        root.addView(subtitle);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        buttonParams.topMargin = 50;
        root.addView(startButton, buttonParams);

        setContentView(root);
    }
}
