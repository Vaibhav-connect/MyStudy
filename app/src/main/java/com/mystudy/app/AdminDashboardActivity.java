package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private String adminName;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        adminName = getIntent().getStringExtra("adminName");
        userRole = getIntent().getStringExtra("userRole");

        if (adminName == null || adminName.trim().isEmpty()) {
            adminName = "Admin";
        }

        if (userRole == null) {
            userRole = "admin";
        }

        createDashboard();
    }

    private void createDashboard() {

        ScrollView scrollView = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(30, 40, 30, 40);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("MyStudy Admin");
        title.setTextSize(30);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setGravity(Gravity.CENTER);

        root.addView(title);

        TextView welcome = new TextView(this);
        welcome.setText("Welcome, " + adminName + " 👋");
        welcome.setTextSize(20);
        welcome.setTextColor(Color.rgb(79, 70, 229));
        welcome.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams welcomeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        welcomeParams.topMargin = 15;
        root.addView(welcome, welcomeParams);

        TextView roleText = new TextView(this);

        if (userRole.equals("main_admin")) {
            roleText.setText("MAIN ADMIN");
        } else {
            roleText.setText("ADMIN");
        }

        roleText.setTextSize(13);
        roleText.setTextColor(Color.rgb(100, 116, 139));
        roleText.setGravity(Gravity.CENTER);

        root.addView(roleText);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        grid.setUseDefaultMargins(true);

        LinearLayout.LayoutParams gridParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        gridParams.topMargin = 35;

        root.addView(grid, gridParams);

        addDashboardButton(
                grid,
                "👥\nStudents",
                "Student Management"
        );

        addDashboardButton(
                grid,
                "📚\nClasses",
                "Class Management"
        );

        addDashboardButton(
                grid,
                "📖\nSubjects",
                "Subject Management"
        );

        addDashboardButton(
                grid,
                "📑\nChapters",
                "Chapter Management"
        );

        addDashboardButton(
                grid,
                "🎓\nLessons",
                "Lesson Management"
        );

        addDashboardButton(
                grid,
                "❓\nQuestions",
                "Quiz Management"
        );

        addDashboardButton(
                grid,
                "🏆\nPoints & Badges",
                "Gamification"
        );

        addDashboardButton(
                grid,
                "📢\nAnnouncements",
                "Announcements"
        );

        if (userRole.equals("main_admin")) {

            addDashboardButton(
                    grid,
                    "👤\nAdmin Management",
                    "Manage Admins"
            );
        }

        addDashboardButton(
                grid,
                "⚙️\nSettings",
                "Admin Settings"
        );

        Button logoutButton = new Button(this);
        logoutButton.setText("Logout");
        logoutButton.setTextSize(16);

        LinearLayout.LayoutParams logoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        logoutParams.topMargin = 30;

        root.addView(logoutButton, logoutParams);

        logoutButton.setOnClickListener(v -> logout());

        scrollView.addView(root);

        setContentView(scrollView);
    }

    private void addDashboardButton(
            GridLayout grid,
            String title,
            String description
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(15, 25, 15, 25);

        card.setBackgroundColor(Color.WHITE);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(19);
        titleView.setTextColor(Color.rgb(17, 24, 39));
        titleView.setGravity(Gravity.CENTER);

        TextView descriptionView = new TextView(this);
        descriptionView.setText(description);
        descriptionView.setTextSize(11);
        descriptionView.setTextColor(Color.rgb(100, 116, 139));
        descriptionView.setGravity(Gravity.CENTER);

        card.addView(titleView);
        card.addView(descriptionView);

        GridLayout.LayoutParams params =
                new GridLayout.LayoutParams();

        params.width = 0;
        params.height =
                (int) (125 * getResources().getDisplayMetrics().density);

        params.columnSpec =
                GridLayout.spec(
                        GridLayout.UNDEFINED,
                        1f
                );

        params.rowSpec =
                GridLayout.spec(
                        GridLayout.UNDEFINED,
                        1f
                );

        params.setMargins(8, 8, 8, 8);

        grid.addView(card, params);

        card.setOnClickListener(v -> {

    if (description.equals("Student Management")) {

        Intent intent =
                new Intent(
                        AdminDashboardActivity.this,
                        StudentManagementActivity.class
                );

        startActivity(intent);

    } else {

        Toast.makeText(
                this,
                description + " will be available soon.",
                Toast.LENGTH_SHORT
        ).show();
    }
});

    private void logout() {

        auth.signOut();

        Intent intent =
                new Intent(
                        AdminDashboardActivity.this,
                        IntroActivity.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}
