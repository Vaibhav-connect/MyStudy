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

    private LinearLayout root;
    private LinearLayout classContainer;
    private TextView statusView;
    private TextView retryButton;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String studentClass = "";
    private String studentMedium = "";
    private String studentName = "";

    private static final int BLUE = Color.rgb(55, 105, 220);
    private static final int DARK_BLUE = Color.rgb(31, 63, 145);
    private static final int LIGHT_BLUE = Color.rgb(235, 243, 255);
    private static final int YELLOW = Color.rgb(255, 211, 74);
    private static final int BACKGROUND = Color.rgb(246, 249, 255);
    private static final int TEXT = Color.rgb(27, 39, 64);
    private static final int MUTED = Color.rgb(104, 119, 145);
    private static final int RED = Color.rgb(205, 55, 55);
    private static final int GREEN = Color.rgb(35, 145, 95);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        studentName = safe(getIntent().getStringExtra("studentName"));
        studentClass = safe(getIntent().getStringExtra("studentClass"));
        studentMedium = safe(getIntent().getStringExtra("studentMedium"));

        createUI();
        loadStudentProfile();
    }

    private void createUI() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(BACKGROUND);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(18), dp(20), dp(32));
        root.setBackgroundColor(BACKGROUND);

        TextView back = new TextView(this);
        back.setText("‹   Back");
        back.setTextSize(16);
        back.setTextColor(BLUE);
        back.setTypeface(null, Typeface.BOLD);
        back.setPadding(0, dp(4), 0, dp(14));

        back.setOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );

        root.addView(back, matchWrap());

        LinearLayout brandRow = new LinearLayout(this);
        brandRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView logo = new TextView(this);
        logo.setText("📘");
        logo.setTextSize(25);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(round(LIGHT_BLUE, 18));

        brandRow.addView(
                logo,
                new LinearLayout.LayoutParams(dp(52), dp(52))
        );

        LinearLayout brandText = new LinearLayout(this);
        brandText.setOrientation(LinearLayout.VERTICAL);
        brandText.setPadding(dp(12), 0, 0, 0);

        TextView brand = new TextView(this);
        brand.setText("MyStudy");
        brand.setTextSize(21);
        brand.setTextColor(DARK_BLUE);
        brand.setTypeface(null, Typeface.BOLD);

        TextView tagline = new TextView(this);
        tagline.setText("Learn  •  Practice  •  Grow");
        tagline.setTextSize(12);
        tagline.setTextColor(MUTED);

        brandText.addView(brand);
        brandText.addView(tagline);

        brandRow.addView(
                brandText,
                new LinearLayout.LayoutParams(0, -2, 1)
        );

        root.addView(brandRow, matchWrap());

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(20), dp(22), dp(20), dp(22));
        hero.setBackground(round(BLUE, 24));
        hero.setElevation(dp(3));

        TextView eyebrow = new TextView(this);
        eyebrow.setText("YOUR LEARNING SPACE");
        eyebrow.setTextSize(11);
        eyebrow.setLetterSpacing(0.08f);
        eyebrow.setTextColor(Color.rgb(220, 232, 255));
        eyebrow.setTypeface(null, Typeface.BOLD);

        TextView heading = new TextView(this);
        heading.setText("Let's start learning! ✨");
        heading.setTextSize(24);
        heading.setTextColor(Color.WHITE);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setPadding(0, dp(8), 0, dp(5));

        TextView intro = new TextView(this);
        intro.setText(
                "Your subjects and lessons are ready in one place."
        );
        intro.setTextSize(14);
        intro.setTextColor(Color.rgb(235, 242, 255));

        hero.addView(eyebrow);
        hero.addView(heading);
        hero.addView(intro);

        TextView badge = new TextView(this);
        badge.setText("📚  LEARN AT YOUR OWN PACE");
        badge.setTextSize(11);
        badge.setTextColor(DARK_BLUE);
        badge.setTypeface(null, Typeface.BOLD);
        badge.setPadding(dp(12), dp(8), dp(12), dp(8));
        badge.setBackground(round(YELLOW, 30));

        LinearLayout.LayoutParams badgeParams =
                new LinearLayout.LayoutParams(-2, -2);

        badgeParams.topMargin = dp(16);
        hero.addView(badge, badgeParams);

        LinearLayout.LayoutParams heroParams = matchWrap();
        heroParams.topMargin = dp(22);

        root.addView(hero, heroParams);

        TextView sectionTitle = new TextView(this);
        sectionTitle.setText("Your Class");
        sectionTitle.setTextSize(21);
        sectionTitle.setTextColor(TEXT);
        sectionTitle.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams sectionParams = matchWrap();
        sectionParams.topMargin = dp(25);

        root.addView(sectionTitle, sectionParams);

        TextView sectionSubtitle = new TextView(this);
        sectionSubtitle.setText(
                "Choose your class card to explore available subjects."
        );
        sectionSubtitle.setTextSize(13);
        sectionSubtitle.setTextColor(MUTED);

        LinearLayout.LayoutParams subtitleParams = matchWrap();
        subtitleParams.topMargin = dp(5);

        root.addView(sectionSubtitle, subtitleParams);

        statusView = new TextView(this);
        statusView.setTextSize(13);
        statusView.setGravity(Gravity.CENTER);
        statusView.setTextColor(BLUE);
        statusView.setPadding(dp(8), dp(18), dp(8), dp(12));

        root.addView(statusView, matchWrap());

        classContainer = new LinearLayout(this);
        classContainer.setOrientation(LinearLayout.VERTICAL);

        root.addView(classContainer, matchWrap());

        retryButton = new TextView(this);
        retryButton.setText("Try Again");
        retryButton.setTextSize(14);
        retryButton.setTextColor(Color.WHITE);
        retryButton.setGravity(Gravity.CENTER);
        retryButton.setTypeface(null, Typeface.BOLD);
        retryButton.setPadding(dp(20), dp(12), dp(20), dp(12));
        retryButton.setBackground(round(BLUE, 14));
        retryButton.setVisibility(View.GONE);

        retryButton.setOnClickListener(v -> loadStudentProfile());

        LinearLayout.LayoutParams retryParams =
                new LinearLayout.LayoutParams(-1, -2);

        retryParams.topMargin = dp(10);
        root.addView(retryButton, retryParams);

        TextView footer = new TextView(this);
        footer.setText(
                "🌟 Small steps every day lead to big achievements."
        );
        footer.setTextSize(12);
        footer.setTextColor(MUTED);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(dp(8), dp(28), dp(8), dp(4));

        root.addView(footer, matchWrap());

        scrollView.addView(root);
        setContentView(scrollView);
    }

    private void loadStudentProfile() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            showError("Please log in again to continue.");
            return;
        }

        showLoading("Loading your learning profile...");

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {

                    if (isFinishing() || isDestroyed()) return;

                    if (!document.exists()) {
                        showEmpty(
                                "Your student profile could not be found."
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

                        studentClass = savedClass.trim();
                    }

                    if (savedMedium != null &&
                            !savedMedium.trim().isEmpty()) {

                        studentMedium = savedMedium.trim();
                    }

                    if (savedName != null &&
                            !savedName.trim().isEmpty()) {

                        studentName = savedName.trim();
                    }

                    if (studentClass.isEmpty()) {

                        showEmpty(
                                "Your class is not selected in your profile. Please contact your administrator."
                        );

                        return;
                    }

                    loadSelectedClass();
                })
                .addOnFailureListener(error -> {

                    if (isFinishing() || isDestroyed()) return;

                    showError(
                            "Unable to load your profile. Please check your internet connection."
                    );

                    Toast.makeText(
                            this,
                            "Profile loading failed",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void loadSelectedClass() {

        showLoading("Finding your class...");

        db.collection("classes")
                .whereEqualTo("name", studentClass)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (isFinishing() || isDestroyed()) return;

                    classContainer.removeAllViews();

                    if (snapshot.isEmpty()) {

                        showEmpty(
                                studentClass + " content is not available yet."
                        );

                        return;
                    }

                    for (QueryDocumentSnapshot document : snapshot) {

                        String classId = document.getId();

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

                        statusView.setText(
                                "Your learning space is ready!"
                        );

                        statusView.setTextColor(GREEN);
                        retryButton.setVisibility(View.GONE);

                        break;
                    }
                })
                .addOnFailureListener(error -> {

                    if (isFinishing() || isDestroyed()) return;

                    showError(
                            "Unable to load your class. Please try again."
                    );

                    Toast.makeText(
                            this,
                            "Class loading failed",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void addClassCard(
            String classId,
            String className,
            String medium
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));
        card.setBackground(round(Color.WHITE, 22));
        card.setElevation(dp(4));
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout topRow = new LinearLayout(this);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView icon = new TextView(this);
        icon.setText("🎓");
        icon.setTextSize(29);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(round(LIGHT_BLUE, 18));

        topRow.addView(
                icon,
                new LinearLayout.LayoutParams(dp(62), dp(62))
        );

        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setPadding(dp(14), 0, 0, 0);

        TextView name = new TextView(this);
        name.setText(className);
        name.setTextSize(21);
        name.setTextColor(TEXT);
        name.setTypeface(null, Typeface.BOLD);

        TextView mediumText = new TextView(this);

        mediumText.setText(
                medium.isEmpty()
                        ? "Medium not selected"
                        : "Medium: " + medium
        );

        mediumText.setTextSize(13);
        mediumText.setTextColor(MUTED);
        mediumText.setPadding(0, dp(4), 0, 0);

        labels.addView(name);
        labels.addView(mediumText);

        topRow.addView(
                labels,
                new LinearLayout.LayoutParams(0, -2, 1)
        );

        TextView arrow = new TextView(this);
        arrow.setText("›");
        arrow.setTextSize(34);
        arrow.setTextColor(BLUE);
        arrow.setGravity(Gravity.CENTER);

        topRow.addView(
                arrow,
                new LinearLayout.LayoutParams(dp(30), dp(52))
        );

        card.addView(topRow);

        View divider = new View(this);
        divider.setBackgroundColor(
                Color.rgb(232, 238, 248)
        );

        LinearLayout.LayoutParams dividerParams = matchWrap();
        dividerParams.height = dp(1);
        dividerParams.topMargin = dp(16);
        dividerParams.bottomMargin = dp(12);

        card.addView(divider, dividerParams);

        LinearLayout bottomRow = new LinearLayout(this);
        bottomRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView hint = new TextView(this);
        hint.setText(
                "Explore subjects, chapters and lessons"
        );
        hint.setTextSize(13);
        hint.setTextColor(MUTED);

        bottomRow.addView(
                hint,
                new LinearLayout.LayoutParams(0, -2, 1)
        );

        TextView explore = new TextView(this);
        explore.setText("EXPLORE  →");
        explore.setTextSize(11);
        explore.setTextColor(BLUE);
        explore.setTypeface(null, Typeface.BOLD);

        bottomRow.addView(explore);

        card.addView(bottomRow);

        LinearLayout.LayoutParams params = matchWrap();
        params.topMargin = dp(4);
        params.bottomMargin = dp(12);

        classContainer.addView(card, params);

        final String selectedClassName = className;

        card.setOnClickListener(v -> {

            Intent intent = new Intent(
                    StudentClassActivity.this,
                    StudentSubjectActivity.class
            );

            intent.putExtra("classId", classId);
            intent.putExtra("className", selectedClassName);
            intent.putExtra("studentMedium", medium);
            intent.putExtra("studentName", studentName);

            startActivity(intent);

            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );
        });

        card.setOnTouchListener((view, event) -> {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                view.animate()
                        .scaleX(0.985f)
                        .scaleY(0.985f)
                        .setDuration(100)
                        .start();

            } else if (
                    event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL
            ) {

                view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                        .start();
            }

            return false;
        });
    }

    private void showLoading(String message) {

        classContainer.removeAllViews();
        retryButton.setVisibility(View.GONE);

        statusView.setText("⏳  " + message);
        statusView.setTextColor(BLUE);
    }

    private void showEmpty(String message) {

        classContainer.removeAllViews();

        statusView.setText(
                "📚  No class content available"
        );

        statusView.setTextColor(MUTED);
        retryButton.setVisibility(View.GONE);

        TextView empty = new TextView(this);
        empty.setText(message);
        empty.setTextSize(14);
        empty.setTextColor(MUTED);
        empty.setGravity(Gravity.CENTER);
        empty.setPadding(dp(22), dp(24), dp(22), dp(24));
        empty.setBackground(round(Color.WHITE, 18));

        classContainer.addView(empty, matchWrap());
    }

    private void showError(String message) {

        classContainer.removeAllViews();

        statusView.setText("Something went wrong");
        statusView.setTextColor(RED);

        retryButton.setVisibility(View.VISIBLE);

        TextView error = new TextView(this);
        error.setText("⚠️  " + message);
        error.setTextSize(14);
        error.setTextColor(RED);
        error.setGravity(Gravity.CENTER);
        error.setPadding(dp(20), dp(22), dp(20), dp(22));
        error.setBackground(round(Color.WHITE, 18));

        classContainer.addView(error, matchWrap());
    }

    private GradientDrawable round(int color, int radiusDp) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));

        return drawable;
    }

    private LinearLayout.LayoutParams matchWrap() {

        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private int dp(int value) {

        return Math.round(
                value * getResources().getDisplayMetrics().density
        );
    }

    private String safe(String value) {

        return value == null ? "" : value.trim();
    }
}
