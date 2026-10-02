package com.mystudy.app;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
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

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private LinearLayout container;

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

        if (studentMedium == null || studentMedium.trim().isEmpty()) {
            studentMedium = "English";
        }

        createUI();
        loadPublishedQuizzes();
    }

    private void createUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 30, 24, 24);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("Exams & Quizzes");
        title.setTextSize(27);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 8);

        root.addView(title);

        TextView studentInfo = new TextView(this);
        studentInfo.setText(
                "Class: " +
                        (studentClass == null ? "Not selected" : studentClass) +
                        " • Medium: " +
                        studentMedium
        );
        studentInfo.setTextSize(14);
        studentInfo.setTextColor(Color.rgb(100, 116, 139));
        studentInfo.setGravity(Gravity.CENTER);
        studentInfo.setPadding(0, 0, 0, 20);

        root.addView(studentInfo);

        ScrollView scrollView = new ScrollView(this);

        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(0, 10, 0, 20);

        scrollView.addView(container);

        root.addView(
                scrollView,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        Button backButton = new Button(this);
        backButton.setText("Back");
        backButton.setAllCaps(false);

        backButton.setOnClickListener(v -> finish());

        root.addView(backButton);

        setContentView(root);
    }

    private void loadPublishedQuizzes() {

        container.removeAllViews();
        quizzes.clear();

        if (studentClass == null || studentClass.trim().isEmpty()) {

            showMessage(
                    "Class information is missing. Please login again."
            );

            return;
        }

        db.collection("quizzes")
                .whereEqualTo("published", true)
                .get()
                .addOnSuccessListener(snapshot -> {

                    for (DocumentSnapshot doc :
                            snapshot.getDocuments()) {

                        String classId =
                                doc.getString("classId");

                        String medium =
                                doc.getString("medium");

                        if (classId == null) {
                            continue;
                        }

                        if (!studentClass.equals(classId)) {
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
                                "No published exams or quizzes are available for your class and medium."
                        );

                        return;
                    }

                    Collections.sort(
                            quizzes,
                            (a, b) ->
                                    a.title.compareToIgnoreCase(
                                            b.title
                                    )
                    );

                    for (QuizData quiz : quizzes) {
                        addQuizCard(quiz);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to load exams: " +
                                        e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void addQuizCard(QuizData quiz) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 22, 24, 22);
        card.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText(quiz.title);
        title.setTextSize(20);
        title.setTextColor(Color.rgb(17, 24, 39));

        TextView type = new TextView(this);
        type.setText(
                "Type: " +
                        quiz.type
        );
        type.setTextSize(14);
        type.setTextColor(Color.rgb(79, 70, 229));
        type.setPadding(0, 8, 0, 0);

        TextView subject = new TextView(this);
        subject.setText(
                "Subject: " +
                        quiz.subjectName
        );
        subject.setTextSize(14);
        subject.setTextColor(Color.rgb(14, 116, 144));
        subject.setPadding(0, 6, 0, 0);

        TextView chapter = new TextView(this);
        chapter.setText(
                "Chapter: " +
                        quiz.chapterName
        );
        chapter.setTextSize(14);
        chapter.setTextColor(Color.rgb(124, 58, 237));
        chapter.setPadding(0, 6, 0, 0);

        TextView details = new TextView(this);
        details.setText(
                "Questions: " +
                        quiz.questionCount +
                        " • Marks: " +
                        quiz.totalMarks +
                        " • Time: " +
                        quiz.durationMinutes +
                        " min"
        );
        details.setTextSize(14);
        details.setTextColor(Color.rgb(100, 116, 139));
        details.setPadding(0, 8, 0, 0);

        TextView difficulty = new TextView(this);
        difficulty.setText(
                "Difficulty: " +
                        quiz.difficulty +
                        " • " +
                        quiz.medium
        );
        difficulty.setTextSize(13);
        difficulty.setTextColor(Color.rgb(22, 163, 74));
        difficulty.setPadding(0, 6, 0, 14);

        Button startButton = new Button(this);
        startButton.setText("Start Quiz");
        startButton.setAllCaps(false);
        startButton.setTextSize(15);

        startButton.setOnClickListener(v ->
                showStartConfirmation(quiz)
        );

        card.addView(title);
        card.addView(type);
        card.addView(subject);
        card.addView(chapter);
        card.addView(details);
        card.addView(difficulty);
        card.addView(startButton);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.setMargins(
                0,
                0,
                0,
                18
        );

        container.addView(card, params);
    }

    private void showStartConfirmation(QuizData quiz) {

        String message =
                "Quiz: " +
                        quiz.title +
                        "\n\n" +
                        "Questions: " +
                        quiz.questionCount +
                        "\n" +
                        "Total Marks: " +
                        quiz.totalMarks +
                        "\n" +
                        "Time: " +
                        quiz.durationMinutes +
                        " minutes\n" +
                        "Difficulty: " +
                        quiz.difficulty +
                        "\n\n" +
                        "Do you want to start this quiz?";

        new AlertDialog.Builder(this)
                .setTitle("Start Quiz")
                .setMessage(message)
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Start",
                        (dialog, which) ->
                                startQuiz(quiz)
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

        db.collection("questions")
                .whereEqualTo(
                        "classId",
                        studentClass
                )
                .get()
                .addOnSuccessListener(snapshot -> {

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

                        if (!studentMedium.equalsIgnoreCase(
                                medium
                        )) {
                            continue;
                        }

                        if (!quiz.subjectId.isEmpty()) {

                            String subjectId =
                                    doc.getString("subjectId");

                            if (subjectId == null ||
                                    !quiz.subjectId.equals(
                                            subjectId
                                    )) {
                                continue;
                            }
                        }

                        if (!quiz.chapterId.isEmpty()) {

                            String chapterId =
                                    doc.getString("chapterId");

                            if (chapterId == null ||
                                    !quiz.chapterId.equals(
                                            chapterId
                                    )) {
                                continue;
                            }
                        }

                        if (!quiz.difficulty.isEmpty()) {

                            String difficulty =
                                    doc.getString("difficulty");

                            if (difficulty != null &&
                                    !quiz.difficulty.equalsIgnoreCase(
                                            difficulty
                                    )) {
                                continue;
                            }
                        }

                        matchingQuestions.add(doc);
                    }

                    if (matchingQuestions.isEmpty()) {

                        Toast.makeText(
                                this,
                                "No matching questions found for this quiz.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    Collections.shuffle(
                            matchingQuestions
                    );

                    int required =
                            Math.min(
                                    quiz.questionCount,
                                    matchingQuestions.size()
                            );

                    ArrayList<String> questionIds =
                            new ArrayList<>();

                    for (int i = 0;
                         i < required;
                         i++) {

                        questionIds.add(
                                matchingQuestions
                                        .get(i)
                                        .getId()
                        );
                    }

                    if (required <= 0) {

                        Toast.makeText(
                                this,
                                "No questions available.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    android.content.Intent intent =
                            new android.content.Intent(
                                    this,
                                    StudentExamAttemptActivity.class
                            );

                    intent.putExtra(
                            "quizId",
                            quiz.id
                    );

                    intent.putExtra(
                            "quizTitle",
                            quiz.title
                    );

                    intent.putExtra(
                            "quizType",
                            quiz.type
                    );

                    intent.putExtra(
                            "classId",
                            studentClass
                    );

                    intent.putExtra(
                            "className",
                            quiz.className
                    );

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
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to prepare quiz: " +
                                        e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void showMessage(String message) {

        TextView text = new TextView(this);

        text.setText(message);
        text.setTextSize(16);
        text.setTextColor(Color.rgb(100, 116, 139));
        text.setGravity(Gravity.CENTER);
        text.setPadding(30, 80, 30, 80);

        container.addView(
                text,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }

    private String value(
            String value,
            String fallback
    ) {

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
