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

public class AdminLoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createAdminLoginScreen();
    }

    private void createAdminLoginScreen() {

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(45, 45, 45, 45);

        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.rgb(30, 27, 75),
                        Color.rgb(79, 70, 229),
                        Color.rgb(124, 58, 237)
                }
        );

        root.setBackground(background);

        TextView icon = new TextView(this);

        icon.setText("👑");
        icon.setTextSize(58);
        icon.setGravity(Gravity.CENTER);

        root.addView(icon);

        TextView title = new TextView(this);

        title.setText("Admin Login");
        title.setTextSize(30);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        titleParams.topMargin = 12;

        root.addView(title, titleParams);

        TextView subtitle = new TextView(this);

        subtitle.setText("Manage MyStudy securely");
        subtitle.setTextSize(16);
        subtitle.setTextColor(Color.WHITE);
        subtitle.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        subtitleParams.topMargin = 6;

        root.addView(subtitle, subtitleParams);

        emailInput = createInput(
                "Admin Email",
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        addInput(root, emailInput, 45);

        passwordInput = createInput(
                "Admin Password",
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        addInput(root, passwordInput, 15);

        Button loginButton = new Button(this);

        loginButton.setText("Admin Login");
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

        forgotPassword.setText("Forgot Admin Password?");
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

        TextView backText = new TextView(this);

        backText.setText("← Back to Student Login");
        backText.setTextSize(16);
        backText.setTextColor(Color.WHITE);
        backText.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams backParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        backParams.topMargin = 25;

        root.addView(backText, backParams);

        loginButton.setOnClickListener(v -> validateAdminLogin());

        forgotPassword.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Admin password recovery will use email verification.",
                    Toast.LENGTH_SHORT
            ).show();
        });

        backText.setOnClickListener(v -> finish());

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

    private void validateAdminLogin() {

        String email =
                emailInput.getText().toString().trim();

        String password =
                passwordInput.getText().toString();

        if (email.isEmpty()) {
            emailInput.setError("Enter admin email");
            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Enter admin password");
            passwordInput.requestFocus();
            return;
        }

        Toast.makeText(
                this,
                "Admin authentication will be connected with Firebase.",
                Toast.LENGTH_SHORT
        ).show();
    }
}
