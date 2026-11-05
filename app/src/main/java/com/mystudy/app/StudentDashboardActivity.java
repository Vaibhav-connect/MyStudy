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

    private static final int NAVY = Color.rgb(25, 48, 91);
    private static final int BLUE = Color.rgb(50, 105, 220);
    private static final int PALE_BLUE = Color.rgb(235, 243, 255);
    private static final int YELLOW = Color.rgb(255, 205, 70);
    private static final int TEXT_DARK = Color.rgb(31, 45, 70);
    private static final int TEXT_MUTED = Color.rgb(112, 128, 150);
    private static final int PAGE = Color.rgb(246, 249, 254);

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
    private boolean firstLoadStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        studentName = getIntent().getStringExtra("studentName");
        if (studentName == null || studentName.trim().isEmpty()) {
            studentName = "Student";
        }

        createDashboard();
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (db != null && auth != null && isScreenActive()) {
            loadDashboardStats();
        }
    }

    private boolean isScreenActive() {
        return !isFinishing() && !isDestroyed();
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) {
            view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }
        return view;
    }

    private LinearLayout vertical() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private void createDashboard() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setClipToPadding(false);
        scrollView.setBackgroundColor(PAGE);

        LinearLayout root = vertical();
        root.setPadding(dp(18), dp(18), dp(18), dp(30));

        LinearLayout topBar = new LinearLayout(this);
        topBar.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout brand = vertical();
        TextView brandName = text("MyStudy", 22, NAVY, true);
        TextView brandTag = text("LEARN  •  PRACTICE  •  GROW", 10, TEXT_MUTED, true);
        brand.addView(brandName);
        brand.addView(brandTag);
        topBar.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));

        TextView settings = text("⚙", 25, NAVY, true);
        settings.setGravity(Gravity.CENTER);
        settings.setBackground(shape(Color.WHITE, 18));
        settings.setElevation(dp(3));
        topBar.addView(settings, new LinearLayout.LayoutParams(dp(48), dp(48)));
        settings.setOnClickListener(v -> openActivity(SettingsActivity.class, null));
        addPressAnimation(settings);
        root.addView(topBar);

        LinearLayout hero = vertical();
        hero.setPadding(dp(20), dp(22), dp(20), dp(20));
        hero.setBackground(shape(NAVY, 26));
        hero.setElevation(dp(4));

        TextView welcome = text("Hello, " + studentName + "! 👋", 25, Color.WHITE, true);
        TextView welcomeSub = text("Ready for a little learning adventure today?", 14,
                Color.rgb(222, 233, 255), false);
        welcomeSub.setPadding(0, dp(7), 0, dp(16));
        hero.addView(welcome);
        hero.addView(welcomeSub);

        LinearLayout heroPill = new LinearLayout(this);
        heroPill.setGravity(Gravity.CENTER_VERTICAL);
        heroPill.setPadding(dp(12), dp(8), dp(12), dp(8));
        heroPill.setBackground(shape(Color.rgb(45, 73, 125), 16));
        TextView pillText = text("✨  Every small step makes you smarter!", 12, Color.WHITE, true);
        heroPill.addView(pillText);
        hero.addView(heroPill);
        LinearLayout.LayoutParams heroParams = new LinearLayout.LayoutParams(-1, -2);
        heroParams.setMargins(0, dp(18), 0, dp(16));
        root.addView(hero, heroParams);

        dashboardStatus = text("⏳ Loading your learning journey...", 12, BLUE, true);
        dashboardStatus.setGravity(Gravity.CENTER);
        dashboardStatus.setPadding(dp(8), dp(6), dp(8), dp(10));
        root.addView(dashboardStatus);

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.addView(createStatCard("⭐", "0", "POINTS", BLUE, pointsValue = new TextView(this)),
                weightedParams(1, 112, 4));
        stats.addView(createStatCard("🔥", "0", "DAY STREAK", Color.rgb(235, 133, 53),
                streakValue = new TextView(this)), weightedParams(1, 112, 4));
        stats.addView(createStatCard("🎯", "0%", "ACCURACY", Color.rgb(39, 160, 125),
                accuracyValue = new TextView(this)), weightedParams(1, 112, 0));
        root.addView(stats);

        addSection(root, "📚  Start Learning", "Choose a subject and explore new chapters");
        root.addView(createActionCard("📖", "Learn Subjects",
                "Choose your class, subject and chapter", BLUE,
                () -> openActivity(StudentClassActivity.class, null)));

        addSection(root, "🎯  Practice & Exams", "Learn by trying and testing yourself");
        root.addView(createActionCard("🧠", "Practice Questions",
                "Solve questions and improve your knowledge", Color.rgb(116, 91, 210),
                () -> openActivity(StudentClassActivity.class, null)));
        root.addView(createActionCard("📝", "Take Exams",
                "Attempt published exams", Color.rgb(36, 151, 132),
                () -> openActivity(StudentExamActivity.class, new String[]{"studentName", studentName})));
        root.addView(createActionCard("📜", "Exam History",
                "Check your previous exam results", Color.rgb(220, 151, 49),
                () -> openActivity(StudentExamHistoryActivity.class, null)));

        addSection(root, "👨‍👩‍👧  Family", "Keep your family connected with your learning");
        root.addView(createActionCard("🔐", "Parent Area",
                "View learning progress and student activity", Color.rgb(65, 130, 190),
                () -> openActivity(ParentPinActivity.class, null)));

        addSection(root, "📊  Your Progress", "Your learning journey, one step at a time");
        root.addView(createProgressCard());

        addSection(root, "💡  Topics to Practice", "A little revision can make a big difference");
        weakTopicsContainer = createWeakTopicsCard();
        root.addView(weakTopicsContainer);

        TextView footer = text("MyStudy  •  Learn something new every day\nDeveloped by Vaibhav Bhosale",
                11, TEXT_MUTED, false);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(28), 0, dp(8));
        root.addView(footer);

        scrollView.addView(root);
        setContentView(scrollView);
        root.setAlpha(0f);
        root.setTranslationY(dp(12));
        root.animate().alpha(1f).translationY(0f).setDuration(350).start();
    }

    private LinearLayout.LayoutParams weightedParams(float weight, int height, int rightMargin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(height), weight);
        params.setMargins(0, 0, dp(rightMargin), 0);
        return params;
    }

    private void addSection(LinearLayout root, String title, String subtitle) {
        LinearLayout section = vertical();
        TextView heading = text(title, 19, TEXT_DARK, true);
        TextView hint = text(subtitle, 12, TEXT_MUTED, false);
        hint.setPadding(0, dp(4), 0, 0);
        section.addView(heading);
        section.addView(hint);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, dp(24), 0, dp(12));
        root.addView(section, params);
    }

    private LinearLayout createStatCard(String icon, String value, String label,
                                        int accent, TextView valueView) {
        LinearLayout card = vertical();
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(4), dp(10), dp(4), dp(8));
        card.setBackground(shape(Color.WHITE, 20));
        card.setElevation(dp(3));

        TextView iconView = text(icon, 20, accent, true);
        iconView.setGravity(Gravity.CENTER);
        card.addView(iconView);

        valueView.setText(value);
        valueView.setTextSize(18);
        valueView.setTextColor(accent);
        valueView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        valueView.setGravity(Gravity.CENTER);
        card.addView(valueView);

        TextView labelView = text(label, 9, TEXT_MUTED, true);
        labelView.setGravity(Gravity.CENTER);
        card.addView(labelView);
        addPressAnimation(card);
        return card;
    }

    private LinearLayout createActionCard(String icon, String title, String description,
                                          int accent, Runnable action) {
        LinearLayout card = new LinearLayout(this);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), dp(13), dp(14), dp(13));
        card.setBackground(shape(Color.WHITE, 20));
        card.setElevation(dp(2));

        TextView iconView = text(icon, 27, accent, true);
        iconView.setGravity(Gravity.CENTER);
        iconView.setBackground(shape(PALE_BLUE, 16));
        card.addView(iconView, new LinearLayout.LayoutParams(dp(54), dp(54)));

        LinearLayout details = vertical();
        details.setPadding(dp(13), 0, dp(5), 0);
        details.addView(text(title, 16, TEXT_DARK, true));
        TextView desc = text(description, 12, TEXT_MUTED, false);
        desc.setPadding(0, dp(4), 0, 0);
        details.addView(desc);
        card.addView(details, new LinearLayout.LayoutParams(0, -2, 1));

        TextView arrow = text("›", 29, accent, true);
        arrow.setGravity(Gravity.CENTER);
        card.addView(arrow, new LinearLayout.LayoutParams(dp(25), dp(45)));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 0, 0, dp(10));
        card.setLayoutParams(params);
        addPressAnimation(card);
        card.setOnClickListener(v -> {
            if (isScreenActive()) action.run();
        });
        return card;
    }

    private LinearLayout createProgressCard() {
        LinearLayout card = vertical();
        card.setPadding(dp(18), dp(17), dp(18), dp(17));
        card.setBackground(shape(Color.WHITE, 22));
        card.setElevation(dp(2));

        card.addView(text("Learning Progress", 16, TEXT_DARK, true));
        lessonProgressValue = text("⏳ Loading lessons...", 13, TEXT_MUTED, false);
        lessonProgressValue.setPadding(0, dp(10), 0, dp(8));
        card.addView(lessonProgressValue);

        TextView encouragement = text("🏅 Keep learning to earn badges!", 12, BLUE, true);
        card.addView(encouragement);
        return card;
    }

    private LinearLayout createWeakTopicsCard() {
        LinearLayout card = vertical();
        card.setPadding(dp(16), dp(15), dp(16), dp(15));
        card.setBackground(shape(Color.WHITE, 22));
        card.setElevation(dp(2));

        TextView loading = text("⏳ Checking your practice performance...", 13, TEXT_MUTED, false);
        loading.setGravity(Gravity.CENTER);
        card.addView(loading);
        return card;
    }

    private void openActivity(Class<?> target, String[] extra) {
        if (!isScreenActive()) return;
        Intent intent = new Intent(StudentDashboardActivity.this, target);
        if (extra != null && extra.length == 2) {
            intent.putExtra(extra[0], extra[1]);
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void loadDashboardStats() {
        if (!isScreenActive() || dashboardLoading) return;

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            showDashboardError("Please login again to load your dashboard.");
            return;
        }

        dashboardLoading = true;
        showDashboardLoading();
        String userId = user.getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    if (!isScreenActive()) {
                        dashboardLoading = false;
                        return;
                    }
                    if (!document.exists()) {
                        dashboardLoading = false;
                        showDashboardError("Your profile data was not found.");
                        return;
                    }

                    Long points = document.getLong("points");
                    Long streak = document.getLong("streak");
                    Double overallAccuracy = document.getDouble("overallAccuracy");

                    pointsValue.setText(points != null ? String.valueOf(points) : "0");
                    streakValue.setText(streak != null ? String.valueOf(streak) : "0");
                    accuracyValue.setText(overallAccuracy != null
                            ? String.format(Locale.US, "%.0f%%", overallAccuracy) : "0%");

                    dashboardStatus.setText("✅ Your dashboard is up to date");
                    dashboardStatus.setTextColor(Color.rgb(39, 145, 112));
                    dashboardLoading = false;
                    loadWeakTopics(document);
                })
                .addOnFailureListener(e -> {
                    if (!isScreenActive()) {
                        dashboardLoading = false;
                        return;
                    }
                    dashboardLoading = false;
                    showDashboardError("Unable to load profile data. Please try again.");
                    accuracyValue.setText("0%");
                });

        loadQuizAccuracy(userId);
        loadCompletedLessons(userId);
    }

    private void showDashboardLoading() {
        if (dashboardStatus != null) {
            dashboardStatus.setText("⏳ Loading your latest learning data...");
            dashboardStatus.setTextColor(BLUE);
        }
        if (lessonProgressValue != null) {
            lessonProgressValue.setText("⏳ Loading lessons...");
        }
    }

    private void showDashboardError(String message) {
        if (dashboardStatus != null) {
            dashboardStatus.setText("⚠️ " + message);
            dashboardStatus.setTextColor(Color.rgb(210, 65, 65));
        }
        if (isScreenActive()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadQuizAccuracy(String userId) {
        totalQuizScore = 0;
        totalQuizQuestions = 0;

        db.collection("quizProgress").whereEqualTo("userId", userId).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!isScreenActive()) return;

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Long score = document.getLong("score");
                        Long total = document.getLong("totalQuestions");
                        if (score != null) totalQuizScore += score.intValue();
                        if (total != null) totalQuizQuestions += total.intValue();
                    }

                    if (totalQuizQuestions > 0) {
                        double accuracy = totalQuizScore * 100.0 / totalQuizQuestions;
                        accuracyValue.setText(String.format(Locale.US, "%.0f%%", accuracy));
                    } else {
                        accuracyValue.setText("0%");
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isScreenActive()) return;
                    accuracyValue.setText("0%");
                    dashboardStatus.setText("⚠️ Some practice data could not be loaded.");
                    dashboardStatus.setTextColor(Color.rgb(210, 65, 65));
                });
    }

    private void loadCompletedLessons(String userId) {
        db.collection("lessonProgress")
                .whereEqualTo("userId", userId)
                .whereEqualTo("completed", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!isScreenActive()) return;
                    completedLessons = querySnapshot.size();
                    lessonProgressValue.setText(completedLessons == 0
                            ? "No lessons completed yet"
                            : completedLessons + " lessons completed");
                })
                .addOnFailureListener(e -> {
                    if (!isScreenActive()) return;
                    lessonProgressValue.setText("Unable to load lesson progress");
                    dashboardStatus.setText("⚠️ Lesson progress could not be loaded.");
                    dashboardStatus.setTextColor(Color.rgb(210, 65, 65));
                });
    }

    private void loadWeakTopics(DocumentSnapshot document) {
        if (!isScreenActive() || weakTopicsContainer == null) return;
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
            if (entry.getKey() == null) continue;
            String chapterId = String.valueOf(entry.getKey());
            if (!chapterId.trim().isEmpty()) chapterIds.add(chapterId);
        }

        if (chapterIds.isEmpty()) {
            showNoWeakTopics();
            return;
        }

        for (String chapterId : chapterIds) {
            Object topicObject = weakTopics.get(chapterId);
            double topicAccuracy = 0;
            String status = "needs_practice";

            if (topicObject instanceof Map) {
                Map<?, ?> topicData = (Map<?, ?>) topicObject;
                Object accuracyObject = topicData.get("accuracy");
                Object statusObject = topicData.get("status");
                if (accuracyObject instanceof Number) {
                    topicAccuracy = ((Number) accuracyObject).doubleValue();
                }
                if (statusObject != null) status = String.valueOf(statusObject);
            }
            loadChapterNameAndAddTopic(chapterId, topicAccuracy, status);
        }
    }

    private void loadChapterNameAndAddTopic(String chapterId, double accuracy, String status) {
        db.collection("chapters").document(chapterId).get()
                .addOnSuccessListener(document -> {
                    if (!isScreenActive()) return;
                    String chapterName = document.getString("name");
                    if (chapterName == null || chapterName.trim().isEmpty()) {
                        chapterName = document.getString("chapterName");
                    }
                    if (chapterName == null || chapterName.trim().isEmpty()) {
                        chapterName = "Chapter";
                    }
                    addWeakTopicRow(chapterName, accuracy, status);
                })
                .addOnFailureListener(e -> {
                    if (isScreenActive()) addWeakTopicRow("Chapter", accuracy, status);
                });
    }

    private void addWeakTopicRow(String chapterName, double accuracy, String status) {
        if (!isScreenActive() || weakTopicsContainer == null) return;

        LinearLayout row = vertical();
        row.setPadding(dp(13), dp(11), dp(13), dp(11));
        row.setBackground(shape(PAGE, 16));

        TextView title = text("📌 " + chapterName, 14, TEXT_DARK, true);
        row.addView(title);

        TextView accuracyText = text(
                String.format(Locale.US, "Accuracy: %.0f%%", accuracy),
                12, TEXT_MUTED, false);
        accuracyText.setPadding(0, dp(5), 0, 0);
        row.addView(accuracyText);

        String statusLabel;
        if ("weak".equalsIgnoreCase(status)) {
            statusLabel = "⚠️ Needs more practice";
        } else if ("needs_practice".equalsIgnoreCase(status)) {
            statusLabel = "📚 Practice again";
        } else {
            statusLabel = "✅ Good progress";
        }

        TextView statusText = text(statusLabel, 12, BLUE, true);
        statusText.setPadding(0, dp(4), 0, 0);
        row.addView(statusText);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 0, 0, dp(8));
        weakTopicsContainer.addView(row, params);
    }

    private void showNoWeakTopics() {
        if (!isScreenActive() || weakTopicsContainer == null) return;
        weakTopicsContainer.removeAllViews();

        TextView message = text(
                "🌟 No weak topics yet!\nComplete some practice to see where you can improve.",
                13, TEXT_MUTED, false);
        message.setGravity(Gravity.CENTER);
        message.setPadding(dp(8), dp(14), dp(8), dp(14));
        weakTopicsContainer.addView(message);
    }

    private void addPressAnimation(View view) {
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.98f).scaleY(0.98f).setDuration(90).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        dashboardLoading = false;
        if (pointsValue != null) pointsValue.animate().cancel();
        if (streakValue != null) streakValue.animate().cancel();
        if (accuracyValue != null) accuracyValue.animate().cancel();
        super.onDestroy();
    }
}
