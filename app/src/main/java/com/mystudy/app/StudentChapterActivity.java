package com.mystudy.app;

import android.content.Intent;
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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class StudentChapterActivity extends AppCompatActivity {

    private LinearLayout root;
    private LinearLayout chapterContainer;

    private TextView statusView;
    private TextView countView;

    private FirebaseFirestore db;

    private String classId = "";
    private String className = "";
    private String studentMedium = "";
    private String studentName = "";
    private String subjectId = "";
    private String subjectName = "";

    private final int backgroundColor = Color.rgb(247, 249, 255);
    private final int primaryColor = Color.rgb(59, 91, 219);
    private final int darkBlue = Color.rgb(35, 55, 120);
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
        subjectId = getIntent().getStringExtra("subjectId");
        subjectName = getIntent().getStringExtra("subjectName");

        if (classId == null || classId.trim().isEmpty()
                || subjectId == null || subjectId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Subject information missing",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        if (className == null) className = "";
        if (studentMedium == null) studentMedium = "";
        if (studentName == null) studentName = "";
        if (subjectName == null) subjectName = "";

        db = FirebaseFirestore.getInstance();

        createUI();
        loadChapters();
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
                dp(30)
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

        backButton.setOnClickListener(v -> finish());

        LinearLayout brandContainer = new LinearLayout(this);
        brandContainer.setOrientation(LinearLayout.VERTICAL);
        brandContainer.setPadding(dp(12), 0, 0, 0);

        TextView brand = createText(
                "📘 MyStudy",
                21,
                darkBlue,
                true
        );

        TextView tagline = createText(
                "Learn • Practice • Grow",
                11,
                textSecondary,
                false
        );

        brandContainer.addView(brand);
        brandContainer.addView(tagline);

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
                23,
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

        // HERO CARD

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

        heroParams.setMargins(
                0,
                dp(24),
                0,
                dp(20)
        );

        root.addView(hero, heroParams);

        TextView heroLabel = createText(
                "YOUR LEARNING JOURNEY",
                11,
                Color.rgb(225, 232, 255),
                true
        );

        hero.addView(heroLabel);

        TextView heroTitle = createText(
                "Explore Chapters 📚",
                25,
                Color.WHITE,
                true
        );

        heroTitle.setPadding(
                0,
                dp(12),
                0,
                dp(8)
        );

        hero.addView(heroTitle);

        TextView heroDescription = createText(
                "Every chapter is a new opportunity to learn something amazing!",
                14,
                Color.WHITE,
                false
        );

        heroDescription.setLineSpacing(dp(3), 1f);
        hero.addView(heroDescription);

        // SUBJECT INFORMATION CARD

        LinearLayout infoCard = new LinearLayout(this);
        infoCard.setOrientation(LinearLayout.VERTICAL);

        infoCard.setPadding(
                dp(17),
                dp(16),
                dp(17),
                dp(16)
        );

        infoCard.setBackground(
                roundedBackground(Color.WHITE, 18)
        );

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        infoParams.setMargins(
                0,
                0,
                0,
                dp(22)
        );

        root.addView(infoCard, infoParams);

        TextView infoLabel = createText(
                "YOUR SELECTED SUBJECT",
                11,
                textSecondary,
                true
        );

        infoCard.addView(infoLabel);

        TextView subjectText = createText(
                "📚  " + (
                        subjectName.trim().isEmpty()
                                ? "Your Subject"
                                : subjectName
                ),
                19,
                textPrimary,
                true
        );

        subjectText.setPadding(
                0,
                dp(10),
                0,
                dp(8)
        );

        infoCard.addView(subjectText);

        TextView classText = createText(
                "🎒  " + (
                        className.trim().isEmpty()
                                ? "Class"
                                : className
                ),
                13,
                textSecondary,
                false
        );

        infoCard.addView(classText);

        TextView mediumText = createText(
                "🌐  Medium: " + (
                        studentMedium.trim().isEmpty()
                                ? "Not selected"
                                : studentMedium
                ),
                13,
                textSecondary,
                false
        );

        mediumText.setPadding(0, dp(6), 0, 0);
        infoCard.addView(mediumText);

        // CHAPTER SECTION HEADER

        LinearLayout sectionHeader = new LinearLayout(this);
        sectionHeader.setOrientation(LinearLayout.HORIZONTAL);
        sectionHeader.setGravity(Gravity.CENTER_VERTICAL);

        TextView sectionTitle = createText(
                "Your Chapters",
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

        // STATUS VIEW

        statusView = createText(
                "Loading chapters...",
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

        // CHAPTER CONTAINER

        chapterContainer = new LinearLayout(this);
        chapterContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        root.addView(
                chapterContainer,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        // FOOTER

        TextView footer = createText(
                "✨ One chapter at a time, one step closer to success!",
                12,
                textSecondary,
                false
        );

        footer.setGravity(Gravity.CENTER);

        footer.setPadding(
                dp(5),
                dp(25),
                dp(5),
                dp(5)
        );

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

    private void loadChapters() {

        if (studentMedium.trim().isEmpty()) {
            showEmpty(
                    "Medium information is missing. Please login again."
            );
            return;
        }

        showLoading();

        db.collection("chapters")
                .whereEqualTo("classId", classId)
                .whereEqualTo("subjectId", subjectId)
                .whereEqualTo("medium", studentMedium)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    chapterContainer.removeAllViews();

                    if (querySnapshot.isEmpty()) {
                        showEmpty(
                                "No chapters available for this subject yet."
                        );
                        return;
                    }

                    int chapterCount = querySnapshot.size();

                    statusView.setText(
                            "Choose from your "
                                    + chapterCount
                                    + " available chapter"
                                    + (chapterCount == 1 ? "" : "s")
                    );

                    statusView.setTextColor(textSecondary);

                    countView.setText(
                            chapterCount + " CHAPTERS"
                    );

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String chapterId = document.getId();

                        String chapterName =
                                document.getString("name");

                        if (chapterName == null ||
                                chapterName.trim().isEmpty()) {

                            chapterName =
                                    document.getString("chapterName");
                        }

                        if (chapterName == null ||
                                chapterName.trim().isEmpty()) {

                            chapterName = "Chapter";
                        }

                        addChapterCard(
                                chapterId,
                                chapterName
                        );
                    }
                })
                .addOnFailureListener(error -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    showError(
                            "Unable to load chapters. Please try again."
                    );

                    Toast.makeText(
                            StudentChapterActivity.this,
                            "Unable to load chapters",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void addChapterCard(
            String chapterId,
            String chapterName
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

        // CHAPTER ICON

        TextView icon = createText(
                "📘",
                29,
                textPrimary,
                false
        );

        icon.setGravity(Gravity.CENTER);

        icon.setBackground(
                roundedBackground(
                        Color.rgb(229, 236, 255),
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

        // CHAPTER DETAILS

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);

        details.setPadding(
                dp(14),
                0,
                dp(6),
                0
        );

        TextView name = createText(
                chapterName,
                18,
                textPrimary,
                true
        );

        TextView description = createText(
                "Tap to explore lessons",
                12,
                textSecondary,
                false
        );

        description.setPadding(
                0,
                dp(5),
                0,
                0
        );

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

        chapterContainer.addView(card, cardParams);

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

        // NAVIGATION TO LESSON SCREEN

        card.setOnClickListener(view -> {

            Intent intent = new Intent(
                    StudentChapterActivity.this,
                    StudentLessonActivity.class
            );

            intent.putExtra("classId", classId);
            intent.putExtra("className", className);
            intent.putExtra("studentMedium", studentMedium);
            intent.putExtra("studentName", studentName);

            intent.putExtra("subjectId", subjectId);
            intent.putExtra("subjectName", subjectName);

            intent.putExtra("chapterId", chapterId);
            intent.putExtra("chapterName", chapterName);

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

    private void showLoading() {

        if (statusView != null) {
            statusView.setText("⏳ Loading your chapters...");
            statusView.setTextColor(primaryColor);
        }

        if (countView != null) {
            countView.setText("LOADING");
        }

        if (chapterContainer != null) {

            chapterContainer.removeAllViews();

            TextView loading = createText(
                    "📚  Please wait while we prepare your chapters.",
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

            chapterContainer.addView(loading);
        }
    }

    private void showEmpty(String message) {

        if (chapterContainer == null) {
            return;
        }

        chapterContainer.removeAllViews();

        if (statusView != null) {
            statusView.setText("No chapters available");
            statusView.setTextColor(textSecondary);
        }

        if (countView != null) {
            countView.setText("0 CHAPTERS");
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

        title.setPadding(
                0,
                dp(12),
                0,
                dp(8)
        );

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

        chapterContainer.addView(
                emptyCard,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }

    private void showError(String message) {

        if (chapterContainer == null) {
            return;
        }

        chapterContainer.removeAllViews();

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

        errorText.setPadding(
                0,
                dp(12),
                0,
                dp(18)
        );

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

        retry.setOnClickListener(v -> loadChapters());

        errorCard.addView(icon);
        errorCard.addView(errorText);
        errorCard.addView(retry);

        chapterContainer.addView(
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
