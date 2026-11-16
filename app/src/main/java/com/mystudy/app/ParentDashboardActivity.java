package com.mystudy.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    private LinearLayout weakTopicsContainer;

    private final int BLUE = Color.rgb(59, 130, 246);
    private final int DARK_BLUE = Color.rgb(37, 99, 235);
    private final int YELLOW = Color.rgb(250, 204, 21);
    private final int BACKGROUND = Color.rgb(245, 249, 255);
    private final int DARK = Color.rgb(17, 24, 39);
    private final int GREY = Color.rgb(100, 116, 139);
    private final int GREEN = Color.rgb(22, 163, 74);
    private final int WHITE = Color.WHITE;

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
        scrollView.setBackgroundColor(BACKGROUND);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(20), dp(18), dp(30));
        root.setBackgroundColor(BACKGROUND);

        // Header

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView logo = new TextView(this);
        logo.setText("📘");
        logo.setTextSize(30);
        logo.setGravity(Gravity.CENTER);

        header.addView(
                logo,
                new LinearLayout.LayoutParams(dp(48), dp(48))
        );

        LinearLayout brandInfo = new LinearLayout(this);
        brandInfo.setOrientation(LinearLayout.VERTICAL);
        brandInfo.setPadding(dp(8), 0, 0, 0);

        TextView brand = new TextView(this);
        brand.setText("MyStudy");
        brand.setTextSize(21);
        brand.setTextColor(DARK_BLUE);
        brand.setTypeface(null, Typeface.BOLD);

        brandInfo.addView(brand);

        TextView brandSubtitle = new TextView(this);
        brandSubtitle.setText("Parent Learning Portal");
        brandSubtitle.setTextSize(12);
        brandSubtitle.setTextColor(GREY);

        brandInfo.addView(brandSubtitle);

        header.addView(
                brandInfo,
                new LinearLayout.LayoutParams(0, -2, 1)
        );

        TextView backButton = new TextView(this);
        backButton.setText("←");
        backButton.setTextSize(25);
        backButton.setTextColor(DARK_BLUE);
        backButton.setGravity(Gravity.CENTER);
        backButton.setBackground(createRoundedBackground(WHITE, 16));

        header.addView(
                backButton,
                new LinearLayout.LayoutParams(dp(45), dp(45))
        );

        backButton.setOnClickListener(v -> finish());
        addPressAnimation(backButton);

        root.addView(
                header,
                new LinearLayout.LayoutParams(-1, -2)
        );

        // Main heading

        TextView title = createTitle("Parent Dashboard");
        title.setPadding(0, dp(25), 0, dp(4));
        root.addView(title);

        TextView subtitle = createSubtitle(
                "Track your child's learning journey."
        );

        root.addView(subtitle);

        // Student profile card

        LinearLayout studentCard = createCard();
        studentCard.setOrientation(LinearLayout.VERTICAL);
        studentCard.setBackground(
                createRoundedBackground(
                        Color.rgb(232, 242, 255),
                        24
                )
        );

        TextView studentLabel = createSmallLabel("STUDENT PROFILE");
        studentCard.addView(studentLabel);

        studentNameText = new TextView(this);
        studentNameText.setText("Loading student...");
        studentNameText.setTextSize(23);
        studentNameText.setTextColor(DARK_BLUE);
        studentNameText.setTypeface(null, Typeface.BOLD);
        studentNameText.setPadding(0, dp(8), 0, dp(5));

        studentCard.addView(studentNameText);

        classText = createSubtitle("Class: Loading...");
        studentCard.addView(classText);

        root.addView(
                studentCard,
                createMargins(0, 0, 0, 22)
        );

        // Learning overview

        root.addView(
                createSectionTitle("Learning Overview")
        );

        LinearLayout firstRow = createStatsRow();

        pointsText = createStatCard(
                firstRow,
                "⭐",
                "Points",
                "0"
        );

        streakText = createStatCard(
                firstRow,
                "🔥",
                "Day Streak",
                "0"
        );

        root.addView(
                firstRow,
                createMargins(0, 0, 0, 12)
        );

        LinearLayout secondRow = createStatsRow();

        accuracyText = createStatCard(
                secondRow,
                "🎯",
                "Accuracy",
                "0%"
        );

        lessonsText = createStatCard(
                secondRow,
                "📖",
                "Lessons Done",
                "0"
        );

        root.addView(
                secondRow,
                createMargins(0, 0, 0, 22)
        );

        // Quiz activity

        root.addView(
                createSectionTitle("Practice Activity")
        );

        LinearLayout quizCard = createCard();
        quizCard.setOrientation(LinearLayout.HORIZONTAL);
        quizCard.setGravity(Gravity.CENTER_VERTICAL);

        TextView quizIcon = new TextView(this);
        quizIcon.setText("📝");
        quizIcon.setTextSize(32);
        quizIcon.setGravity(Gravity.CENTER);

        quizCard.addView(
                quizIcon,
                new LinearLayout.LayoutParams(dp(55), dp(55))
        );

        LinearLayout quizInfo = new LinearLayout(this);
        quizInfo.setOrientation(LinearLayout.VERTICAL);
        quizInfo.setPadding(dp(12), 0, 0, 0);

        TextView quizHeading = createCardHeading("Quiz Activity");
        quizInfo.addView(quizHeading);

        quizzesText = createSubtitle("Quiz attempts: 0");
        quizInfo.addView(quizzesText);

        quizCard.addView(
                quizInfo,
                new LinearLayout.LayoutParams(0, -2, 1)
        );

        root.addView(
                quizCard,
                createMargins(0, 0, 0, 22)
        );

        // Weak topics

        root.addView(
                createSectionTitle("📌 Topics to Practice")
        );

        weakTopicsContainer = new LinearLayout(this);
        weakTopicsContainer.setOrientation(LinearLayout.VERTICAL);
        weakTopicsContainer.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(8)
        );

        weakTopicsContainer.setBackground(
                createRoundedBackground(WHITE, 22)
        );

        weakTopicsContainer.setElevation(dp(3));

        TextView loadingTopics = createSubtitle(
                "Checking learning performance..."
        );

        weakTopicsContainer.addView(loadingTopics);

        root.addView(
                weakTopicsContainer,
                createMargins(0, 0, 0, 22)
        );

        // Parent note

        LinearLayout noteCard = createCard();
        noteCard.setOrientation(LinearLayout.HORIZONTAL);
        noteCard.setGravity(Gravity.CENTER_VERTICAL);
        noteCard.setBackground(
                createRoundedBackground(
                        Color.rgb(255, 250, 220),
                        18
                )
        );

        TextView lockIcon = new TextView(this);
        lockIcon.setText("🔒");
        lockIcon.setTextSize(23);

        noteCard.addView(
                lockIcon,
                new LinearLayout.LayoutParams(dp(42), dp(42))
        );

        TextView note = createSubtitle(
                "This dashboard displays learning progress " +
                        "linked to the logged-in student account."
        );

        note.setTextColor(DARK);
        noteCard.addView(
                note,
                new LinearLayout.LayoutParams(0, -2, 1)
        );

        root.addView(
                noteCard,
                createMargins(0, 0, 0, 18)
        );

        // Logout

        TextView logout = new TextView(this);
        logout.setText("Logout");
        logout.setTextSize(15);
        logout.setTextColor(DARK_BLUE);
        logout.setTypeface(null, Typeface.BOLD);
        logout.setGravity(Gravity.CENTER);
        logout.setPadding(dp(12), dp(16), dp(12), dp(16));
        logout.setBackground(
                createRoundedBackground(WHITE, 16)
        );

        root.addView(
                logout,
                new LinearLayout.LayoutParams(-1, -2)
        );

        logout.setOnClickListener(v -> {

            auth.signOut();
            finish();

        });

        addPressAnimation(logout);

        scrollView.addView(root);
        setContentView(scrollView);

        // Entrance animation

        root.setAlpha(0f);
        root.setTranslationY(dp(18));

        root.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(450)
                .start();
    }

    // Firebase student data

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

        String userId = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    if (!document.exists()) {

                        studentNameText.setText("Student");
                        classText.setText("Class: Not set");
                        return;
                    }

                    String name = document.getString("name");
                    String studentClass = document.getString("class");

                    Long points = document.getLong("points");
                    Long streak = document.getLong("streak");

                    Double overallAccuracy =
                            document.getDouble("overallAccuracy");

                    if (name != null && !name.trim().isEmpty()) {
                        studentNameText.setText(name);
                    } else {
                        studentNameText.setText("Student");
                    }

                    if (studentClass != null &&
                            !studentClass.trim().isEmpty()) {

                        classText.setText("Class: " + studentClass);

                    } else {

                        classText.setText("Class: Not set");
                    }

                    pointsText.setText(
                            String.valueOf(points != null ? points : 0)
                    );

                    streakText.setText(
                            String.valueOf(streak != null ? streak : 0)
                    );

                    if (overallAccuracy != null) {

                        accuracyText.setText(
                                String.format(
                                        Locale.US,
                                        "%.0f%%",
                                        overallAccuracy
                                )
                        );
                    }

                    loadWeakTopics(document);

                })
                .addOnFailureListener(error -> {

                    if (!isFinishing() && !isDestroyed()) {

                        Toast.makeText(
                                this,
                                "Unable to load student profile.",
                                Toast.LENGTH_SHORT
                        ).show();

                        studentNameText.setText("Unable to load");
                    }
                });

        loadLessonProgress(userId);
        loadQuizProgress(userId);
    }

    // Completed lessons

    private void loadLessonProgress(String userId) {

        db.collection("lessonProgress")
                .whereEqualTo("userId", userId)
                .whereEqualTo("completed", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    lessonsText.setText(
                            String.valueOf(querySnapshot.size())
                    );

                })
                .addOnFailureListener(error -> {

                    if (!isFinishing() && !isDestroyed()) {
                        lessonsText.setText("0");
                    }
                });
    }

    // Quiz attempts and accuracy

    private void loadQuizProgress(String userId) {

        db.collection("quizProgress")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    int attempts = querySnapshot.size();

                    quizzesText.setText(
                            "Quiz attempts: " + attempts
                    );

                    if (attempts == 0) {

                        accuracyText.setText("0%");
                        return;
                    }

                    double totalScore = 0;
                    double totalQuestions = 0;

                    for (QueryDocumentSnapshot document : querySnapshot) {

                        Long score = document.getLong("score");
                        Long questions =
                                document.getLong("totalQuestions");

                        if (score != null) {
                            totalScore += score;
                        }

                        if (questions != null) {
                            totalQuestions += questions;
                        }
                    }

                    if (totalQuestions > 0) {

                        double accuracy =
                                (totalScore * 100.0) / totalQuestions;

                        accuracyText.setText(
                                String.format(
                                        Locale.US,
                                        "%.0f%%",
                                        accuracy
                                )
                        );

                    } else {

                        accuracyText.setText("0%");
                    }

                })
                .addOnFailureListener(error -> {

                    if (!isFinishing() && !isDestroyed()) {

                        quizzesText.setText("Quiz attempts: 0");
                        accuracyText.setText("0%");
                    }
                });
    }

    // Weak topics

    private void loadWeakTopics(DocumentSnapshot document) {

        if (weakTopicsContainer == null) {
            return;
        }

        weakTopicsContainer.removeAllViews();

        Object weakTopicsObject = document.get("weakTopics");

        if (!(weakTopicsObject instanceof Map)) {

            showNoWeakTopics();
            return;
        }

        Map<?, ?> weakTopics = (Map<?, ?>) weakTopicsObject;

        if (weakTopics.isEmpty()) {

            showNoWeakTopics();
            return;
        }

        List<String> chapterIds = new ArrayList<>();

        for (Map.Entry<?, ?> entry : weakTopics.entrySet()) {

            if (entry.getKey() == null) {
                continue;
            }

            String chapterId = String.valueOf(entry.getKey());

            if (!chapterId.trim().isEmpty()) {
                chapterIds.add(chapterId);
            }
        }

        if (chapterIds.isEmpty()) {

            showNoWeakTopics();
            return;
        }

        for (String chapterId : chapterIds) {

            Object topicObject = weakTopics.get(chapterId);

            double accuracy = 0;
            String status = "needs_practice";

            if (topicObject instanceof Map) {

                Map<?, ?> topicData = (Map<?, ?>) topicObject;

                Object accuracyObject = topicData.get("accuracy");
                Object statusObject = topicData.get("status");

                if (accuracyObject instanceof Number) {

                    accuracy = ((Number) accuracyObject).doubleValue();
                }

                if (statusObject != null) {

                    status = String.valueOf(statusObject);
                }
            }

            loadChapterNameAndAddTopic(
                    chapterId,
                    accuracy,
                    status
            );
        }
    }

    private void loadChapterNameAndAddTopic(
            String chapterId,
            double accuracy,
            String status
    ) {

        db.collection("chapters")
                .document(chapterId)
                .get()
                .addOnSuccessListener(document -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    String chapterName = document.getString("name");

                    if (chapterName == null ||
                            chapterName.trim().isEmpty()) {

                        chapterName = document.getString("chapterName");
                    }

                    if (chapterName == null ||
                            chapterName.trim().isEmpty()) {

                        chapterName = "Chapter";
                    }

                    addWeakTopicRow(
                            chapterName,
                            accuracy,
                            status
                    );

                })
                .addOnFailureListener(error -> {

                    if (!isFinishing() && !isDestroyed()) {

                        addWeakTopicRow(
                                "Chapter",
                                accuracy,
                                status
                        );
                    }
                });
    }

    private void addWeakTopicRow(
            String chapterName,
            double accuracy,
            String status
    ) {

        if (weakTopicsContainer == null) {
            return;
        }

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dp(14), dp(13), dp(14), dp(13));

        row.setBackground(
                createRoundedBackground(
                        Color.rgb(245, 249, 255),
                        17
                )
        );

        TextView title = createCardHeading(
                "📌 " + chapterName
        );

        row.addView(title);

        TextView accuracyView = createSubtitle(
                String.format(
                        Locale.US,
                        "Accuracy: %.0f%%",
                        accuracy
                )
        );

        accuracyView.setPadding(0, dp(5), 0, 0);
        row.addView(accuracyView);

        TextView statusView = new TextView(this);
        statusView.setTextSize(13);
        statusView.setTypeface(null, Typeface.BOLD);
        statusView.setPadding(0, dp(5), 0, 0);

        if ("weak".equalsIgnoreCase(status)) {

            statusView.setText("⚠️ Needs more practice");
            statusView.setTextColor(Color.rgb(220, 38, 38));

        } else if ("needs_practice".equalsIgnoreCase(status)) {

            statusView.setText("📚 Practice again");
            statusView.setTextColor(Color.rgb(217, 119, 6));

        } else {

            statusView.setText("✅ Good progress");
            statusView.setTextColor(GREEN);
        }

        row.addView(statusView);

        weakTopicsContainer.addView(
                row,
                createMargins(0, 0, 0, 10)
        );

        addPressAnimation(row);
    }

    private void showNoWeakTopics() {

        if (weakTopicsContainer == null) {
            return;
        }

        TextView message = new TextView(this);

        message.setText(
                "🌟 No weak topics yet!\n\n" +
                        "Complete some practice sessions to see " +
                        "where your child can improve."
        );

        message.setTextSize(14);
        message.setTextColor(GREY);
        message.setGravity(Gravity.CENTER);
        message.setPadding(dp(12), dp(20), dp(12), dp(20));

        weakTopicsContainer.addView(
                message,
                new LinearLayout.LayoutParams(-1, -2)
        );
    }

    // UI helpers

    private LinearLayout createCard() {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));

        card.setBackground(
                createRoundedBackground(WHITE, 22)
        );

        card.setElevation(dp(3));

        return card;
    }

    private LinearLayout createStatsRow() {

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        return row;
    }

    private TextView createStatCard(
            LinearLayout parent,
            String icon,
            String label,
            String value
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(8), dp(14), dp(8), dp(14));

        card.setBackground(
                createRoundedBackground(WHITE, 20)
        );

        card.setElevation(dp(3));

        TextView iconView = new TextView(this);
        iconView.setText(icon);
        iconView.setTextSize(25);
        iconView.setGravity(Gravity.CENTER);

        card.addView(
                iconView,
                new LinearLayout.LayoutParams(-1, dp(38))
        );

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextSize(23);
        valueView.setTextColor(DARK_BLUE);
        valueView.setTypeface(null, Typeface.BOLD);
        valueView.setGravity(Gravity.CENTER);

        card.addView(
                valueView,
                new LinearLayout.LayoutParams(-1, dp(38))
        );

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextSize(12);
        labelView.setTextColor(GREY);
        labelView.setGravity(Gravity.CENTER);

        card.addView(
                labelView,
                new LinearLayout.LayoutParams(-1, dp(28))
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(0, -2, 1);

        params.setMargins(0, 0, dp(7), 0);

        parent.addView(card, params);

        addPressAnimation(card);

        return valueView;
    }

    private TextView createTitle(String text) {

        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(25);
        view.setTextColor(DARK);
        view.setTypeface(null, Typeface.BOLD);

        return view;
    }

    private TextView createSectionTitle(String text) {

        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(18);
        view.setTextColor(DARK);
        view.setTypeface(null, Typeface.BOLD);
        view.setPadding(0, dp(5), 0, dp(13));

        return view;
    }

    private TextView createCardHeading(String text) {

        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(16);
        view.setTextColor(DARK);
        view.setTypeface(null, Typeface.BOLD);

        return view;
    }

    private TextView createSubtitle(String text) {

        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(14);
        view.setTextColor(GREY);

        return view;
    }

    private TextView createSmallLabel(String text) {

        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(11);
        view.setTextColor(DARK_BLUE);
        view.setTypeface(null, Typeface.BOLD);

        return view;
    }

    private GradientDrawable createRoundedBackground(
            int color,
            int radius
    ) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));

        return drawable;
    }

    private LinearLayout.LayoutParams createMargins(
            int left,
            int top,
            int right,
            int bottom
    ) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(
                dp(left),
                dp(top),
                dp(right),
                dp(bottom)
        );

        return params;
    }

    private void addPressAnimation(View view) {

        view.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                v.animate()
                        .scaleX(0.97f)
                        .scaleY(0.97f)
                        .setDuration(100)
                        .start();

            } else if (
                    event.getAction() == MotionEvent.ACTION_UP ||
                            event.getAction() == MotionEvent.ACTION_CANCEL
            ) {

                v.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start();
            }

            return false;
        });
    }

    private int dp(float value) {

        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}
