package com.mystudy.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("MyStudyPrefs", MODE_PRIVATE);

        if (preferences.contains("language")) {
            showWelcomeScreen(preferences.getString("language", "English"));
        } else {
            showLanguageScreen();
        }
    }

    private void showLanguageScreen() {

        LinearLayout root = createRoot();

        TextView emoji = createText("📚", 54, Color.WHITE);
        root.addView(emoji);

        TextView title = createText("MyStudy", 38, Color.WHITE);
        root.addView(title);

        TextView subtitle = createText(
                "Learn • Practice • Grow",
                18,
                Color.WHITE
        );

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        subtitleParams.topMargin = 8;
        root.addView(subtitle, subtitleParams);

        TextView chooseText = createText(
                "Choose your language",
                22,
                Color.WHITE
        );

        LinearLayout.LayoutParams chooseParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        chooseParams.topMargin = 55;
        root.addView(chooseText, chooseParams);

        addLanguageButton(root, "English", "English");
        addLanguageButton(root, "मराठी", "Marathi");
        addLanguageButton(root, "हिन्दी", "Hindi");

        setContentView(root);
    }

    private void addLanguageButton(
            LinearLayout root,
            String displayName,
            String language
    ) {

        Button button = new Button(this);

        button.setText(displayName);
        button.setTextSize(17);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(40);

        button.setBackground(background);
        button.setTextColor(Color.rgb(79, 70, 229));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        600,
                        65
                );

        params.topMargin = 16;

        root.addView(button, params);

        button.setOnClickListener(view -> {

            preferences.edit()
                    .putString("language", language)
                    .apply();

            showWelcomeScreen(language);
        });
    }

    private void showWelcomeScreen(String language) {

        LinearLayout root = createRoot();

        TextView logo = createText("📚", 58, Color.WHITE);
        root.addView(logo);

        TextView title = createText(
                "Welcome to MyStudy! 👋",
                30,
                Color.WHITE
        );

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        titleParams.topMargin = 20;
        root.addView(title, titleParams);

        TextView languageText = createText(
                "Language: " + language,
                17,
                Color.WHITE
        );

        LinearLayout.LayoutParams languageParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        languageParams.topMargin = 12;
        root.addView(languageText, languageParams);

        TextView message = createText(
                "Learn new things,\npractice every day,\nand grow smarter! 🌟",
                20,
                Color.WHITE
        );

        LinearLayout.LayoutParams messageParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        messageParams.topMargin = 35;
        root.addView(message, messageParams);

        Button startButton = new Button(this);

        startButton.setText("Get Started 🚀");
        startButton.setTextSize(18);
        startButton.setAllCaps(false);

        GradientDrawable buttonBackground = new GradientDrawable();
        buttonBackground.setColor(Color.WHITE);
        buttonBackground.setCornerRadius(40);

        startButton.setBackground(buttonBackground);
        startButton.setTextColor(Color.rgb(79, 70, 229));

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        600,
                        70
                );

        buttonParams.topMargin = 55;

        root.addView(startButton, buttonParams);

        startButton.setOnClickListener(view -> {
            // Login screen will be connected here.
        });

        setContentView(root);
    }

    private LinearLayout createRoot() {

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(40, 40, 40, 40);

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.rgb(79, 70, 229),
                        Color.rgb(124, 58, 237),
                        Color.rgb(37, 99, 235)
                }
        );

        root.setBackground(gradient);

        return root;
    }

    private TextView createText(
            String text,
            float size,
            int color
    ) {

        TextView view = new TextView(this);

        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER);

        return view;
    }
}
