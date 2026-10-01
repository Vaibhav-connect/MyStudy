package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
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

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("📚 Choose Your Class");
        title.setTextSize(26);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTypeface(null, 1);

        root.addView(title, new LinearLayout.LayoutParams(
                -1,
                70
        ));

        TextView subtitle = new TextView(this);
        subtitle.setText("Select a class to start learning");
        subtitle.setTextSize(16);
        subtitle.setTextColor(Color.rgb(100, 116, 139));
        subtitle.setPadding(0, 0, 0, 20);

        root.addView(subtitle);

        classContainer = new LinearLayout(this);
        classContainer.setOrientation(LinearLayout.VERTICAL);

        root.addView(classContainer, new LinearLayout.LayoutParams(
                -1,
                -1
        ));

        setContentView(root);
    }

    private void loadClasses() {

        classContainer.removeAllViews();

        db.collection("classes")
                .orderBy("order")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {
                        showMessage("No classes available yet.");
                        return;
                    }

                    for (QueryDocumentSnapshot doc : querySnapshot) {

                        String classId = doc.getId();
                        String className = doc.getString("name");

                        if (className == null || className.trim().isEmpty()) {
                            className = "Class";
                        }

                        addClassCard(classId, className);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Unable to load classes",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void addClassCard(String classId, String className) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(24, 20, 20, 20);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(28);
        card.setBackground(background);

        TextView icon = new TextView(this);
        icon.setText("🎓");
        icon.setTextSize(30);
        icon.setGravity(Gravity.CENTER);

        card.addView(icon, new LinearLayout.LayoutParams(
                60,
                70
        ));

        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setPadding(18, 0, 0, 0);

        TextView name = new TextView(this);
        name.setText(className);
        name.setTextSize(20);
        name.setTextColor(Color.rgb(17, 24, 39));
        name.setTypeface(null, 1);

        TextView start = new TextView(this);
        start.setText("Tap to explore subjects");
        start.setTextSize(14);
        start.setTextColor(Color.rgb(100, 116, 139));
        start.setPadding(0, 5, 0, 0);

        textContainer.addView(name);
        textContainer.addView(start);

        card.addView(textContainer, new LinearLayout.LayoutParams(
                0,
                -2,
                1
        ));

        TextView arrow = new TextView(this);
        arrow.setText("›");
        arrow.setTextSize(32);
        arrow.setTextColor(Color.rgb(79, 70, 229));
        arrow.setGravity(Gravity.CENTER);

        card.addView(arrow, new LinearLayout.LayoutParams(
                50,
                70
        ));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        -1,
                        100
                );

        cardParams.setMargins(0, 0, 0, 18);

        classContainer.addView(card, cardParams);

        card.setOnClickListener(v -> {

            Intent intent = new Intent(
                    StudentClassActivity.this,
                    StudentSubjectActivity.class
            );

            intent.putExtra("classId", classId);
            intent.putExtra("className", className);

            startActivity(intent);
        });
    }

    private void showMessage(String message) {

        TextView messageView = new TextView(this);

        messageView.setText(message);
        messageView.setTextSize(16);
        messageView.setTextColor(Color.rgb(100, 116, 139));
        messageView.setGravity(Gravity.CENTER);
        messageView.setPadding(20, 40, 20, 40);

        classContainer.addView(
                messageView,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }
}
