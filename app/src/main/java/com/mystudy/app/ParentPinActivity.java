package com.mystudy.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ParentPinActivity extends AppCompatActivity {

    private EditText pinInput;
    private Button verifyButton;
    private TextView visibilityButton;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private boolean pinVisible = false;
    private boolean verifying = false;

    private final int BLUE = Color.rgb(37, 99, 235);
    private final int DARK = Color.rgb(17, 24, 39);
    private final int MUTED = Color.rgb(100, 116, 139);
    private final int BG = Color.rgb(248, 250, 252);
    private final int YELLOW = Color.rgb(250, 204, 21);
    private final int BORDER = Color.rgb(226, 232, 240);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        createUI();
    }

    private void createUI() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(24), dp(25), dp(24), dp(30));
        root.setBackgroundColor(BG);

        TextView brand = new TextView(this);
        brand.setText("📘 MyStudy");
        brand.setTextSize(21);
        brand.setTextColor(BLUE);
        brand.setTypeface(null, Typeface.BOLD);
        brand.setGravity(Gravity.CENTER);

        root.addView(
                brand,
                new LinearLayout.LayoutParams(-1, dp(45))
        );

        TextView icon = new TextView(this);
        icon.setText("👨‍👩‍👧");
        icon.setTextSize(55);
        icon.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(-1, dp(105));

        iconParams.topMargin = dp(22);
        root.addView(icon, iconParams);

        TextView title = new TextView(this);
        title.setText("Parent Area");
        title.setTextSize(28);
        title.setTextColor(DARK);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText(
                "Stay connected with your child's learning journey."
        );
        subtitle.setTextSize(15);
        subtitle.setTextColor(MUTED);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(dp(10), dp(10), dp(10), dp(25));

        root.addView(subtitle);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(22), dp(25), dp(22), dp(25));
        card.setBackground(rounded(Color.WHITE, 24, BORDER));
        card.setElevation(dp(4));

        TextView cardTitle = new TextView(this);
        cardTitle.setText("🔐  Parent Verification");
        cardTitle.setTextSize(19);
        cardTitle.setTextColor(DARK);
        cardTitle.setTypeface(null, Typeface.BOLD);

        card.addView(cardTitle);

        TextView description = new TextView(this);
        description.setText(
                "Enter your Parent PIN to securely access your child's progress."
        );
        description.setTextSize(14);
        description.setTextColor(MUTED);
        description.setPadding(0, dp(9), 0, dp(22));

        card.addView(description);

        TextView pinLabel = new TextView(this);
        pinLabel.setText("Parent PIN");
        pinLabel.setTextSize(15);
        pinLabel.setTextColor(DARK);
        pinLabel.setTypeface(null, Typeface.BOLD);

        card.addView(pinLabel);

        LinearLayout inputRow = new LinearLayout(this);
        inputRow.setOrientation(LinearLayout.HORIZONTAL);
        inputRow.setGravity(Gravity.CENTER_VERTICAL);
        inputRow.setPadding(dp(12), 0, dp(12), 0);
        inputRow.setBackground(rounded(BG, 14, BORDER));

        pinInput = new EditText(this);
        pinInput.setHint("Enter your PIN");
        pinInput.setTextSize(17);
        pinInput.setTextColor(DARK);
        pinInput.setHintTextColor(MUTED);
        pinInput.setSingleLine(true);
        pinInput.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_VARIATION_PASSWORD
        );
        pinInput.setBackgroundColor(Color.TRANSPARENT);
        pinInput.setPadding(dp(5), dp(12), dp(5), dp(12));

        inputRow.addView(
                pinInput,
                new LinearLayout.LayoutParams(0, dp(55), 1)
        );

        visibilityButton = new TextView(this);
        visibilityButton.setText("SHOW");
        visibilityButton.setTextSize(12);
        visibilityButton.setTextColor(BLUE);
        visibilityButton.setTypeface(null, Typeface.BOLD);
        visibilityButton.setGravity(Gravity.CENTER);
        visibilityButton.setPadding(dp(10), 0, dp(5), 0);

        inputRow.addView(
                visibilityButton,
                new LinearLayout.LayoutParams(-2, dp(55))
        );

        LinearLayout.LayoutParams inputParams =
                new LinearLayout.LayoutParams(-1, -2);

        inputParams.topMargin = dp(10);
        inputParams.bottomMargin = dp(22);

        card.addView(inputRow, inputParams);

        verifyButton = new Button(this);
        verifyButton.setText("Unlock Parent Area");
        verifyButton.setTextSize(16);
        verifyButton.setTextColor(Color.WHITE);
        verifyButton.setTypeface(null, Typeface.BOLD);
        verifyButton.setAllCaps(false);
        verifyButton.setBackground(rounded(BLUE, 15, BLUE));
        verifyButton.setElevation(dp(3));

        card.addView(
                verifyButton,
                new LinearLayout.LayoutParams(-1, dp(55))
        );

        root.addView(
                card,
                new LinearLayout.LayoutParams(-1, -2)
        );

        TextView securityInfo = new TextView(this);
        securityInfo.setText(
                "🔒  Your Parent Area is protected."
        );
        securityInfo.setTextSize(14);
        securityInfo.setTextColor(MUTED);
        securityInfo.setGravity(Gravity.CENTER);
        securityInfo.setPadding(0, dp(25), 0, dp(12));

        root.addView(securityInfo);

        TextView footer = new TextView(this);
        footer.setText("Learn • Practice • Grow");
        footer.setTextSize(13);
        footer.setTextColor(BLUE);
        footer.setGravity(Gravity.CENTER);

        root.addView(footer);

        scrollView.addView(root);
        setContentView(scrollView);

        visibilityButton.setOnClickListener(
                v -> togglePinVisibility()
        );

        verifyButton.setOnClickListener(
                v -> verifyParentPin()
        );

        addPressAnimation(verifyButton);
    }

    private void togglePinVisibility() {

        if (pinVisible) {

            pinInput.setInputType(
                    InputType.TYPE_CLASS_NUMBER |
                            InputType.TYPE_NUMBER_VARIATION_PASSWORD
            );

            visibilityButton.setText("SHOW");
            pinVisible = false;

        } else {

            pinInput.setInputType(
                    InputType.TYPE_CLASS_NUMBER
            );

            visibilityButton.setText("HIDE");
            pinVisible = true;
        }

        pinInput.setSelection(
                pinInput.getText().length()
        );
    }

    private void verifyParentPin() {

        if (verifying) {
            return;
        }

        String enteredPin = pinInput.getText()
                .toString()
                .trim();

        if (enteredPin.isEmpty()) {

            pinInput.setError("Enter Parent PIN");
            pinInput.requestFocus();
            return;
        }

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        hideKeyboard();

        verifying = true;
        verifyButton.setEnabled(false);
        verifyButton.setText("Verifying...");

        String userId = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    verifying = false;
                    verifyButton.setEnabled(true);
                    verifyButton.setText("Unlock Parent Area");

                    if (!document.exists()) {

                        showMessage("User profile not found.");
                        return;
                    }

                    String savedPin = document.getString("parentPin");

                    if (savedPin != null &&
                            savedPin.equals(enteredPin)) {

                        openParentDashboard();

                    } else {

                        pinInput.setText("");
                        pinInput.setError("Incorrect PIN");

                        Toast.makeText(
                                ParentPinActivity.this,
                                "Incorrect Parent PIN.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(error -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    verifying = false;
                    verifyButton.setEnabled(true);
                    verifyButton.setText("Unlock Parent Area");

                    showMessage("Unable to verify PIN. Please try again.");
                });
    }

    private void openParentDashboard() {

        Intent intent = new Intent();

        intent.setClassName(
                ParentPinActivity.this,
                "com.mystudy.app.ParentDashboardActivity"
        );

        startActivity(intent);
        finish();
    }

    private void showMessage(String message) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    private GradientDrawable rounded(
            int color,
            int radius,
            int stroke
    ) {

        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), stroke);

        return drawable;
    }

    private int dp(int value) {

        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    private void hideKeyboard() {

        try {

            InputMethodManager manager =
                    (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE
                    );

            if (manager != null && getCurrentFocus() != null) {

                manager.hideSoftInputFromWindow(
                        getCurrentFocus().getWindowToken(),
                        0
                );
            }

        } catch (Exception ignored) {
        }
    }

    private void addPressAnimation(Button button) {

        button.setOnTouchListener((view, event) -> {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                view.animate()
                        .scaleX(0.97f)
                        .scaleY(0.97f)
                        .setDuration(100)
                        .start();

            } else if (
                    event.getAction() == MotionEvent.ACTION_UP ||
                            event.getAction() == MotionEvent.ACTION_CANCEL
            ) {

                view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start();
            }

            return false;
        });
    }
}
