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

public class StudentSubjectActivity extends AppCompatActivity {

    private LinearLayout subjectContainer;
    private FirebaseFirestore db;

    private String classId;
    private String className;

    private final int backgroundColor = Color.rgb(248, 250, 252);
    private final int textPrimary = Color.rgb(17, 24, 39);
    private final int textSecondary = Color.rgb(100, 116, 139);
    private final int primaryColor = Color.rgb(79, 70, 229);

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

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 24, 20, 30);
        root.setBackgroundColor(backgroundColor);

        TextView title = new TextView(this);

        title.setText("📚 Subjects");
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

        if (className != null &&
                !className.trim().isEmpty()) {

            subtitle.setText(
                    className + " • Choose a subject"
            );

        } else {

            subtitle.setText(
                    "Choose a subject"
            );
        }

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

        sectionTitle.setText("Start Learning");
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

        subjectContainer =
                new LinearLayout(this);

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

        scrollView.addView(root);

        setContentView(scrollView);
    }

    private void loadSubjects() {

        subjectContainer.removeAllViews();

        db.collection("subjects")
                .whereEqualTo(
                        "classId",
                        classId
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            if (querySnapshot.isEmpty()) {

                                showMessage(
                                        "No subjects available for this class."
                                );

                                return;
                            }

                            for (
                                    QueryDocumentSnapshot document :
                                    querySnapshot
                            ) {

                                String subjectId =
                                        document.getId();

                                String subjectName =
                                        document.getString("name");

                                if (
                                        subjectName == null ||
                                        subjectName.trim().isEmpty()
                                ) {

                                    subjectName = "Subject";
                                }

                                addSubjectCard(
                                        subjectId,
                                        subjectName
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        error -> {

                            showMessage(
                                    "Unable to load subjects."
                            );

                            Toast.makeText(
                                    StudentSubjectActivity.this,
                                    "Unable to load subjects",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }

    private void addSubjectCard(
            String subjectId,
            String subjectName
    ) {

        final String selectedSubjectId =
                subjectId;

        final String selectedSubjectName =
                subjectName;

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

        icon.setText(
                getSubjectIcon(
                        selectedSubjectName
                )
        );

        icon.setTextSize(30);
        icon.setGravity(Gravity.CENTER);

        GradientDrawable iconBackground =
                new GradientDrawable();

        iconBackground.setColor(
                getSubjectIconBackground(
                        selectedSubjectName
                )
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
                selectedSubjectName
        );

        name.setTextSize(20);
        name.setTextColor(textPrimary);
        name.setTypeface(
                null,
                Typeface.BOLD
        );

        TextView description =
                new TextView(this);

        description.setText(
                "Explore chapters and lessons"
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

        subjectContainer.addView(
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
                            StudentSubjectActivity.this,
                            "com.mystudy.app.StudentChapterActivity"
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
                }
        );
    }

    private String getSubjectIcon(
            String subjectName
    ) {

        if (subjectName == null) {
            return "📚";
        }

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

    private int getSubjectIconBackground(
            String subjectName
    ) {

        if (subjectName == null) {
            return Color.rgb(238, 242, 255);
        }

        String value =
                subjectName.toLowerCase();

        if (value.contains("math")) {
            return Color.rgb(254, 249, 195);
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

        return Color.rgb(238, 242, 255);
    }

    private void showMessage(
            String message
    ) {

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

        subjectContainer.addView(
                messageView,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }
}
