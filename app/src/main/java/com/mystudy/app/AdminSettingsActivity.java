package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminSettingsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView nameText;
    private TextView emailText;
    private TextView roleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buildUI();
        loadAdminData();
    }

    private void buildUI() {

        ScrollView scrollView = new ScrollView(this);

        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(
                Color.rgb(248, 250, 252)
        );

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                32,
                36,
                32,
                40
        );

        TextView title =
                new TextView(this);

        title.setText(
                "Admin Settings"
        );

        title.setTextSize(30);

        title.setTextColor(
                Color.rgb(17, 24, 39)
        );

        title.setTypeface(null, 1);

        root.addView(
                title,
                createParams(0, 0, 0, 8)
        );

        TextView subtitle =
                new TextView(this);

        subtitle.setText(
                "Manage your administrator account"
        );

        subtitle.setTextSize(15);

        subtitle.setTextColor(
                Color.rgb(100, 116, 139)
        );

        root.addView(
                subtitle,
                createParams(0, 0, 0, 24)
        );

        LinearLayout profileCard =
                createCard();

        TextView profileTitle =
                createSectionTitle(
                        "Administrator Profile"
                );

        profileCard.addView(
                profileTitle
        );

        nameText =
                createInfoText(
                        "Loading name..."
                );

        profileCard.addView(
                nameText,
                createParams(0, 14, 0, 5)
        );

        emailText =
                createInfoText(
                        "Loading email..."
                );

        profileCard.addView(
                emailText,
                createParams(0, 0, 0, 5)
        );

        roleText =
                createInfoText(
                        "Loading role..."
                );

        profileCard.addView(
                roleText
        );

        root.addView(
                profileCard,
                createParams(0, 0, 0, 18)
        );

        LinearLayout securityCard =
                createCard();

        TextView securityTitle =
                createSectionTitle(
                        "Security"
                );

        securityCard.addView(
                securityTitle
        );

        TextView resetPassword =
                createActionButton(
                        "Send Password Reset Email",
                        Color.rgb(79, 70, 229)
                );

        resetPassword.setOnClickListener(
                v -> sendPasswordReset()
        );

        securityCard.addView(
                resetPassword,
                createParams(0, 16, 0, 10)
        );

        TextView securityInfo =
                createInfoText(
                        "A secure Firebase password-reset email will be sent to the administrator's registered email address."
                );

        securityInfo.setTextSize(13);

        securityCard.addView(
                securityInfo
        );

        root.addView(
                securityCard,
                createParams(0, 0, 0, 18)
        );

        LinearLayout accessCard =
                createCard();

        TextView accessTitle =
                createSectionTitle(
                        "Access"
                );

        accessCard.addView(
                accessTitle
        );

        TextView accessInfo =
                createInfoText(
                        "Administrator access is controlled through the user's Firebase role."
                );

        accessInfo.setTextSize(14);

        accessCard.addView(
                accessInfo,
                createParams(0, 12, 0, 0)
        );

        root.addView(
                accessCard,
                createParams(0, 0, 0, 18)
        );

        LinearLayout accountCard =
                createCard();

        TextView accountTitle =
                createSectionTitle(
                        "Account"
                );

        accountCard.addView(
                accountTitle
        );

        TextView logoutButton =
                createActionButton(
                        "Logout",
                        Color.rgb(239, 68, 68)
                );

        logoutButton.setOnClickListener(
                v -> logout()
        );

        accountCard.addView(
                logoutButton,
                createParams(0, 16, 0, 0)
        );

        root.addView(
                accountCard,
                createParams(0, 0, 0, 18)
        );

        Button backButton =
                new Button(this);

        backButton.setText(
                "Back to Admin Dashboard"
        );

        backButton.setAllCaps(false);

        backButton.setTextSize(15);

        backButton.setOnClickListener(
                v -> finish()
        );

        root.addView(
                backButton
        );

        TextView developer =
                new TextView(this);

        developer.setText(
                "MyStudy\nDeveloped by Vaibhav Bhosale"
        );

        developer.setTextSize(13);

        developer.setTextColor(
                Color.rgb(148, 163, 184)
        );

        developer.setGravity(
                Gravity.CENTER
        );

        developer.setPadding(
                0,
                30,
                0,
                0
        );

        root.addView(
                developer
        );

        scrollView.addView(root);

        setContentView(scrollView);
    }

    private void loadAdminData() {

        if (auth.getCurrentUser() == null) {
            return;
        }

        String uid =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (!document.exists()) {
                                return;
                            }

                            String name =
                                    document.getString(
                                            "name"
                                    );

                            String email =
                                    document.getString(
                                            "email"
                                    );

                            String role =
                                    document.getString(
                                            "role"
                                    );

                            nameText.setText(
                                    name == null ||
                                            name.trim().isEmpty()
                                            ? "Admin"
                                            : name
                            );

                            emailText.setText(
                                    email == null
                                            ? ""
                                            : email
                            );

                            roleText.setText(
                                    "Role: " +
                                            (
                                                    role == null ||
                                                            role.trim().isEmpty()
                                                            ? "admin"
                                                            : role
                                            )
                            );
                        }
                )
                .addOnFailureListener(
                        e ->
                                Toast.makeText(
                                        this,
                                        "Unable to load admin details",
                                        Toast.LENGTH_SHORT
                                ).show()
                );
    }

    private void sendPasswordReset() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "No admin account is currently signed in.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String email =
                auth.getCurrentUser().getEmail();

        if (email == null ||
                email.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Admin email not available.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(
                        unused ->
                                Toast.makeText(
                                        this,
                                        "Password reset email sent.",
                                        Toast.LENGTH_LONG
                                ).show()
                )
                .addOnFailureListener(
                        e ->
                                Toast.makeText(
                                        this,
                                        "Unable to send reset email: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show()
                );
    }

    private void logout() {

        auth.signOut();

        Intent intent =
                new Intent(
                        this,
                        IntroActivity.class
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
                Color.WHITE
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

        view.setTextColor(
                Color.rgb(17, 24, 39)
        );

        view.setTypeface(
                null,
                1
        );

        return view;
    }

    private TextView createInfoText(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);

        view.setTextSize(15);

        view.setTextColor(
                Color.rgb(100, 116, 139)
        );

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

        view.setTextColor(
                Color.WHITE
        );

        view.setGravity(
                Gravity.CENTER
        );

        view.setTypeface(
                null,
                1
        );

        view.setBackgroundColor(
                color
        );

        view.setPadding(
                10,
                14,
                10,
                14
        );

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
