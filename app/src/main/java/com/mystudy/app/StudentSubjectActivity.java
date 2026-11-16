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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class StudentSubjectActivity extends AppCompatActivity {

    private LinearLayout root;
    private LinearLayout subjectContainer;
    private TextView statusView;
    private TextView countView;

    private FirebaseFirestore db;

    private String classId = "";
    private String className = "";
    private String studentMedium = "";
    private String studentName = "";

    private final int backgroundColor = Color.rgb(247, 249, 255);
    private final int primaryColor = Color.rgb(59, 91, 219);
    private final int darkBlue = Color.rgb(35, 55, 120);
    private final int yellowColor = Color.rgb(255, 207, 64);
    private final int textPrimary = Color.rgb(25, 35, 65);
    private final int textSecondary = Color.rgb(105, 116, 145);
    private final int errorColor = Color.rgb(220, 53, 69);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");
        studentMedium = getIntent().getStringExtra("studentMedium");
        studentName = getIntent().getStringExtra("studentName");

        if (classId == null || classId.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Class information missing",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        if (className == null) className = "";
        if (studentMedium == null) studentMedium = "";
        if (studentName == null) studentName = "";

        db = FirebaseFirestore.getInstance();

        createUI();
        loadSubjects();
    }

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    private GradientDrawable roundedBackground(
            int color,
            int radius
    ) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private TextView createText(
            String text,
            int size,
            int color,
            boolean bold
    ) {
        TextView view = new TextView(this);

        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }

        return view;
    }

    private void createUI() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(backgroundColor);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(
                dp(20),
                dp(18),
                dp(20),
                dp(32)
        );

        // TOP BAR

        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);

        TextView backButton = createText(
                "‹",
                36,
                darkBlue,
                true
        );

        backButton.setGravity(Gravity.CENTER);
        backButton.setBackground(
                roundedBackground(Color.WHITE, 16)
        );

        topBar.addView(
                backButton,
                new LinearLayout.LayoutParams(
                        dp(46),
                        dp(46)
                )
        );

        backButton.setOnClickListener(v -> onBackPressed());

        LinearLayout brandContainer = new LinearLayout(this);
        brandContainer.setOrientation(LinearLayout.VERTICAL);
        brandContainer.setPadding(dp(12), 0, 0, 0);

        TextView brand = createText(
                "📘 MyStudy",
                21,
                darkBlue,
                true
        );

        TextView brandSubtitle = createText(
                "Learn • Practice • Grow",
                11,
                textSecondary,
                false
        );

        brandContainer.addView(brand);
        brandContainer.addView(brandSubtitle);

        topBar.addView(
                brandContainer,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        TextView profileIcon = createText(
                "🎓",
                24,
                darkBlue,
                false
        );

        profileIcon.setGravity(Gravity.CENTER);
        profileIcon.setBackground(
                roundedBackground(Color.WHITE, 16)
        );

        topBar.addView(
                profileIcon,
                new LinearLayout.LayoutParams(
                        dp(46),
                        dp(46)
                )
        );

        root.addView(topBar);

        // HERO SECTION

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(
                dp(22),
                dp(22),
                dp(22),
                dp(22)
        );

        hero.setBackground(
                roundedBackground(primaryColor, 24)
        );

        LinearLayout.LayoutParams heroParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        heroParams.setMargins(0, dp(24), 0, dp(22));

        root.addView(hero, heroParams);

        TextView welcomeLabel = createText(
                "YOUR LEARNING JOURNEY",
                11,
                Color.rgb(225, 232, 255),
                true
        );

        hero.addView(welcomeLabel);

        TextView heading = createText(
                "Choose Your Subject 📚",
                25,
                Color.WHITE,
                true
        );

        heading.setPadding(0, dp(12), 0, dp(8));
        hero.addView(heading);

        TextView heroDescription = createText(
                "Explore interesting chapters and start learning something new every day!",
                14,
                Color.WHITE,
                false
        );

        heroDescription.setLineSpacing(dp(3), 1f);
        hero.addView(heroDescription);

        // CLASS INFORMATION

        LinearLayout infoCard = new LinearLayout(this);
        infoCard.setOrientation(LinearLayout.VERTICAL);
        infoCard.setPadding(
                dp(17),
                dp(15),
                dp(17),
                dp(15)
        );

        infoCard.setBackground(
                roundedBackground(Color.WHITE, 18)
        );

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        infoParams.setMargins(0, 0, 0, dp(22));

        root.addView(infoCard, infoParams);

        TextView infoTitle = createText(
                "YOUR CLASS DETAILS",
                11,
                textSecondary,
                true
        );

        infoCard.addView(infoTitle);

        TextView classText = createText(
                "🎒  " + (
                        className.trim().isEmpty()
                                ? "Your Class"
                                : className
                ),
                18,
                textPrimary,
                true
        );

        classText.setPadding(0, dp(10), 0, dp(5));
        infoCard.addView(classText);

        TextView mediumText = createText(
                "🌐  Medium: " + (
                        studentMedium.trim().isEmpty()
                                ? "Not selected"
                                : studentMedium
                ),
                14,
                textSecondary,
                false
        );

        infoCard.addView(mediumText);

        // SUBJECT SECTION TITLE

        LinearLayout sectionHeader = new LinearLayout(this);
        sectionHeader.setOrientation(LinearLayout.HORIZONTAL);
        sectionHeader.setGravity(Gravity.CENTER_VERTICAL);

        TextView sectionTitle = createText(
                "Start Learning",
                21,
                textPrimary,
                true
        );

        sectionHeader.addView(
                sectionTitle,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        countView = createText(
                "",
                12,
                primaryColor,
                true
        );

        countView.setGravity(Gravity.CENTER);
        countView.setPadding(
                dp(10),
                dp(7),
                dp(10),
                dp(7)
        );

        countView.setBackground(
                roundedBackground(
                        Color.rgb(229, 236, 255),
                        20
                )
        );

        sectionHeader.addView(countView);

        root.addView(sectionHeader);

        // STATUS

        statusView = createText(
                "Loading subjects...",
                13,
                primaryColor,
                false
        );

        statusView.setPadding(
                0,
                dp(10),
                0,
                dp(12)
        );

        root.addView(statusView);

        // SUBJECT CARDS

        subjectContainer = new LinearLayout(this);
        subjectContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        root.addView(
                subjectContainer,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        // FOOTER

        TextView footer = createText(
                "✨ Keep learning, keep growing!",
                13,
                textSecondary,
                false
        );

        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(25), 0, dp(5));

        root.addView(footer);

        scrollView.addView(root);
        setContentView(scrollView);

        // ENTRY ANIMATION

        root.setAlpha(0f);
        root.setTranslationY(dp(15));

        root.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(450)
                .start();
    }

    private void loadSubjects() {

        if (studentMedium.trim().isEmpty()) {
            showEmpty(
                    "Medium information is missing. Please update your profile."
            );
            return;
        }

        showLoading();

        db.collection("subjects")
                .whereEqualTo("classId", classId)
                .whereEqualTo("medium", studentMedium)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    subjectContainer.removeAllViews();

                    if (querySnapshot.isEmpty()) {
                        showEmpty(
                                "No subjects available for "
                                        + className
                                        + " - "
                                        + studentMedium
                                        + " yet."
                        );
                        return;
                    }

                    int subjectCount = querySnapshot.size();

                    statusView.setText(
                            "Choose one of your "
                                    + subjectCount
                                    + " available subjects."
                    );

                    statusView.setTextColor(textSecondary);

                    countView.setText(
                            subjectCount + " SUBJECTS"
                    );

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String subjectId = document.getId();

                        String subjectName =
                                document.getString("name");

                        if (subjectName == null ||
                                subjectName.trim().isEmpty()) {

                            subjectName =
                                    document.getString("subjectName");
                        }

                        if (subjectName == null ||
                                subjectName.trim().isEmpty()) {

                            subjectName = "Subject";
                        }

                        addSubjectCard(
                                subjectId,
                                subjectName
                        );
                    }
                })
                .addOnFailureListener(error -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    showError(
                            "Unable to load subjects. Please try again."
                    );

                    Toast.makeText(
                            StudentSubjectActivity.this,
                            "Unable to load subjects",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void addSubjectCard(
            String subjectId,
            String subjectName
    ) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);

        card.setPadding(
                dp(16),
                dp(16),
                dp(12),
                dp(16)
        );

        card.setBackground(
                roundedBackground(Color.WHITE, 22)
        );

        card.setElevation(dp(3));

        // SUBJECT ICON

        TextView icon = createText(
                getSubjectIcon(subjectName),
                29,
                textPrimary,
                false
        );

        icon.setGravity(Gravity.CENTER);

        icon.setBackground(
                roundedBackground(
                        getSubjectIconBackground(subjectName),
                        18
                )
        );

        card.addView(
                icon,
                new LinearLayout.LayoutParams(
                        dp(62),
                        dp(62)
                )
        );

        // SUBJECT DETAILS

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);

        details.setPadding(
                dp(14),
                0,
                dp(6),
                0
        );

        TextView name = createText(
                subjectName,
                18,
                textPrimary,
                true
        );

        TextView description = createText(
                "Explore chapters and lessons",
                12,
                textSecondary,
                false
        );

        description.setPadding(0, dp(5), 0, 0);

        details.addView(name);
        details.addView(description);

        card.addView(
                details,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        // ARROW

        TextView arrow = createText(
                "›",
                32,
                primaryColor,
                true
        );

        arrow.setGravity(Gravity.CENTER);

        card.addView(
                arrow,
                new LinearLayout.LayoutParams(
                        dp(30),
                        dp(55)
                )
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        cardParams.setMargins(
                0,
                dp(7),
                0,
                dp(12)
        );

        subjectContainer.addView(card, cardParams);

        // TOUCH ANIMATION

        card.setOnTouchListener((view, event) -> {

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

        // NAVIGATION

        card.setOnClickListener(view -> {

            Intent intent = new Intent(
                    StudentSubjectActivity.this,
                    StudentChapterActivity.class
            );

            intent.putExtra("classId", classId);
            intent.putExtra("className", className);
            intent.putExtra("studentMedium", studentMedium);
            intent.putExtra("studentName", studentName);
            intent.putExtra("subjectId", subjectId);
            intent.putExtra("subjectName", subjectName);

            startActivity(intent);

            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );
        });

        // CARD ENTRY ANIMATION

        card.setAlpha(0f);
        card.setTranslationY(dp(12));

        card.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(350)
                .start();
    }

    private String getSubjectIcon(String subjectName) {

        if (subjectName == null) return "📚";

        String value = subjectName.toLowerCase();

        if (value.contains("math")) return "🔢";
        if (value.contains("english")) return "🔤";
        if (value.contains("marathi")) return "📕";
        if (value.contains("evs")) return "🌱";
        if (value.contains("hindi")) return "📗";
        if (value.contains("science")) return "🔬";
        if (value.contains("computer")) return "💻";
        if (value.contains("history")) return "🏛️";
        if (value.contains("geography")) return "🌍";

        return "📚";
    }

    private int getSubjectIconBackground(String subjectName) {

        if (subjectName == null) {
            return Color.rgb(238, 242, 255);
        }

        String value = subjectName.toLowerCase();

        if (value.contains("math")) {
            return Color.rgb(255, 247, 204);
        }

        if (value.contains("english")) {
            return Color.rgb(219, 234, 254);
        }

        if (value.contains("marathi")) {
            return Color.rgb(254, 226, 226);
        }

        if (value.contains("evs")) {
            return Color.rgb(220, 252, 231);
        }

        if (value.contains("hindi")) {
            return Color.rgb(220, 252, 231);
        }

        if (value.contains("science")) {
            return Color.rgb(224, 231, 255);
        }

        if (value.contains("computer")) {
            return Color.rgb(224, 242, 254);
        }

        return Color.rgb(238, 242, 255);
    }

    private void showLoading() {

        if (statusView != null) {
            statusView.setText("⏳ Loading your subjects...");
            statusView.setTextColor(primaryColor);
        }

        if (countView != null) {
            countView.setText("LOADING");
        }

        if (subjectContainer != null) {
            subjectContainer.removeAllViews();

            TextView loading = createText(
                    "📚  Please wait while we prepare your subjects.",
                    14,
                    textSecondary,
                    false
            );

            loading.setGravity(Gravity.CENTER);
            loading.setPadding(
                    dp(15),
                    dp(35),
                    dp(15),
                    dp(35)
            );

            subjectContainer.addView(loading);
        }
    }

    private void showEmpty(String message) {

        if (subjectContainer == null) return;

        subjectContainer.removeAllViews();

        if (statusView != null) {
            statusView.setText("No subjects available");
            statusView.setTextColor(textSecondary);
        }

        if (countView != null) {
            countView.setText("0 SUBJECTS");
        }

        LinearLayout emptyCard = new LinearLayout(this);
        emptyCard.setOrientation(LinearLayout.VERTICAL);
        emptyCard.setGravity(Gravity.CENTER);
        emptyCard.setPadding(
                dp(22),
                dp(30),
                dp(22),
                dp(30)
        );

        emptyCard.setBackground(
                roundedBackground(Color.WHITE, 22)
        );

        TextView icon = createText(
                "📚",
                42,
                textPrimary,
                false
        );

        icon.setGravity(Gravity.CENTER);

        TextView title = createText(
                "Nothing here yet!",
                19,
                textPrimary,
                true
        );

        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(12), 0, dp(8));

        TextView description = createText(
                message,
                14,
                textSecondary,
                false
        );

        description.setGravity(Gravity.CENTER);
        description.setLineSpacing(dp(3), 1f);

        emptyCard.addView(icon);
        emptyCard.addView(title);
        emptyCard.addView(description);

        subjectContainer.addView(
                emptyCard,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }

    private void showError(String message) {

        if (subjectContainer == null) return;

        subjectContainer.removeAllViews();

        if (statusView != null) {
            statusView.setText("Something went wrong");
            statusView.setTextColor(errorColor);
        }

        if (countView != null) {
            countView.setText("ERROR");
        }

        LinearLayout errorCard = new LinearLayout(this);
        errorCard.setOrientation(LinearLayout.VERTICAL);
        errorCard.setGravity(Gravity.CENTER);
        errorCard.setPadding(
                dp(22),
                dp(28),
                dp(22),
                dp(28)
        );

        errorCard.setBackground(
                roundedBackground(Color.WHITE, 22)
        );

        TextView icon = createText(
                "⚠️",
                38,
                errorColor,
                false
        );

        icon.setGravity(Gravity.CENTER);

        TextView errorText = createText(
                message,
                14,
                errorColor,
                false
        );

        errorText.setGravity(Gravity.CENTER);
        errorText.setPadding(0, dp(12), 0, dp(18));

        TextView retry = createText(
                "Try Again",
                15,
                Color.WHITE,
                true
        );

        retry.setGravity(Gravity.CENTER);
        retry.setPadding(
                dp(24),
                dp(12),
                dp(24),
                dp(12)
        );

        retry.setBackground(
                roundedBackground(primaryColor, 25)
        );

        retry.setOnClickListener(v -> loadSubjects());

        errorCard.addView(icon);
        errorCard.addView(errorText);
        errorCard.addView(retry);

        subjectContainer.addView(
                errorCard,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (root != null) {
            root.animate().cancel();
        }
    }
}
