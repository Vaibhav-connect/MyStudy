package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
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

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        createLoginScreen();
    }

    private void createLoginScreen() {

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(45, 45, 45, 45);

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

        root.addView(logo);

        TextView title = new TextView(this);
        title.setText("Welcome Back! 👋");
        title.setTextSize(30);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        titleParams.topMargin = 15;
        root.addView(title, titleParams);

        TextView subtitle = new TextView(this);
        subtitle.setText("Continue your learning journey");
        subtitle.setTextSize(16);
        subtitle.setTextColor(Color.WHITE);
        subtitle.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        subtitleParams.topMargin = 8;
        root.addView(subtitle, subtitleParams);

        email = createInput(
                "Email",
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        addInput(root, email, 45);

        password = createInput(
                "Password",
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        addInput(root, password, 15);

        Button loginButton = new Button(this);

        loginButton.setText("Login");
        loginButton.setTextSize(18);
        loginButton.setAllCaps(false);
        loginButton.setTextColor(Color.rgb(79, 70, 229));

        GradientDrawable loginBackground =
                new GradientDrawable();

        loginBackground.setColor(Color.WHITE);
        loginBackground.setCornerRadius(40);

        loginButton.setBackground(loginBackground);

        LinearLayout.LayoutParams loginParams =
                new LinearLayout.LayoutParams(
                        650,
                        70
                );

        loginParams.topMargin = 25;
        root.addView(loginButton, loginParams);

        TextView forgotPassword = new TextView(this);

        forgotPassword.setText("Forgot Password?");
        forgotPassword.setTextSize(15);
        forgotPassword.setTextColor(Color.WHITE);
        forgotPassword.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams forgotParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        forgotParams.topMargin = 18;
        root.addView(forgotPassword, forgotParams);

        TextView registerText = new TextView(this);

        registerText.setText("New to MyStudy?  Register");
        registerText.setTextSize(16);
        registerText.setTextColor(Color.WHITE);
        registerText.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams registerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        registerParams.topMargin = 20;
        root.addView(registerText, registerParams);

        TextView adminLogin = new TextView(this);

        adminLogin.setText("👑  Admin Login");
        adminLogin.setTextSize(16);
        adminLogin.setTextColor(Color.WHITE);
        adminLogin.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams adminParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        adminParams.topMargin = 25;
        root.addView(adminLogin, adminParams);

        loginButton.setOnClickListener(v -> loginUser());

        registerText.setOnClickListener(v -> {

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
        });

        forgotPassword.setOnClickListener(v -> sendPasswordReset());

        adminLogin.setOnClickListener(v -> {

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
        input.setPadding(30, 10, 30, 10);

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(35);

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
                        650,
                        65
                );

        params.topMargin = topMargin;

        root.addView(input, params);
    }

    private void loginUser() {

        String emailText =
                email.getText().toString().trim();

        String passwordText =
                password.getText().toString();

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

        Toast.makeText(
                this,
                "Signing in...",
                Toast.LENGTH_SHORT
        ).show();

        auth.signInWithEmailAndPassword(
                emailText,
                passwordText
        ).addOnCompleteListener(this, task -> {

            if (!task.isSuccessful()) {

                String message =
                        task.getException() != null
                                ? task.getException().getMessage()
                                : "Login failed";

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

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {

                        Toast.makeText(
                                this,
                                "Profile not found.",
                                Toast.LENGTH_LONG
                        ).show();

                        auth.signOut();
                        return;
                    }

                    String name =
                            document.getString("name");

                    if (name == null || name.isEmpty()) {
                        name = "Student";
                    }

                    Toast.makeText(
                            this,
                            "Welcome, " + name + "! 👋",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent =
                            new Intent(
                                    LoginActivity.this,
                                    MainActivity.class
                            );

                    intent.putExtra("studentName", name);

                    startActivity(intent);

                    overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                    );

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Could not load profile: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void sendPasswordReset() {

        String emailText =
                email.getText().toString().trim();

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

        auth.sendPasswordResetEmail(emailText)
                .addOnCompleteListener(task -> {

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
}
