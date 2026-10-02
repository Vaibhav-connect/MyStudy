package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class StudentClassActivity extends AppCompatActivity {

    private LinearLayout classContainer;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        createUI();
        loadClasses();
    }

    private void createUI() {

        ScrollView scrollView = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 24, 20, 30);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("📚 Choose Your Class");
        title.setTextSize(26);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER_VERTICAL);

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1,
                        65
                )
        );

        TextView subtitle = new TextView(this);
        subtitle.setText(
                "Select your class to start learning"
        );
        subtitle.setTextSize(16);
        subtitle.setTextColor(
                Color.rgb(100, 116, 139)
        );
        subtitle.setPadding(0, 0, 0, 22);

        root.addView(subtitle);

        classContainer = new LinearLayout(this);
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

    private void loadClasses() {

        classContainer.removeAllViews();

        db.collection("classes")
                .orderBy("order")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        showMessage(
                                "No classes available yet."
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

                            className = "Class";
                        }

                        addClassCard(
                                classId,
                                className
                        );
                    }
                })
                .addOnFailureListener(error -> {

                    showMessage(
                            "Unable to load classes."
                    );

                    Toast.makeText(
                            StudentClassActivity.this,
                            "Unable to load classes",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void addClassCard(
            String classId,
            String className
    ) {

        final String selectedClassId =
                classId;

        final String selectedClassName =
                className;

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

        background.setColor(Color.WHITE);
        background.setCornerRadius(28);

        card.setBackground(background);

        card.setElevation(4);

        TextView icon =
                new TextView(this);

        icon.setText("🎓");
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

        TextView name =
                new TextView(this);

        name.setText(selectedClassName);
        name.setTextSize(20);
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
                "Tap to explore subjects"
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

            startActivity(intent);
        });

        card.setOnTouchListener(
                (view, event) -> {

                    switch (event.getAction()) {

                        case android.view.MotionEvent.ACTION_DOWN:

                            view.animate()
                                    .scaleX(0.97f)
                                    .scaleY(0.97f)
                                    .setDuration(100)
                                    .start();

                            break;

                        case android.view.MotionEvent.ACTION_UP:
                        case android.view.MotionEvent.ACTION_CANCEL:

                            view.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start();

                            break;
                    }

                    return false;
                }
        );
    }

    private void showMessage(String message) {

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

        classContainer.addView(
                messageView,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }
}
