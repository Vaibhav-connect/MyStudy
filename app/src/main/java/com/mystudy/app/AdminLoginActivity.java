package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminLoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView passwordToggle;

    private boolean passwordVisible = false;

    private final int BLUE = Color.rgb(37, 99, 235);
    private final int DARK_BLUE = Color.rgb(30, 58, 138);
    private final int YELLOW = Color.rgb(250, 204, 21);
    private final int BACKGROUND = Color.rgb(243, 247, 255);
    private final int TEXT_DARK = Color.rgb(30, 41, 59);
    private final int TEXT_GRAY = Color.rgb(100, 116, 139);
    private final int WHITE = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getWindow().setStatusBarColor(BACKGROUND);
        getWindow().setNavigationBarColor(BACKGROUND);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        createUi();
    }

    private void createUi() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(BACKGROUND);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(24), dp(20), dp(24), dp(25));

        scrollView.addView(root);

        // Back Button

        TextView backButton = new TextView(this);
        backButton.setText("‹  Back");
        backButton.setTextSize(16);
        backButton.setTextColor(BLUE);
        backButton.setTypeface(null, Typeface.BOLD);
        backButton.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams backParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(42)
                );

        root.addView(backButton, backParams);

        backButton.setOnClickListener(v -> finish());

        // MyStudy Logo Icon

        TextView logo = new TextView(this);
        logo.setText("🎓");
        logo.setTextSize(42);
        logo.setGravity(Gravity.CENTER);

        GradientDrawable logoBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{BLUE, DARK_BLUE}
        );

        logoBg.setCornerRadius(dp(24));
        logo.setBackground(logoBg);

        LinearLayout.LayoutParams logoParams =
                new LinearLayout.LayoutParams(dp(90), dp(90));

        logoParams.gravity = Gravity.CENTER;
        logoParams.topMargin = dp(18);

        root.addView(logo, logoParams);

        // App Name

        TextView appName = new TextView(this);
        appName.setText("MyStudy");
        appName.setTextSize(30);
        appName.setTypeface(null, Typeface.BOLD);
        appName.setTextColor(DARK_BLUE);
        appName.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams appNameParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        appNameParams.topMargin = dp(12);

        root.addView(appName, appNameParams);

        TextView tagline = new TextView(this);
        tagline.setText("Learn • Practice • Grow");
        tagline.setTextSize(13);
        tagline.setTextColor(TEXT_GRAY);
        tagline.setGravity(Gravity.CENTER);

        root.addView(tagline);

        // Admin Badge

        TextView adminBadge = new TextView(this);
        adminBadge.setText("✦  ADMINISTRATOR PORTAL");
        adminBadge.setTextSize(11);
        adminBadge.setTypeface(null, Typeface.BOLD);
        adminBadge.setTextColor(DARK_BLUE);
        adminBadge.setGravity(Gravity.CENTER);
        adminBadge.setPadding(dp(16), dp(9), dp(16), dp(9));

        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(Color.rgb(219, 234, 254));
        badgeBg.setCornerRadius(dp(30));

        adminBadge.setBackground(badgeBg);

        LinearLayout.LayoutParams badgeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        badgeParams.topMargin = dp(22);

        root.addView(adminBadge, badgeParams);

        // Login Card

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(22), dp(25), dp(22), dp(25));

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(WHITE);
        cardBg.setCornerRadius(dp(24));
        cardBg.setStroke(dp(1), Color.rgb(226, 232, 240));

        card.setBackground(cardBg);
        card.setElevation(dp(5));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.topMargin = dp(25);

        root.addView(card, cardParams);

        TextView welcome = new TextView(this);
        welcome.setText("Welcome, Admin!");
        welcome.setTextSize(23);
        welcome.setTextColor(TEXT_DARK);
        welcome.setTypeface(null, Typeface.BOLD);

        card.addView(welcome);

        TextView description = new TextView(this);
        description.setText(
                "Sign in to manage your MyStudy platform."
        );

        description.setTextSize(13);
        description.setTextColor(TEXT_GRAY);

        LinearLayout.LayoutParams descriptionParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        descriptionParams.topMargin = dp(7);

        card.addView(description, descriptionParams);

        // Email Label

        TextView emailLabel = createLabel("Admin Email");

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        labelParams.topMargin = dp(27);

        card.addView(emailLabel, labelParams);

        // Email Input

        emailInput = new EditText(this);
        emailInput.setHint("Enter your admin email");
        emailInput.setTextSize(15);
        emailInput.setSingleLine(true);
        emailInput.setPadding(dp(14), 0, dp(14), 0);
        emailInput.setTextColor(TEXT_DARK);
        emailInput.setHintTextColor(Color.rgb(148, 163, 184));

        emailInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        card.addView(createInputContainer(emailInput));

        // Password Label

        TextView passwordLabel = createLabel("Password");

        LinearLayout.LayoutParams passwordLabelParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        passwordLabelParams.topMargin = dp(20);

        card.addView(passwordLabel, passwordLabelParams);

        // Password Input Container

        LinearLayout passwordContainer = new LinearLayout(this);
        passwordContainer.setOrientation(LinearLayout.HORIZONTAL);
        passwordContainer.setGravity(Gravity.CENTER_VERTICAL);
        passwordContainer.setPadding(dp(12), 0, dp(12), 0);

        GradientDrawable passwordBg = new GradientDrawable();
        passwordBg.setColor(Color.rgb(248, 250, 252));
        passwordBg.setCornerRadius(dp(13));
        passwordBg.setStroke(dp(1), Color.rgb(203, 213, 225));

        passwordContainer.setBackground(passwordBg);

        passwordInput = new EditText(this);
        passwordInput.setHint("Enter your password");
        passwordInput.setTextSize(15);
        passwordInput.setSingleLine(true);
        passwordInput.setBackgroundColor(Color.TRANSPARENT);
        passwordInput.setTextColor(TEXT_DARK);
        passwordInput.setHintTextColor(Color.rgb(148, 163, 184));

        passwordInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        passwordInput.setPadding(0, 0, dp(5), 0);

        LinearLayout.LayoutParams passwordInputParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(54),
                        1
                );

        passwordContainer.addView(
                passwordInput,
                passwordInputParams
        );

        passwordToggle = new TextView(this);
        passwordToggle.setText("Show");
        passwordToggle.setTextSize(13);
        passwordToggle.setTypeface(null, Typeface.BOLD);
        passwordToggle.setTextColor(BLUE);
        passwordToggle.setGravity(Gravity.CENTER);

        passwordContainer.addView(
                passwordToggle,
                new LinearLayout.LayoutParams(
                        dp(50),
                        dp(50)
                )
        );

        LinearLayout.LayoutParams passwordContainerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(56)
                );

        card.addView(passwordContainer, passwordContainerParams);

        passwordToggle.setOnClickListener(v -> togglePassword());

        // Forgot Password

        TextView forgotPassword = new TextView(this);
        forgotPassword.setText("Forgot Password?");
        forgotPassword.setTextSize(14);
        forgotPassword.setTextColor(BLUE);
        forgotPassword.setTypeface(null, Typeface.BOLD);
        forgotPassword.setGravity(Gravity.END);

        LinearLayout.LayoutParams forgotParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(45)
                );

        forgotParams.topMargin = dp(8);

        card.addView(forgotPassword, forgotParams);

        forgotPassword.setOnClickListener(v -> resetPassword());

        // Login Button

        loginButton = new Button(this);
        loginButton.setText("Login as Admin  →");
        loginButton.setTextSize(15);
        loginButton.setTypeface(null, Typeface.BOLD);
        loginButton.setTextColor(WHITE);
        loginButton.setAllCaps(false);
        loginButton.setElevation(dp(3));

        GradientDrawable buttonBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{BLUE, DARK_BLUE}
        );

        buttonBg.setCornerRadius(dp(14));
        loginButton.setBackground(buttonBg);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(55)
                );

        buttonParams.topMargin = dp(15);

        card.addView(loginButton, buttonParams);

        loginButton.setOnClickListener(v -> loginAdmin());

        // Security Information

        TextView securityText = new TextView(this);
        securityText.setText(
                "🔒  Secure Admin Access\n" +
                "Only authorized administrators can access this area."
        );

        securityText.setTextSize(12);
        securityText.setTextColor(TEXT_GRAY);
        securityText.setGravity(Gravity.CENTER);
        securityText.setPadding(dp(10), dp(18), dp(10), dp(5));

        root.addView(securityText);

        // Footer

        TextView footer = new TextView(this);
        footer.setText("MyStudy © 2026  •  Developed by Vaibhav Bhosale");
        footer.setTextSize(11);
        footer.setTextColor(TEXT_GRAY);
        footer.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams footerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(45)
                );

        footerParams.topMargin = dp(15);

        root.addView(footer, footerParams);

        setContentView(scrollView);

        // Entry Animation

        root.setAlpha(0f);
        root.setTranslationY(dp(20));

        root.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(500)
                .start();
    }

    private TextView createLabel(String text) {

        TextView label = new TextView(this);
        label.setText(text);
        label.setTextSize(14);
        label.setTextColor(TEXT_DARK);
        label.setTypeface(null, Typeface.BOLD);

        return label;
    }

    private LinearLayout createInputContainer(EditText editText) {

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.rgb(248, 250, 252));
        background.setCornerRadius(dp(13));
        background.setStroke(dp(1), Color.rgb(203, 213, 225));

        container.setBackground(background);
        container.setPadding(dp(10), 0, dp(10), 0);

        container.addView(
                editText,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(56)
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(56)
                );

        container.setLayoutParams(params);

        return container;
    }

    private void togglePassword() {

        if (passwordVisible) {

            passwordInput.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
            );

            passwordToggle.setText("Show");
            passwordVisible = false;

        } else {

            passwordInput.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );

            passwordToggle.setText("Hide");
            passwordVisible = true;
        }

        passwordInput.setSelection(
                passwordInput.getText().length()
        );
    }

    private void loginAdmin() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty()) {
            emailInput.setError("Enter admin email");
            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Enter password");
            passwordInput.requestFocus();
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Checking Admin Account...");

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (!task.isSuccessful()) {

                        resetLoginButton();

                        Toast.makeText(
                                this,
                                getLoginError(task.getException()),
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    if (auth.getCurrentUser() == null) {

                        resetLoginButton();

                        Toast.makeText(
                                this,
                                "Authentication failed.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    String uid = auth.getCurrentUser().getUid();

                    checkAdminRole(uid);
                });
    }

    private void checkAdminRole(String uid) {

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        auth.signOut();
                        resetLoginButton();

                        Toast.makeText(
                                this,
                                "Admin profile not found.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    String role = documentSnapshot.getString("role");

                    if (role == null) {

                        auth.signOut();
                        resetLoginButton();

                        Toast.makeText(
                                this,
                                "No admin role assigned.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    if (role.equals("main_admin") || role.equals("admin")) {

                        String name =
                                documentSnapshot.getString("name");

                        if (name == null || name.trim().isEmpty()) {
                            name = "Admin";
                        }

                        Toast.makeText(
                                this,
                                "Welcome, " + name + "!",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                AdminLoginActivity.this,
                                AdminDashboardActivity.class
                        );

                        intent.putExtra("userRole", role);
                        intent.putExtra("adminName", name);

                        startActivity(intent);

                        finish();

                    } else {

                        auth.signOut();
                        resetLoginButton();

                        Toast.makeText(
                                this,
                                "Access denied. Admin account required.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    auth.signOut();
                    resetLoginButton();

                    Toast.makeText(
                            this,
                            "Unable to verify admin profile.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void resetPassword() {

        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()) {

            emailInput.setError("Enter your admin email first");
            emailInput.requestFocus();

            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Password reset email sent.",
                            Toast.LENGTH_LONG
                    ).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Unable to send reset email.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private String getLoginError(Exception exception) {

        if (exception == null) {
            return "Login failed.";
        }

        String message = exception.getMessage();

        if (message == null) {
            return "Invalid email or password.";
        }

        if (message.contains("INVALID_LOGIN_CREDENTIALS")) {
            return "Invalid email or password.";
        }

        if (message.contains("INVALID_EMAIL")) {
            return "Invalid email address.";
        }

        if (message.contains("TOO_MANY_ATTEMPTS")) {
            return "Too many attempts. Try again later.";
        }

        if (message.contains("NETWORK_REQUEST_FAILED")) {
            return "Network error. Check your internet connection.";
        }

        return "Login failed. Please check your details.";
    }

    private void resetLoginButton() {

        loginButton.setEnabled(true);
        loginButton.setText("Login as Admin  →");
    }

    private int dp(float value) {

        return (int) (value *
                getResources().getDisplayMetrics().density + 0.5f);
    }
}
