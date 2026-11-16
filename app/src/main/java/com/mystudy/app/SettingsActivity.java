package com.mystudy.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREF_NAME = "MyStudySettings";
    private static final String LANGUAGE_KEY = "appLanguage";
    private static final String DARK_MODE_KEY = "darkMode";

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView nameText;
    private TextView emailText;
    private TextView languageText;

    private TextView englishButton;
    private TextView marathiButton;
    private TextView hindiButton;

    private LinearLayout root;

    private final int BLUE = Color.rgb(59, 130, 246);
    private final int DARK_BLUE = Color.rgb(37, 99, 235);
    private final int YELLOW = Color.rgb(250, 204, 21);
    private final int GREEN = Color.rgb(22, 163, 74);

    private final int LIGHT_BG = Color.rgb(245, 249, 255);
    private final int LIGHT_CARD = Color.WHITE;
    private final int LIGHT_TEXT = Color.rgb(17, 24, 39);
    private final int LIGHT_SECONDARY = Color.rgb(100, 116, 139);

    private final int DARK_BG = Color.rgb(17, 24, 39);
    private final int DARK_CARD = Color.rgb(31, 41, 55);
    private final int DARK_TEXT = Color.WHITE;
    private final int DARK_SECONDARY = Color.rgb(203, 213, 225);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applySavedTheme();
        applySavedLanguage();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buildUI();
        loadUserData();
    }

    // Theme

    private void applySavedTheme() {

        if (isDarkMode()) {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );

        } else {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }
    }

    private boolean isDarkMode() {

        return getSharedPreferences(
                PREF_NAME,
                MODE_PRIVATE
        ).getBoolean(DARK_MODE_KEY, false);
    }

    private int getBackgroundColor() {
        return isDarkMode() ? DARK_BG : LIGHT_BG;
    }

    private int getCardColor() {
        return isDarkMode() ? DARK_CARD : LIGHT_CARD;
    }

    private int getTextColor() {
        return isDarkMode() ? DARK_TEXT : LIGHT_TEXT;
    }

    private int getSecondaryColor() {
        return isDarkMode() ? DARK_SECONDARY : LIGHT_SECONDARY;
    }

    // Language

    private void applySavedLanguage() {

        SharedPreferences preferences =
                getSharedPreferences(
                        PREF_NAME,
                        MODE_PRIVATE
                );

        String language = preferences.getString(
                LANGUAGE_KEY,
                "English"
        );

        applyLanguage(language);
    }

    private void applyLanguage(String language) {

        String languageTag;

        if ("Marathi".equalsIgnoreCase(language)) {

            languageTag = "mr";

        } else if ("Hindi".equalsIgnoreCase(language)) {

            languageTag = "hi";

        } else {

            languageTag = "en";
        }

        LocaleListCompat localeList =
                LocaleListCompat.forLanguageTags(languageTag);

        AppCompatDelegate.setApplicationLocales(localeList);
    }

    private void saveLocalLanguage(String language) {

        getSharedPreferences(
                PREF_NAME,
                MODE_PRIVATE
        )
                .edit()
                .putString(LANGUAGE_KEY, language)
                .apply();
    }

    private void selectLanguage(String language) {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        languageText.setText(language);
        updateLanguageButtons(language);

        String uid = auth.getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("language", language);

        db.collection("users")
                .document(uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(unused -> {

                    saveLocalLanguage(language);

                    applyLanguage(language);

                    Toast.makeText(
                            SettingsActivity.this,
                            "Language saved successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                })
                .addOnFailureListener(error -> {

                    Toast.makeText(
                            SettingsActivity.this,
                            "Unable to save language",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }

    private void updateLanguageButtons(String language) {

        updateLanguageButton(
                englishButton,
                "English".equalsIgnoreCase(language)
        );

        updateLanguageButton(
                marathiButton,
                "Marathi".equalsIgnoreCase(language)
        );

        updateLanguageButton(
                hindiButton,
                "Hindi".equalsIgnoreCase(language)
        );
    }

    private void updateLanguageButton(
            TextView button,
            boolean selected
    ) {

        if (button == null) {
            return;
        }

        button.setTextColor(
                selected ? Color.WHITE : DARK_BLUE
        );

        button.setBackground(
                createBackground(
                        selected ? DARK_BLUE :
                                (isDarkMode()
                                        ? Color.rgb(55, 65, 81)
                                        : Color.rgb(239, 246, 255)),
                        14,
                        selected ? YELLOW : Color.TRANSPARENT
                )
        );
    }

    // Main UI

    private void buildUI() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(getBackgroundColor());

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        root.setPadding(
                dp(18),
                dp(22),
                dp(18),
                dp(35)
        );

        root.setBackgroundColor(getBackgroundColor());

        // Header

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView logo = new TextView(this);
        logo.setText("📘");
        logo.setTextSize(29);
        logo.setGravity(Gravity.CENTER);

        header.addView(
                logo,
                new LinearLayout.LayoutParams(dp(48), dp(48))
        );

        LinearLayout brandInfo = new LinearLayout(this);
        brandInfo.setOrientation(LinearLayout.VERTICAL);
        brandInfo.setPadding(dp(8), 0, 0, 0);

        TextView brand = new TextView(this);
        brand.setText("MyStudy");
        brand.setTextSize(22);
        brand.setTextColor(DARK_BLUE);
        brand.setTypeface(null, Typeface.BOLD);

        brandInfo.addView(brand);

        TextView tagline = createSubtitle(
                "Learn • Practice • Grow"
        );

        brandInfo.addView(tagline);

        header.addView(
                brandInfo,
                new LinearLayout.LayoutParams(0, -2, 1)
        );

        TextView backButton = new TextView(this);
        backButton.setText("←");
        backButton.setTextSize(25);
        backButton.setTextColor(DARK_BLUE);
        backButton.setGravity(Gravity.CENTER);
        backButton.setBackground(
                createBackground(getCardColor(), 15)
        );

        header.addView(
                backButton,
                new LinearLayout.LayoutParams(dp(46), dp(46))
        );

        backButton.setOnClickListener(v -> finish());
        addPressAnimation(backButton);

        root.addView(
                header,
                createMargins(0, 0, 0, 22)
        );

        // Page title

        TextView title = createTitle("Settings");
        root.addView(title);

        TextView subtitle = createSubtitle(
                "Manage your MyStudy preferences"
        );

        root.addView(
                subtitle,
                createMargins(0, dp(5), 0, dp(22))
        );

        // Profile card

        LinearLayout profileCard = createCard();

        profileCard.addView(
                createSectionTitle("👤  Profile")
        );

        nameText = createInfoText("Loading name...");

        nameText.setTextSize(19);
        nameText.setTypeface(null, Typeface.BOLD);
        nameText.setTextColor(DARK_BLUE);

        profileCard.addView(
                nameText,
                createMargins(0, dp(13), 0, dp(5))
        );

        emailText = createInfoText("Loading email...");

        profileCard.addView(emailText);

        root.addView(
                profileCard,
                createMargins(0, 0, 0, 17)
        );

        // Language card

        LinearLayout languageCard = createCard();

        languageCard.addView(
                createSectionTitle("🌐  Language")
        );

        languageText = createInfoText(
                getSavedLanguage()
        );

        languageCard.addView(
                languageText,
                createMargins(0, dp(10), 0, dp(14))
        );

        LinearLayout languageRow = new LinearLayout(this);
        languageRow.setOrientation(LinearLayout.HORIZONTAL);

        englishButton = createLanguageButton("English");
        marathiButton = createLanguageButton("मराठी");
        hindiButton = createLanguageButton("हिन्दी");

        englishButton.setOnClickListener(
                v -> selectLanguage("English")
        );

        marathiButton.setOnClickListener(
                v -> selectLanguage("Marathi")
        );

        hindiButton.setOnClickListener(
                v -> selectLanguage("Hindi")
        );

        languageRow.addView(
                englishButton,
                createLanguageParams()
        );

        languageRow.addView(
                marathiButton,
                createLanguageParams()
        );

        languageRow.addView(
                hindiButton,
                createLanguageParams()
        );

        languageCard.addView(languageRow);

        updateLanguageButtons(getSavedLanguage());

        root.addView(
                languageCard,
                createMargins(0, 0, 0, 17)
        );

        // Appearance card

        LinearLayout appearanceCard = createCard();

        appearanceCard.addView(
                createSectionTitle("🎨  Appearance")
        );

        LinearLayout darkRow = new LinearLayout(this);
        darkRow.setOrientation(LinearLayout.HORIZONTAL);
        darkRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView darkText = createInfoText("Dark Theme");
        darkText.setTextSize(16);

        darkRow.addView(
                darkText,
                new LinearLayout.LayoutParams(0, dp(58), 1)
        );

        Switch darkSwitch = new Switch(this);
        darkSwitch.setChecked(isDarkMode());

        darkRow.addView(
                darkSwitch,
                new LinearLayout.LayoutParams(
                        dp(60),
                        dp(58)
                )
        );

        appearanceCard.addView(
                darkRow,
                createMargins(0, dp(8), 0, 0)
        );

        darkSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    getSharedPreferences(
                            PREF_NAME,
                            MODE_PRIVATE
                    )
                            .edit()
                            .putBoolean(DARK_MODE_KEY, isChecked)
                            .apply();

                    Toast.makeText(
                            SettingsActivity.this,
                            isChecked
                                    ? "Dark theme enabled"
                                    : "Light theme enabled",
                            Toast.LENGTH_SHORT
                    ).show();

                    AppCompatDelegate.setDefaultNightMode(
                            isChecked
                                    ? AppCompatDelegate.MODE_NIGHT_YES
                                    : AppCompatDelegate.MODE_NIGHT_NO
                    );
                }
        );

        root.addView(
                appearanceCard,
                createMargins(0, 0, 0, 17)
        );

        // Account card

        LinearLayout accountCard = createCard();

        accountCard.addView(
                createSectionTitle("🔐  Account")
        );

        TextView logoutButton = createActionButton(
                "Logout",
                Color.rgb(239, 68, 68)
        );

        logoutButton.setOnClickListener(v -> logout());

        accountCard.addView(
                logoutButton,
                createMargins(0, dp(15), 0, 0)
        );

        root.addView(accountCard);

        // Footer

        TextView developer = new TextView(this);

        developer.setText(
                "MyStudy\nDeveloped by Vaibhav Bhosale"
        );

        developer.setTextSize(13);
        developer.setTextColor(getSecondaryColor());
        developer.setGravity(Gravity.CENTER);

        developer.setPadding(
                dp(10),
                dp(28),
                dp(10),
                dp(5)
        );

        root.addView(developer);

        scrollView.addView(root);
        setContentView(scrollView);

        // Entrance animation

        root.setAlpha(0f);
        root.setTranslationY(dp(15));

        root.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(400)
                .start();
    }

    // Load Firebase profile

    private void loadUserData() {

        if (auth.getCurrentUser() == null) {

            nameText.setText("Not logged in");
            emailText.setText("");

            return;
        }

        String uid = auth.getCurrentUser().getUid();

        String authEmail = auth.getCurrentUser().getEmail();

        if (authEmail != null) {
            emailText.setText(authEmail);
        }

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    if (!document.exists()) {

                        nameText.setText("Student");
                        return;
                    }

                    String name = document.getString("name");
                    String email = document.getString("email");
                    String language = document.getString("language");

                    if (name != null && !name.trim().isEmpty()) {

                        nameText.setText(name);

                    } else {

                        nameText.setText("Student");
                    }

                    if (email != null && !email.trim().isEmpty()) {

                        emailText.setText(email);
                    }

                    if (language != null &&
                            !language.trim().isEmpty()) {

                        String savedLanguage = getSavedLanguage();

                        languageText.setText(language);

                        saveLocalLanguage(language);

                        updateLanguageButtons(language);

                        if (!savedLanguage.equalsIgnoreCase(language)) {
                            applyLanguage(language);
                        }
                    }
                })
                .addOnFailureListener(error -> {

                    if (!isFinishing() && !isDestroyed()) {

                        Toast.makeText(
                                SettingsActivity.this,
                                "Unable to load profile",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // Logout

    private void logout() {

        auth.signOut();

        Intent intent = new Intent(
                SettingsActivity.this,
                LoginActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    // Helpers

    private String getSavedLanguage() {

        return getSharedPreferences(
                PREF_NAME,
                MODE_PRIVATE
        ).getString(LANGUAGE_KEY, "English");
    }

    private LinearLayout createCard() {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);

        card.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(18)
        );

        card.setBackground(
                createBackground(getCardColor(), 22)
        );

        card.setElevation(dp(3));

        return card;
    }

    private TextView createTitle(String text) {

        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(27);
        view.setTextColor(getTextColor());
        view.setTypeface(null, Typeface.BOLD);

        return view;
    }

    private TextView createSectionTitle(String text) {

        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(18);
        view.setTextColor(getTextColor());
        view.setTypeface(null, Typeface.BOLD);

        return view;
    }

    private TextView createInfoText(String text) {

        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(15);
        view.setTextColor(getSecondaryColor());

        return view;
    }

    private TextView createSubtitle(String text) {

        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(13);
        view.setTextColor(getSecondaryColor());

        return view;
    }

    private TextView createLanguageButton(String text) {

        TextView button = new TextView(this);

        button.setText(text);
        button.setTextSize(13);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);

        button.setPadding(
                dp(4),
                dp(12),
                dp(4),
                dp(12)
        );

        addPressAnimation(button);

        return button;
    }

    private LinearLayout.LayoutParams createLanguageParams() {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(0, dp(48), 1);

        params.setMargins(
                0,
                0,
                dp(6),
                0
        );

        return params;
    }

    private TextView createActionButton(
            String text,
            int color
    ) {

        TextView button = new TextView(this);

        button.setText(text);
        button.setTextSize(16);
        button.setTextColor(Color.WHITE);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);

        button.setPadding(
                dp(10),
                dp(15),
                dp(10),
                dp(15)
        );

        button.setBackground(
                createBackground(color, 15)
        );

        addPressAnimation(button);

        return button;
    }

    private GradientDrawable createBackground(
            int color,
            int radius
    ) {

        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));

        return drawable;
    }

    private GradientDrawable createBackground(
            int color,
            int radius,
            int strokeColor
    ) {

        GradientDrawable drawable = createBackground(
                color,
                radius
        );

        if (strokeColor != Color.TRANSPARENT) {
            drawable.setStroke(dp(2), strokeColor);
        }

        return drawable;
    }

    private LinearLayout.LayoutParams createMargins(
            int left,
            int top,
            int right,
            int bottom
    ) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(
                dp(left),
                dp(top),
                dp(right),
                dp(bottom)
        );

        return params;
    }

    private void addPressAnimation(View view) {

        view.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                v.animate()
                        .scaleX(0.96f)
                        .scaleY(0.96f)
                        .setDuration(100)
                        .start();

            } else if (
                    event.getAction() == MotionEvent.ACTION_UP ||
                            event.getAction() == MotionEvent.ACTION_CANCEL
            ) {

                v.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start();
            }

            return false;
        });
    }

    private int dp(float value) {

        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}
