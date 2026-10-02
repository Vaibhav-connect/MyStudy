package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
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

        adminName =
                getIntent().getStringExtra("adminName");

        userRole =
                getIntent().getStringExtra("userRole");

        if (adminName == null ||
                adminName.trim().isEmpty()) {

            adminName = "Admin";
        }

        if (userRole == null ||
                userRole.trim().isEmpty()) {

            userRole = "admin";
        }

        createDashboard();
    }

    private void createDashboard() {

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                32,
                40,
                32,
                32
        );

        root.setBackgroundColor(
                Color.rgb(248, 250, 252)
        );

        TextView title =
                new TextView(this);

        title.setText(
                "MyStudy Admin"
        );

        title.setTextSize(30);

        title.setTextColor(
                Color.rgb(17, 24, 39)
        );

        title.setGravity(
                Gravity.CENTER
        );

        TextView welcome =
                new TextView(this);

        welcome.setText(
                "Welcome, " +
                        adminName +
                        " 👋"
        );

        welcome.setTextSize(20);

        welcome.setTextColor(
                Color.rgb(79, 70, 229)
        );

        welcome.setGravity(
                Gravity.CENTER
        );

        welcome.setPadding(
                0,
                12,
                0,
                4
        );

        TextView role =
                new TextView(this);

        role.setText(
                "Role: " +
                        userRole
        );

        role.setTextSize(14);

        role.setTextColor(
                Color.rgb(100, 116, 139)
        );

        role.setGravity(
                Gravity.CENTER
        );

        role.setPadding(
                0,
                0,
                0,
                25
        );

        root.addView(title);
        root.addView(welcome);
        root.addView(role);

        GridLayout grid =
                new GridLayout(this);

        grid.setColumnCount(2);
        grid.setRowCount(7);

        addDashboardButton(
                grid,
                "👨‍🎓 Students",
                "Student Management"
        );

        addDashboardButton(
                grid,
                "🏫 Classes",
                "Class Management"
        );

        addDashboardButton(
                grid,
                "📚 Subjects",
                "Subject Management"
        );

        addDashboardButton(
                grid,
                "📖 Chapters",
                "Chapter Management"
        );

        addDashboardButton(
                grid,
                "📝 Lessons",
                "Lesson Management"
        );

        addDashboardButton(
                grid,
                "❓ Questions",
                "Quiz Management"
        );

        addDashboardButton(
                grid,
                "📊 Exam Results",
                "Exam Results"
        );

        addDashboardButton(
                grid,
                "🏆 Points & Badges",
                "Gamification"
        );

        addDashboardButton(
                grid,
                "📢 Announcements",
                "Announcements"
        );

        if ("main_admin".equals(userRole)) {

            addDashboardButton(
                    grid,
                    "👑 Admins",
                    "Admin Management"
            );
        }

        addDashboardButton(
                grid,
                "⚙️ Settings",
                "Settings"
        );

        root.addView(
                grid,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        Button logoutButton =
                new Button(this);

        logoutButton.setText(
                "Logout"
        );

        logoutButton.setTextSize(16);

        logoutButton.setAllCaps(false);

        logoutButton.setOnClickListener(
                v -> logout()
        );

        LinearLayout.LayoutParams logoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        logoutParams.topMargin = 20;

        root.addView(
                logoutButton,
                logoutParams
        );

        setContentView(root);
    }

    private void addDashboardButton(
            GridLayout grid,
            String title,
            String description
    ) {

        Button button =
                new Button(this);

        button.setText(title);

        button.setTextSize(15);

        button.setAllCaps(false);

        GridLayout.LayoutParams params =
                new GridLayout.LayoutParams();

        params.width = 0;
        params.height = 180;

        params.columnSpec =
                GridLayout.spec(
                        GridLayout.UNDEFINED,
                        1f
                );

        params.setMargins(
                10,
                10,
                10,
                10
        );

        grid.addView(
                button,
                params
        );

        button.setOnClickListener(
                v -> {

                    if ("Student Management".equals(description)) {

                        startActivity(
                                new Intent(
                                        this,
                                        StudentManagementActivity.class
                                )
                        );

                    } else if ("Class Management".equals(description)) {

                        startActivity(
                                new Intent(
                                        this,
                                        ClassManagementActivity.class
                                )
                        );

                    } else if ("Subject Management".equals(description)) {

                        startActivity(
                                new Intent(
                                        this,
                                        SubjectManagementActivity.class
                                )
                        );

                    } else if ("Chapter Management".equals(description)) {

                        startActivity(
                                new Intent(
                                        this,
                                        ChapterManagementActivity.class
                                )
                        );

                    } else if ("Lesson Management".equals(description)) {

                        startActivity(
                                new Intent(
                                        this,
                                        LessonManagementActivity.class
                                )
                        );

                    } else if ("Quiz Management".equals(description)) {

                        startActivity(
                                new Intent(
                                        this,
                                        QuizManagementActivity.class
                                )
                        );

                    } else if ("Exam Results".equals(description)) {

                        startActivity(
                                new Intent(
                                        this,
                                        AdminExamResultsActivity.class
                                )
                        );

                    } else if ("Gamification".equals(description)) {

                        startActivity(
                                new Intent(
                                        this,
                                        GamificationManagementActivity.class
                                )
                        );

                    } else if ("Announcements".equals(description)) {

                        startActivity(
                                new Intent(
                                        this,
                                        AnnouncementManagementActivity.class
                                )
                        );

                    } else if ("Admin Management".equals(description)) {

                        if ("main_admin".equals(userRole)) {

                            startActivity(
                                    new Intent(
                                            this,
                                            AdminManagementActivity.class
                                    )
                            );

                        } else {

                            Toast.makeText(
                                    this,
                                    "Main Admin access required.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    } else {

                        Toast.makeText(
                                this,
                                description +
                                        " will be available soon.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

    private void logout() {

        auth.signOut();

        Intent intent =
                new Intent(
                        this,
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
