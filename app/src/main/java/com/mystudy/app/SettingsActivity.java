package com.mystudy.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView nameText;
    private TextView emailText;
    private TextView languageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LanguageManager.applySavedLanguage(this);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buildUI();
        loadUserData();
    }

    private void buildUI() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(0xFFF8FAFC);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 36, 32, 40);

        TextView title = new TextView(this);
        title.setText("Settings");
        title.setTextSize(30);
        title.setTextColor(0xFF111827);
        title.setTypeface(null, 1);

        root.addView(title, createParams(0, 0, 0, 20));

        TextView subtitle = new TextView(this);
        subtitle.setText("Manage your MyStudy preferences");
        subtitle.setTextSize(15);
        subtitle.setTextColor(0xFF64748B);

        root.addView(subtitle, createParams(0, 0, 0, 24));

        LinearLayout profileCard = createCard();

        TextView profileTitle = createSectionTitle("Profile");
        profileCard.addView(profileTitle);

        nameText = createInfoText("Loading name...");
        profileCard.addView(
                nameText,
                createParams(0, 12, 0, 4)
        );

        emailText = createInfoText("Loading email...");
        profileCard.addView(emailText);

        root.addView(
                profileCard,
                createParams(0, 0, 0, 18)
        );

        LinearLayout languageCard = createCard();

        TextView languageTitle =
                createSectionTitle("Language");

        languageCard.addView(languageTitle);

        languageText =
                createInfoText(
                        LanguageManager.getLanguage(this)
                );

        languageCard.addView(
                languageText,
                createParams(0, 12, 0, 12)
        );

        LinearLayout languageButtons =
                new LinearLayout(this);

        languageButtons.setOrientation(
                LinearLayout.HORIZONTAL
        );

        TextView english =
                createChoiceButton("English");

        TextView marathi =
                createChoiceButton("मराठी");

        TextView hindi =
                createChoiceButton("हिन्दी");

        english.setOnClickListener(
                v -> saveLanguage("English")
        );

        marathi.setOnClickListener(
                v -> saveLanguage("Marathi")
        );

        hindi.setOnClickListener(
                v -> saveLanguage("Hindi")
        );

        languageButtons.addView(
                english,
                new LinearLayout.LayoutParams(
                        0,
                        52,
                        1
                )
        );

        languageButtons.addView(
                marathi,
                new LinearLayout.LayoutParams(
                        0,
                        52,
                        1
                )
        );

        languageButtons.addView(
                hindi,
                new LinearLayout.LayoutParams(
                        0,
                        52,
                        1
                )
        );

        languageCard.addView(languageButtons);

        root.addView(
                languageCard,
                createParams(0, 0, 0, 18)
        );

        LinearLayout appearanceCard =
                createCard();

        TextView appearanceTitle =
                createSectionTitle("Appearance");

        appearanceCard.addView(
                appearanceTitle
        );

        LinearLayout darkRow =
                new LinearLayout(this);

        darkRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        darkRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView darkText =
                createInfoText("Dark Theme");

        Switch darkSwitch =
                new Switch(this);

        boolean darkMode =
                getSharedPreferences(
                        "MyStudySettings",
                        MODE_PRIVATE
                ).getBoolean(
                        "darkMode",
                        false
                );

        darkSwitch.setChecked(darkMode);

        darkRow.addView(
                darkText,
                new LinearLayout.LayoutParams(
                        0,
                        60,
                        1
                )
        );

        darkRow.addView(
                darkSwitch,
                new LinearLayout.LayoutParams(
                        60,
                        60
                )
        );

        appearanceCard.addView(
                darkRow,
                createParams(0, 8, 0, 0)
        );

        darkSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    getSharedPreferences(
                            "MyStudySettings",
                            MODE_PRIVATE
                    )
                            .edit()
                            .putBoolean(
                                    "darkMode",
                                    isChecked
                            )
                            .apply();

                    Toast.makeText(
                            SettingsActivity.this,
                            isChecked
                                    ? "Dark theme saved"
                                    : "Light theme saved",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        root.addView(
                appearanceCard,
                createParams(0, 0, 0, 18)
        );

        LinearLayout accountCard =
                createCard();

        TextView accountTitle =
                createSectionTitle("Account");

        accountCard.addView(accountTitle);

        TextView logoutButton =
                createActionButton(
                        "Logout",
                        0xFFEF4444
                );

        logoutButton.setOnClickListener(
                v -> logout()
        );

        accountCard.addView(
                logoutButton,
                createParams(0, 16, 0, 0)
        );

        root.addView(accountCard);

        TextView developer =
                new TextView(this);

        developer.setText(
                "MyStudy\nDeveloped by Vaibhav Bhosale"
        );

        developer.setTextSize(13);
        developer.setTextColor(0xFF94A3B8);
        developer.setGravity(Gravity.CENTER);
        developer.setPadding(0, 30, 0, 0);

        root.addView(
                developer,
                createParams(0, 0, 0, 0)
        );

        scrollView.addView(root);

        setContentView(scrollView);
    }

    private void loadUserData() {

        if (auth.getCurrentUser() == null) {
            return;
        }

        String uid =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        return;
                    }

                    String name =
                            document.getString("name");

                    String email =
                            document.getString("email");

                    String language =
                            document.getString("language");

                    nameText.setText(
                            name == null
                                    ? "Student"
                                    : name
                    );

                    emailText.setText(
                            email == null
                                    ? ""
                                    : email
                    );

                    if (language != null &&
                            !language.trim().isEmpty()) {

                        languageText.setText(language);

                        LanguageManager.saveLanguage(
                                SettingsActivity.this,
                                language
                        );
                    }
                });
    }

    private void saveLanguage(
            String language
    ) {

        if (auth.getCurrentUser() == null) {
            return;
        }

        String uid =
                auth.getCurrentUser().getUid();

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "language",
                language
        );

        db.collection("users")
                .document(uid)
                .set(
                        data,
                        SetOptions.merge()
                )
                .addOnSuccessListener(unused -> {

                    LanguageManager.saveLanguage(
                            SettingsActivity.this,
                            language
                    );

                    languageText.setText(language);

                    Toast.makeText(
                            SettingsActivity.this,
                            "Language saved",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                SettingsActivity.this,
                                "Unable to save language",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void logout() {

        auth.signOut();

        Intent intent =
                new Intent(
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

    private LinearLayout createCard() {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                22,
                20,
                22,
                20
        );

        card.setBackgroundColor(
                0xFFFFFFFF
        );

        card.setElevation(6);

        return card;
    }

    private TextView createSectionTitle(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);
        view.setTextSize(18);
        view.setTextColor(0xFF111827);
        view.setTypeface(null, 1);

        return view;
    }

    private TextView createInfoText(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);
        view.setTextSize(15);
        view.setTextColor(0xFF64748B);

        return view;
    }

    private TextView createChoiceButton(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);
        view.setTextSize(14);
        view.setTextColor(0xFF4F46E5);
        view.setGravity(Gravity.CENTER);
        view.setBackgroundColor(0xFFF1F5F9);
        view.setPadding(8, 8, 8, 8);

        return view;
    }

    private TextView createActionButton(
            String text,
            int color
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);
        view.setTextSize(16);
        view.setTextColor(0xFFFFFFFF);
        view.setGravity(Gravity.CENTER);
        view.setTypeface(null, 1);
        view.setBackgroundColor(color);
        view.setPadding(10, 14, 10, 14);

        return view;
    }

    private LinearLayout.LayoutParams createParams(
            int left,
            int top,
            int right,
            int bottom
    ) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                left,
                top,
                right,
                bottom
        );

        return params;
    }
}
