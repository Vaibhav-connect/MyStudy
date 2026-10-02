package com.mystudy.app;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StudentDashboardActivity extends AppCompatActivity {

    private String studentName;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private TextView pointsValue;
    private TextView streakValue;
    private TextView accuracyValue;
    private TextView lessonProgressValue;
    private TextView dashboardStatus;
    private LinearLayout weakTopicsContainer;

    private int totalQuizScore = 0;
    private int totalQuizQuestions = 0;
    private int completedLessons = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        studentName =
                getIntent().getStringExtra("studentName");

        if (studentName == null ||
                studentName.trim().isEmpty()) {

            studentName = "Student";
        }

        createDashboard();
        loadDashboardStats();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (db != null && auth != null) {
            loadDashboardStats();
        }
    }

    private void createDashboard() {

        ScrollView scrollView =
                new ScrollView(this);

        scrollView.setFillViewport(true);

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                20,
                24,
                20,
                30
        );

        root.setBackgroundColor(
                Color.rgb(248, 250, 252)
        );

        LinearLayout topBar =
                new LinearLayout(this);

        topBar.setOrientation(
                LinearLayout.HORIZONTAL
        );

        topBar.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView greeting =
                new TextView(this);

        greeting.setText(
                "Welcome, " +
                        studentName +
                        "! 👋"
        );

        greeting.setTextSize(27);
        greeting.setTextColor(
                Color.rgb(17, 24, 39)
        );

        greeting.setTypeface(
                null,
                Typeface.BOLD
        );

        greeting.setGravity(
                Gravity.CENTER_VERTICAL
        );

        topBar.addView(
                greeting,
                new LinearLayout.LayoutParams(
                        0,
                        65,
                        1
                )
        );

        TextView settingsButton =
                createTopButton("⚙️");

        settingsButton.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    StudentDashboardActivity.this,
                                    SettingsActivity.class
                            );

                    startActivity(intent);
                }
        );

        topBar.addView(
                settingsButton,
                new LinearLayout.LayoutParams(
                        58,
                        58
                )
        );

        root.addView(topBar);

        TextView subtitle =
                new TextView(this);

        subtitle.setText(
                "Ready to learn something new today?"
        );

        subtitle.setTextSize(16);

        subtitle.setTextColor(
                Color.rgb(100, 116, 139)
        );

        subtitle.setPadding(
                0,
                0,
                0,
                10
        );

        root.addView(subtitle);

        dashboardStatus =
                new TextView(this);

        dashboardStatus.setText(
                "⏳ Loading your dashboard..."
        );

        dashboardStatus.setTextSize(14);

        dashboardStatus.setTextColor(
                Color.rgb(79, 70, 229)
        );

        dashboardStatus.setGravity(
                Gravity.CENTER
        );

        dashboardStatus.setPadding(
                0,
                8,
                0,
                16
        );

        root.addView(dashboardStatus);

        LinearLayout stats =
                new LinearLayout(this);

        stats.setOrientation(
                LinearLayout.HORIZONTAL
        );

        pointsValue =
                new TextView(this);

        streakValue =
                new TextView(this);

        accuracyValue =
                new TextView(this);

        LinearLayout.LayoutParams statParams =
                new LinearLayout.LayoutParams(
                        0,
                        125,
                        1
                );

        statParams.setMargins(
                0,
                0,
                5,
                0
        );

        stats.addView(
                createStatCard(
                        "⭐",
                        "0",
                        "Points",
                        pointsValue
                ),
                statParams
        );

        LinearLayout.LayoutParams streakParams =
                new LinearLayout.LayoutParams(
                        0,
                        125,
                        1
                );

        streakParams.setMargins(
                5,
                0,
                5,
                0
        );

        stats.addView(
                createStatCard(
                        "🔥",
                        "0",
                        "Day Streak",
                        streakValue
                ),
                streakParams
        );

        LinearLayout.LayoutParams accuracyParams =
                new LinearLayout.LayoutParams(
                        0,
                        125,
                        1
                );

        accuracyParams.setMargins(
                5,
                0,
                0,
                0
        );

        stats.addView(
                createStatCard(
                        "🎯",
                        "0%",
                        "Accuracy",
                        accuracyValue
                ),
                accuracyParams
        );

        root.addView(stats);

        TextView learningTitle =
                createSectionTitle(
                        "📚 Start Learning"
                );

        root.addView(learningTitle);

        LinearLayout learningCard =
                createActionCard(
                        "📖",
                        "Learn Subjects",
                        "Choose your class, subject and chapter",
                        view -> {

                            Intent intent =
                                    new Intent(
                                            StudentDashboardActivity.this,
                                            StudentClassActivity.class
                                    );

                            startActivity(intent);

                            overridePendingTransition(
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                            );
                        }
                );

        root.addView(learningCard);

        TextView practiceTitle =
                createSectionTitle(
                        "🎯 Practice"
                );

        root.addView(practiceTitle);

        LinearLayout practiceCard =
                createActionCard(
                        "🧠",
                        "Practice Questions",
                        "Test your knowledge and improve your score",
                        view -> {

                            Intent intent =
                                    new Intent(
                                            StudentDashboardActivity.this,
                                            StudentClassActivity.class
                                    );

                            startActivity(intent);

                            overridePendingTransition(
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                            );
                        }
                );

        root.addView(practiceCard);

        TextView examTitle =
                createSectionTitle(
                        "📝 Exams"
                );

        root.addView(examTitle);

        LinearLayout examCard =
                createActionCard(
                        "📝",
                        "Take Exams",
                        "Attempt published exams and check your knowledge",
                        view -> {

                            Intent intent =
                                    new Intent(
                                            StudentDashboardActivity.this,
                                            StudentExamActivity.class
                                    );

                            intent.putExtra(
                                    "studentName",
                                    studentName
                            );

                            startActivity(intent);

                            overridePendingTransition(
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                            );
                        }
                );

        root.addView(examCard);

        LinearLayout historyCard =
                createActionCard(
                        "📜",
                        "Exam History",
                        "View your previous exam results and performance",
                        view -> {

                            Intent intent =
                                    new Intent(
                                            StudentDashboardActivity.this,
                                            StudentExamHistoryActivity.class
                                    );

                            startActivity(intent);

                            overridePendingTransition(
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                            );
                        }
                );

        root.addView(historyCard);

        TextView familyTitle =
                createSectionTitle(
                        "👨‍👩‍👧 Family"
                );

        root.addView(familyTitle);

        LinearLayout parentCard =
                createActionCard(
                        "🔐",
                        "Parent Area",
                        "View learning progress and student activity",
                        view -> {

                            Intent intent =
                                    new Intent(
                                            StudentDashboardActivity.this,
                                            ParentPinActivity.class
                                    );

                            startActivity(intent);

                            overridePendingTransition(
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                            );
                        }
                );

        root.addView(parentCard);

        TextView progressTitle =
                createSectionTitle(
                        "📊 Your Progress"
                );

        root.addView(progressTitle);

        LinearLayout progressCard =
                createProgressCard();

        root.addView(progressCard);

        TextView weakTitle =
                createSectionTitle(
                        "⚠️ Topics to Practice"
                );

        root.addView(weakTitle);

        weakTopicsContainer =
                createWeakTopicsCard();

        root.addView(
                weakTopicsContainer
        );

        TextView about =
                new TextView(this);

        about.setText(
                "MyStudy\n\n" +
                        "Developed by Vaibhav Bhosale\n" +
                        "vb1961869@gmail.com"
        );

        about.setTextSize(13);

        about.setTextColor(
                Color.rgb(100, 116, 139)
        );

        about.setGravity(
                Gravity.CENTER
        );

        about.setPadding(
                0,
                35,
                0,
                10
        );

        root.addView(about);

        scrollView.addView(root);

        setContentView(scrollView);
    }

    private TextView createTopButton(
            String text
    ) {

        TextView button =
                new TextView(this);

        button.setText(text);
        button.setTextSize(25);
        button.setGravity(
                Gravity.CENTER
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(22);

        button.setBackground(background);
        button.setElevation(4);

        addPressAnimation(button);

        return button;
    }

    private TextView createSectionTitle(
            String text
    ) {

        TextView title =
                new TextView(this);

        title.setText(text);
        title.setTextSize(21);

        title.setTextColor(
                Color.rgb(17, 24, 39)
        );

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        title.setPadding(
                0,
                28,
                0,
                14
        );

        return title;
    }

    private LinearLayout createStatCard(
            String icon,
            String value,
            String label,
            TextView valueReference
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setGravity(
                Gravity.CENTER
        );

        card.setPadding(
                6,
                8,
                6,
                8
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
        iconView.setTextSize(21);
        iconView.setGravity(
                Gravity.CENTER
        );

        card.addView(iconView);

        valueReference.setText(value);
        valueReference.setTextSize(19);

        valueReference.setTextColor(
                Color.rgb(79, 70, 229)
        );

        valueReference.setTypeface(
                null,
                Typeface.BOLD
        );

        valueReference.setGravity(
                Gravity.CENTER
        );

        card.addView(valueReference);

        TextView labelView =
                new TextView(this);

        labelView.setText(label);
        labelView.setTextSize(11);

        labelView.setTextColor(
                Color.rgb(100, 116, 139)
        );

        labelView.setGravity(
                Gravity.CENTER
        );

        card.addView(labelView);

        addPressAnimation(card);

        return card;
    }

    private LinearLayout createActionCard(
            String icon,
            String title,
            String description,
            View.OnClickListener listener
    ) {

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
                16,
                18,
                16
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(28);

        card.setBackground(background);
        card.setElevation(4);

        TextView iconView =
                new TextView(this);

        iconView.setText(icon);
        iconView.setTextSize(32);
        iconView.setGravity(
                Gravity.CENTER
        );

        card.addView(
                iconView,
                new LinearLayout.LayoutParams(
                        60,
                        70
                )
        );

        LinearLayout textContainer =
                new LinearLayout(this);

        textContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        textContainer.setPadding(
                14,
                0,
                8,
                0
        );

        TextView titleView =
                new TextView(this);

        titleView.setText(title);
        titleView.setTextSize(18);

        titleView.setTextColor(
                Color.rgb(17, 24, 39)
        );

        titleView.setTypeface(
                null,
                Typeface.BOLD
        );

        textContainer.addView(titleView);

        TextView descriptionView =
                new TextView(this);

        descriptionView.setText(description);
        descriptionView.setTextSize(13);

        descriptionView.setTextColor(
                Color.rgb(100, 116, 139)
        );

        descriptionView.setPadding(
                0,
                5,
                0,
                0
        );

        textContainer.addView(
                descriptionView
        );

        card.addView(
                textContainer,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        TextView arrow =
                new TextView(this);

        arrow.setText("›");
        arrow.setTextSize(30);

        arrow.setTextColor(
                Color.rgb(79, 70, 229)
        );

        arrow.setGravity(
                Gravity.CENTER
        );

        card.addView(
                arrow,
                new LinearLayout.LayoutParams(
                        40,
                        60
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        105
                );

        params.setMargins(
                0,
                0,
                0,
                12
        );

        card.setLayoutParams(params);

        addPressAnimation(card);

        card.setOnClickListener(listener);

        return card;
    }

    private LinearLayout createProgressCard() {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                20,
                18,
                20,
                18
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(28);

        card.setBackground(background);
        card.setElevation(4);

        TextView title =
                new TextView(this);

        title.setText(
                "Learning Progress"
        );

        title.setTextSize(17);

        title.setTextColor(
                Color.rgb(17, 24, 39)
        );

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(title);

        lessonProgressValue =
                new TextView(this);

        lessonProgressValue.setText(
                "⏳ Loading lessons..."
        );

        lessonProgressValue.setTextSize(14);

        lessonProgressValue.setTextColor(
                Color.rgb(100, 116, 139)
        );

        lessonProgressValue.setPadding(
                0,
                8,
                0,
                0
        );

        card.addView(
                lessonProgressValue
        );

        TextView badge =
                new TextView(this);

        badge.setText(
                "🏅 Keep learning to earn badges!"
        );

        badge.setTextSize(14);

        badge.setTextColor(
                Color.rgb(79, 70, 229)
        );

        badge.setPadding(
                0,
                14,
                0,
                0
        );

        card.addView(badge);

        addPressAnimation(card);

        return card;
    }

    private LinearLayout createWeakTopicsCard() {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                20,
                18,
                20,
                18
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(28);

        card.setBackground(background);
        card.setElevation(4);

        TextView loading =
                new TextView(this);

        loading.setText(
                "⏳ Checking your practice performance..."
        );

        loading.setTextSize(14);

        loading.setTextColor(
                Color.rgb(100, 116, 139)
        );

        loading.setGravity(
                Gravity.CENTER
        );

        card.addView(loading);

        addPressAnimation(card);

        return card;
    }

    private void loadDashboardStats() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            showDashboardError(
                    "Please login again to load your dashboard."
            );

            return;
        }

        showDashboardLoading();

        String userId =
                user.getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (!document.exists()) {

                                showDashboardError(
                                        "Your profile data was not found."
                                );

                                return;
                            }

                            Long points =
                                    document.getLong(
                                            "points"
                                    );

                            Long streak =
                                    document.getLong(
                                            "streak"
                                    );

                            Double overallAccuracy =
                                    document.getDouble(
                                            "overallAccuracy"
                                    );

                            if (points != null) {

                                pointsValue.setText(
                                        String.valueOf(
                                                points
                                        )
                                );

                            } else {

                                pointsValue.setText("0");
                            }

                            if (streak != null) {

                                streakValue.setText(
                                        String.valueOf(
                                                streak
                                        )
                                );

                            } else {

                                streakValue.setText("0");
                            }

                            if (overallAccuracy != null) {

                                accuracyValue.setText(
                                        String.format(
                                                Locale.US,
                                                "%.0f%%",
                                                overallAccuracy
                                        )
                                );

                            } else {

                                accuracyValue.setText(
                                        "0%"
                                );
                            }

                            dashboardStatus.setText(
                                    "✅ Dashboard updated"
                            );

                            loadWeakTopics(
                                    document
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            showDashboardError(
                                    "Unable to load profile data. Please try again."
                            );

                            accuracyValue.setText(
                                    "0%"
                            );
                        }
                );

        loadQuizAccuracy(userId);
        loadCompletedLessons(userId);
    }

    private void showDashboardLoading() {

        if (dashboardStatus != null) {

            dashboardStatus.setText(
                    "⏳ Loading your latest data..."
            );

            dashboardStatus.setTextColor(
                    Color.rgb(79, 70, 229)
            );
        }

        if (lessonProgressValue != null) {

            lessonProgressValue.setText(
                    "⏳ Loading lessons..."
            );
        }
    }

    private void showDashboardError(
            String message
    ) {

        if (dashboardStatus != null) {

            dashboardStatus.setText(
                    "⚠️ " + message
            );

            dashboardStatus.setTextColor(
                    Color.rgb(220, 38, 38)
            );
        }

        Toast.makeText(
                StudentDashboardActivity.this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    private void loadQuizAccuracy(
            String userId
    ) {

        totalQuizScore = 0;
        totalQuizQuestions = 0;

        db.collection("quizProgress")
                .whereEqualTo(
                        "userId",
                        userId
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            for (
                                    QueryDocumentSnapshot document :
                                    querySnapshot
                            ) {

                                Long score =
                                        document.getLong(
                                                "score"
                                        );

                                Long total =
                                        document.getLong(
                                                "totalQuestions"
                                        );

                                if (score != null) {

                                    totalQuizScore +=
                                            score.intValue();
                                }

                                if (total != null) {

                                    totalQuizQuestions +=
                                            total.intValue();
                                }
                            }

                            if (totalQuizQuestions > 0) {

                                double accuracy =
                                        (
                                                totalQuizScore
                                                        * 100.0
                                        )
                                                /
                                                totalQuizQuestions;

                                accuracyValue.setText(
                                        String.format(
                                                Locale.US,
                                                "%.0f%%",
                                                accuracy
                                        )
                                );

                            } else {

                                accuracyValue.setText(
                                        "0%"
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        e -> {

                            accuracyValue.setText(
                                    "0%"
                            );

                            if (dashboardStatus != null) {

                                dashboardStatus.setText(
                                        "⚠️ Some practice data could not be loaded."
                                );

                                dashboardStatus.setTextColor(
                                        Color.rgb(220, 38, 38)
                                );
                            }
                        }
                );
    }

    private void loadCompletedLessons(
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

                            completedLessons =
                                    querySnapshot.size();

                            if (completedLessons == 0) {

                                lessonProgressValue.setText(
                                        "No lessons completed yet"
                                );

                            } else {

                                lessonProgressValue.setText(
                                        completedLessons +
                                                " lessons completed"
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        e -> {

                            lessonProgressValue.setText(
                                    "Unable to load lesson progress"
                            );

                            if (dashboardStatus != null) {

                                dashboardStatus.setText(
                                        "⚠️ Lesson progress could not be loaded."
                                );

                                dashboardStatus.setTextColor(
                                        Color.rgb(220, 38, 38)
                                );
                            }
                        }
                );
    }

    private void loadWeakTopics(
            com.google.firebase.firestore.DocumentSnapshot document
    ) {

        if (weakTopicsContainer == null) {
            return;
        }

        weakTopicsContainer.removeAllViews();

        Object weakTopicsObject =
                document.get("weakTopics");

        if (!(weakTopicsObject instanceof Map)) {

            showNoWeakTopics();

            return;
        }

        Map<?, ?> weakTopics =
                (Map<?, ?>) weakTopicsObject;

        if (weakTopics.isEmpty()) {

            showNoWeakTopics();

            return;
        }

        List<String> chapterIds =
                new ArrayList<>();

        for (Map.Entry<?, ?> entry :
                weakTopics.entrySet()) {

            if (entry.getKey() == null) {
                continue;
            }

            String chapterId =
                    String.valueOf(
                            entry.getKey()
                    );

            if (!chapterId.trim().isEmpty()) {
                chapterIds.add(chapterId);
            }
        }

        if (chapterIds.isEmpty()) {

            showNoWeakTopics();

            return;
        }

        for (String chapterId :
                chapterIds) {

            Object topicObject =
                    weakTopics.get(
                            chapterId
                    );

            double topicAccuracy = 0;
            String status = "needs_practice";

            if (topicObject instanceof Map) {

                Map<?, ?> topicData =
                        (Map<?, ?>) topicObject;

                Object accuracyObject =
                        topicData.get(
                                "accuracy"
                        );

                Object statusObject =
                        topicData.get(
                                "status"
                        );

                if (accuracyObject instanceof Number) {

                    topicAccuracy =
                            ((Number)
                                    accuracyObject)
                                    .doubleValue();
                }

                if (statusObject != null) {

                    status =
                            String.valueOf(
                                    statusObject
                            );
                }
            }

            loadChapterNameAndAddTopic(
                    chapterId,
                    topicAccuracy,
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
                .addOnSuccessListener(
                        document -> {

                            String chapterName =
                                    document.getString(
                                            "name"
                                    );

                            if (chapterName == null ||
                                    chapterName.trim().isEmpty()) {

                                chapterName =
                                        document.getString(
                                                "chapterName"
                                        );
                            }

                            if (chapterName == null ||
                                    chapterName.trim().isEmpty()) {

                                chapterName =
                                        "Chapter";
                            }

                            addWeakTopicRow(
                                    chapterName,
                                    accuracy,
                                    status
                            );
                        }
                )
                .addOnFailureListener(
                        e ->
                                addWeakTopicRow(
                                        "Chapter",
                                        accuracy,
                                        status
                                )
                );
    }

    private void addWeakTopicRow(
            String chapterName,
            double accuracy,
            String status
    ) {

        if (weakTopicsContainer == null) {
            return;
        }

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.VERTICAL
        );

        row.setPadding(
                14,
                12,
                14,
                12
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(248, 250, 252)
        );

        background.setCornerRadius(18);

        row.setBackground(background);

        TextView title =
                new TextView(this);

        title.setText(
                "📌 " + chapterName
        );

        title.setTextSize(16);

        title.setTextColor(
                Color.rgb(17, 24, 39)
        );

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        row.addView(title);

        TextView accuracyText =
                new TextView(this);

        accuracyText.setText(
                String.format(
                        Locale.US,
                        "Accuracy: %.0f%%",
                        accuracy
                )
        );

        accuracyText.setTextSize(14);

        accuracyText.setTextColor(
                Color.rgb(100, 116, 139)
        );

        accuracyText.setPadding(
                0,
                5,
                0,
                0
        );

        row.addView(accuracyText);

        TextView statusText =
                new TextView(this);

        String statusLabel;

        if ("weak".equalsIgnoreCase(status)) {

            statusLabel =
                    "⚠️ Needs more practice";

        } else if (
                "needs_practice"
                        .equalsIgnoreCase(status)
        ) {

            statusLabel =
                    "📚 Practice again";

        } else {

            statusLabel =
                    "✅ Good progress";
        }

        statusText.setText(statusLabel);
        statusText.setTextSize(13);

        statusText.setTextColor(
                Color.rgb(79, 70, 229)
        );

        statusText.setPadding(
                0,
                4,
                0,
                0
        );

        row.addView(statusText);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.setMargins(
                0,
                0,
                0,
                10
        );

        weakTopicsContainer.addView(
                row,
                params
        );
    }

    private void showNoWeakTopics() {

        if (weakTopicsContainer == null) {
            return;
        }

        weakTopicsContainer.removeAllViews();

        TextView message =
                new TextView(this);

        message.setText(
                "🌟 No weak topics yet!\n" +
                        "Complete some practice to see where you can improve."
        );

        message.setTextSize(14);

        message.setTextColor(
                Color.rgb(100, 116, 139)
        );

        message.setGravity(
                Gravity.CENTER
        );

        message.setPadding(
                10,
                15,
                10,
                15
        );

        weakTopicsContainer.addView(
                message
        );
    }

    private void addPressAnimation(
            View view
    ) {

        view.setOnTouchListener(
                (v, event) -> {

                    if (event.getAction() ==
                            MotionEvent.ACTION_DOWN) {

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
