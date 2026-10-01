package com.mystudy.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        createUi();
    }

    private void createUi() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(40, 40, 40, 40);

        TextView title = new TextView(this);
        title.setText("MyStudy Admin");
        title.setTextSize(30);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText("Secure Administrator Login");
        subtitle.setTextSize(16);
        subtitle.setGravity(Gravity.CENTER);

        emailInput = new EditText(this);
        emailInput.setHint("Admin Email");
        emailInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        passwordInput = new EditText(this);
        passwordInput.setHint("Password");
        passwordInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        loginButton = new Button(this);
        loginButton.setText("Login as Admin");

        TextView forgotPassword = new TextView(this);
        forgotPassword.setText("Forgot Password?");
        forgotPassword.setTextSize(15);
        forgotPassword.setGravity(Gravity.CENTER);
        forgotPassword.setPadding(0, 25, 0, 25);

        root.addView(title);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        subtitleParams.topMargin = 10;
        root.addView(subtitle, subtitleParams);

        LinearLayout.LayoutParams inputParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        inputParams.topMargin = 25;
        root.addView(emailInput, inputParams);

        inputParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        inputParams.topMargin = 15;
        root.addView(passwordInput, inputParams);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        buttonParams.topMargin = 25;
        root.addView(loginButton, buttonParams);

        root.addView(forgotPassword);

        setContentView(root);

        loginButton.setOnClickListener(v -> loginAdmin());

        forgotPassword.setOnClickListener(v -> resetPassword());
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
        loginButton.setText("Checking...");

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (!task.isSuccessful()) {
                        loginButton.setEnabled(true);
                        loginButton.setText("Login as Admin");

                        Toast.makeText(
                                this,
                                getLoginError(task.getException()),
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    if (auth.getCurrentUser() == null) {
                        loginButton.setEnabled(true);
                        loginButton.setText("Login as Admin");

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

                        loginButton.setEnabled(true);
                        loginButton.setText("Login as Admin");

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

                        loginButton.setEnabled(true);
                        loginButton.setText("Login as Admin");

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

                        Intent intent =
                                new Intent(
                                        AdminLoginActivity.this,
                                        AdminDashboardActivity.class
                        );

                        intent.putExtra("userRole", role);
                        intent.putExtra("adminName", name);

                        startActivity(intent);

                        finish();

                    } else {

                        auth.signOut();

                        loginButton.setEnabled(true);
                        loginButton.setText("Login as Admin");

                        Toast.makeText(
                                this,
                                "Access denied. Admin account required.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    auth.signOut();

                    loginButton.setEnabled(true);
                    loginButton.setText("Login as Admin");

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
}
