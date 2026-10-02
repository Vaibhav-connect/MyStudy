package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button loginButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private boolean loginInProgress = false;
    private boolean screenOpening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        createLoginScreen();
    }

    private int dp(float value) {
        return (int) (
                value * getResources().getDisplayMetrics().density + 0.5f
        );
    }

    private void createLoginScreen() {

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(
                dp(24),
                dp(24),
                dp(24),
                dp(24)
        );

        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.rgb(79, 70, 229),
                        Color.rgb(124, 58, 237),
                        Color.rgb(37, 99, 235)
                }
        );

        root.setBackground(background);

        TextView logo = new TextView(this);
        logo.setText("📖");
        logo.setTextSize(55);
        logo.setGravity(Gravity.CENTER);

        root.addView(
                logo,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        TextView title = new TextView(this);
        title.setText("Welcome Back! 👋");
        title.setTextSize(30);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        titleParams.topMargin = dp(15);

        root.addView(title, titleParams);

        TextView subtitle = new TextView(this);
        subtitle.setText("Continue your learning journey");
        subtitle.setTextSize(16);
        subtitle.setTextColor(Color.WHITE);
        subtitle.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        subtitleParams.topMargin = dp(8);

        root.addView(subtitle, subtitleParams);

        email = createInput(
                "Email",
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        addInput(root, email, 35);

        password = createInput(
                "Password",
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        addInput(root, password, 15);

        loginButton = new Button(this);

        loginButton.setText("Login");
        loginButton.setTextSize(18);
        loginButton.setAllCaps(false);
        loginButton.setTextColor(Color.rgb(79, 70, 229));
        loginButton.setGravity(Gravity.CENTER);

        GradientDrawable loginBackground =
                new GradientDrawable();

        loginBackground.setColor(Color.WHITE);
        loginBackground.setCornerRadius(dp(40));

        loginButton.setBackground(loginBackground);

        LinearLayout.LayoutParams loginParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(58)
                );

        loginParams.topMargin = dp(25);

        root.addView(loginButton, loginParams);

        TextView forgotPassword = new TextView(this);

        forgotPassword.setText("Forgot Password?");
        forgotPassword.setTextSize(15);
        forgotPassword.setTextColor(Color.WHITE);
        forgotPassword.setGravity(Gravity.CENTER);
        forgotPassword.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(10)
        );

        LinearLayout.LayoutParams forgotParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        forgotParams.topMargin = dp(10);

        root.addView(forgotPassword, forgotParams);

        TextView registerText = new TextView(this);

        registerText.setText("New to MyStudy?  Register");
        registerText.setTextSize(16);
        registerText.setTextColor(Color.WHITE);
        registerText.setGravity(Gravity.CENTER);
        registerText.setPadding(
                dp(12),
                dp(8),
                dp(12),
                dp(8)
        );

        LinearLayout.LayoutParams registerParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        registerParams.topMargin = dp(8);

        root.addView(registerText, registerParams);

        TextView adminLogin = new TextView(this);

        adminLogin.setText("👑  Admin Login");
        adminLogin.setTextSize(16);
        adminLogin.setTextColor(Color.WHITE);
        adminLogin.setGravity(Gravity.CENTER);
        adminLogin.setPadding(
                dp(12),
                dp(8),
                dp(12),
                dp(8)
        );

        LinearLayout.LayoutParams adminParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        adminParams.topMargin = dp(10);

        root.addView(adminLogin, adminParams);

        loginButton.setOnClickListener(v -> loginUser());

        registerText.setOnClickListener(v -> {

            if (screenOpening || isFinishing()) {
                return;
            }

            screenOpening = true;

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            RegisterActivity.class
                    );

            startActivity(intent);

            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );

            screenOpening = false;
        });

        forgotPassword.setOnClickListener(
                v -> sendPasswordReset()
        );

        adminLogin.setOnClickListener(v -> {

            if (screenOpening || isFinishing()) {
                return;
            }

            screenOpening = true;

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            AdminLoginActivity.class
                    );

            startActivity(intent);

            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );

            screenOpening = false;
        });

        setContentView(root);
    }

    private EditText createInput(
            String hint,
            int inputType
    ) {

        EditText input = new EditText(this);

        input.setHint(hint);
        input.setTextSize(16);
        input.setSingleLine(true);
        input.setInputType(inputType);
        input.setPadding(
                dp(20),
                dp(8),
                dp(20),
                dp(8)
        );

        input.setTextColor(Color.DKGRAY);
        input.setHintTextColor(Color.GRAY);

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(dp(35));

        input.setBackground(background);

        return input;
    }

    private void addInput(
            LinearLayout root,
            EditText input,
            int topMargin
    ) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(58)
                );

        params.topMargin = dp(topMargin);

        root.addView(input, params);
    }

    private void loginUser() {

        if (loginInProgress) {
            return;
        }

        String emailText =
                email.getText().toString().trim();

        String passwordText =
                password.getText().toString();

        email.setError(null);
        password.setError(null);

        if (emailText.isEmpty()) {

            email.setError("Enter your email");
            email.requestFocus();

            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(emailText)
                .matches()) {

            email.setError("Enter a valid email");
            email.requestFocus();

            return;
        }

        if (passwordText.isEmpty()) {

            password.setError("Enter your password");
            password.requestFocus();

            return;
        }

        hideKeyboard();

        loginInProgress = true;

        if (loginButton != null) {
            loginButton.setEnabled(false);
            loginButton.setText("Signing in...");
            loginButton.setAlpha(0.7f);
        }

        Toast.makeText(
                this,
                "Signing in...",
                Toast.LENGTH_SHORT
        ).show();

        auth.signInWithEmailAndPassword(
                emailText,
                passwordText
        ).addOnCompleteListener(this, task -> {

            if (isFinishing() || isDestroyed()) {
                return;
            }

            if (!task.isSuccessful()) {

                String message =
                        task.getException() != null
                                ? task.getException().getMessage()
                                : "Login failed";

                resetLoginButton();

                Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            FirebaseUser user =
                    auth.getCurrentUser();

            if (user == null) {

                resetLoginButton();

                Toast.makeText(
                        this,
                        "Login successful, but user data could not be loaded.",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            loadUserProfile(user.getUid());
        });
    }

    private void loadUserProfile(String uid) {

        if (uid == null || uid.trim().isEmpty()) {

            auth.signOut();
            resetLoginButton();

            Toast.makeText(
                    this,
                    "Invalid user account.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    if (!document.exists()) {

                        auth.signOut();
                        resetLoginButton();

                        Toast.makeText(
                                this,
                                "Profile not found.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    String role =
                            document.getString("role");

                    String name =
                            document.getString("name");

                    String selectedClass =
                            document.getString("class");

                    String selectedMedium =
                            document.getString("medium");

                    String selectedLanguage =
                            document.getString("language");

                    if (role == null ||
                            role.trim().isEmpty()) {

                        role = "student";
                    }

                    if (name == null ||
                            name.trim().isEmpty()) {

                        name = "Student";
                    }

                    if (selectedClass == null) {
                        selectedClass = "";
                    }

                    if (selectedMedium == null) {
                        selectedMedium = "";
                    }

                    if (selectedLanguage == null ||
                            selectedLanguage.trim().isEmpty()) {

                        selectedLanguage = "English";
                    }

                    if (role.equals("main_admin") ||
                            role.equals("admin")) {

                        auth.signOut();
                        resetLoginButton();

                        Toast.makeText(
                                this,
                                "Please use Admin Login for admin access.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    Toast.makeText(
                            this,
                            "Welcome, " + name + "! 👋",
                            Toast.LENGTH_SHORT
                    ).show();

                    screenOpening = true;

                    Intent intent =
                            new Intent(
                                    LoginActivity.this,
                                    StudentDashboardActivity.class
                            );

                    intent.putExtra(
                            "studentName",
                            name
                    );

                    intent.putExtra(
                            "studentClass",
                            selectedClass
                    );

                    intent.putExtra(
                            "studentMedium",
                            selectedMedium
                    );

                    intent.putExtra(
                            "studentLanguage",
                            selectedLanguage
                    );

                    startActivity(intent);

                    overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                    );

                    finish();
                })
                .addOnFailureListener(e -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    auth.signOut();
                    resetLoginButton();

                    String error =
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Could not load profile.";

                    Toast.makeText(
                            this,
                            "Could not load profile: " + error,
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void sendPasswordReset() {

        if (loginInProgress) {
            return;
        }

        String emailText =
                email.getText().toString().trim();

        email.setError(null);

        if (emailText.isEmpty()) {

            email.setError(
                    "Enter your email first"
            );

            email.requestFocus();

            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(emailText)
                .matches()) {

            email.setError(
                    "Enter a valid email"
            );

            email.requestFocus();

            return;
        }

        hideKeyboard();

        Toast.makeText(
                this,
                "Sending reset email...",
                Toast.LENGTH_SHORT
        ).show();

        auth.sendPasswordResetEmail(emailText)
                .addOnCompleteListener(this, task -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                this,
                                "Password reset email sent. Check your inbox.",
                                Toast.LENGTH_LONG
                        ).show();

                    } else {

                        String message =
                                task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Could not send reset email";

                        Toast.makeText(
                                this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void resetLoginButton() {

        loginInProgress = false;

        if (loginButton != null &&
                !isFinishing() &&
                !isDestroyed()) {

            loginButton.setEnabled(true);
            loginButton.setText("Login");
            loginButton.setAlpha(1f);
        }
    }

    private void hideKeyboard() {

        View currentFocus = getCurrentFocus();

        if (currentFocus == null) {
            return;
        }

        InputMethodManager manager =
                (InputMethodManager)
                        getSystemService(
                                Context.INPUT_METHOD_SERVICE
                        );

        if (manager != null) {

            manager.hideSoftInputFromWindow(
                    currentFocus.getWindowToken(),
                    0
            );
        }
    }

    @Override
    protected void onDestroy() {

        loginInProgress = false;
        screenOpening = false;

        super.onDestroy();
    }
}
