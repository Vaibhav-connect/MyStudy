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

import androidx.appcompat.app.AppCompatActivity;

public class StudentDashboardActivity extends AppCompatActivity {

    private String studentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        studentName = getIntent().getStringExtra("studentName");

        if (studentName == null || studentName.trim().isEmpty()) {
            studentName = "Student";
        }

        createDashboard();
    }

    private void createDashboard() {

        ScrollView scrollView = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 24, 20, 30);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView greeting = new TextView(this);
        greeting.setText("Welcome, " + studentName + "! 👋");
        greeting.setTextSize(26);
        greeting.setTextColor(Color.rgb(17, 24, 39));
        greeting.setTypeface(null, Typeface.BOLD);

        root.addView(
                greeting,
                new LinearLayout.LayoutParams(-1, 60)
        );

        TextView subtitle = new TextView(this);
        subtitle.setText("Ready to learn something new today?");
        subtitle.setTextSize(16);
        subtitle.setTextColor(Color.rgb(100, 116, 139));
        subtitle.setPadding(0, 0, 0, 20);

        root.addView(subtitle);

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);

        stats.addView(
                createStatCard("⭐", "0", "Points"),
                new LinearLayout.LayoutParams(0, 120, 1)
        );

        stats.addView(
                createStatCard("🔥", "0", "Day Streak"),
                new LinearLayout.LayoutParams(0, 120, 1)
        );

        stats.addView(
                createStatCard("🎯", "0%", "Accuracy"),
                new LinearLayout.LayoutParams(0, 120, 1)
        );

        root.addView(stats);

        TextView learningTitle = new TextView(this);
        learningTitle.setText("📚 Start Learning");
        learningTitle.setTextSize(21);
        learningTitle.setTextColor(Color.rgb(17, 24, 39));
        learningTitle.setTypeface(null, Typeface.BOLD);
        learningTitle.setPadding(0, 28, 0, 14);

        root.addView(learningTitle);

        LinearLayout learningCard = createActionCard(
                "📖",
                "Learn Subjects",
                "Choose your class, subject and chapter",
                view -> {

                    Intent intent = new Intent(
                            StudentDashboardActivity.this,
                            StudentClassActivity.class
                    );

                    startActivity(intent);
                }
        );

        root.addView(learningCard);

        TextView practiceTitle = new TextView(this);
        practiceTitle.setText("🎯 Practice");
        practiceTitle.setTextSize(21);
        practiceTitle.setTextColor(Color.rgb(17, 24, 39));
        practiceTitle.setTypeface(null, Typeface.BOLD);
        practiceTitle.setPadding(0, 28, 0, 14);

        root.addView(practiceTitle);

        LinearLayout practiceCard = createActionCard(
                "🧠",
                "Practice Questions",
                "Test your knowledge and improve your score",
                view -> {

                    Intent intent = new Intent(
                            StudentDashboardActivity.this,
                            StudentClassActivity.class
                    );

                    startActivity(intent);
                }
        );

        root.addView(practiceCard);

        TextView progressTitle = new TextView(this);
        progressTitle.setText("📊 Your Progress");
        progressTitle.setTextSize(21);
        progressTitle.setTextColor(Color.rgb(17, 24, 39));
        progressTitle.setTypeface(null, Typeface.BOLD);
        progressTitle.setPadding(0, 28, 0, 14);

        root.addView(progressTitle);

        LinearLayout progressCard = createProgressCard();

        root.addView(progressCard);

        TextView about = new TextView(this);
        about.setText(
                "MyStudy\n\nDeveloped by Vaibhav Bhosale\n" +
                "vb1961869@gmail.com"
        );

        about.setTextSize(13);
        about.setTextColor(Color.rgb(100, 116, 139));
        about.setGravity(Gravity.CENTER);
        about.setPadding(0, 35, 0, 10);

        root.addView(about);

        scrollView.addView(root);

        setContentView(scrollView);
    }

    private LinearLayout createStatCard(
            String icon,
            String value,
            String label
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(8, 8, 8, 8);

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(24);

        card.setBackground(background);

        TextView iconView = new TextView(this);
        iconView.setText(icon);
        iconView.setTextSize(22);
        iconView.setGravity(Gravity.CENTER);

        card.addView(iconView);

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextSize(19);
        valueView.setTextColor(Color.rgb(79, 70, 229));
        valueView.setTypeface(null, Typeface.BOLD);
        valueView.setGravity(Gravity.CENTER);

        card.addView(valueView);

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextSize(11);
        labelView.setTextColor(Color.rgb(100, 116, 139));
        labelView.setGravity(Gravity.CENTER);

        card.addView(labelView);

        return card;
    }

    private LinearLayout createActionCard(
            String icon,
            String title,
            String description,
            View.OnClickListener listener
    ) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(
                LinearLayout.HORIZONTAL
        );

        card.setGravity(
                Gravity.CENTER_VERTICAL
        );

        card.setPadding(
                18,
                16,
                18,
                16
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(28);

        card.setBackground(background);

        TextView iconView = new TextView(this);
        iconView.setText(icon);
        iconView.setTextSize(32);
        iconView.setGravity(Gravity.CENTER);

        card.addView(
                iconView,
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
                14,
                0,
                8,
                0
        );

        TextView titleView =
                new TextView(this);

        titleView.setText(title);
        titleView.setTextSize(18);
        titleView.setTextColor(
                Color.rgb(17, 24, 39)
        );
        titleView.setTypeface(
                null,
                Typeface.BOLD
        );

        textContainer.addView(titleView);

        TextView descriptionView =
                new TextView(this);

        descriptionView.setText(description);
        descriptionView.setTextSize(13);
        descriptionView.setTextColor(
                Color.rgb(100, 116, 139)
        );
        descriptionView.setPadding(
                0,
                5,
                0,
                0
        );

        textContainer.addView(descriptionView);

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
        arrow.setTextSize(30);
        arrow.setTextColor(
                Color.rgb(79, 70, 229)
        );
        arrow.setGravity(Gravity.CENTER);

        card.addView(
                arrow,
                new LinearLayout.LayoutParams(
                        40,
                        60
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        105
                );

        params.setMargins(
                0,
                0,
                0,
                12
        );

        card.setOnClickListener(listener);

        card.setLayoutParams(params);

        return card;
    }

    private LinearLayout createProgressCard() {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                20,
                18,
                20,
                18
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(Color.WHITE);
        background.setCornerRadius(28);

        card.setBackground(background);

        TextView title = new TextView(this);
        title.setText("Learning Progress");
        title.setTextSize(17);
        title.setTextColor(
                Color.rgb(17, 24, 39)
        );
        title.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(title);

        TextView progress = new TextView(this);
        progress.setText(
                "0 lessons completed"
        );
        progress.setTextSize(14);
        progress.setTextColor(
                Color.rgb(100, 116, 139)
        );
        progress.setPadding(
                0,
                8,
                0,
                0
        );

        card.addView(progress);

        TextView badge = new TextView(this);
        badge.setText(
                "🏅 Keep learning to earn badges!"
        );
        badge.setTextSize(14);
        badge.setTextColor(
                Color.rgb(79, 70, 229)
        );
        badge.setPadding(
                0,
                14,
                0,
                0
        );

        card.addView(badge);

        return card;
    }
}
