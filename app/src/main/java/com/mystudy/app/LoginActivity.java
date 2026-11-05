package com.mystudy.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private TextView loginButton;
    private TextView passwordToggle;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private boolean loginInProgress = false;
    private boolean screenOpening = false;
    private boolean passwordVisible = false;

    private final int BLUE = Color.rgb(0, 119, 245);
    private final int DARK_BLUE = Color.rgb(16, 55, 145);
    private final int LIGHT_BLUE = Color.rgb(225, 243, 255);
    private final int YELLOW = Color.rgb(255, 190, 35);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getWindow().setStatusBarColor(Color.rgb(0, 105, 225));
        getWindow().setNavigationBarColor(Color.rgb(8, 45, 120));

        createLoginScreen();
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable shape(int color, int radius, int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        if (strokeWidth > 0) {
            drawable.setStroke(dp(strokeWidth), strokeColor);
        }
        return drawable;
    }

    private GradientDrawable gradient(int start, int end, int radius) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{start, end}
        );
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private LinearLayout.LayoutParams params(int width, int height) {
        return new LinearLayout.LayoutParams(width < 0 ? width : dp(width),
                height < 0 ? height : dp(height));
    }

    private void createLoginScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setBackground(gradient(Color.rgb(0, 91, 210),
                Color.rgb(90, 190, 255), 0));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(20), dp(18), dp(20), dp(28));

        // MyStudy brand header
        TextView logoIcon = text("📖💡", 42, Color.WHITE, true);
        root.addView(logoIcon, params(-1, 58));

        TextView brand = text("MyStudy", 34, Color.WHITE, true);
        brand.setShadowLayer(dp(4), 0, dp(2), Color.rgb(0, 65, 160));
        root.addView(brand, params(-1, 44));

        TextView tagline = text("Learn  •  Practice  •  Grow", 14, Color.WHITE, false);
        root.addView(tagline, params(-1, 28));

        TextView welcome = text("Welcome Back!", 29, DARK_BLUE, true);
        LinearLayout.LayoutParams welcomeParams = params(-1, 42);
        welcomeParams.topMargin = dp(22);
        root.addView(welcome, welcomeParams);

        TextView subtitle = text("Login to continue your learning journey", 14,
                DARK_BLUE, false);
        root.addView(subtitle, params(-1, 28));

        // Main white login card
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setPadding(dp(18), dp(22), dp(18), dp(20));
        card.setBackground(shape(Color.WHITE, 30, Color.rgb(195, 225, 255), 2));
        card.setElevation(dp(12));

        LinearLayout.LayoutParams cardParams = params(-1, -2);
        cardParams.topMargin = dp(20);
        root.addView(card, cardParams);

        TextView studentLabel = text("🔒  Student Login", 19, DARK_BLUE, true);
        studentLabel.setBackground(gradient(Color.rgb(0, 151, 255), BLUE, 28));
        studentLabel.setTextColor(Color.WHITE);
        studentLabel.setElevation(dp(5));
        card.addView(studentLabel, params(-1, 50));

        TextView emailLabel = text("✉", 20, BLUE, true);
        email = createInput("Enter Email Address",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        addInputRow(card, emailLabel, email);

        TextView lockLabel = text("🔒", 18, BLUE, true);
        password = createInput("Enter Password",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordToggle = text("Show", 12, BLUE, true);
        passwordToggle.setPadding(dp(8), 0, dp(4), 0);
        passwordToggle.setOnClickListener(v -> togglePassword());
        addPasswordRow(card, lockLabel, password, passwordToggle);

        LinearLayout options = new LinearLayout(this);
        options.setOrientation(LinearLayout.HORIZONTAL);
        options.setGravity(Gravity.CENTER_VERTICAL);
        options.setPadding(dp(2), dp(8), dp(2), dp(4));

        CheckBox remember = new CheckBox(this);
        remember.setText("Remember Me");
        remember.setTextColor(DARK_BLUE);
        remember.setTextSize(13);
        remember.setButtonTintList(android.content.res.ColorStateList.valueOf(BLUE));
        options.addView(remember, new LinearLayout.LayoutParams(0, dp(42), 1));

        TextView forgot = text("Forgot Password?", 13, BLUE, true);
        forgot.setPadding(dp(6), dp(8), 0, dp(8));
        options.addView(forgot, params(-2, 42));
        forgot.setOnClickListener(v -> sendPasswordReset());
        card.addView(options, params(-1, 48));

        loginButton = text("➜  Login", 19, Color.WHITE, true);
        loginButton.setBackground(gradient(Color.rgb(0, 166, 255), Color.rgb(0, 70, 245), 30));
        loginButton.setElevation(dp(7));
        LinearLayout.LayoutParams loginParams = params(-1, 56);
        loginParams.topMargin = dp(8);
        card.addView(loginButton, loginParams);
        loginButton.setOnClickListener(v -> loginUser());
        addTouchEffect(loginButton);

        TextView divider = text("────────────  OR  ────────────", 13,
                Color.rgb(125, 150, 190), true);
        LinearLayout.LayoutParams dividerParams = params(-1, 42);
        dividerParams.topMargin = dp(7);
        card.addView(divider, dividerParams);

        TextView admin = text("🛡  Admin Login", 16, DARK_BLUE, true);
        admin.setBackground(shape(Color.rgb(255, 247, 224), 26,
                Color.rgb(255, 205, 90), 2));
        card.addView(admin, params(-1, 50));
        admin.setOnClickListener(v -> openScreen(AdminLoginActivity.class));
        addTouchEffect(admin);

        TextView register = text("New Student?  Register Here", 15, BLUE, true);
        LinearLayout.LayoutParams registerParams = params(-1, 48);
        registerParams.topMargin = dp(10);
        card.addView(register, registerParams);
        register.setOnClickListener(v -> openScreen(RegisterActivity.class));
        addTouchEffect(register);

        TextView footer = text("Happy Learning!  ⭐", 15, Color.WHITE, true);
        LinearLayout.LayoutParams footerParams = params(-1, 42);
        footerParams.topMargin = dp(14);
        root.addView(footer, footerParams);

        scroll.addView(root);
        setContentView(scroll);

        // Gentle entrance animation for the complete screen.
        root.setAlpha(0f);
        root.setTranslationY(dp(22));
        root.animate().alpha(1f).translationY(0f).setDuration(650).start();
    }

    private EditText createInput(String hint, int inputType) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setTextSize(15);
        input.setSingleLine(true);
        input.setInputType(inputType);
        input.setTextColor(DARK_BLUE);
        input.setHintTextColor(Color.rgb(125, 145, 180));
        input.setPadding(dp(12), dp(8), dp(10), dp(8));
        input.setBackgroundColor(Color.TRANSPARENT);
        input.setSelectAllOnFocus(false);
        return input;
    }

    private void addInputRow(LinearLayout parent, TextView icon, EditText field) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12), 0, dp(12), 0);
        row.setBackground(shape(Color.rgb(248, 252, 255), 28,
                Color.rgb(175, 211, 255), 1));

        row.addView(icon, params(34, 52));
        row.addView(field, new LinearLayout.LayoutParams(0, dp(52), 1));

        LinearLayout.LayoutParams rowParams = params(-1, 56);
        rowParams.topMargin = dp(22);
        parent.addView(row, rowParams);
    }

    private void addPasswordRow(LinearLayout parent, TextView icon,
                                EditText field, TextView toggle) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12), 0, dp(10), 0);
        row.setBackground(shape(Color.rgb(248, 252, 255), 28,
                Color.rgb(175, 211, 255), 1));

        row.addView(icon, params(34, 52));
        row.addView(field, new LinearLayout.LayoutParams(0, dp(52), 1));
        row.addView(toggle, params(-2, 52));

        LinearLayout.LayoutParams rowParams = params(-1, 56);
        rowParams.topMargin = dp(14);
        parent.addView(row, rowParams);
    }

    private void togglePassword() {
        passwordVisible = !passwordVisible;
        int cursor = password.getSelectionStart();
        if (passwordVisible) {
            password.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggle.setText("Hide");
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggle.setText("Show");
        }
        password.setSelection(Math.max(0, cursor));
    }

    private void addTouchEffect(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(90).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                    break;
            }
            return false;
        });
    }

    private void openScreen(Class<?> target) {
        if (screenOpening || isFinishing()) return;
        screenOpening = true;
        startActivity(new Intent(LoginActivity.this, target));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        screenOpening = false;
    }

    private void loginUser() {
        if (loginInProgress) return;

        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString();

        email.setError(null);
        password.setError(null);

        if (emailText.isEmpty()) {
            email.setError("Enter your email");
            email.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
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
        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");
        loginButton.setAlpha(0.75f);

        auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, task -> {
                    if (isFinishing() || isDestroyed()) return;

                    if (!task.isSuccessful()) {
                        String message = task.getException() != null
                                ? task.getException().getMessage() : "Login failed";
                        resetLoginButton();
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) {
                        resetLoginButton();
                        Toast.makeText(this, "User data could not be loaded.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    loadUserProfile(user.getUid());
                });
    }

    private void loadUserProfile(String uid) {
        if (uid == null || uid.trim().isEmpty()) {
            auth.signOut();
            resetLoginButton();
            Toast.makeText(this, "Invalid user account.", Toast.LENGTH_LONG).show();
            return;
        }

        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (isFinishing() || isDestroyed()) return;

                    if (!document.exists()) {
                        auth.signOut();
                        resetLoginButton();
                        Toast.makeText(this, "Profile not found.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String role = document.getString("role");
                    String name = document.getString("name");
                    String selectedClass = document.getString("class");
                    String selectedMedium = document.getString("medium");
                    String selectedLanguage = document.getString("language");

                    if (role == null || role.trim().isEmpty()) role = "student";
                    if (name == null || name.trim().isEmpty()) name = "Student";
                    if (selectedClass == null) selectedClass = "";
                    if (selectedMedium == null) selectedMedium = "";
                    if (selectedLanguage == null || selectedLanguage.trim().isEmpty()) {
                        selectedLanguage = "English";
                    }

                    if (role.equals("main_admin") || role.equals("admin")) {
                        auth.signOut();
                        resetLoginButton();
                        Toast.makeText(this,
                                "Please use Admin Login for admin access.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    Toast.makeText(this, "Welcome, " + name + "! 👋",
                            Toast.LENGTH_SHORT).show();

                    screenOpening = true;
                    Intent intent = new Intent(LoginActivity.this,
                            StudentDashboardActivity.class);
                    intent.putExtra("studentName", name);
                    intent.putExtra("studentClass", selectedClass);
                    intent.putExtra("studentMedium", selectedMedium);
                    intent.putExtra("studentLanguage", selectedLanguage);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    finish();
                })
                .addOnFailureListener(e -> {
                    if (isFinishing() || isDestroyed()) return;
                    auth.signOut();
                    resetLoginButton();
                    String error = e.getMessage() != null
                            ? e.getMessage() : "Could not load profile.";
                    Toast.makeText(this, "Could not load profile: " + error,
                            Toast.LENGTH_LONG).show();
                });
    }

    private void sendPasswordReset() {
        if (loginInProgress) return;

        String emailText = email.getText().toString().trim();
        email.setError(null);

        if (emailText.isEmpty()) {
            email.setError("Enter your email first");
            email.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Enter a valid email");
            email.requestFocus();
            return;
        }

        hideKeyboard();
        Toast.makeText(this, "Sending reset email...", Toast.LENGTH_SHORT).show();

        auth.sendPasswordResetEmail(emailText)
                .addOnCompleteListener(this, task -> {
                    if (isFinishing() || isDestroyed()) return;

                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Password reset email sent. Check your inbox.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        String message = task.getException() != null
                                ? task.getException().getMessage()
                                : "Could not send reset email";
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void resetLoginButton() {
        loginInProgress = false;
        if (loginButton != null && !isFinishing() && !isDestroyed()) {
            loginButton.setEnabled(true);
            loginButton.setText("➜  Login");
            loginButton.setAlpha(1f);
        }
    }

    private void hideKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus == null) return;

        InputMethodManager manager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        loginInProgress = false;
        screenOpening = false;
        super.onDestroy();
    }
}
