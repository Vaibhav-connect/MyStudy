```java
package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class StudentLessonActivity extends AppCompatActivity {

    private LinearLayout lessonContainer;
    private TextView statusView;
    private TextView countView;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressManager progressManager;

    private String classId = "";
    private String className = "";
    private String studentMedium = "";
    private String studentName = "";
    private String subjectId = "";
    private String subjectName = "";
    private String chapterId = "";
    private String chapterName = "";

    private final int backgroundColor =
            Color.rgb(245, 249, 255);

    private final int primaryColor =
            Color.rgb(37, 99, 235);

    private final int darkBlue =
            Color.rgb(30, 64, 175);

    private final int textPrimary =
            Color.rgb(23, 37, 84);

    private final int textSecondary =
            Color.rgb(100, 116, 139);

    private final int yellowColor =
            Color.rgb(250, 204, 21);

    private final int successColor =
            Color.rgb(22, 163, 74);

    private final int errorColor =
            Color.rgb(220, 38, 38);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        readIntentData();

        if (classId.trim().isEmpty()
                || subjectId.trim().isEmpty()
                || chapterId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Lesson information missing",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        progressManager = new ProgressManager();

        createUI();
        loadLessons();
    }

    private void readIntentData() {

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");
        studentMedium = getIntent().getStringExtra("studentMedium");
        studentName = getIntent().getStringExtra("studentName");
        subjectId = getIntent().getStringExtra("subjectId");
        subjectName = getIntent().getStringExtra("subjectName");
        chapterId = getIntent().getStringExtra("chapterId");
        chapterName = getIntent().getStringExtra("chapterName");

        if (classId == null) classId = "";
        if (className == null) className = "";
        if (studentName == null) studentName = "";
        if (subjectId == null) subjectId = "";
        if (subjectName == null) subjectName = "";
        if (chapterId == null) chapterId = "";
        if (chapterName == null) chapterName = "";

        if (studentMedium == null
                || studentMedium.trim().isEmpty()) {
            studentMedium = "English";
        }
    }

    private void createUI() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 22, 20, 30);
        root.setBackgroundColor(backgroundColor);

        // MyStudy Header

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView logo = new TextView(this);
        logo.setText("📘");
        logo.setTextSize(30);

        header.addView(
                logo,
                new LinearLayout.LayoutParams(
                        48,
                        55
                )
        );

        LinearLayout brandLayout = new LinearLayout(this);
        brandLayout.setOrientation(LinearLayout.VERTICAL);

        TextView brand = new TextView(this);
        brand.setText("MyStudy");
        brand.setTextSize(23);
        brand.setTextColor(primaryColor);
        brand.setTypeface(null, Typeface.BOLD);

        TextView tagline = new TextView(this);
        tagline.setText("Learn • Practice • Grow");
        tagline.setTextSize(11);
        tagline.setTextColor(textSecondary);

        brandLayout.addView(brand);
        brandLayout.addView(tagline);

        header.addView(
                brandLayout,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        TextView mediumBadge = new TextView(this);
        mediumBadge.setText("🌐 " + studentMedium);
        mediumBadge.setTextSize(12);
        mediumBadge.setTextColor(darkBlue);
        mediumBadge.setTypeface(null, Typeface.BOLD);
        mediumBadge.setPadding(12, 9, 12, 9);
        mediumBadge.setBackground(
                roundedBackground(
                        Color.rgb(219, 234, 254),
                        30
                )
        );

        header.addView(mediumBadge);

        root.addView(
                header,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        // Chapter Hero Card

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(20, 20, 20, 20);
        hero.setBackground(
                roundedBackground(primaryColor, 26)
        );
        hero.setElevation(5);

        TextView heroLabel = new TextView(this);
        heroLabel.setText("YOUR LEARNING JOURNEY");
        heroLabel.setTextSize(11);
        heroLabel.setTextColor(Color.WHITE);
        heroLabel.setTypeface(null, Typeface.BOLD);

        hero.addView(heroLabel);

        TextView heroTitle = new TextView(this);
        heroTitle.setText(
                "📚 " +
                        (chapterName.trim().isEmpty()
                                ? "Lessons"
                                : chapterName)
        );
        heroTitle.setTextSize(25);
        heroTitle.setTextColor(Color.WHITE);
        heroTitle.setTypeface(null, Typeface.BOLD);
        heroTitle.setPadding(0, 12, 0, 8);

        hero.addView(heroTitle);

        TextView heroSubtitle = new TextView(this);

        String info = "";

        if (!className.trim().isEmpty()) {
            info += className;
        }

        if (!subjectName.trim().isEmpty()) {
            if (!info.isEmpty()) info += "  •  ";
            info += subjectName;
        }

        if (info.isEmpty()) {
            info = "Choose a lesson and start learning";
        }

        heroSubtitle.setText(info);
        heroSubtitle.setTextSize(14);
        heroSubtitle.setTextColor(
                Color.rgb(219, 234, 254)
        );

        hero.addView(heroSubtitle);

        TextView yellowLine = new TextView(this);
        yellowLine.setBackgroundColor(yellowColor);

        LinearLayout.LayoutParams lineParams =
                new LinearLayout.LayoutParams(
                        55,
                        5
                );

        lineParams.topMargin = 16;

        hero.addView(yellowLine, lineParams);

        LinearLayout.LayoutParams heroParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        heroParams.topMargin = 22;
        heroParams.bottomMargin = 18;

        root.addView(hero, heroParams);

        // Section Heading

        TextView sectionTitle = new TextView(this);
        sectionTitle.setText("Let's Start Learning ✨");
        sectionTitle.setTextSize(21);
        sectionTitle.setTextColor(textPrimary);
        sectionTitle.setTypeface(null, Typeface.BOLD);

        root.addView(
                sectionTitle,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        TextView sectionSubtitle = new TextView(this);
        sectionSubtitle.setText(
                "Select a lesson to read, explore and practice."
        );
        sectionSubtitle.setTextSize(13);
        sectionSubtitle.setTextColor(textSecondary);
        sectionSubtitle.setPadding(0, 5, 0, 10);

        root.addView(sectionSubtitle);

        // Lesson Count

        countView = new TextView(this);
        countView.setText("Preparing your lessons...");
        countView.setTextSize(13);
        countView.setTextColor(primaryColor);
        countView.setTypeface(null, Typeface.BOLD);
        countView.setPadding(0, 8, 0, 8);

        root.addView(countView);

        // Status

        statusView = new TextView(this);
        statusView.setTextSize(14);
        statusView.setTextColor(primaryColor);
        statusView.setGravity(Gravity.CENTER);
        statusView.setPadding(12, 12, 12, 12);

        root.addView(
                statusView,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        // Lesson Container

        lessonContainer = new LinearLayout(this);
        lessonContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        root.addView(
                lessonContainer,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        scrollView.addView(root);
        setContentView(scrollView);
    }

    private void loadLessons() {

        showLoading();

        db.collection("lessons")
                .whereEqualTo("classId", classId)
                .whereEqualTo("subjectId", subjectId)
                .whereEqualTo("chapterId", chapterId)
                .whereEqualTo("medium", studentMedium)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    lessonContainer.removeAllViews();

                    if (querySnapshot.isEmpty()) {
                        showEmpty(
                                "No lessons available for "
                                        + studentMedium
                                        + " medium yet."
                        );
                        return;
                    }

                    int lessonCount = querySnapshot.size();

                    countView.setText(
                            "📖 " + lessonCount
                                    + " LESSON"
                                    + (lessonCount == 1 ? "" : "S")
                                    + " AVAILABLE"
                    );

                    statusView.setText(
                            "Choose any lesson below and enjoy learning!"
                    );

                    statusView.setTextColor(successColor);

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String lessonId = document.getId();

                        String title = document.getString("title");
                        String description =
                                document.getString("description");
                        String content =
                                document.getString("content");
                        String contentType =
                                document.getString("contentType");
                        String contentUrl =
                                document.getString("contentUrl");

                        if (title == null || title.trim().isEmpty()) {
                            title = document.getString("lessonName");
                        }

                        if (title == null || title.trim().isEmpty()) {
                            title = "Lesson";
                        }

                        addLessonCard(
                                lessonId,
                                title,
                                description,
                                content,
                                contentType,
                                contentUrl
                        );
                    }

                })
                .addOnFailureListener(error -> {

                    showError("Unable to load lessons.");

                    Toast.makeText(
                            StudentLessonActivity.this,
                            "Unable to load lessons",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void addLessonCard(
            String lessonId,
            String title,
            String description,
            String content,
            String contentType,
            String contentUrl
    ) {

        final String selectedLessonId = lessonId;
        final String selectedLessonTitle = title;

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 18, 18, 18);
        card.setBackground(
                roundedBackground(Color.WHITE, 24)
        );
        card.setElevation(4);

        // Lesson Number / Icon

        LinearLayout heading = new LinearLayout(this);
        heading.setOrientation(LinearLayout.HORIZONTAL);
        heading.setGravity(Gravity.CENTER_VERTICAL);

        TextView icon = new TextView(this);
        icon.setText("📘");
        icon.setTextSize(27);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(
                roundedBackground(
                        Color.rgb(219, 234, 254),
                        16
                )
        );

        heading.addView(
                icon,
                new LinearLayout.LayoutParams(
                        55,
                        55
                )
        );

        TextView lessonTitle = new TextView(this);
        lessonTitle.setText(selectedLessonTitle);
        lessonTitle.setTextSize(19);
        lessonTitle.setTextColor(textPrimary);
        lessonTitle.setTypeface(null, Typeface.BOLD);
        lessonTitle.setPadding(12, 0, 0, 0);

        heading.addView(
                lessonTitle,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        card.addView(heading);

        // Medium Information

        TextView mediumText = new TextView(this);
        mediumText.setText("🌐 Medium: " + studentMedium);
        mediumText.setTextSize(12);
        mediumText.setTextColor(primaryColor);
        mediumText.setTypeface(null, Typeface.BOLD);
        mediumText.setPadding(0, 13, 0, 0);

        card.addView(mediumText);

        // Description

        if (description != null
                && !description.trim().isEmpty()) {

            TextView descriptionView = new TextView(this);
            descriptionView.setText(description);
            descriptionView.setTextSize(14);
            descriptionView.setTextColor(textSecondary);
            descriptionView.setLineSpacing(2, 1.05f);
            descriptionView.setPadding(0, 10, 0, 0);

            card.addView(descriptionView);
        }

        // Lesson Content

        if (content != null
                && !content.trim().isEmpty()) {

            LinearLayout contentBox =
                    new LinearLayout(this);

            contentBox.setOrientation(
                    LinearLayout.VERTICAL
            );

            contentBox.setPadding(14, 14, 14, 14);
            contentBox.setBackground(
                    roundedBackground(
                            Color.rgb(239, 246, 255),
                            16
                    )
            );

            TextView contentHeading = new TextView(this);
            contentHeading.setText("📖 Lesson Notes");
            contentHeading.setTextSize(14);
            contentHeading.setTextColor(darkBlue);
            contentHeading.setTypeface(null, Typeface.BOLD);

            contentBox.addView(contentHeading);

            TextView lessonContent = new TextView(this);
            lessonContent.setText(content);
            lessonContent.setTextSize(15);
            lessonContent.setTextColor(
                    Color.rgb(55, 65, 81)
            );
            lessonContent.setLineSpacing(4, 1.08f);
            lessonContent.setPadding(0, 10, 0, 0);

            contentBox.addView(lessonContent);

            LinearLayout.LayoutParams contentParams =
                    new LinearLayout.LayoutParams(
                            -1,
                            -2
                    );

            contentParams.topMargin = 14;

            card.addView(contentBox, contentParams);
        }

        // Media Content

        addMediaContent(
                card,
                contentType,
                contentUrl
        );

        // Complete Button

        Button completeButton = createButton(
                "✓  Mark Lesson Complete",
                successColor
        );

        LinearLayout.LayoutParams completeParams =
                new LinearLayout.LayoutParams(
                        -1,
                        54
                );

        completeParams.topMargin = 16;

        card.addView(completeButton, completeParams);

        // Practice Button

        Button practiceButton = createButton(
                "Start Practice  →",
                primaryColor
        );

        LinearLayout.LayoutParams practiceParams =
                new LinearLayout.LayoutParams(
                        -1,
                        54
                );

        practiceParams.topMargin = 10;

        card.addView(practiceButton, practiceParams);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        cardParams.setMargins(0, 5, 0, 18);

        lessonContainer.addView(card, cardParams);

        addPressAnimation(card);
        addPressAnimation(completeButton);
        addPressAnimation(practiceButton);

        // Complete Lesson

        completeButton.setOnClickListener(view -> {

            FirebaseUser user = auth.getCurrentUser();

            if (user == null) {

                Toast.makeText(
                        StudentLessonActivity.this,
                        "Please login again.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            completeButton.setEnabled(false);
            completeButton.setText("✓ Lesson Completed");
            completeButton.setBackground(
                    roundedBackground(
                            Color.rgb(134, 239, 172),
                            30
                    )
            );

            progressManager.saveLessonCompleted(
                    user.getUid(),
                    classId,
                    subjectId,
                    chapterId,
                    selectedLessonId
            );

            progressManager.addPoints(
                    user.getUid(),
                    10
            );

            progressManager.updateStreak(
                    user.getUid()
            );

            Toast.makeText(
                    StudentLessonActivity.this,
                    "+10 points 🎉",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Start Practice

        practiceButton.setOnClickListener(view -> {

            Intent intent = new Intent(
                    StudentLessonActivity.this,
                    StudentPracticeActivity.class
            );

            intent.putExtra("classId", classId);
            intent.putExtra("className", className);
            intent.putExtra("studentMedium", studentMedium);
            intent.putExtra("studentName", studentName);
            intent.putExtra("subjectId", subjectId);
            intent.putExtra("subjectName", subjectName);
            intent.putExtra("chapterId", chapterId);
            intent.putExtra("chapterName", chapterName);
            intent.putExtra("lessonId", selectedLessonId);
            intent.putExtra("lessonTitle", selectedLessonTitle);

            startActivity(intent);

            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );
        });

        // Entry Animation

        card.setAlpha(0f);
        card.setTranslationY(25f);

        card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(350)
                .start();
    }

    private void addMediaContent(
            LinearLayout card,
            String contentType,
            String contentUrl
    ) {

        if (contentType == null
                || contentType.trim().isEmpty()
                || contentUrl == null
                || contentUrl.trim().isEmpty()) {
            return;
        }

        String type = contentType.trim().toUpperCase();

        String buttonText;

        switch (type) {

            case "IMAGE":
                buttonText = "🖼  Open Image";
                break;

            case "PDF":
                buttonText = "📄  Open PDF";
                break;

            case "VIDEO":
                buttonText = "▶  Watch Video";
                break;

            case "AUDIO":
                buttonText = "🔊  Listen to Audio";
                break;

            case "LINK":
                buttonText = "🔗  Open Learning Link";
                break;

            default:
                return;
        }

        TextView mediaLabel = new TextView(this);
        mediaLabel.setText("Learning Material");
        mediaLabel.setTextSize(14);
        mediaLabel.setTextColor(darkBlue);
        mediaLabel.setTypeface(null, Typeface.BOLD);
        mediaLabel.setPadding(0, 16, 0, 5);

        card.addView(mediaLabel);

        Button mediaButton = createButton(
                buttonText,
                Color.rgb(14, 165, 233)
        );

        LinearLayout.LayoutParams mediaParams =
                new LinearLayout.LayoutParams(
                        -1,
                        52
                );

        mediaParams.topMargin = 6;

        card.addView(mediaButton, mediaParams);

        mediaButton.setOnClickListener(
                view -> openMedia(contentUrl)
        );
    }

    private Button createButton(
            String text,
            int color
    ) {

        Button button = new Button(this);

        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setTextColor(Color.WHITE);
        button.setTypeface(null, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);

        button.setBackground(
                roundedBackground(color, 30)
        );

        button.setElevation(2);

        return button;
    }

    private void openMedia(String url) {

        try {

            Uri uri = Uri.parse(url);

            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    uri
            );

            startActivity(intent);

        } catch (Exception error) {

            Toast.makeText(
                    StudentLessonActivity.this,
                    "Unable to open this content.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private GradientDrawable roundedBackground(
            int color,
            int radius
    ) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(color);
        drawable.setCornerRadius(radius);

        return drawable;
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
                    event.getAction() == MotionEvent.ACTION_UP
                            || event.getAction()
                            == MotionEvent.ACTION_CANCEL
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

    private void showLoading() {

        if (statusView != null) {
            statusView.setText("⏳ Loading your lessons...");
            statusView.setTextColor(primaryColor);
        }

        if (countView != null) {
            countView.setText("PLEASE WAIT");
        }

        if (lessonContainer != null) {
            lessonContainer.removeAllViews();
        }
    }

    private void showEmpty(String message) {

        lessonContainer.removeAllViews();

        countView.setText("NO LESSONS FOUND");

        statusView.setText(
                "📖 No lessons available"
        );

        statusView.setTextColor(textSecondary);

        LinearLayout emptyCard = new LinearLayout(this);
        emptyCard.setOrientation(LinearLayout.VERTICAL);
        emptyCard.setGravity(Gravity.CENTER);
        emptyCard.setPadding(20, 30, 20, 30);

        emptyCard.setBackground(
                roundedBackground(Color.WHITE, 22)
        );

        TextView emptyIcon = new TextView(this);
        emptyIcon.setText("📚");
        emptyIcon.setTextSize(45);
        emptyIcon.setGravity(Gravity.CENTER);

        emptyCard.addView(emptyIcon);

        TextView emptyText = new TextView(this);
        emptyText.setText(message);
        emptyText.setTextSize(15);
        emptyText.setTextColor(textSecondary);
        emptyText.setGravity(Gravity.CENTER);
        emptyText.setPadding(10, 15, 10, 15);

        emptyCard.addView(emptyText);

        Button retryButton = createButton(
                "↻  Try Again",
                primaryColor
        );

        emptyCard.addView(
                retryButton,
                new LinearLayout.LayoutParams(
                        -1,
                        52
                )
        );

        retryButton.setOnClickListener(
                view -> loadLessons()
        );

        lessonContainer.addView(
                emptyCard,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }

    private void showError(String message) {

        lessonContainer.removeAllViews();

        countView.setText("LOADING FAILED");

        statusView.setText(
                "⚠️ Something went wrong"
        );

        statusView.setTextColor(errorColor);

        LinearLayout errorCard = new LinearLayout(this);
        errorCard.setOrientation(LinearLayout.VERTICAL);
        errorCard.setGravity(Gravity.CENTER);
        errorCard.setPadding(20, 30, 20, 30);

        errorCard.setBackground(
                roundedBackground(Color.WHITE, 22)
        );

        TextView errorIcon = new TextView(this);
        errorIcon.setText("⚠️");
        errorIcon.setTextSize(42);
        errorIcon.setGravity(Gravity.CENTER);

        errorCard.addView(errorIcon);

        TextView errorText = new TextView(this);
        errorText.setText(message);
        errorText.setTextSize(15);
        errorText.setTextColor(errorColor);
        errorText.setGravity(Gravity.CENTER);
        errorText.setPadding(10, 15, 10, 15);

        errorCard.addView(errorText);

        Button retryButton = createButton(
                "↻  Retry",
                primaryColor
        );

        errorCard.addView(
                retryButton,
                new LinearLayout.LayoutParams(
                        -1,
                        52
                )
        );

        retryButton.setOnClickListener(
                view -> loadLessons()
        );

        lessonContainer.addView(
                errorCard,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }
}
```
