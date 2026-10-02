package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressManager progressManager;

    private String classId;
    private String className;
    private String subjectId;
    private String subjectName;
    private String chapterId;
    private String chapterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");
        subjectId = getIntent().getStringExtra("subjectId");
        subjectName = getIntent().getStringExtra("subjectName");
        chapterId = getIntent().getStringExtra("chapterId");
        chapterName = getIntent().getStringExtra("chapterName");

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

        if (chapterName != null &&
                !chapterName.trim().isEmpty()) {

            subtitle.setText(
                    chapterName +
                            " • Choose a lesson"
            );

        } else {

            subtitle.setText(
                    "Choose a lesson"
            );
        }

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
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            if (querySnapshot.isEmpty()) {

                                showMessage(
                                        "No lessons available yet."
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

                                if (title == null ||
                                        title.trim().isEmpty()) {

                                    title = "Lesson";
                                }

                                addLessonCard(
                                        lessonId,
                                        title,
                                        description,
                                        content
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
            String content
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

    private void addPressAnimation(View view) {

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
