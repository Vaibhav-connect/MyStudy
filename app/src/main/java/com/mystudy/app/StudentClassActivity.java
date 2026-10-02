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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class StudentClassActivity extends AppCompatActivity {

    private LinearLayout classContainer;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String studentClass = "";
    private String studentMedium = "";
    private String studentName = "";

    private final int backgroundColor = Color.rgb(248, 250, 252);
    private final int textPrimary = Color.rgb(17, 24, 39);
    private final int textSecondary = Color.rgb(100, 116, 139);
    private final int primaryColor = Color.rgb(79, 70, 229);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        studentName =
                getIntent().getStringExtra("studentName");

        studentClass =
                getIntent().getStringExtra("studentClass");

        studentMedium =
                getIntent().getStringExtra("studentMedium");

        if (studentClass == null) {
            studentClass = "";
        }

        if (studentMedium == null) {
            studentMedium = "";
        }

        if (studentName == null) {
            studentName = "";
        }

        createUI();

        loadStudentProfile();
    }

    private void createUI() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                20,
                24,
                20,
                30
        );

        root.setBackgroundColor(
                backgroundColor
        );

        TextView title = new TextView(this);

        title.setText(
                "📚 My Class"
        );

        title.setTextSize(28);
        title.setTextColor(textPrimary);
        title.setTypeface(
                null,
                Typeface.BOLD
        );

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        TextView subtitle = new TextView(this);

        subtitle.setText(
                "Your learning content is based on your selected class and medium."
        );

        subtitle.setTextSize(16);
        subtitle.setTextColor(textSecondary);
        subtitle.setPadding(
                0,
                8,
                0,
                24
        );

        root.addView(
                subtitle,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        classContainer =
                new LinearLayout(this);

        classContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        root.addView(
                classContainer,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        scrollView.addView(root);

        setContentView(scrollView);
    }

    private void loadStudentProfile() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            showMessage(
                    "Please login again."
            );

            return;
        }

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {

                        showMessage(
                                "Student profile not found."
                        );

                        return;
                    }

                    String savedClass =
                            document.getString("class");

                    String savedMedium =
                            document.getString("medium");

                    String savedName =
                            document.getString("name");

                    if (savedClass != null &&
                            !savedClass.trim().isEmpty()) {

                        studentClass =
                                savedClass.trim();
                    }

                    if (savedMedium != null &&
                            !savedMedium.trim().isEmpty()) {

                        studentMedium =
                                savedMedium.trim();
                    }

                    if (savedName != null &&
                            !savedName.trim().isEmpty()) {

                        studentName =
                                savedName.trim();
                    }

                    if (studentClass.isEmpty()) {

                        showMessage(
                                "Your class is not selected in your profile."
                        );

                        return;
                    }

                    loadSelectedClass();
                })
                .addOnFailureListener(error -> {

                    showMessage(
                            "Unable to load your profile."
                    );

                    Toast.makeText(
                            StudentClassActivity.this,
                            "Unable to load student profile",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void loadSelectedClass() {

        classContainer.removeAllViews();

        db.collection("classes")
                .whereEqualTo(
                        "name",
                        studentClass
                )
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        showMessage(
                                studentClass +
                                        " is not available yet."
                        );

                        return;
                    }

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String classId =
                                document.getId();

                        String className =
                                document.getString("name");

                        if (className == null ||
                                className.trim().isEmpty()) {

                            className = studentClass;
                        }

                        addClassCard(
                                classId,
                                className,
                                studentMedium
                        );

                        break;
                    }
                })
                .addOnFailureListener(error -> {

                    showMessage(
                            "Unable to load your class."
                    );

                    Toast.makeText(
                            StudentClassActivity.this,
                            "Unable to load class",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void addClassCard(
            String classId,
            String className,
            String medium
    ) {

        final String selectedClassId =
                classId;

        final String selectedClassName =
                className;

        final String selectedMedium =
                medium;

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

        icon.setText("🎓");
        icon.setTextSize(30);
        icon.setGravity(Gravity.CENTER);

        GradientDrawable iconBackground =
                new GradientDrawable();

        iconBackground.setColor(
                Color.rgb(238, 242, 255)
        );

        iconBackground.setCornerRadius(22);

        icon.setBackground(
                iconBackground
        );

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
                selectedClassName
        );

        name.setTextSize(20);
        name.setTextColor(textPrimary);
        name.setTypeface(
                null,
                Typeface.BOLD
        );

        TextView mediumText =
                new TextView(this);

        if (selectedMedium == null ||
                selectedMedium.trim().isEmpty()) {

            mediumText.setText(
                    "Medium not selected"
            );

        } else {

            mediumText.setText(
                    "Medium: " + selectedMedium
            );
        }

        mediumText.setTextSize(14);
        mediumText.setTextColor(textSecondary);
        mediumText.setPadding(
                0,
                5,
                0,
                0
        );

        TextView description =
                new TextView(this);

        description.setText(
                "Tap to explore subjects"
        );

        description.setTextSize(14);
        description.setTextColor(textSecondary);
        description.setPadding(
                0,
                3,
                0,
                0
        );

        textContainer.addView(name);
        textContainer.addView(mediumText);
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

        classContainer.addView(
                card,
                cardParams
        );

        card.setOnClickListener(view -> {

            Intent intent =
                    new Intent(
                            StudentClassActivity.this,
                            StudentSubjectActivity.class
                    );

            intent.putExtra(
                    "classId",
                    selectedClassId
            );

            intent.putExtra(
                    "className",
                    selectedClassName
            );

            intent.putExtra(
                    "studentMedium",
                    selectedMedium
            );

            intent.putExtra(
                    "studentName",
                    studentName
            );

            startActivity(intent);

            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );
        });

        card.setOnTouchListener(
                (view, event) -> {

                    if (event.getAction() ==
                            MotionEvent.ACTION_DOWN) {

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
    }

    private void showMessage(
            String message
    ) {

        classContainer.removeAllViews();

        TextView messageView =
                new TextView(this);

        messageView.setText(message);
        messageView.setTextSize(16);
        messageView.setTextColor(
                textSecondary
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

        classContainer.addView(
                messageView,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }
}
