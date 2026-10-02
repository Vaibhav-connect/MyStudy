package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
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
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private final int backgroundColor = Color.rgb(248, 250, 252);
    private final int primaryColor = Color.rgb(79, 70, 229);
    private final int textPrimary = Color.rgb(17, 24, 39);
    private final int textSecondary = Color.rgb(100, 116, 139);

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

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(28, 50, 28, 40);
        root.setBackgroundColor(backgroundColor);

        TextView icon = new TextView(this);
        icon.setText("👨‍👩‍👧");
        icon.setTextSize(52);
        icon.setGravity(Gravity.CENTER);

        root.addView(
                icon,
                new LinearLayout.LayoutParams(
                        -1,
                        90
                )
        );

        TextView title = new TextView(this);
        title.setText("Parent Area");
        title.setTextSize(28);
        title.setTextColor(textPrimary);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        TextView subtitle = new TextView(this);
        subtitle.setText(
                "Enter the Parent PIN to view your child's progress."
        );
        subtitle.setTextSize(15);
        subtitle.setTextColor(textSecondary);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(20, 10, 20, 30);

        root.addView(
                subtitle,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 28, 24, 28);

        GradientDrawable cardBackground =
                new GradientDrawable();

        cardBackground.setColor(Color.WHITE);
        cardBackground.setCornerRadius(30);

        card.setBackground(cardBackground);
        card.setElevation(6);

        TextView pinLabel = new TextView(this);
        pinLabel.setText("Parent PIN");
        pinLabel.setTextSize(16);
        pinLabel.setTextColor(textPrimary);
        pinLabel.setTypeface(null, Typeface.BOLD);

        card.addView(
                pinLabel,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        pinInput = new EditText(this);
        pinInput.setHint("Enter PIN");
        pinInput.setTextSize(18);
        pinInput.setTextColor(textPrimary);
        pinInput.setHintTextColor(textSecondary);
        pinInput.setGravity(Gravity.CENTER);
        pinInput.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_VARIATION_PASSWORD
        );
        pinInput.setPadding(16, 14, 16, 14);

        GradientDrawable inputBackground =
                new GradientDrawable();

        inputBackground.setColor(
                Color.rgb(248, 250, 252)
        );

        inputBackground.setCornerRadius(18);
        inputBackground.setStroke(
                2,
                Color.rgb(226, 232, 240)
        );

        pinInput.setBackground(inputBackground);

        LinearLayout.LayoutParams inputParams =
                new LinearLayout.LayoutParams(
                        -1,
                        60
                );

        inputParams.setMargins(0, 12, 0, 20);

        card.addView(
                pinInput,
                inputParams
        );

        Button verifyButton = new Button(this);
        verifyButton.setText("Unlock Parent Area");
        verifyButton.setTextSize(16);
        verifyButton.setTextColor(Color.WHITE);
        verifyButton.setTypeface(null, Typeface.BOLD);
        verifyButton.setAllCaps(false);

        GradientDrawable buttonBackground =
                new GradientDrawable();

        buttonBackground.setColor(primaryColor);
        buttonBackground.setCornerRadius(20);

        verifyButton.setBackground(buttonBackground);
        verifyButton.setElevation(3);

        card.addView(
                verifyButton,
                new LinearLayout.LayoutParams(
                        -1,
                        58
                )
        );

        root.addView(
                card,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        TextView info = new TextView(this);
        info.setText(
                "🔒 Your Parent Area is protected."
        );
        info.setTextSize(14);
        info.setTextColor(textSecondary);
        info.setGravity(Gravity.CENTER);
        info.setPadding(0, 24, 0, 0);

        root.addView(
                info,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        scrollView.addView(root);
        setContentView(scrollView);

        verifyButton.setOnClickListener(
                view -> verifyParentPin()
        );

        addPressAnimation(verifyButton);
    }

    private void verifyParentPin() {

        String enteredPin =
                pinInput.getText()
                        .toString()
                        .trim();

        if (enteredPin.isEmpty()) {

            pinInput.setError(
                    "Enter Parent PIN"
            );

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

        String userId =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (!document.exists()) {

                                Toast.makeText(
                                        ParentPinActivity.this,
                                        "User profile not found.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            String savedPin =
                                    document.getString(
                                            "parentPin"
                                    );

                            if (
                                    savedPin != null &&
                                    savedPin.equals(enteredPin)
                            ) {

                                Intent intent = new Intent();

                                intent.setClassName(
                                        ParentPinActivity.this,
                                        "com.mystudy.app.ParentDashboardActivity"
                                );

                                startActivity(intent);
                                finish();

                            } else {

                                pinInput.setText("");

                                Toast.makeText(
                                        ParentPinActivity.this,
                                        "Incorrect Parent PIN.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .addOnFailureListener(
                        error -> {

                            Toast.makeText(
                                    ParentPinActivity.this,
                                    "Unable to verify PIN.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }

    private void addPressAnimation(
            Button button
    ) {

        button.setOnTouchListener(
                (view, event) -> {

                    if (
                            event.getAction() ==
                                    MotionEvent.ACTION_DOWN
                    ) {

                        view.animate()
                                .scaleX(0.97f)
                                .scaleY(0.97f)
                                .setDuration(100)
                                .start();

                    } else if (
                            event.getAction() ==
                                    MotionEvent.ACTION_UP ||
                            event.getAction() ==
                                    MotionEvent.ACTION_CANCEL
                    ) {

                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                    }

                    return false;
                }
        );
    }
}
