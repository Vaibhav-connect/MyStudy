package com.mystudy.app;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentExamActivity extends AppCompatActivity {

    private static final int BLUE = Color.rgb(45, 105, 210);
    private static final int DARK_BLUE = Color.rgb(28, 65, 130);
    private static final int YELLOW = Color.rgb(255, 205, 65);
    private static final int BG = Color.rgb(244, 248, 255);
    private static final int TEXT = Color.rgb(35, 48, 70);
    private static final int GREEN = Color.rgb(35, 155, 95);
    private static final int GRAY = Color.rgb(110, 125, 145);

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private LinearLayout root;
    private LinearLayout container;
    private ProgressBar loadingBar;

    private String studentClass;
    private String studentMedium;
    private String studentName;

    private final List<QuizData> quizzes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        studentClass = getIntent().getStringExtra("classId");

        if (studentClass == null) {
            studentClass = getIntent().getStringExtra("studentClass");
        }

        studentMedium = getIntent().getStringExtra("studentMedium");
        studentName = getIntent().getStringExtra("studentName");

        if (studentClass == null) studentClass = "";
        if (studentName == null) studentName = "";

        if (studentMedium == null ||
                studentMedium.trim().isEmpty()) {
            studentMedium = "English";
        }

        createUI();
        loadPublishedQuizzes();
    }

    private void createUI() {

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(20), dp(20), dp(20), dp(22));
        header.setBackground(rounded(BLUE, 22));

        TextView brand = text(
                "📘 MyStudy",
                24,
                Color.WHITE,
                true
        );

        header.addView(brand);

        TextView tagline = text(
                "Learn • Practice • Grow",
                13,
                Color.WHITE,
                false
        );

        header.addView(tagline);

        TextView heading = text(
                "Exams & Quizzes",
                25,
                Color.WHITE,
                true
        );

        heading.setPadding(0, dp(20), 0, dp(5));
        header.addView(heading);

        TextView description = text(
                "Test your knowledge and improve every day!",
                13,
                Color.WHITE,
                false
        );

        header.addView(description);

        root.addView(header);

        LinearLayout infoCard = createCard();

        TextView classInfo = text(
                "🎓 Class: " +
                        (studentClass.isEmpty()
                                ? "Not selected" : studentClass),
                14,
                DARK_BLUE,
                true
        );

        TextView mediumInfo = text(
                "🌐 Medium: " + studentMedium,
                14,
                GRAY,
                false
        );

        mediumInfo.setPadding(0, dp(8), 0, 0);

        infoCard.addView(classInfo);
        infoCard.addView(mediumInfo);

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(-1, -2);

        infoParams.setMargins(
                dp(16), dp(16), dp(16), dp(8)
        );

        root.addView(infoCard, infoParams);

        TextView sectionTitle = text(
                "Available Exams",
                19,
                DARK_BLUE,
                true
        );

        sectionTitle.setPadding(
                dp(20), dp(12), dp(20), dp(8)
        );

        root.addView(sectionTitle);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);

        container.setPadding(
                dp(16), dp(8), dp(16), dp(20)
        );

        scrollView.addView(container);

        root.addView(
                scrollView,
                new LinearLayout.LayoutParams(-1, 0, 1)
        );

        Button backButton = createButton(
                "← Back",
                DARK_BLUE
        );

        LinearLayout.LayoutParams backParams =
                new LinearLayout.LayoutParams(-1, dp(50));

        backParams.setMargins(
                dp(16), dp(8), dp(16), dp(16)
        );

        root.addView(backButton, backParams);

        backButton.setOnClickListener(v -> finish());

        setContentView(root);
    }

    private void loadPublishedQuizzes() {

        container.removeAllViews();
        quizzes.clear();

        if (studentClass == null ||
                studentClass.trim().isEmpty()) {

            showMessage(
                    "⚠️",
                    "Class Information Missing",
                    "Please login again and select your class."
            );

            return;
        }

        showLoading();

        db.collection("quizzes")
                .whereEqualTo("published", true)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (isFinishing() || isDestroyed()) return;

                    hideLoading();

                    quizzes.clear();

                    for (DocumentSnapshot doc :
                            snapshot.getDocuments()) {

                        String classId =
                                doc.getString("classId");

                        String medium =
                                doc.getString("medium");

                        if (classId == null ||
                                !studentClass.equals(classId)) {
                            continue;
                        }

                        if (medium == null ||
                                medium.trim().isEmpty()) {
                            medium = "English";
                        }

                        if (!studentMedium.equalsIgnoreCase(medium)) {
                            continue;
                        }

                        QuizData quiz = new QuizData();

                        quiz.id = doc.getId();

                        quiz.title = value(
                                doc.getString("title"),
                                "Untitled Quiz"
                        );

                        quiz.type = value(
                                doc.getString("type"),
                                "PRACTICE_QUIZ"
                        );

                        quiz.className = value(
                                doc.getString("className"),
                                studentClass
                        );

                        quiz.medium = medium;

                        quiz.subjectId = value(
                                doc.getString("subjectId"),
                                ""
                        );

                        quiz.subjectName = value(
                                doc.getString("subjectName"),
                                "All Subjects"
                        );

                        quiz.chapterId = value(
                                doc.getString("chapterId"),
                                ""
                        );

                        quiz.chapterName = value(
                                doc.getString("chapterName"),
                                "All Chapters"
                        );

                        quiz.difficulty = value(
                                doc.getString("difficulty"),
                                "easy"
                        );

                        Long questionCount =
                                doc.getLong("questionCount");

                        Long totalMarks =
                                doc.getLong("totalMarks");

                        Long duration =
                                doc.getLong("durationMinutes");

                        quiz.questionCount =
                                questionCount != null
                                        ? questionCount.intValue()
                                        : 10;

                        quiz.totalMarks =
                                totalMarks != null
                                        ? totalMarks
                                        : 0;

                        quiz.durationMinutes =
                                duration != null
                                        ? duration
                                        : 10;

                        quizzes.add(quiz);
                    }

                    if (quizzes.isEmpty()) {

                        showMessage(
                                "📚",
                                "No Exams Available",
                                "No published exams or quizzes are available for your class and medium."
                        );

                        return;
                    }

                    Collections.sort(
                            quizzes,
                            (a, b) ->
                                    a.title.compareToIgnoreCase(b.title)
                    );

                    showQuizCards();
                })
                .addOnFailureListener(e -> {

                    if (isFinishing() || isDestroyed()) return;

                    hideLoading();

                    showError(
                            "Unable to load exams. Please check your internet connection."
                    );
                });
    }

    private void showQuizCards() {

        container.removeAllViews();

        for (QuizData quiz : quizzes) {
            addQuizCard(quiz);
        }
    }

    private void addQuizCard(QuizData quiz) {

        LinearLayout card = createCard();

        TextView typeBadge = text(
                quiz.type.replace("_", " "),
                12,
                BLUE,
                true
        );

        typeBadge.setBackground(rounded(
                Color.rgb(230, 239, 255),
                12
        ));

        typeBadge.setPadding(
                dp(12), dp(7), dp(12), dp(7)
        );

        card.addView(typeBadge);

        TextView title = text(
                quiz.title,
                20,
                DARK_BLUE,
                true
        );

        title.setPadding(0, dp(14), 0, dp(8));

        card.addView(title);

        addDetail(card,
                "📚 Subject: " + quiz.subjectName);

        addDetail(card,
                "📖 Chapter: " + quiz.chapterName);

        addDetail(card,
                "❓ Questions: " + quiz.questionCount);

        addDetail(card,
                "🏆 Total Marks: " + quiz.totalMarks);

        addDetail(card,
                "⏱ Time: " + quiz.durationMinutes + " minutes");

        TextView difficulty = text(
                "⭐ " + quiz.difficulty +
                        "  •  " + quiz.medium,
                13,
                GREEN,
                true
        );

        difficulty.setPadding(0, dp(12), 0, dp(12));

        card.addView(difficulty);

        Button startButton = createButton(
                "Start Quiz →",
                BLUE
        );

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(-1, dp(48));

        card.addView(startButton, buttonParams);

        startButton.setOnClickListener(v ->
                showStartConfirmation(quiz)
        );

        container.addView(card);

        animateView(card);
    }

    private void addDetail(
            LinearLayout card,
            String detail) {

        TextView view = text(
                detail,
                14,
                GRAY,
                false
        );

        view.setPadding(0, dp(5), 0, dp(5));

        card.addView(view);
    }

    private void showStartConfirmation(QuizData quiz) {

        String message =
                "Quiz: " + quiz.title +
                        "\n\nQuestions: " + quiz.questionCount +
                        "\nTotal Marks: " + quiz.totalMarks +
                        "\nTime: " + quiz.durationMinutes +
                        " minutes" +
                        "\nDifficulty: " + quiz.difficulty +
                        "\n\nDo you want to start this quiz?";

        new AlertDialog.Builder(this)
                .setTitle("🎯 Start Quiz")
                .setMessage(message)
                .setNegativeButton("Cancel", null)
                .setPositiveButton(
                        "Start",
                        (dialog, which) -> startQuiz(quiz)
                )
                .show();
    }

    private void startQuiz(QuizData quiz) {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login again.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Toast.makeText(
                this,
                "Preparing your quiz...",
                Toast.LENGTH_SHORT
        ).show();

        db.collection("questions")
                .whereEqualTo("classId", studentClass)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (isFinishing() || isDestroyed()) return;

                    List<DocumentSnapshot> matchingQuestions =
                            new ArrayList<>();

                    for (DocumentSnapshot doc :
                            snapshot.getDocuments()) {

                        String medium =
                                doc.getString("medium");

                        if (medium == null ||
                                medium.trim().isEmpty()) {
                            medium = "English";
                        }

                        if (!studentMedium.equalsIgnoreCase(medium)) {
                            continue;
                        }

                        if (!quiz.subjectId.isEmpty()) {

                            String subjectId =
                                    doc.getString("subjectId");

                            if (subjectId == null ||
                                    !quiz.subjectId.equals(subjectId)) {
                                continue;
                            }
                        }

                        if (!quiz.chapterId.isEmpty()) {

                            String chapterId =
                                    doc.getString("chapterId");

                            if (chapterId == null ||
                                    !quiz.chapterId.equals(chapterId)) {
                                continue;
                            }
                        }

                        if (!quiz.difficulty.isEmpty()) {

                            String difficulty =
                                    doc.getString("difficulty");

                            if (difficulty != null &&
                                    !quiz.difficulty.equalsIgnoreCase(
                                            difficulty)) {
                                continue;
                            }
                        }

                        matchingQuestions.add(doc);
                    }

                    if (matchingQuestions.isEmpty()) {

                        new AlertDialog.Builder(this)
                                .setTitle("No Questions Found")
                                .setMessage(
                                        "No matching questions found for this quiz."
                                )
                                .setPositiveButton("OK", null)
                                .show();

                        return;
                    }

                    Collections.shuffle(matchingQuestions);

                    int required = Math.min(
                            quiz.questionCount,
                            matchingQuestions.size()
                    );

                    ArrayList<String> questionIds =
                            new ArrayList<>();

                    for (int i = 0; i < required; i++) {

                        questionIds.add(
                                matchingQuestions.get(i).getId()
                        );
                    }

                    if (required <= 0) return;

                    android.content.Intent intent =
                            new android.content.Intent(
                                    this,
                                    StudentExamAttemptActivity.class
                            );

                    intent.putExtra("quizId", quiz.id);
                    intent.putExtra("quizTitle", quiz.title);
                    intent.putExtra("quizType", quiz.type);

                    intent.putExtra("classId", studentClass);
                    intent.putExtra("className", quiz.className);

                    intent.putExtra(
                            "studentMedium",
                            studentMedium
                    );

                    intent.putExtra(
                            "studentName",
                            studentName
                    );

                    intent.putExtra(
                            "subjectId",
                            quiz.subjectId
                    );

                    intent.putExtra(
                            "subjectName",
                            quiz.subjectName
                    );

                    intent.putExtra(
                            "chapterId",
                            quiz.chapterId
                    );

                    intent.putExtra(
                            "chapterName",
                            quiz.chapterName
                    );

                    intent.putExtra(
                            "durationMinutes",
                            quiz.durationMinutes
                    );

                    intent.putExtra(
                            "totalMarks",
                            quiz.totalMarks
                    );

                    intent.putExtra(
                            "questionCount",
                            required
                    );

                    intent.putStringArrayListExtra(
                            "questionIds",
                            questionIds
                    );

                    startActivity(intent);
                })
                .addOnFailureListener(e -> {

                    if (isFinishing() || isDestroyed()) return;

                    Toast.makeText(
                            this,
                            "Failed to prepare quiz: " +
                                    e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void showLoading() {

        container.removeAllViews();

        LinearLayout loadingCard = createCard();

        loadingCard.setGravity(Gravity.CENTER);

        ProgressBar progressBar = new ProgressBar(this);

        loadingCard.addView(
                progressBar,
                new LinearLayout.LayoutParams(
                        dp(45), dp(45)
                )
        );

        TextView message = text(
                "Loading available exams...",
                15,
                TEXT,
                true
        );

        message.setPadding(0, dp(15), 0, dp(5));

        loadingCard.addView(message);

        container.addView(loadingCard);
    }

    private void hideLoading() {
        // Loading content is replaced by the next screen state.
    }

    private void showMessage(
            String icon,
            String heading,
            String message) {

        container.removeAllViews();

        LinearLayout card = createCard();

        card.setGravity(Gravity.CENTER);
        card.setPadding(
                dp(22), dp(35), dp(22), dp(35)
        );

        TextView iconView = text(
                icon, 42, BLUE, true);

        iconView.setGravity(Gravity.CENTER);

        card.addView(iconView);

        TextView title = text(
                heading, 20, DARK_BLUE, true);

        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(12), 0, dp(8));

        card.addView(title);

        TextView description = text(
                message, 14, GRAY, false);

        description.setGravity(Gravity.CENTER);

        card.addView(description);

        container.addView(card);
    }

    private void showError(String message) {

        container.removeAllViews();

        LinearLayout card = createCard();

        card.setGravity(Gravity.CENTER);

        TextView icon = text(
                "⚠️", 40, BLUE, true);

        icon.setGravity(Gravity.CENTER);

        card.addView(icon);

        TextView title = text(
                "Unable to Load Exams",
                19,
                DARK_BLUE,
                true
        );

        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(12), 0, dp(8));

        card.addView(title);

        TextView description = text(
                message,
                14,
                GRAY,
                false
        );

        description.setGravity(Gravity.CENTER);

        card.addView(description);

        Button retry = createButton(
                "Try Again",
                BLUE
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, dp(48));

        params.topMargin = dp(18);

        card.addView(retry, params);

        retry.setOnClickListener(v ->
                loadPublishedQuizzes()
        );

        container.addView(card);
    }

    private LinearLayout createCard() {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);

        card.setPadding(
                dp(18), dp(18), dp(18), dp(18)
        );

        card.setBackground(rounded(Color.WHITE, 18));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.bottomMargin = dp(16);

        card.setLayoutParams(params);

        return card;
    }

    private TextView text(
            String value,
            int size,
            int color,
            boolean bold) {

        TextView view = new TextView(this);

        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );
        }

        return view;
    }

    private Button createButton(
            String label,
            int color) {

        Button button = new Button(this);

        button.setText(label);
        button.setTextSize(14);
        button.setAllCaps(false);

        button.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        button.setTextColor(Color.WHITE);
        button.setBackground(rounded(color, 12));

        return button;
    }

    private GradientDrawable rounded(
            int color,
            int radius) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));

        return drawable;
    }

    private void animateView(View view) {

        AlphaAnimation animation =
                new AlphaAnimation(0f, 1f);

        animation.setDuration(350);

        view.startAnimation(animation);
    }

    private int dp(int value) {

        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    private String value(
            String value,
            String fallback) {

        if (value == null ||
                value.trim().isEmpty()) {
            return fallback;
        }

        return value;
    }

    private static class QuizData {

        String id = "";
        String title = "";
        String type = "";
        String className = "";
        String medium = "";
        String subjectId = "";
        String subjectName = "";
        String chapterId = "";
        String chapterName = "";
        String difficulty = "";

        int questionCount = 10;
        long totalMarks = 0;
        long durationMinutes = 10;
    }
}
