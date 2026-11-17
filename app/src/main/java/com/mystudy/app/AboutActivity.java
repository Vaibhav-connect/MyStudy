package com.mystudy.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private final int DARK_BLUE = Color.rgb(37, 99, 235);
    private final int BACKGROUND = Color.rgb(245, 249, 255);
    private final int TEXT = Color.rgb(17, 24, 39);
    private final int SECONDARY = Color.rgb(100, 116, 139);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildUI();
    }

    private void buildUI() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(BACKGROUND);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        root.setPadding(
                dp(20),
                dp(25),
                dp(20),
                dp(35)
        );

        // Header

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView backButton = new TextView(this);
        backButton.setText("←");
        backButton.setTextSize(26);
        backButton.setTextColor(DARK_BLUE);
        backButton.setGravity(Gravity.CENTER);
        backButton.setBackground(createBackground(Color.WHITE, 15));

        header.addView(
                backButton,
                new LinearLayout.LayoutParams(dp(48), dp(48))
        );

        backButton.setOnClickListener(v -> finish());

        TextView headerTitle = new TextView(this);
        headerTitle.setText("About MyStudy");
        headerTitle.setTextSize(21);
        headerTitle.setTextColor(TEXT);
        headerTitle.setTypeface(null, Typeface.BOLD);
        headerTitle.setPadding(dp(14), 0, 0, 0);

        header.addView(headerTitle);

        root.addView(header);

        // Brand Card

        LinearLayout brandCard = createCard();

        TextView logo = new TextView(this);
        logo.setText("📘");
        logo.setTextSize(65);
        logo.setGravity(Gravity.CENTER);

        brandCard.addView(logo);

        TextView appName = new TextView(this);
        appName.setText("MyStudy");
        appName.setTextSize(32);
        appName.setTextColor(DARK_BLUE);
        appName.setTypeface(null, Typeface.BOLD);
        appName.setGravity(Gravity.CENTER);

        brandCard.addView(appName);

        TextView tagline = createInfoText(
                "Learn • Practice • Grow"
        );

        tagline.setGravity(Gravity.CENTER);

        brandCard.addView(
                tagline,
                margins(0, dp(5), 0, 0)
        );

        TextView version = new TextView(this);
        version.setText("Version " + getAppVersion());
        version.setTextSize(13);
        version.setTextColor(DARK_BLUE);
        version.setGravity(Gravity.CENTER);

        version.setPadding(
                dp(14),
                dp(8),
                dp(14),
                dp(8)
        );

        version.setBackground(
                createBackground(
                        Color.rgb(239, 246, 255),
                        20
                )
        );

        brandCard.addView(
                version,
                margins(0, dp(15), 0, 0)
        );

        root.addView(
                brandCard,
                margins(0, dp(20), 0, dp(17))
        );

        // About App

        LinearLayout descriptionCard = createCard();

        descriptionCard.addView(
                sectionTitle("📚  About the App")
        );

        descriptionCard.addView(
                createInfoText(
                        "MyStudy is an educational learning app designed to make learning simple, interactive and enjoyable for students."
                ),
                margins(0, dp(12), 0, 0)
        );

        root.addView(
                descriptionCard,
                margins(0, 0, 0, dp(17))
        );

        // Features

        LinearLayout featuresCard = createCard();

        featuresCard.addView(
                sectionTitle("✨  App Features")
        );

        addFeature(featuresCard, "📖", "Interactive Lessons");
        addFeature(featuresCard, "✏️", "Practice Questions");
        addFeature(featuresCard, "📝", "Online Exams");
        addFeature(featuresCard, "📊", "Learning Progress Tracking");
        addFeature(featuresCard, "👨‍👩‍👦", "Parent Dashboard");

        root.addView(
                featuresCard,
                margins(0, 0, 0, dp(17))
        );

        // Classes

        LinearLayout classesCard = createCard();

        classesCard.addView(
                sectionTitle("🎓  Supported Classes")
        );

        classesCard.addView(
                createInfoText("Classes 1 to 10"),
                margins(0, dp(12), 0, 0)
        );

        root.addView(
                classesCard,
                margins(0, 0, 0, dp(17))
        );

        // Languages

        LinearLayout languagesCard = createCard();

        languagesCard.addView(
                sectionTitle("🌐  Supported Languages")
        );

        languagesCard.addView(
                createInfoText(
                        "English • Marathi • Hindi • Semi-English"
                ),
                margins(0, dp(12), 0, 0)
        );

        root.addView(
                languagesCard,
                margins(0, 0, 0, dp(17))
        );

        // Developers

        LinearLayout developerCard = createCard();

        developerCard.addView(
                sectionTitle("💙  Made By")
        );

        TextView developers = new TextView(this);

        developers.setText(
                "Vaibhav Bhosale\n&\nPrathamesh Bhosale"
        );

        developers.setTextSize(19);
        developers.setTextColor(DARK_BLUE);
        developers.setTypeface(null, Typeface.BOLD);
        developers.setGravity(Gravity.CENTER);

        developers.setPadding(
                dp(10),
                dp(20),
                dp(10),
                dp(15)
        );

        developerCard.addView(developers);

        TextView madeWith = createInfoText(
                "Made with care for better learning."
        );

        madeWith.setGravity(Gravity.CENTER);

        developerCard.addView(madeWith);

        root.addView(
                developerCard,
                margins(0, 0, 0, dp(20))
        );

        // Copyright

        TextView copyright = createInfoText(
                "© 2026 MyStudy. All Rights Reserved."
        );

        copyright.setGravity(Gravity.CENTER);
        copyright.setTextSize(12);

        root.addView(
                copyright,
                margins(0, dp(10), 0, dp(10))
        );

        scrollView.addView(root);

        setContentView(scrollView);

        // Entrance Animation

        root.setAlpha(0f);
        root.setTranslationY(dp(15));

        root.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(400)
                .start();
    }

    // Get App Version

    private String getAppVersion() {

        try {

            return getPackageManager()
                    .getPackageInfo(getPackageName(), 0)
                    .versionName;

        } catch (Exception e) {

            return "1.0.0";
        }
    }

    // Add Feature

    private void addFeature(
            LinearLayout card,
            String icon,
            String feature
    ) {

        TextView item = createInfoText(
                icon + "   " + feature
        );

        item.setTextSize(15);
        item.setTextColor(TEXT);

        card.addView(
                item,
                margins(0, dp(13), 0, 0)
        );
    }

    // Create Card

    private LinearLayout createCard() {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);

        card.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(18)
        );

        card.setBackground(
                createBackground(Color.WHITE, 22)
        );

        card.setElevation(dp(3));

        return card;
    }

    // Section Title

    private TextView sectionTitle(String text) {

        TextView view = new TextView(this);

        view.setText(text);
        view.setTextSize(18);
        view.setTextColor(TEXT);
        view.setTypeface(null, Typeface.BOLD);

        return view;
    }

    // Information Text

    private TextView createInfoText(String text) {

        TextView view = new TextView(this);

        view.setText(text);
        view.setTextSize(14);
        view.setTextColor(SECONDARY);
        view.setLineSpacing(dp(4), 1f);

        return view;
    }

    // Background

    private GradientDrawable createBackground(
            int color,
            int radius
    ) {

        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));

        return drawable;
    }

    // Margins

    private LinearLayout.LayoutParams margins(
            int left,
            int top,
            int right,
            int bottom
    ) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(
                dp(left),
                dp(top),
                dp(right),
                dp(bottom)
        );

        return params;
    }

    // DP Conversion

    private int dp(float value) {

        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}
