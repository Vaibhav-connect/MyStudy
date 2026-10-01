package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class StudentSubjectActivity extends AppCompatActivity {

    private LinearLayout subjectContainer;
    private FirebaseFirestore db;

    private String classId;
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");

        if (classId == null || classId.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Class information missing",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        createUI();
        loadSubjects();
    }

    private void createUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("📚 Subjects");
        title.setTextSize(26);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setTypeface(null, 1);
        title.setGravity(Gravity.CENTER_VERTICAL);

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1,
                        70
                )
        );

        TextView subtitle = new TextView(this);

        if (className != null && !className.trim().isEmpty()) {
            subtitle.setText(
                    className + " • Choose a subject"
            );
        } else {
            subtitle.setText(
                    "Choose a subject"
            );
        }

        subtitle.setTextSize(16);
        subtitle.setTextColor(Color.rgb(100, 116, 139));
        subtitle.setPadding(0, 0, 0, 24);

        root.addView(subtitle);

        subjectContainer = new LinearLayout(this);
        subjectContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        root.addView(
                subjectContainer,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        setContentView(root);
    }

    private void loadSubjects() {

        subjectContainer.removeAllViews();

        db.collection("subjects")
                .whereEqualTo("classId", classId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        showMessage(
                                "No subjects available for this class."
                        );

                        return;
                    }

                    for (QueryDocumentSnapshot document : querySnapshot) {

                        String subjectId = document.getId();

                        String subjectName =
                                document.getString("name");

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

        final String selectedSubjectId = subjectId;
        final String selectedSubjectName = subjectName;

        LinearLayout card = new LinearLayout(this);

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

        TextView icon = new TextView(this);

        icon.setText(
                getSubjectIcon(
                        selectedSubjectName
                )
        );

        icon.setTextSize(30);
        icon.setGravity(Gravity.CENTER);

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

        TextView name = new TextView(this);

        name.setText(
                selectedSubjectName
        );

        name.setTextSize(20);
        name.setTextColor(
                Color.rgb(17, 24, 39)
        );

        name.setTypeface(
                null,
                1
        );

        TextView description =
                new TextView(this);

        description.setText(
                "Explore chapters and lessons"
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

        TextView arrow = new TextView(this);

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

        subjectContainer.addView(
                card,
                cardParams
        );

        card.setOnClickListener(view -> {

            Intent intent = new Intent(
                    StudentSubjectActivity.this,
                    StudentChapterActivity.class
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
                    selectedSubjectId
            );

            intent.putExtra(
                    "subjectName",
                    selectedSubjectName
            );

            startActivity(intent);
        });
    }

    private String getSubjectIcon(
            String subjectName
    ) {

        String value =
                subjectName.toLowerCase();

        if (value.contains("math")) {
            return "🔢";
        }

        if (value.contains("english")) {
            return "🔤";
        }

        if (value.contains("marathi")) {
            return "📕";
        }

        if (value.contains("evs")) {
            return "🌱";
        }

        return "📚";
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
                40,
                20,
                40
        );

        subjectContainer.addView(
                messageView,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }
}
