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
    private String studentMedium;
    private String studentName;
    private String subjectId;
    private String subjectName;

    private final int backgroundColor = Color.rgb(248, 250, 252);
    private final int textPrimary = Color.rgb(17, 24, 39);
    private final int textSecondary = Color.rgb(100, 116, 139);
    private final int primaryColor = Color.rgb(79, 70, 229);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");
        studentMedium = getIntent().getStringExtra("studentMedium");
        studentName = getIntent().getStringExtra("studentName");
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
        scrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 24, 20, 30);
        root.setBackgroundColor(backgroundColor);

        TextView title = new TextView(this);

        title.setText("📚 Chapters");
        title.setTextSize(28);
        title.setTextColor(textPrimary);
        title.setTypeface(null, Typeface.BOLD);

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        TextView subtitle = new TextView(this);

        String subtitleText = "";

        if (subjectName != null &&
                !subjectName.trim().isEmpty()) {

            subtitleText = subjectName;

        } else {

            subtitleText = "Choose a chapter";
        }

        if (studentMedium != null &&
                !studentMedium.trim().isEmpty()) {

            subtitleText += " • " + studentMedium;
        }

        subtitleText += " • Choose a chapter";

        subtitle.setText(subtitleText);
        subtitle.setTextSize(16);
        subtitle.setTextColor(textSecondary);
        subtitle.setPadding(0, 8, 0, 24);

        root.addView(
                subtitle,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        TextView sectionTitle = new TextView(this);

        sectionTitle.setText("Your Chapters");
        sectionTitle.setTextSize(18);
        sectionTitle.setTextColor(textPrimary);
        sectionTitle.setTypeface(null, Typeface.BOLD);
        sectionTitle.setPadding(0, 0, 0, 14);

        root.addView(
                sectionTitle,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

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

        scrollView.addView(root);

        setContentView(scrollView);
    }

    private void loadChapters() {

        chapterContainer.removeAllViews();

        if (studentMedium == null ||
                studentMedium.trim().isEmpty()) {

            showMessage("Medium information missing.");
            return;
        }

        db.collection("chapters")
                .whereEqualTo(
                        "classId",
                        classId
                )
                .whereEqualTo(
                        "subjectId",
                        subjectId
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
                                        "No chapters available for this class and medium yet."
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
                                        document.getString("name");

                                if (
                                        chapterName == null ||
                                        chapterName.trim().isEmpty()
                                ) {

                                    chapterName = "Chapter";
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
                18,
                18,
                14,
                18
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(30);

        card.setBackground(background);
        card.setElevation(6);

        TextView icon =
                new TextView(this);

        icon.setText("📘");
        icon.setTextSize(30);
        icon.setGravity(Gravity.CENTER);

        GradientDrawable iconBackground =
                new GradientDrawable();

        iconBackground.setColor(
                Color.rgb(219, 234, 254)
        );

        iconBackground.setCornerRadius(22);

        icon.setBackground(iconBackground);

        card.addView(
                icon,
                new LinearLayout.LayoutParams(
                        68,
                        68
                )
        );

        LinearLayout textContainer =
                new LinearLayout(this);

        textContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        textContainer.setPadding(
                16,
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
        name.setTextColor(textPrimary);
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
        description.setTextColor(textSecondary);
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
        arrow.setTextSize(34);
        arrow.setTextColor(primaryColor);
        arrow.setGravity(Gravity.CENTER);
        arrow.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(
                arrow,
                new LinearLayout.LayoutParams(
                        42,
                        68
                )
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
                16
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

    private void showMessage(String message) {

        TextView messageView =
                new TextView(this);

        messageView.setText(message);
        messageView.setTextSize(16);
        messageView.setTextColor(textSecondary);
        messageView.setGravity(Gravity.CENTER);

        messageView.setPadding(
                20,
                50,
                20,
                50
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
