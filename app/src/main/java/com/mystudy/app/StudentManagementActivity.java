package com.mystudy.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private LinearLayout studentContainer;
    private LinearLayout loadingContainer;

    private EditText searchInput;
    private TextView totalStudents;

    private final List<StudentData> studentList = new ArrayList<>();

    private final int BLUE = Color.rgb(37, 99, 235);
    private final int DARK_BLUE = Color.rgb(30, 58, 138);
    private final int YELLOW = Color.rgb(250, 204, 21);
    private final int BACKGROUND = Color.rgb(243, 247, 255);
    private final int WHITE = Color.WHITE;
    private final int TEXT_DARK = Color.rgb(30, 41, 59);
    private final int TEXT_GRAY = Color.rgb(100, 116, 139);
    private final int GREEN = Color.rgb(22, 163, 74);
    private final int RED = Color.rgb(220, 38, 38);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        createUi();
        loadStudents();
    }

    private void createUi() {

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

        TextView backButton = new TextView(this);
        backButton.setText("‹");
        backButton.setTextSize(34);
        backButton.setTextColor(BLUE);
        backButton.setGravity(Gravity.CENTER);

        header.addView(
                backButton,
                new LinearLayout.LayoutParams(dp(40), dp(45))
        );

        backButton.setOnClickListener(v -> finish());

        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.VERTICAL);
        brand.setPadding(dp(8), 0, 0, 0);

        TextView brandName = new TextView(this);
        brandName.setText("MyStudy");
        brandName.setTextSize(21);
        brandName.setTextColor(DARK_BLUE);
        brandName.setTypeface(null, Typeface.BOLD);

        TextView brandSubtitle = new TextView(this);
        brandSubtitle.setText("ADMIN PANEL");
        brandSubtitle.setTextSize(10);
        brandSubtitle.setTextColor(TEXT_GRAY);

        brand.addView(brandName);
        brand.addView(brandSubtitle);

        header.addView(
                brand,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        TextView icon = new TextView(this);
        icon.setText("👨‍🎓");
        icon.setTextSize(25);
        icon.setGravity(Gravity.CENTER);

        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setColor(Color.rgb(219, 234, 254));
        iconBg.setCornerRadius(dp(15));
        icon.setBackground(iconBg);

        header.addView(
                icon,
                new LinearLayout.LayoutParams(dp(48), dp(48))
        );

        root.addView(header);

        // TITLE

        TextView title = new TextView(this);
        title.setText("Student Management");
        title.setTextSize(25);
        title.setTextColor(TEXT_DARK);
        title.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        titleParams.topMargin = dp(27);

        root.addView(title, titleParams);

        TextView subtitle = new TextView(this);
        subtitle.setText(
                "View and manage registered student accounts."
        );
        subtitle.setTextSize(13);
        subtitle.setTextColor(TEXT_GRAY);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        subtitleParams.topMargin = dp(6);

        root.addView(subtitle, subtitleParams);

        // STUDENT COUNT CARD

        LinearLayout countCard = new LinearLayout(this);
        countCard.setOrientation(LinearLayout.HORIZONTAL);
        countCard.setGravity(Gravity.CENTER_VERTICAL);
        countCard.setPadding(dp(18), dp(17), dp(18), dp(17));

        GradientDrawable countBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{BLUE, DARK_BLUE}
        );

        countBg.setCornerRadius(dp(20));
        countCard.setBackground(countBg);
        countCard.setElevation(dp(3));

        LinearLayout.LayoutParams countParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(100)
                );

        countParams.topMargin = dp(22);

        root.addView(countCard, countParams);

        LinearLayout countText = new LinearLayout(this);
        countText.setOrientation(LinearLayout.VERTICAL);

        countCard.addView(
                countText,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        TextView countLabel = new TextView(this);
        countLabel.setText("REGISTERED STUDENTS");
        countLabel.setTextSize(10);
        countLabel.setTextColor(Color.rgb(219, 234, 254));
        countLabel.setTypeface(null, Typeface.BOLD);

        countText.addView(countLabel);

        totalStudents = new TextView(this);
        totalStudents.setText("0");
        totalStudents.setTextSize(30);
        totalStudents.setTextColor(WHITE);
        totalStudents.setTypeface(null, Typeface.BOLD);

        countText.addView(totalStudents);

        TextView countIcon = new TextView(this);
        countIcon.setText("🎓");
        countIcon.setTextSize(35);

        countCard.addView(countIcon);

        // SEARCH

        TextView searchLabel = new TextView(this);
        searchLabel.setText("Search Students");
        searchLabel.setTextSize(15);
        searchLabel.setTextColor(TEXT_DARK);
        searchLabel.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams searchLabelParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        searchLabelParams.topMargin = dp(25);

        root.addView(searchLabel, searchLabelParams);

        searchInput = new EditText(this);
        searchInput.setHint("Search by name or email...");
        searchInput.setTextSize(14);
        searchInput.setSingleLine(true);
        searchInput.setTextColor(TEXT_DARK);
        searchInput.setHintTextColor(Color.rgb(148, 163, 184));
        searchInput.setPadding(dp(15), 0, dp(15), 0);

        GradientDrawable searchBg = new GradientDrawable();
        searchBg.setColor(WHITE);
        searchBg.setCornerRadius(dp(14));
        searchBg.setStroke(dp(1), Color.rgb(203, 213, 225));

        searchInput.setBackground(searchBg);

        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(54)
                );

        searchParams.topMargin = dp(10);

        root.addView(searchInput, searchParams);

        // LIST TITLE

        LinearLayout listHeader = new LinearLayout(this);
        listHeader.setGravity(Gravity.CENTER_VERTICAL);

        TextView listTitle = new TextView(this);
        listTitle.setText("All Students");
        listTitle.setTextSize(19);
        listTitle.setTextColor(TEXT_DARK);
        listTitle.setTypeface(null, Typeface.BOLD);

        listHeader.addView(
                listTitle,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        TextView liveBadge = new TextView(this);
        liveBadge.setText("● STUDENTS");
        liveBadge.setTextSize(10);
        liveBadge.setTextColor(GREEN);
        liveBadge.setTypeface(null, Typeface.BOLD);

        listHeader.addView(liveBadge);

        LinearLayout.LayoutParams listHeaderParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        listHeaderParams.topMargin = dp(25);

        root.addView(listHeader, listHeaderParams);

        // LOADING CONTAINER

        loadingContainer = new LinearLayout(this);
        loadingContainer.setOrientation(LinearLayout.VERTICAL);
        loadingContainer.setGravity(Gravity.CENTER);
        loadingContainer.setPadding(0, dp(35), 0, dp(35));

        TextView loadingText = new TextView(this);
        loadingText.setText("⏳");
        loadingText.setTextSize(30);
        loadingText.setGravity(Gravity.CENTER);

        loadingContainer.addView(loadingText);

        TextView loadingLabel = new TextView(this);
        loadingLabel.setText("Loading students...");
        loadingLabel.setTextSize(13);
        loadingLabel.setTextColor(TEXT_GRAY);
        loadingLabel.setGravity(Gravity.CENTER);

        loadingContainer.addView(loadingLabel);

        root.addView(loadingContainer);

        // STUDENT LIST

        studentContainer = new LinearLayout(this);
        studentContainer.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams containerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        containerParams.topMargin = dp(15);

        root.addView(studentContainer, containerParams);

        // FOOTER

        TextView footer = new TextView(this);
        footer.setText("MyStudy © 2026 • Admin Panel");
        footer.setTextSize(11);
        footer.setTextColor(TEXT_GRAY);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(25), 0, dp(5));

        root.addView(footer);

        setContentView(scrollView);

        // SEARCH FILTER

        searchInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
                filterStudents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void loadStudents() {

        loadingContainer.setVisibility(View.VISIBLE);
        studentContainer.removeAllViews();

        db.collection("users")
                .whereEqualTo("role", "student")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    loadingContainer.setVisibility(View.GONE);

                    studentList.clear();

                    for (QueryDocumentSnapshot document : querySnapshot) {

                        String name = document.getString("name");
                        String email = document.getString("email");
                        String studentClass = document.getString("class");
                        String medium = document.getString("medium");

                        StudentData student = new StudentData();

                        student.name = name == null ? "Student" : name;
                        student.email = email == null ? "" : email;
                        student.studentClass =
                                studentClass == null ? "Not set" : studentClass;

                        student.medium =
                                medium == null ? "Not set" : medium;

                        studentList.add(student);
                    }

                    totalStudents.setText(
                            String.valueOf(studentList.size())
                    );

                    filterStudents(searchInput.getText().toString());

                })
                .addOnFailureListener(e -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    loadingContainer.setVisibility(View.GONE);

                    showErrorState();
                });
    }

    private void filterStudents(String query) {

        if (studentContainer == null) {
            return;
        }

        studentContainer.removeAllViews();

        String search = query.toLowerCase(Locale.ROOT).trim();

        int visibleCount = 0;

        for (StudentData student : studentList) {

            boolean matches =
                    student.name.toLowerCase(Locale.ROOT).contains(search)
                    ||
                    student.email.toLowerCase(Locale.ROOT).contains(search);

            if (matches) {

                addStudentCard(student);
                visibleCount++;
            }
        }

        if (visibleCount == 0) {

            showEmptyState(
                    search.isEmpty()
                            ? "No students registered yet."
                            : "No matching students found."
            );
        }
    }

    private void addStudentCard(StudentData student) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(17), dp(17), dp(17), dp(17));

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(WHITE);
        cardBg.setCornerRadius(dp(18));
        cardBg.setStroke(dp(1), Color.rgb(226, 232, 240));

        card.setBackground(cardBg);
        card.setElevation(dp(2));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.bottomMargin = dp(13);

        studentContainer.addView(card, cardParams);

        // STUDENT HEADER

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        card.addView(header);

        TextView avatar = new TextView(this);

        String initial = student.name.isEmpty()
                ? "S"
                : student.name.substring(0, 1).toUpperCase(Locale.ROOT);

        avatar.setText(initial);
        avatar.setTextSize(21);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setTextColor(BLUE);
        avatar.setGravity(Gravity.CENTER);

        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setColor(Color.rgb(219, 234, 254));
        avatarBg.setCornerRadius(dp(15));

        avatar.setBackground(avatarBg);

        header.addView(
                avatar,
                new LinearLayout.LayoutParams(dp(48), dp(48))
        );

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(12), 0, 0, 0);

        header.addView(
                details,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        TextView nameView = new TextView(this);
        nameView.setText(student.name);
        nameView.setTextSize(16);
        nameView.setTextColor(TEXT_DARK);
        nameView.setTypeface(null, Typeface.BOLD);

        details.addView(nameView);

        TextView emailView = new TextView(this);
        emailView.setText(
                student.email.isEmpty()
                        ? "Email not available"
                        : student.email
        );

        emailView.setTextSize(12);
        emailView.setTextColor(TEXT_GRAY);

        details.addView(emailView);

        TextView status = new TextView(this);
        status.setText("STUDENT");
        status.setTextSize(9);
        status.setTextColor(GREEN);
        status.setTypeface(null, Typeface.BOLD);
        status.setGravity(Gravity.CENTER);
        status.setPadding(dp(9), dp(6), dp(9), dp(6));

        GradientDrawable statusBg = new GradientDrawable();
        statusBg.setColor(Color.rgb(220, 252, 231));
        statusBg.setCornerRadius(dp(20));

        status.setBackground(statusBg);

        header.addView(status);

        // DIVIDER

        View divider = new View(this);
        divider.setBackgroundColor(Color.rgb(226, 232, 240));

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        dividerParams.topMargin = dp(15);
        dividerParams.bottomMargin = dp(13);

        card.addView(divider, dividerParams);

        // CLASS AND MEDIUM

        LinearLayout infoRow = new LinearLayout(this);
        infoRow.setOrientation(LinearLayout.HORIZONTAL);

        card.addView(infoRow);

        addInfoItem(
                infoRow,
                "🏫",
                "CLASS",
                student.studentClass
        );

        addInfoItem(
                infoRow,
                "🌐",
                "MEDIUM",
                student.medium
        );
    }

    private void addInfoItem(
            LinearLayout parent,
            String icon,
            String label,
            String value
    ) {

        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);

        TextView iconView = new TextView(this);
        iconView.setText(icon + "  " + label);
        iconView.setTextSize(10);
        iconView.setTextColor(TEXT_GRAY);
        iconView.setTypeface(null, Typeface.BOLD);

        item.addView(iconView);

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextSize(13);
        valueView.setTextColor(BLUE);
        valueView.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams valueParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        valueParams.topMargin = dp(5);

        item.addView(valueView, valueParams);

        parent.addView(
                item,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );
    }

    private void showEmptyState(String message) {

        LinearLayout emptyBox = new LinearLayout(this);
        emptyBox.setOrientation(LinearLayout.VERTICAL);
        emptyBox.setGravity(Gravity.CENTER);
        emptyBox.setPadding(dp(15), dp(30), dp(15), dp(30));

        TextView icon = new TextView(this);
        icon.setText("🎓");
        icon.setTextSize(35);
        icon.setGravity(Gravity.CENTER);

        emptyBox.addView(icon);

        TextView text = new TextView(this);
        text.setText(message);
        text.setTextSize(14);
        text.setTextColor(TEXT_GRAY);
        text.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        textParams.topMargin = dp(10);

        emptyBox.addView(text, textParams);

        studentContainer.addView(emptyBox);
    }

    private void showErrorState() {

        studentContainer.removeAllViews();

        LinearLayout errorBox = new LinearLayout(this);
        errorBox.setOrientation(LinearLayout.VERTICAL);
        errorBox.setGravity(Gravity.CENTER);
        errorBox.setPadding(dp(15), dp(25), dp(15), dp(25));

        TextView errorText = new TextView(this);
        errorText.setText("⚠️  Unable to load students.");
        errorText.setTextSize(14);
        errorText.setTextColor(RED);
        errorText.setGravity(Gravity.CENTER);

        errorBox.addView(errorText);

        TextView retry = new TextView(this);
        retry.setText("Try Again");
        retry.setTextSize(14);
        retry.setTextColor(BLUE);
        retry.setTypeface(null, Typeface.BOLD);
        retry.setGravity(Gravity.CENTER);
        retry.setPadding(dp(15), dp(15), dp(15), dp(15));

        errorBox.addView(retry);

        retry.setOnClickListener(v -> loadStudents());

        studentContainer.addView(errorBox);
    }

    private int dp(float value) {

        return (int) (
                value *
                getResources().getDisplayMetrics().density
                + 0.5f
        );
    }

    private static class StudentData {

        String name;
        String email;
        String studentClass;
        String medium;
    }
}
