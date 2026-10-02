package com.mystudy.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class StudentLessonActivity extends AppCompatActivity {

    private LinearLayout lessonContainer;
    private FirebaseFirestore db;

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

        if (classId == null || subjectId == null || chapterId == null) {
            Toast.makeText(
                    this,
                    "Lesson information missing",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        createUI();
        loadLessons();
    }

    private void createUI() {

        ScrollView scrollView = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("📖 Lessons");
        title.setTextSize(26);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setTypeface(null, Typeface.BOLD);

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1,
                        70
                )
        );

        TextView subtitle = new TextView(this);

        if (chapterName != null &&
                !chapterName.trim().isEmpty()) {

            subtitle.setText(
                    chapterName + " • Choose a lesson"
            );

        } else {
            subtitle.setText("Choose a lesson");
        }

        subtitle.setTextSize(16);
        subtitle.setTextColor(
                Color.rgb(100, 116, 139)
        );
        subtitle.setPadding(0, 0, 0, 24);

        root.addView(subtitle);

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

        lessonContainer.removeAllViews();

        db.collection("lessons")
                .whereEqualTo("classId", classId)
                .whereEqualTo("subjectId", subjectId)
                .whereEqualTo("chapterId", chapterId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        showMessage(
                                "No lessons available yet."
                        );

                        return;
                    }

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String lessonId =
                                document.getId();

                        String title =
                                document.getString("title");

                        String description =
                                document.getString("description");

                        String content =
                                document.getString("content");

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
                })
                .addOnFailureListener(error -> {

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
            String content
    ) {

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

        android.graphics.drawable.GradientDrawable background =
                new android.graphics.drawable.GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(28);

        card.setBackground(background);

        TextView lessonTitle =
                new TextView(this);

        lessonTitle.setText(
                "📘 " + title
        );

        lessonTitle.setTextSize(19);
        lessonTitle.setTextColor(
                Color.rgb(17, 24, 39)
        );
        lessonTitle.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(lessonTitle);

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
                    8,
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

            lessonContent.setPadding(
                    0,
                    12,
                    0,
                    0
            );

            card.addView(
                    lessonContent
            );
        }

        TextView openText =
                new TextView(this);

        openText.setText(
                "Start Learning →"
        );

        openText.setTextSize(15);
        openText.setTextColor(
                Color.rgb(79, 70, 229)
        );
        openText.setTypeface(
                null,
                Typeface.BOLD
        );
        openText.setGravity(
                Gravity.END
        );
        openText.setPadding(
                0,
                16,
                0,
                0
        );

        card.addView(openText);

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

        card.setOnClickListener(view -> {

            Toast.makeText(
                    StudentLessonActivity.this,
                    "Lesson opened",
                    Toast.LENGTH_SHORT
            ).show();
        });
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
