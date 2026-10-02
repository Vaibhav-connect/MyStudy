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

    private LinearLayout chapterContainer;
    private FirebaseFirestore db;

    private String classId;
    private String className;
    private String subjectId;
    private String subjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");
        subjectId = getIntent().getStringExtra("subjectId");
        subjectName = getIntent().getStringExtra("subjectName");

        if (classId == null ||
                classId.trim().isEmpty() ||
                subjectId == null ||
                subjectId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Subject information missing",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        createUI();
        loadChapters();
    }

    private void createUI() {

        ScrollView scrollView = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 24, 20, 30);
        root.setBackgroundColor(
                Color.rgb(248, 250, 252)
        );

        TextView title = new TextView(this);
        title.setText("📚 Chapters");
        title.setTextSize(26);
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

        if (subjectName != null &&
                !subjectName.trim().isEmpty()) {

            subtitle.setText(
                    subjectName +
                            " • Choose a chapter"
            );

        } else {

            subtitle.setText(
                    "Choose a chapter"
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

        chapterContainer =
                new LinearLayout(this);

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

        scrollView.addView(root);

        setContentView(scrollView);
    }

    private void loadChapters() {

        chapterContainer.removeAllViews();

        db.collection("chapters")
                .whereEqualTo(
                        "classId",
                        classId
                )
                .whereEqualTo(
                        "subjectId",
                        subjectId
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            if (querySnapshot.isEmpty()) {

                                showMessage(
                                        "No chapters available yet."
                                );

                                return;
                            }

                            for (
                                    QueryDocumentSnapshot document :
                                    querySnapshot
                            ) {

                                String chapterId =
                                        document.getId();

                                String chapterName =
                                        document.getString(
                                                "name"
                                        );

                                if (
                                        chapterName == null ||
                                        chapterName.trim().isEmpty()
                                ) {

                                    chapterName =
                                            "Chapter";
                                }

                                addChapterCard(
                                        chapterId,
                                        chapterName
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        error -> {

                            showMessage(
                                    "Unable to load chapters."
                            );

                            Toast.makeText(
                                    StudentChapterActivity.this,
                                    "Unable to load chapters",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }

    private void addChapterCard(
            String chapterId,
            String chapterName
    ) {

        final String selectedChapterId =
                chapterId;

        final String selectedChapterName =
                chapterName;

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.HORIZONTAL
        );

        card.setGravity(
                Gravity.CENTER_VERTICAL
        );

        card.setPadding(
                20,
                16,
                16,
                16
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

        card.setElevation(4);

        TextView icon =
                new TextView(this);

        icon.setText("📘");
        icon.setTextSize(30);
        icon.setGravity(
                Gravity.CENTER
        );

        card.addView(
                icon,
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
                18,
                0,
                8,
                0
        );

        TextView name =
                new TextView(this);

        name.setText(
                selectedChapterName
        );

        name.setTextSize(19);
        name.setTextColor(
                Color.rgb(17, 24, 39)
        );

        name.setTypeface(
                null,
                Typeface.BOLD
        );

        TextView description =
                new TextView(this);

        description.setText(
                "Tap to view lessons"
        );

        description.setTextSize(14);
        description.setTextColor(
                Color.rgb(100, 116, 139)
        );

        description.setPadding(
                0,
                5,
                0,
                0
        );

        textContainer.addView(name);
        textContainer.addView(description);

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
        arrow.setTextSize(32);
        arrow.setTextColor(
                Color.rgb(79, 70, 229)
        );
        arrow.setGravity(
                Gravity.CENTER
        );

        card.addView(
                arrow,
                new LinearLayout.LayoutParams(
                        45,
                        70
                )
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        -1,
                        100
                );

        cardParams.setMargins(
                0,
                0,
                0,
                18
        );

        chapterContainer.addView(
                card,
                cardParams
        );

        card.setOnTouchListener(
                (view, event) -> {

                    if (
                            event.getAction() ==
                                    MotionEvent.ACTION_DOWN
                    ) {

                        view.animate()
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

                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                    }

                    return false;
                }
        );

        card.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent();

                    intent.setClassName(
                            StudentChapterActivity.this,
                            "com.mystudy.app.StudentLessonActivity"
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
                            selectedChapterId
                    );

                    intent.putExtra(
                            "chapterName",
                            selectedChapterName
                    );

                    startActivity(intent);
                }
        );
    }

    private void showMessage(
            String message
    ) {

        TextView messageView =
                new TextView(this);

        messageView.setText(message);
        messageView.setTextSize(16);
        messageView.setTextColor(
                Color.rgb(100, 116, 139)
        );

        messageView.setGravity(
                Gravity.CENTER
        );

        messageView.setPadding(
                20,
                40,
                20,
                40
        );

        chapterContainer.addView(
                messageView,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }
}
