package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
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

    private boolean dashboardLoading = false;

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

    private int dp(float value) {
        return (int) (
                value * getResources().getDisplayMetrics().density
                        + 0.5f
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (db != null && auth != null) {
            loadDashboardStats();
        }
    }

    private boolean isScreenActive() {
        return !isFinishing() &&
                !isDestroyed();
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
                dp(20),
                dp(24),
                dp(20),
                dp(30)
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
                        dp(58),
                        1
                )
        );

        TextView settingsButton =
                createTopButton("⚙️");

        settingsButton.setOnClickListener(
                view -> {

                    if (!isScreenActive()) {
                        return;
                    }

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
                        dp(52),
                        dp(52)
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
                dp(10)
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
                dp(8),
                0,
                dp(16)
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
                        dp(125),
                        1
                );

        statParams.setMargins(
                0,
                0,
                dp(4),
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
                        dp(125),
                        1
                );

        streakParams.setMargins(
                dp(4),
                0,
                dp(4),
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
                        dp(125),
                        1
                );

        accuracyParams.setMargins(
                dp(4),
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

        root.addView(
                createSectionTitle("📚 Start Learning")
        );

        root.addView(
                createActionCard(
                        "📖",
                        "Learn Subjects",
                        "Choose your class, subject and chapter",
                        view -> {

                            if (!isScreenActive()) {
                                return;
                            }

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
                )
        );

        root.addView(
                createSectionTitle("🎯 Practice")
        );

        root.addView(
                createActionCard(
                        "🧠",
                        "Practice Questions",
                        "Test your knowledge and improve your score",
                        view -> {

                            if (!isScreenActive()) {
                                return;
                            }

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
                )
        );

        root.addView(
                createSectionTitle("📝 Exams")
        );

        root.addView(
                createActionCard(
                        "📝",
                        "Take Exams",
                        "Attempt published exams and check your knowledge",
                        view -> {

                            if (!isScreenActive()) {
                                return;
                            }

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
                )
        );

        root.addView(
                createActionCard(
                        "📜",
                        "Exam History",
                        "View your previous exam results and performance",
                        view -> {

                            if (!isScreenActive()) {
                                return;
                            }

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
                )
        );

        root.addView(
                createSectionTitle("👨‍👩‍👧 Family")
        );

        root.addView(
                createActionCard(
                        "🔐",
                        "Parent Area",
                        "View learning progress and student activity",
                        view -> {

                            if (!isScreenActive()) {
                                return;
                            }

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
                )
        );

        root.addView(
                createSectionTitle("📊 Your Progress")
        );

        root.addView(createProgressCard());

        root.addView(
                createSectionTitle("⚠️ Topics to Practice")
        );

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
                dp(35),
                0,
                dp(10)
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
        background.setCornerRadius(dp(22));

        button.setBackground(background);
        button.setElevation(dp(4));

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
                dp(28),
                0,
                dp(14)
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
                dp(6),
                dp(8),
                dp(6),
                dp(8)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(dp(24));

        card.setBackground(background);
        card.setElevation(dp(4));

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
                dp(18),
                dp(16),
                dp(18),
                dp(16)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(dp(28));

        card.setBackground(background);
        card.setElevation(dp(4));

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
                        dp(60),
                        dp(70)
                )
        );

        LinearLayout textContainer =
                new LinearLayout(this);

        textContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        textContainer.setPadding(
                dp(14),
                0,
                dp(8),
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
                dp(5),
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
                        ViewGroup.LayoutParams.WRAP_CONTENT,
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
                        dp(40),
                        dp(60)
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(105)
                );

        params.setMargins(
                0,
                0,
                0,
                dp(12)
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
                dp(20),
                dp(18),
                dp(20),
                dp(18)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(dp(28));

        card.setBackground(background);
        card.setElevation(dp(4));

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
                dp(8),
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
                dp(14),
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
                dp(20),
                dp(18),
                dp(20),
                dp(18)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(dp(28));

        card.setBackground(background);
        card.setElevation(dp(4));

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

        if (!isScreenActive() ||
                dashboardLoading) {
            return;
        }

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            showDashboardError(
                    "Please login again to load your dashboard."
            );

            return;
        }

        dashboardLoading = true;

        showDashboardLoading();

        String userId =
                user.getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (!isScreenActive()) {
                                dashboardLoading = false;
                                return;
                            }

                            if (!document.exists()) {

                                dashboardLoading = false;

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

                            pointsValue.setText(
                                    points != null
                                            ? String.valueOf(points)
                                            : "0"
                            );

                            streakValue.setText(
                                    streak != null
                                            ? String.valueOf(streak)
                                            : "0"
                            );

                            accuracyValue.setText(
                                    overallAccuracy != null
                                            ? String.format(
                                                    Locale.US,
                                                    "%.0f%%",
                                                    overallAccuracy
                                            )
                                            : "0%"
                            );

                            dashboardStatus.setText(
                                    "✅ Dashboard updated"
                            );

                            dashboardLoading = false;

                            loadWeakTopics(document);
                        }
                )
                .addOnFailureListener(
                        e -> {

                            if (!isScreenActive()) {
                                dashboardLoading = false;
                                return;
                            }

                            dashboardLoading = false;

                            showDashboardError(
                                    "Unable to load profile data. Please try again."
                            );

                            accuracyValue.setText("0%");
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

        if (isScreenActive()) {

            Toast.makeText(
                    StudentDashboardActivity.this,
                    message,
                    Toast.LENGTH_SHORT
            ).show();
        }
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

                            if (!isScreenActive()) {
                                return;
                            }

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

                            if (!isScreenActive()) {
                                return;
                            }

                            accuracyValue.setText("0%");

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

                            if (!isScreenActive()) {
                                return;
                            }

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

                            if (!isScreenActive()) {
                                return;
                            }

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
            DocumentSnapshot document
    ) {

        if (!isScreenActive() ||
                weakTopicsContainer == null) {
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
                    weakTopics.get(chapterId);

            double topicAccuracy = 0;
            String status = "needs_practice";

            if (topicObject instanceof Map) {

                Map<?, ?> topicData =
                        (Map<?, ?>) topicObject;

                Object accuracyObject =
                        topicData.get("accuracy");

                Object statusObject =
                        topicData.get("status");

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

                            if (!isScreenActive()) {
                                return;
                            }

                            String chapterName =
                                    document.getString("name");

                            if (chapterName == null ||
                                    chapterName.trim().isEmpty()) {

                                chapterName =
                                        document.getString(
                                                "chapterName"
                                        );
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
                        }
                )
                .addOnFailureListener(
                        e -> {

                            if (!isScreenActive()) {
                                return;
                            }

                            addWeakTopicRow(
                                    "Chapter",
                                    accuracy,
                                    status
                            );
                        }
                );
    }

    private void addWeakTopicRow(
            String chapterName,
            double accuracy,
            String status
    ) {

        if (!isScreenActive() ||
                weakTopicsContainer == null) {
            return;
        }

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.VERTICAL
        );

        row.setPadding(
                dp(14),
                dp(12),
                dp(14),
                dp(12)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(248, 250, 252)
        );

        background.setCornerRadius(dp(18));

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
                dp(5),
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
                dp(4),
                0,
                0
        );

        row.addView(statusText);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                0,
                0,
                dp(10)
        );

        weakTopicsContainer.addView(
                row,
                params
        );
    }

    private void showNoWeakTopics() {

        if (!isScreenActive() ||
                weakTopicsContainer == null) {
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
                dp(10),
                dp(15),
                dp(10),
                dp(15)
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

    @Override
    protected void onDestroy() {

        dashboardLoading = false;

        if (pointsValue != null) {
            pointsValue.animate().cancel();
        }

        if (streakValue != null) {
            streakValue.animate().cancel();
        }

        if (accuracyValue != null) {
            accuracyValue.animate().cancel();
        }

        super.onDestroy();
    }
}

