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
import android.widget.ImageView;
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

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressManager progressManager;

    private String classId;
    private String className;
    private String studentMedium;
    private String studentName;
    private String subjectId;
    private String subjectName;
    private String chapterId;
    private String chapterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");
        studentMedium = getIntent().getStringExtra("studentMedium");
        studentName = getIntent().getStringExtra("studentName");
        subjectId = getIntent().getStringExtra("subjectId");
        subjectName = getIntent().getStringExtra("subjectName");
        chapterId = getIntent().getStringExtra("chapterId");
        chapterName = getIntent().getStringExtra("chapterName");

        if (studentMedium == null || studentMedium.trim().isEmpty()) {
            studentMedium = "English";
        }

        if (classId == null ||
                classId.trim().isEmpty() ||
                subjectId == null ||
                subjectId.trim().isEmpty() ||
                chapterId == null ||
                chapterId.trim().isEmpty()) {

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

    private void createUI() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        root.setPadding(
                20,
                24,
                20,
                30
        );

        root.setBackgroundColor(
                Color.rgb(248, 250, 252)
        );

        TextView title = new TextView(this);

        title.setText("📖 Lessons");
        title.setTextSize(27);
        title.setTextColor(
                Color.rgb(17, 24, 39)
        );

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        title.setGravity(
                Gravity.CENTER_VERTICAL
        );

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1,
                        65
                )
        );

        TextView subtitle = new TextView(this);

        StringBuilder subtitleText = new StringBuilder();

        if (chapterName != null &&
                !chapterName.trim().isEmpty()) {

            subtitleText.append(chapterName);

        } else {

            subtitleText.append("Choose a lesson");
        }

        subtitleText.append(" • ");
        subtitleText.append(studentMedium);

        subtitle.setText(
                subtitleText.toString()
        );

        subtitle.setTextSize(16);
        subtitle.setTextColor(
                Color.rgb(100, 116, 139)
        );

        subtitle.setPadding(
                0,
                0,
                0,
                22
        );

        root.addView(subtitle);

        lessonContainer =
                new LinearLayout(this);

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

        lessonContainer.removeAllViews();

        db.collection("lessons")
                .whereEqualTo(
                        "classId",
                        classId
                )
                .whereEqualTo(
                        "subjectId",
                        subjectId
                )
                .whereEqualTo(
                        "chapterId",
                        chapterId
                )
                .whereEqualTo(
                        "medium",
                        studentMedium
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            if (querySnapshot.isEmpty()) {

                                showMessage(
                                        "No lessons available for " +
                                                studentMedium +
                                                " medium yet."
                                );

                                return;
                            }

                            for (
                                    QueryDocumentSnapshot document :
                                    querySnapshot
                            ) {

                                String lessonId =
                                        document.getId();

                                String title =
                                        document.getString(
                                                "title"
                                        );

                                String description =
                                        document.getString(
                                                "description"
                                        );

                                String content =
                                        document.getString(
                                                "content"
                                        );

                                String contentType =
                                        document.getString(
                                                "contentType"
                                        );

                                String contentUrl =
                                        document.getString(
                                                "contentUrl"
                                        );

                                if (title == null ||
                                        title.trim().isEmpty()) {

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
                        }
                )
                .addOnFailureListener(
                        error -> {

                            showMessage(
                                    "Unable to load lessons."
                            );

                            Toast.makeText(
                                    StudentLessonActivity.this,
                                    "Unable to load lessons",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }

    private void addLessonCard(
            String lessonId,
            String title,
            String description,
            String content,
            String contentType,
            String contentUrl
    ) {

        final String selectedLessonId =
                lessonId;

        final String selectedLessonTitle =
                title;

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                20,
                20,
                20,
                20
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.WHITE
        );

        background.setCornerRadius(
                28
        );

        card.setBackground(
                background
        );

        card.setElevation(5);

        TextView lessonTitle =
                new TextView(this);

        lessonTitle.setText(
                "📘 " + selectedLessonTitle
        );

        lessonTitle.setTextSize(20);
        lessonTitle.setTextColor(
                Color.rgb(17, 24, 39)
        );

        lessonTitle.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(
                lessonTitle
        );

        TextView mediumText =
                new TextView(this);

        mediumText.setText(
                "Medium: " + studentMedium
        );

        mediumText.setTextSize(13);
        mediumText.setTextColor(
                Color.rgb(79, 70, 229)
        );

        mediumText.setTypeface(
                null,
                Typeface.BOLD
        );

        mediumText.setPadding(
                0,
                8,
                0,
                0
        );

        card.addView(
                mediumText
        );

        if (description != null &&
                !description.trim().isEmpty()) {

            TextView lessonDescription =
                    new TextView(this);

            lessonDescription.setText(
                    description
            );

            lessonDescription.setTextSize(14);
            lessonDescription.setTextColor(
                    Color.rgb(100, 116, 139)
            );

            lessonDescription.setPadding(
                    0,
                    9,
                    0,
                    0
            );

            card.addView(
                    lessonDescription
            );
        }

        if (content != null &&
                !content.trim().isEmpty()) {

            TextView lessonContent =
                    new TextView(this);

            lessonContent.setText(
                    content
            );

            lessonContent.setTextSize(15);
            lessonContent.setTextColor(
                    Color.rgb(55, 65, 81)
            );

            lessonContent.setLineSpacing(
                    3,
                    1.05f
            );

            lessonContent.setPadding(
                    0,
                    14,
                    0,
                    12
            );

            card.addView(
                    lessonContent
            );
        }

        addMediaContent(
                card,
                contentType,
                contentUrl
        );

        Button completeButton =
                new Button(this);

        completeButton.setText(
                "✓ Mark Lesson Complete"
        );

        completeButton.setAllCaps(false);
        completeButton.setTextSize(15);
        completeButton.setTextColor(
                Color.WHITE
        );

        GradientDrawable completeBackground =
                new GradientDrawable();

        completeBackground.setColor(
                Color.rgb(34, 197, 94)
        );

        completeBackground.setCornerRadius(
                30
        );

        completeButton.setBackground(
                completeBackground
        );

        LinearLayout.LayoutParams completeParams =
                new LinearLayout.LayoutParams(
                        -1,
                        58
                );

        completeParams.topMargin = 8;

        card.addView(
                completeButton,
                completeParams
        );

        Button practiceButton =
                new Button(this);

        practiceButton.setText(
                "Start Practice →"
        );

        practiceButton.setAllCaps(false);
        practiceButton.setTextSize(15);
        practiceButton.setTextColor(
                Color.WHITE
        );

        GradientDrawable practiceBackground =
                new GradientDrawable();

        practiceBackground.setColor(
                Color.rgb(79, 70, 229)
        );

        practiceBackground.setCornerRadius(
                30
        );

        practiceButton.setBackground(
                practiceBackground
        );

        LinearLayout.LayoutParams practiceParams =
                new LinearLayout.LayoutParams(
                        -1,
                        58
                );

        practiceParams.topMargin = 10;

        card.addView(
                practiceButton,
                practiceParams
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        cardParams.setMargins(
                0,
                0,
                0,
                18
        );

        lessonContainer.addView(
                card,
                cardParams
        );

        addPressAnimation(card);
        addPressAnimation(completeButton);
        addPressAnimation(practiceButton);

        completeButton.setOnClickListener(
                view -> {

                    FirebaseUser user =
                            auth.getCurrentUser();

                    if (user == null) {

                        Toast.makeText(
                                StudentLessonActivity.this,
                                "Please login again.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

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

                    completeButton.setText(
                            "✓ Lesson Completed"
                    );

                    completeButton.setEnabled(
                            false
                    );

                    Toast.makeText(
                            StudentLessonActivity.this,
                            "+10 points 🎉",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );

        practiceButton.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent();

                    intent.setClassName(
                            StudentLessonActivity.this,
                            "com.mystudy.app.StudentPracticeActivity"
                    );

                    intent.putExtra(
                            "classId",
                            classId
                    );

                    intent.putExtra(
                            "className",
                            className
                    );

                    intent.putExtra(
                            "studentMedium",
                            studentMedium
                    );

                    intent.putExtra(
                            "studentName",
                            studentName
                    );

                    intent.putExtra(
                            "subjectId",
                            subjectId
                    );

                    intent.putExtra(
                            "subjectName",
                            subjectName
                    );

                    intent.putExtra(
                            "chapterId",
                            chapterId
                    );

                    intent.putExtra(
                            "chapterName",
                            chapterName
                    );

                    intent.putExtra(
                            "lessonId",
                            selectedLessonId
                    );

                    intent.putExtra(
                            "lessonTitle",
                            selectedLessonTitle
                    );

                    startActivity(intent);

                    overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                    );
                }
        );
    }

    private void addMediaContent(
            LinearLayout card,
            String contentType,
            String contentUrl
    ) {

        if (contentType == null ||
                contentType.trim().isEmpty() ||
                contentUrl == null ||
                contentUrl.trim().isEmpty()) {

            return;
        }

        String type =
                contentType.trim().toUpperCase();

        if (type.equals("IMAGE")) {

            ImageView imageView =
                    new ImageView(this);

            imageView.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );

            imageView.setAdjustViewBounds(true);

            imageView.setScaleType(
                    ImageView.ScaleType.CENTER_INSIDE
            );

            LinearLayout.LayoutParams imageParams =
                    new LinearLayout.LayoutParams(
                            -1,
                            220
                    );

            imageParams.topMargin = 10;
            imageParams.bottomMargin = 10;

            card.addView(
                    imageView,
                    imageParams
            );

            addOpenMediaButton(
                    card,
                    "🖼 Open Image",
                    contentUrl
            );

        } else if (type.equals("PDF")) {

            addOpenMediaButton(
                    card,
                    "📄 Open PDF",
                    contentUrl
            );

        } else if (type.equals("VIDEO")) {

            addOpenMediaButton(
                    card,
                    "▶ Open Video",
                    contentUrl
            );

        } else if (type.equals("AUDIO")) {

            addOpenMediaButton(
                    card,
                    "🔊 Open Audio",
                    contentUrl
            );

        } else if (type.equals("LINK")) {

            addOpenMediaButton(
                    card,
                    "🔗 Open Learning Link",
                    contentUrl
            );
        }
    }

    private void addOpenMediaButton(
            LinearLayout card,
            String buttonText,
            String url
    ) {

        Button mediaButton =
                new Button(this);

        mediaButton.setText(
                buttonText
        );

        mediaButton.setAllCaps(false);
        mediaButton.setTextSize(14);
        mediaButton.setTextColor(
                Color.WHITE
        );

        GradientDrawable mediaBackground =
                new GradientDrawable();

        mediaBackground.setColor(
                Color.rgb(14, 165, 233)
        );

        mediaBackground.setCornerRadius(
                30
        );

        mediaButton.setBackground(
                mediaBackground
        );

        LinearLayout.LayoutParams mediaParams =
                new LinearLayout.LayoutParams(
                        -1,
                        55
                );

        mediaParams.topMargin = 8;
        mediaParams.bottomMargin = 8;

        card.addView(
                mediaButton,
                mediaParams
        );

        addPressAnimation(mediaButton);

        mediaButton.setOnClickListener(
                view -> openMedia(url)
        );
    }

    private void openMedia(
            String url
    ) {

        try {

            Uri uri =
                    Uri.parse(url);

            Intent intent =
                    new Intent(
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

    private void showMessage(
            String message
    ) {

        TextView messageView =
                new TextView(this);

        messageView.setText(
                message
        );

        messageView.setTextSize(16);

        messageView.setTextColor(
                Color.rgb(100, 116, 139)
        );

        messageView.setGravity(
                Gravity.CENTER
        );

        messageView.setPadding(
                20,
                50,
                20,
                50
        );

        lessonContainer.addView(
                messageView,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }
}
