package com.mystudy.app;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        EditText email = new EditText(this);

        email.setHint("Email");
        email.setTextSize(16);
        email.setSingleLine(true);
        email.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        email.setPadding(30, 10, 30, 10);

        GradientDrawable inputBackground = new GradientDrawable();

        inputBackground.setColor(Color.WHITE);
        inputBackground.setCornerRadius(35);

        email.setBackground(inputBackground);

        LinearLayout.LayoutParams emailParams =
                new LinearLayout.LayoutParams(
                        650,
                        65
                );

        emailParams.topMargin = 45;

        root.addView(email, emailParams);

        EditText password = new EditText(this);

        password.setHint("Password");
        password.setTextSize(16);
        password.setSingleLine(true);
        password.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        password.setPadding(30, 10, 30, 10);
        password.setBackground(inputBackground);

        LinearLayout.LayoutParams passwordParams =
                new LinearLayout.LayoutParams(
                        650,
                        65
                );

        passwordParams.topMargin = 15;

        root.addView(password, passwordParams);

        Button loginButton = new Button(this);

        loginButton.setText("Login");
        loginButton.setTextSize(18);
        loginButton.setAllCaps(false);

        GradientDrawable loginBackground =
                new GradientDrawable();

        loginBackground.setColor(Color.WHITE);
        loginBackground.setCornerRadius(40);

        loginButton.setBackground(loginBackground);
        loginButton.setTextColor(Color.rgb(79, 70, 229));

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

        setContentView(root);
    }
}
