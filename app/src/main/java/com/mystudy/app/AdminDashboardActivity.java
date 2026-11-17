package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
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

    private final int BLUE = Color.rgb(37, 99, 235);
    private final int DARK_BLUE = Color.rgb(30, 58, 138);
    private final int YELLOW = Color.rgb(250, 204, 21);
    private final int BACKGROUND = Color.rgb(243, 247, 255);
    private final int WHITE = Color.WHITE;
    private final int TEXT_DARK = Color.rgb(30, 41, 59);
    private final int TEXT_GRAY = Color.rgb(100, 116, 139);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        adminName = getIntent().getStringExtra("adminName");
        userRole = getIntent().getStringExtra("userRole");

        if (adminName == null || adminName.trim().isEmpty()) {
            adminName = "Admin";
        }

        if (userRole == null || userRole.trim().isEmpty()) {
            userRole = "admin";
        }

        createDashboard();
    }

    private void createDashboard() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(BACKGROUND);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(20), dp(20), dp(25));

        scrollView.addView(root);

        // HEADER

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView logo = new TextView(this);
        logo.setText("🎓");
        logo.setTextSize(27);
        logo.setGravity(Gravity.CENTER);

        GradientDrawable logoBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{BLUE, DARK_BLUE}
        );
        logoBg.setCornerRadius(dp(16));
        logo.setBackground(logoBg);

        header.addView(
                logo,
                new LinearLayout.LayoutParams(dp(55), dp(55))
        );

        LinearLayout brandText = new LinearLayout(this);
        brandText.setOrientation(LinearLayout.VERTICAL);
        brandText.setPadding(dp(12), 0, 0, 0);

        TextView brandName = new TextView(this);
        brandName.setText("MyStudy");
        brandName.setTextSize(22);
        brandName.setTextColor(DARK_BLUE);
        brandName.setTypeface(null, Typeface.BOLD);

        TextView brandSubtitle = new TextView(this);
        brandSubtitle.setText("ADMIN CONTROL PANEL");
        brandSubtitle.setTextSize(10);
        brandSubtitle.setTextColor(TEXT_GRAY);
        brandSubtitle.setTypeface(null, Typeface.BOLD);

        brandText.addView(brandName);
        brandText.addView(brandSubtitle);

        header.addView(
                brandText,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        TextView adminBadge = new TextView(this);
        adminBadge.setText("ADMIN");
        adminBadge.setTextSize(10);
        adminBadge.setTextColor(DARK_BLUE);
        adminBadge.setTypeface(null, Typeface.BOLD);
        adminBadge.setGravity(Gravity.CENTER);
        adminBadge.setPadding(dp(12), dp(8), dp(12), dp(8));

        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(Color.rgb(254, 243, 199));
        badgeBg.setCornerRadius(dp(20));
        adminBadge.setBackground(badgeBg);

        header.addView(adminBadge);

        root.addView(header);

        // WELCOME HERO CARD

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(22), dp(23), dp(22), dp(23));

        GradientDrawable heroBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{BLUE, DARK_BLUE}
        );
        heroBg.setCornerRadius(dp(23));

        hero.setBackground(heroBg);
        hero.setElevation(dp(4));

        LinearLayout.LayoutParams heroParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        heroParams.topMargin = dp(25);

        root.addView(hero, heroParams);

        TextView welcomeLabel = new TextView(this);
        welcomeLabel.setText("WELCOME BACK 👋");
        welcomeLabel.setTextSize(11);
        welcomeLabel.setTextColor(Color.rgb(219, 234, 254));
        welcomeLabel.setTypeface(null, Typeface.BOLD);

        hero.addView(welcomeLabel);

        TextView welcomeName = new TextView(this);
        welcomeName.setText(adminName);
        welcomeName.setTextSize(27);
        welcomeName.setTextColor(WHITE);
        welcomeName.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        nameParams.topMargin = dp(7);

        hero.addView(welcomeName, nameParams);

        TextView heroDescription = new TextView(this);
        heroDescription.setText(
                "Manage your learning platform from one place."
        );
        heroDescription.setTextSize(13);
        heroDescription.setTextColor(Color.rgb(219, 234, 254));

        LinearLayout.LayoutParams heroDescParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        heroDescParams.topMargin = dp(7);

        hero.addView(heroDescription, heroDescParams);

        // ROLE INFORMATION

        LinearLayout roleRow = new LinearLayout(this);
        roleRow.setOrientation(LinearLayout.HORIZONTAL);
        roleRow.setGravity(Gravity.CENTER_VERTICAL);
        roleRow.setPadding(dp(13), dp(11), dp(13), dp(11));

        GradientDrawable roleBg = new GradientDrawable();
        roleBg.setColor(WHITE);
        roleBg.setCornerRadius(dp(13));
        roleBg.setStroke(dp(1), Color.rgb(226, 232, 240));

        roleRow.setBackground(roleBg);

        LinearLayout.LayoutParams roleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        roleParams.topMargin = dp(15);

        root.addView(roleRow, roleParams);

        TextView roleIcon = new TextView(this);
        roleIcon.setText("🛡️");
        roleIcon.setTextSize(19);

        roleRow.addView(roleIcon);

        TextView roleTitle = new TextView(this);
        roleTitle.setText("  Your Access Role");
        roleTitle.setTextSize(13);
        roleTitle.setTextColor(TEXT_DARK);
        roleTitle.setTypeface(null, Typeface.BOLD);

        roleRow.addView(
                roleTitle,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        TextView roleValue = new TextView(this);
        roleValue.setText(userRole.equals("main_admin")
                ? "MAIN ADMIN"
                : "ADMIN");

        roleValue.setTextSize(10);
        roleValue.setTextColor(BLUE);
        roleValue.setTypeface(null, Typeface.BOLD);

        roleRow.addView(roleValue);

        // SECTION TITLE

        TextView sectionTitle = new TextView(this);
        sectionTitle.setText("Management Center");
        sectionTitle.setTextSize(21);
        sectionTitle.setTextColor(TEXT_DARK);
        sectionTitle.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams sectionParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        sectionParams.topMargin = dp(27);

        root.addView(sectionTitle, sectionParams);

        TextView sectionSubtitle = new TextView(this);
        sectionSubtitle.setText(
                "Choose a section to manage your app."
        );
        sectionSubtitle.setTextSize(13);
        sectionSubtitle.setTextColor(TEXT_GRAY);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        subtitleParams.topMargin = dp(5);

        root.addView(sectionSubtitle, subtitleParams);

        // MANAGEMENT GRID

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        grid.setUseDefaultMargins(false);

        LinearLayout.LayoutParams gridParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        gridParams.topMargin = dp(15);

        root.addView(grid, gridParams);

        addDashboardCard(
                grid,
                "👨‍🎓",
                "Students",
                "Manage student accounts",
                "Student Management"
        );

        addDashboardCard(
                grid,
                "🏫",
                "Classes",
                "Manage school classes",
                "Class Management"
        );

        addDashboardCard(
                grid,
                "📚",
                "Subjects",
                "Manage subjects",
                "Subject Management"
        );

        addDashboardCard(
                grid,
                "📖",
                "Chapters",
                "Manage chapters",
                "Chapter Management"
        );

        addDashboardCard(
                grid,
                "📝",
                "Lessons",
                "Manage learning lessons",
                "Lesson Management"
        );

        addDashboardCard(
                grid,
                "❓",
                "Questions",
                "Manage quizzes and questions",
                "Quiz Management"
        );

        addDashboardCard(
                grid,
                "📊",
                "Exam Results",
                "View student exam results",
                "Exam Results"
        );

        addDashboardCard(
                grid,
                "🏆",
                "Points & Badges",
                "Manage student rewards",
                "Gamification"
        );

        addDashboardCard(
                grid,
                "📢",
                "Announcements",
                "Manage app announcements",
                "Announcements"
        );

        if ("main_admin".equals(userRole)) {

            addDashboardCard(
                    grid,
                    "👑",
                    "Admin Management",
                    "Manage administrator accounts",
                    "Admin Management"
            );
        }

        addDashboardCard(
                grid,
                "⚙️",
                "Settings",
                "Manage admin settings",
                "Settings"
        );

        // SECURITY NOTE

        TextView securityNote = new TextView(this);
        securityNote.setText(
                "🔒  Your administrator access is protected."
        );
        securityNote.setTextSize(12);
        securityNote.setTextColor(TEXT_GRAY);
        securityNote.setGravity(Gravity.CENTER);
        securityNote.setPadding(dp(8), dp(20), dp(8), dp(15));

        root.addView(securityNote);

        // LOGOUT BUTTON

        Button logoutButton = new Button(this);
        logoutButton.setText("Logout  ↗");
        logoutButton.setTextSize(15);
        logoutButton.setTypeface(null, Typeface.BOLD);
        logoutButton.setAllCaps(false);
        logoutButton.setTextColor(WHITE);

        GradientDrawable logoutBg = new GradientDrawable();
        logoutBg.setColor(Color.rgb(220, 38, 38));
        logoutBg.setCornerRadius(dp(14));

        logoutButton.setBackground(logoutBg);
        logoutButton.setElevation(dp(3));

        LinearLayout.LayoutParams logoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(54)
                );

        logoutParams.topMargin = dp(5);

        root.addView(logoutButton, logoutParams);

        logoutButton.setOnClickListener(v -> logout());

        // FOOTER

        TextView footer = new TextView(this);
        footer.setText(
                "MyStudy © 2026\nDeveloped by Vaibhav Bhosale"
        );
        footer.setTextSize(11);
        footer.setTextColor(TEXT_GRAY);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(20), 0, dp(5));

        root.addView(footer);

        setContentView(scrollView);

        // ENTRY ANIMATION

        AlphaAnimation animation =
                new AlphaAnimation(0f, 1f);

        animation.setDuration(500);
        root.startAnimation(animation);
    }

    private void addDashboardCard(
            GridLayout grid,
            String icon,
            String title,
            String description,
            String destination
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), dp(15), dp(12), dp(14));

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(WHITE);
        cardBg.setCornerRadius(dp(18));
        cardBg.setStroke(dp(1), Color.rgb(226, 232, 240));

        card.setBackground(cardBg);
        card.setElevation(dp(2));
        card.setClickable(true);
        card.setFocusable(true);

        GridLayout.LayoutParams params =
                new GridLayout.LayoutParams();

        params.width = 0;
        params.height = dp(145);

        params.columnSpec = GridLayout.spec(
                GridLayout.UNDEFINED,
                1f
        );

        params.setMargins(
                dp(5),
                dp(6),
                dp(5),
                dp(6)
        );

        grid.addView(card, params);

        TextView iconView = new TextView(this);
        iconView.setText(icon);
        iconView.setTextSize(26);

        card.addView(iconView);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(14);
        titleView.setTextColor(TEXT_DARK);
        titleView.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        titleParams.topMargin = dp(9);

        card.addView(titleView, titleParams);

        TextView descriptionView = new TextView(this);
        descriptionView.setText(description);
        descriptionView.setTextSize(11);
        descriptionView.setTextColor(TEXT_GRAY);

        LinearLayout.LayoutParams descriptionParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        descriptionParams.topMargin = dp(4);

        card.addView(descriptionView, descriptionParams);

        card.setOnClickListener(v -> openSection(destination));
    }

    private void openSection(String destination) {

        if ("Student Management".equals(destination)) {

            startActivity(new Intent(
                    this,
                    StudentManagementActivity.class
            ));

        } else if ("Class Management".equals(destination)) {

            startActivity(new Intent(
                    this,
                    ClassManagementActivity.class
            ));

        } else if ("Subject Management".equals(destination)) {

            startActivity(new Intent(
                    this,
                    SubjectManagementActivity.class
            ));

        } else if ("Chapter Management".equals(destination)) {

            startActivity(new Intent(
                    this,
                    ChapterManagementActivity.class
            ));

        } else if ("Lesson Management".equals(destination)) {

            startActivity(new Intent(
                    this,
                    LessonManagementActivity.class
            ));

        } else if ("Quiz Management".equals(destination)) {

            startActivity(new Intent(
                    this,
                    QuizManagementActivity.class
            ));

        } else if ("Exam Results".equals(destination)) {

            startActivity(new Intent(
                    this,
                    AdminExamResultsActivity.class
            ));

        } else if ("Gamification".equals(destination)) {

            startActivity(new Intent(
                    this,
                    GamificationManagementActivity.class
            ));

        } else if ("Announcements".equals(destination)) {

            startActivity(new Intent(
                    this,
                    AnnouncementManagementActivity.class
            ));

        } else if ("Admin Management".equals(destination)) {

            if ("main_admin".equals(userRole)) {

                startActivity(new Intent(
                        this,
                        AdminManagementActivity.class
                ));

            } else {

                Toast.makeText(
                        this,
                        "Main Admin access required.",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else if ("Settings".equals(destination)) {

            startActivity(new Intent(
                    this,
                    AdminSettingsActivity.class
            ));

        } else {

            Toast.makeText(
                    this,
                    destination + " will be available soon.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void logout() {

        auth.signOut();

        Intent intent = new Intent(
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

    private int dp(float value) {

        return (int) (
                value *
                getResources().getDisplayMetrics().density
                + 0.5f
        );
    }
}
