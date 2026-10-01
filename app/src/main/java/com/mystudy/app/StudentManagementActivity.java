package com.mystudy.app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class StudentManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout studentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        createUi();
        loadStudents();
    }

    private void createUi() {

        ScrollView scrollView = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(30, 35, 30, 35);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("Student Management");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setGravity(Gravity.CENTER);

        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Registered Students");
        subtitle.setTextSize(16);
        subtitle.setTextColor(Color.rgb(100, 116, 139));

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        subtitleParams.topMargin = 20;
        root.addView(subtitle, subtitleParams);

        studentContainer = new LinearLayout(this);
        studentContainer.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams containerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        containerParams.topMargin = 20;

        root.addView(studentContainer, containerParams);

        scrollView.addView(root);

        setContentView(scrollView);
    }

    private void loadStudents() {

        db.collection("users")
                .whereEqualTo("role", "student")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    studentContainer.removeAllViews();

                    if (querySnapshot.isEmpty()) {

                        TextView empty = new TextView(this);
                        empty.setText("No students registered yet.");
                        empty.setTextSize(16);
                        empty.setTextColor(
                                Color.rgb(100, 116, 139)
                        );
                        empty.setGravity(Gravity.CENTER);

                        studentContainer.addView(empty);

                        return;
                    }

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String name =
                                document.getString("name");

                        String email =
                                document.getString("email");

                        String studentClass =
                                document.getString("class");

                        addStudentCard(
                                name,
                                email,
                                studentClass
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    TextView error = new TextView(this);

                    error.setText(
                            "Unable to load students."
                    );

                    error.setTextSize(16);
                    error.setTextColor(
                            Color.rgb(220, 38, 38)
                    );

                    studentContainer.addView(error);
                });
    }

    private void addStudentCard(
            String name,
            String email,
            String studentClass
    ) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(25, 25, 25, 25);
        card.setBackgroundColor(Color.WHITE);

        TextView nameView = new TextView(this);

        nameView.setText(
                name == null ? "Student" : name
        );

        nameView.setTextSize(20);
        nameView.setTextColor(
                Color.rgb(17, 24, 39)
        );

        card.addView(nameView);

        TextView emailView = new TextView(this);

        emailView.setText(
                email == null ? "" : email
        );

        emailView.setTextSize(14);
        emailView.setTextColor(
                Color.rgb(100, 116, 139)
        );

        card.addView(emailView);

        TextView classView = new TextView(this);

        classView.setText(
                "Class: " +
                        (studentClass == null
                                ? "Not set"
                                : studentClass)
        );

        classView.setTextSize(14);
        classView.setTextColor(
                Color.rgb(79, 70, 229)
        );

        card.addView(classView);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, 20);

        studentContainer.addView(card, params);
    }
}
