package com.mystudy.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ParentDashboardActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView studentNameText;
    private TextView classText;
    private TextView pointsText;
    private TextView streakText;
    private TextView accuracyText;
    private TextView lessonsText;
    private TextView quizzesText;

    private final int backgroundColor = Color.rgb(248, 250, 252);
    private final int textPrimary = Color.rgb(17, 24, 39);
    private final int textSecondary = Color.rgb(100, 116, 139);
    private final int primaryColor = Color.rgb(79, 70, 229);
    private final int successColor = Color.rgb(22, 163, 74);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        createUI();
        loadStudentProgress();
    }

    private void createUI() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 24, 20, 30);
        root.setBackgroundColor(backgroundColor);

        TextView title = new TextView(this);
        title.setText("👨‍👩‍👧 Parent Dashboard");
        title.setTextSize(27);
        title.setTextColor(textPrimary);
        title.setTypeface(null, Typeface.BOLD);

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        TextView subtitle = new TextView(this);
        subtitle.setText(
                "Track your child's learning progress"
        );
        subtitle.setTextSize(15);
        subtitle.setTextColor(textSecondary);
        subtitle.setPadding(0, 7, 0, 22);

        root.addView(
                subtitle,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        LinearLayout studentCard =
                createCard();

        TextView studentLabel =
                createLabel("Student");

        studentCard.addView(studentLabel);

        studentNameText =
                createValueText("Loading...");

        studentCard.addView(studentNameText);

        classText =
                createSecondaryText("Class: Loading...");

        studentCard.addView(classText);

        root.addView(
                studentCard,
                createCardParams(14)
        );

        TextView progressTitle =
                createSectionTitle("Learning Overview");

        root.addView(
                progressTitle,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        LinearLayout statsRow =
                new LinearLayout(this);

        statsRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        pointsText =
                createStatCard(
                        statsRow,
                        "⭐",
                        "Points",
                        "0"
                );

        streakText =
                createStatCard(
                        statsRow,
                        "🔥",
                        "Streak",
                        "0"
                );

        root.addView(
                statsRow,
                createCardParams(14)
        );

        LinearLayout secondStatsRow =
                new LinearLayout(this);

        secondStatsRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        accuracyText =
                createStatCard(
                        secondStatsRow,
                        "🎯",
                        "Accuracy",
                        "0%"
                );

        lessonsText =
                createStatCard(
                        secondStatsRow,
                        "📖",
                        "Lessons",
                        "0"
                );

        root.addView(
                secondStatsRow,
                createCardParams(14)
        );

        LinearLayout quizCard =
                createCard();

        TextView quizIcon =
                new TextView(this);

        quizIcon.setText("📝");
        quizIcon.setTextSize(30);
        quizIcon.setGravity(Gravity.CENTER);

        quizCard.addView(
                quizIcon,
                new LinearLayout.LayoutParams(
                        55,
                        55
                )
        );

        LinearLayout quizInfo =
                new LinearLayout(this);

        quizInfo.setOrientation(
                LinearLayout.VERTICAL
        );

        quizInfo.setPadding(
                14,
                0,
                0,
                0
        );

        TextView quizTitle =
                createLabel("Quiz Activity");

        quizInfo.addView(quizTitle);

        quizzesText =
                createSecondaryText(
                        "Quiz attempts: 0"
                );

        quizInfo.addView(quizzesText);

        quizCard.addView(
                quizInfo,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        root.addView(
                quizCard,
                createCardParams(14)
        );

        TextView privacy =
                new TextView(this);

        privacy.setText(
                "🔒 Parent Area • Progress is linked to the logged-in student account."
        );

        privacy.setTextSize(13);
        privacy.setTextColor(textSecondary);
        privacy.setGravity(Gravity.CENTER);
        privacy.setPadding(10, 18, 10, 10);

        root.addView(
                privacy,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        TextView logout =
                new TextView(this);

        logout.setText("Logout");
        logout.setTextSize(15);
        logout.setTextColor(primaryColor);
        logout.setGravity(Gravity.CENTER);
        logout.setTypeface(null, Typeface.BOLD);
        logout.setPadding(10, 16, 10, 16);

        root.addView(
                logout,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        scrollView.addView(root);

        setContentView(scrollView);

        logout.setOnClickListener(
                view -> {

                    auth.signOut();

                    finish();
                }
        );

        addPressAnimation(logout);
    }

    private void loadStudentProgress() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
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
                                return;
                            }

                            String name =
                                    document.getString("name");

                            String studentClass =
                                    document.getString("class");

                            Long points =
                                    document.getLong("points");

                            Long streak =
                                    document.getLong("streak");

                            if (name != null &&
                                    !name.trim().isEmpty()) {

                                studentNameText.setText(
                                        name
                                );
                            } else {
                                studentNameText.setText(
                                        "Student"
                                );
                            }

                            if (studentClass != null &&
                                    !studentClass.trim().isEmpty()) {

                                classText.setText(
                                        "Class: " + studentClass
                                );
                            } else {
                                classText.setText(
                                        "Class: Not set"
                                );
                            }

                            pointsText.setText(
                                    String.valueOf(
                                            points != null
                                                    ? points
                                                    : 0
                                    )
                            );

                            streakText.setText(
                                    String.valueOf(
                                            streak != null
                                                    ? streak
                                                    : 0
                                    )
                            );
                        }
                )
                .addOnFailureListener(
                        error -> Toast.makeText(
                                ParentDashboardActivity.this,
                                "Unable to load student profile.",
                                Toast.LENGTH_SHORT
                        ).show()
                );

        loadLessonProgress(userId);
        loadQuizProgress(userId);
    }

    private void loadLessonProgress(
            String userId
    ) {

        db.collection("lessonProgress")
                .whereEqualTo(
                        "userId",
                        userId
                )
                .whereEqualTo(
                        "completed",
                        true
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            lessonsText.setText(
                                    String.valueOf(
                                            querySnapshot.size()
                                    )
                            );
                        }
                );
    }

    private void loadQuizProgress(
            String userId
    ) {

        db.collection("quizProgress")
                .whereEqualTo(
                        "userId",
                        userId
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            int attempts =
                                    querySnapshot.size();

                            quizzesText.setText(
                                    "Quiz attempts: " +
                                            attempts
                            );

                            if (attempts == 0) {

                                accuracyText.setText(
                                        "0%"
                                );

                                return;
                            }

                            double totalScore = 0;
                            double totalQuestions = 0;

                            for (
                                    com.google.firebase.firestore.QueryDocumentSnapshot document :
                                    querySnapshot
                            ) {

                                Long score =
                                        document.getLong(
                                                "score"
                                        );

                                Long questions =
                                        document.getLong(
                                                "totalQuestions"
                                        );

                                if (score != null) {
                                    totalScore +=
                                            score;
                                }

                                if (questions != null) {
                                    totalQuestions +=
                                            questions;
                                }
                            }

                            if (totalQuestions > 0) {

                                double accuracy =
                                        (totalScore * 100.0)
                                                / totalQuestions;

                                accuracyText.setText(
                                        String.format(
                                                "%.0f%%",
                                                accuracy
                                        )
                                );
                            } else {

                                accuracyText.setText(
                                        "0%"
                                );
                            }
                        }
                );
    }

    private LinearLayout createCard() {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.HORIZONTAL
        );

        card.setGravity(
                Gravity.CENTER_VERTICAL
        );

        card.setPadding(
                18,
                18,
                18,
                18
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(26);

        card.setBackground(background);
        card.setElevation(5);

        return card;
    }

    private TextView createLabel(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);
        view.setTextSize(14);
        view.setTextColor(textSecondary);

        return view;
    }

    private TextView createValueText(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);
        view.setTextSize(22);
        view.setTextColor(textPrimary);
        view.setTypeface(
                null,
                Typeface.BOLD
        );
        view.setPadding(0, 5, 0, 0);

        return view;
    }

    private TextView createSecondaryText(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);
        view.setTextSize(14);
        view.setTextColor(textSecondary);
        view.setPadding(0, 5, 0, 0);

        return view;
    }

    private TextView createSectionTitle(
            String text
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);
        view.setTextSize(18);
        view.setTextColor(textPrimary);
        view.setTypeface(
                null,
                Typeface.BOLD
        );
        view.setPadding(0, 8, 0, 14);

        return view;
    }

    private TextView createStatCard(
            LinearLayout parent,
            String icon,
            String label,
            String value
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setGravity(Gravity.CENTER);

        card.setPadding(
                10,
                16,
                10,
                16
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(24);

        card.setBackground(background);
        card.setElevation(4);

        TextView iconView =
                new TextView(this);

        iconView.setText(icon);
        iconView.setTextSize(25);
        iconView.setGravity(Gravity.CENTER);

        card.addView(
                iconView,
                new LinearLayout.LayoutParams(
                        -1,
                        38
                )
        );

        TextView valueView =
                new TextView(this);

        valueView.setText(value);
        valueView.setTextSize(22);
        valueView.setTextColor(successColor);
        valueView.setTypeface(
                null,
                Typeface.BOLD
        );
        valueView.setGravity(Gravity.CENTER);

        card.addView(
                valueView,
                new LinearLayout.LayoutParams(
                        -1,
                        38
                )
        );

        TextView labelView =
                new TextView(this);

        labelView.setText(label);
        labelView.setTextSize(13);
        labelView.setTextColor(textSecondary);
        labelView.setGravity(Gravity.CENTER);

        card.addView(
                labelView,
                new LinearLayout.LayoutParams(
                        -1,
                        30
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                );

        params.setMargins(
                0,
                0,
                8,
                0
        );

        parent.addView(card, params);

        addPressAnimation(card);

        return valueView;
    }

    private LinearLayout.LayoutParams createCardParams(
            int bottomMargin
    ) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.setMargins(
                0,
                0,
                0,
                bottomMargin
        );

        return params;
    }

    private void addPressAnimation(
            android.view.View view
    ) {

        view.setOnTouchListener(
                (v, event) -> {

                    if (
                            event.getAction() ==
                                    MotionEvent.ACTION_DOWN
                    ) {

                        v.animate()
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

                        v.animate()
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
