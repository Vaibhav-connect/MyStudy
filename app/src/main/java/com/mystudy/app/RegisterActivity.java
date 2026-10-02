package com.mystudy.app;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private EditText parentPinInput;

    private Spinner classSpinner;
    private Spinner mediumSpinner;
    private Spinner languageSpinner;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        createRegisterScreen();
    }

    private void createRegisterScreen() {

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(45, 40, 45, 40);

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
        logo.setText("📚");
        logo.setTextSize(50);
        logo.setGravity(Gravity.CENTER);
        root.addView(logo);

        TextView title = new TextView(this);
        title.setText("Create Account ✨");
        title.setTextSize(30);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        titleParams.topMargin = 10;
        root.addView(title, titleParams);

        TextView subtitle = new TextView(this);
        subtitle.setText("Start your learning journey");
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

        nameInput = createInput(
                "Student Name",
                InputType.TYPE_CLASS_TEXT
        );

        addInput(root, nameInput, 35);

        emailInput = createInput(
                "Email",
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        addInput(root, emailInput, 12);

        passwordInput = createInput(
                "Password",
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        addInput(root, passwordInput, 12);

        confirmPasswordInput = createInput(
                "Confirm Password",
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        addInput(root, confirmPasswordInput, 12);

        parentPinInput = createInput(
                "Parent PIN (4 digits)",
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_VARIATION_PASSWORD
        );

        addInput(root, parentPinInput, 12);

        classSpinner = createSpinner(
                new String[]{
                        "Select Class",
                        "Class 1",
                        "Class 2",
                        "Class 3",
                        "Class 4",
                        "Class 5",
                        "Class 6",
                        "Class 7",
                        "Class 8",
                        "Class 9",
                        "Class 10"
                }
        );

        root.addView(
                classSpinner,
                createSpinnerParams(12)
        );

        mediumSpinner = createSpinner(
                new String[]{
                        "Select Medium",
                        "English",
                        "Semi-English",
                        "Marathi"
                }
        );

        root.addView(
                mediumSpinner,
                createSpinnerParams(12)
        );

        languageSpinner = createSpinner(
                new String[]{
                        "Select App Language",
                        "English",
                        "Marathi",
                        "Hindi"
                }
        );

        root.addView(
                languageSpinner,
                createSpinnerParams(12)
        );

        Button registerButton = new Button(this);

        registerButton.setText("Create Account");
        registerButton.setTextSize(18);
        registerButton.setAllCaps(false);
        registerButton.setTextColor(Color.rgb(79, 70, 229));

        GradientDrawable buttonBackground =
                new GradientDrawable();

        buttonBackground.setColor(Color.WHITE);
        buttonBackground.setCornerRadius(40);

        registerButton.setBackground(buttonBackground);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        650,
                        70
                );

        buttonParams.topMargin = 20;

        root.addView(registerButton, buttonParams);

        TextView loginText = new TextView(this);

        loginText.setText("Already have an account?  Login");
        loginText.setTextSize(16);
        loginText.setTextColor(Color.WHITE);
        loginText.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams loginParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        loginParams.topMargin = 18;

        root.addView(loginText, loginParams);

        loginText.setOnClickListener(v -> finish());

        registerButton.setOnClickListener(v -> registerUser());

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

    private Spinner createSpinner(String[] items) {

        Spinner spinner = new Spinner(this);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        items
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);

        return spinner;
    }

    private LinearLayout.LayoutParams createSpinnerParams(
            int topMargin
    ) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        650,
                        60
                );

        params.topMargin = topMargin;

        return params;
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

    private void registerUser() {

        String name =
                nameInput.getText().toString().trim();

        String email =
                emailInput.getText().toString().trim();

        String password =
                passwordInput.getText().toString();

        String confirmPassword =
                confirmPasswordInput.getText().toString();

        String parentPin =
                parentPinInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Enter student name");
            nameInput.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailInput.setError("Enter email");
            emailInput.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            emailInput.setError("Enter a valid email");
            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Enter password");
            passwordInput.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError(
                    "Password must be at least 6 characters"
            );
            passwordInput.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.setError(
                    "Confirm your password"
            );
            confirmPasswordInput.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError(
                    "Passwords do not match"
            );
            confirmPasswordInput.requestFocus();
            return;
        }

        if (parentPin.isEmpty()) {
            parentPinInput.setError(
                    "Enter a 4-digit Parent PIN"
            );
            parentPinInput.requestFocus();
            return;
        }

        if (!parentPin.matches("\\d{4}")) {
            parentPinInput.setError(
                    "Parent PIN must contain exactly 4 digits"
            );
            parentPinInput.requestFocus();
            return;
        }

        if (classSpinner.getSelectedItemPosition() == 0) {

            Toast.makeText(
                    this,
                    "Please select your class",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (mediumSpinner.getSelectedItemPosition() == 0) {

            Toast.makeText(
                    this,
                    "Please select your medium",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (languageSpinner.getSelectedItemPosition() == 0) {

            Toast.makeText(
                    this,
                    "Please select your app language",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String selectedClass =
                classSpinner.getSelectedItem().toString();

        String selectedMedium =
                mediumSpinner.getSelectedItem().toString();

        String selectedLanguage =
                languageSpinner.getSelectedItem().toString();

        Toast.makeText(
                this,
                "Creating your account...",
                Toast.LENGTH_SHORT
        ).show();

        auth.createUserWithEmailAndPassword(
                email,
                password
        ).addOnCompleteListener(this, task -> {

            if (!task.isSuccessful()) {

                String message =
                        task.getException() != null
                                ? task.getException().getMessage()
                                : "Registration failed";

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
                        "Account created, but user data could not be loaded.",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            String uid = user.getUid();

            Map<String, Object> userData =
                    new HashMap<>();

            userData.put("name", name);
            userData.put("email", email);

            userData.put("class", selectedClass);
            userData.put("medium", selectedMedium);
            userData.put("language", selectedLanguage);

            userData.put("parentPin", parentPin);

            userData.put("role", "student");

            userData.put("points", 0);
            userData.put("streak", 0);

            userData.put(
                    "createdAt",
                    com.google.firebase.firestore.FieldValue.serverTimestamp()
            );

            db.collection("users")
                    .document(uid)
                    .set(userData)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                this,
                                "Account created successfully! 🎉",
                                Toast.LENGTH_LONG
                        ).show();

                        auth.signOut();

                        finish();
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                this,
                                "Account created, but profile saving failed: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });
    }
}
