package com.mystudy.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StudentExamActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private LinearLayout rootLayout;
    private LinearLayout questionContainer;
    private TextView titleText;
    private TextView infoText;
    private TextView timerText;
    private Button submitButton;

    private String examId;
    private String studentClassId;
    private String studentClassName;
    private String studentMedium;
    private String studentName;
    private String studentSubjectId;
    private String studentSubjectName;
    private String studentChapterId;
    private String studentChapterName;

    private int questionCount = 10;
    private int durationMinutes = 15;
    private int totalMarks = 10;

    private CountDownTimer countDownTimer;

    private final List<ExamQuestion> questions = new ArrayList<>();
    private final Map<Integer, String> selectedAnswers = new HashMap<>();

    private int currentScore = 0;
    private boolean submitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        readIntentData();
        buildUi();
        loadExam();
    }

    private void readIntentData() {
        Intent intent = getIntent();

        examId = intent.getStringExtra("examId");

        studentClassId = intent.getStringExtra("classId");
        studentClassName = intent.getStringExtra("className");
        studentMedium = intent.getStringExtra("studentMedium");
        studentName = intent.getStringExtra("studentName");

        studentSubjectId = intent.getStringExtra("subjectId");
        studentSubjectName = intent.getStringExtra("subjectName");

        studentChapterId = intent.getStringExtra("chapterId");
        studentChapterName = intent.getStringExtra("chapterName");

        if (studentMedium == null || studentMedium.trim().isEmpty()) {
            studentMedium = "English";
        }
    }

    private void buildUi() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(24, 24, 24, 40);
        rootLayout.setBackgroundColor(Color.rgb(248, 250, 252));

        titleText = new TextView(this);
        titleText.setText("Exam");
        titleText.setTextSize(25);
        titleText.setTextColor(Color.rgb(17, 24, 39));
        titleText.setGravity(Gravity.CENTER);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);

        rootLayout.addView(titleText, new LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        infoText = new TextView(this);
        infoText.setText("Loading exam...");
        infoText.setTextSize(15);
        infoText.setTextColor(Color.rgb(100, 116, 139));
        infoText.setGravity(Gravity.CENTER);
        infoText.setPadding(0, 8, 0, 8);

        rootLayout.addView(infoText, new LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        timerText = new TextView(this);
        timerText.setText("Time: --:--");
        timerText.setTextSize(18);
        timerText.setTextColor(Color.rgb(220, 38, 38));
        timerText.setGravity(Gravity.CENTER);
        timerText.setTypeface(null, android.graphics.Typeface.BOLD);
        timerText.setPadding(0, 12, 0, 16);

        rootLayout.addView(timerText, new LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        questionContainer = new LinearLayout(this);
        questionContainer.setOrientation(LinearLayout.VERTICAL);

        rootLayout.addView(questionContainer, new LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        submitButton = new Button(this);
        submitButton.setText("Submit Exam");
        submitButton.setTextSize(16);
        submitButton.setAllCaps(false);

        submitButton.setOnClickListener(v -> submitExam());

        LinearLayout.LayoutParams submitParams =
                new LinearLayout.LayoutParams(
                        -1,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        submitParams.topMargin = 24;

        rootLayout.addView(submitButton, submitParams);

        scrollView.addView(rootLayout);
        setContentView(scrollView);
    }

    private void loadExam() {

        if (examId == null || examId.trim().isEmpty()) {
            Toast.makeText(this, "Exam not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        db.collection("quizzes")
                .document(examId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        Toast.makeText(
                                this,
                                "Exam does not exist.",
                                Toast.LENGTH_LONG
                        ).show();
                        finish();
                        return;
                    }

                    String title = document.getString("title");

                    if (title == null || title.trim().isEmpty()) {
                        title = "MyStudy Exam";
                    }

                    titleText.setText(title);

                    Long countValue = document.getLong("questionCount");
                    if (countValue != null && countValue > 0) {
                        questionCount = countValue.intValue();
                    }

                    Long durationValue = document.getLong("durationMinutes");
                    if (durationValue != null && durationValue > 0) {
                        durationMinutes = durationValue.intValue();
                    }

                    Long marksValue = document.getLong("totalMarks");
                    if (marksValue != null && marksValue > 0) {
                        totalMarks = marksValue.intValue();
                    }

                    String examClassId = document.getString("classId");
                    String examMedium = document.getString("medium");

                    if (examClassId != null &&
                            studentClassId != null &&
                            !examClassId.equals(studentClassId)) {

                        Toast.makeText(
                                this,
                                "This exam is not for your class.",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();
                        return;
                    }

                    if (examMedium != null &&
                            studentMedium != null &&
                            !examMedium.equalsIgnoreCase(studentMedium)) {

                        Toast.makeText(
                                this,
                                "This exam is not available for your medium.",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();
                        return;
                    }

                    infoText.setText(
                            "Class " + safe(studentClassName)
                                    + " • " + safe(studentMedium)
                                    + " • " + questionCount + " Questions"
                    );

                    loadQuestions(document);

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Unable to load exam.",
                            Toast.LENGTH_LONG
                    ).show();

                    finish();
                });
    }

    private void loadQuestions(
            com.google.firebase.firestore.DocumentSnapshot examDocument
    ) {

        questions.clear();

        String classId = examDocument.getString("classId");
        String medium = examDocument.getString("medium");
        String subjectId = examDocument.getString("subjectId");
        String chapterId = examDocument.getString("chapterId");

        db.collection("questions")
                .whereEqualTo("classId", classId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    for (QueryDocumentSnapshot doc : querySnapshot) {

                        String questionMedium = doc.getString("medium");

                        if (questionMedium == null) {
                            questionMedium = "English";
                        }

                        if (!questionMedium.equalsIgnoreCase(
                                medium == null ? "English" : medium
                        )) {
                            continue;
                        }

                        String questionSubjectId =
                                doc.getString("subjectId");

                        String questionChapterId =
                                doc.getString("chapterId");

                        if (subjectId != null &&
                                !subjectId.trim().isEmpty() &&
                                !subjectId.equals("ALL") &&
                                !subjectId.equals(questionSubjectId)) {
                            continue;
                        }

                        if (chapterId != null &&
                                !chapterId.trim().isEmpty() &&
                                !chapterId.equals("ALL") &&
                                !chapterId.equals(questionChapterId)) {
                            continue;
                        }

                        ExamQuestion question = new ExamQuestion();

                        question.id = doc.getId();
                        question.question =
                                safe(doc.getString("question"));

                        question.type =
                                safe(doc.getString("type"));

                        question.optionA =
                                safe(doc.getString("optionA"));

                        question.optionB =
                                safe(doc.getString("optionB"));

                        question.optionC =
                                safe(doc.getString("optionC"));

                        question.optionD =
                                safe(doc.getString("optionD"));

                        question.answer =
                                safe(doc.getString("answer"));

                        question.explanation =
                                safe(doc.getString("explanation"));

                        question.marks = 1;

                        Long marks = doc.getLong("marks");

                        if (marks != null && marks > 0) {
                            question.marks = marks.intValue();
                        }

                        if (!question.question.trim().isEmpty()) {
                            questions.add(question);
                        }
                    }

                    if (questions.isEmpty()) {

                        Toast.makeText(
                                this,
                                "No questions available for this exam.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    Collections.shuffle(questions);

                    if (questions.size() > questionCount) {
                        while (questions.size() > questionCount) {
                            questions.remove(questions.size() - 1);
                        }
                    }

                    totalMarks = 0;

                    for (ExamQuestion question : questions) {
                        totalMarks += question.marks;
                    }

                    displayQuestions();
                    startTimer();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Unable to load questions.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void displayQuestions() {

        questionContainer.removeAllViews();

        for (int i = 0; i < questions.size(); i++) {

            final int index = i;
            ExamQuestion question = questions.get(i);

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(20, 20, 20, 20);
            card.setBackgroundColor(Color.WHITE);

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            -1,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            cardParams.setMargins(0, 0, 0, 18);

            card.setLayoutParams(cardParams);

            TextView numberText = new TextView(this);

            numberText.setText(
                    "Question " + (index + 1)
                            + "   [" + question.marks + " mark]"
            );

            numberText.setTextSize(14);
            numberText.setTextColor(Color.rgb(79, 70, 229));
            numberText.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

            card.addView(numberText);

            TextView questionText = new TextView(this);

            questionText.setText(question.question);
            questionText.setTextSize(18);
            questionText.setTextColor(Color.rgb(17, 24, 39));
            questionText.setPadding(0, 12, 0, 16);

            card.addView(questionText);

            String type = question.type.toUpperCase(Locale.ROOT);

            if (type.equals("TRUE_FALSE")) {

                RadioGroup group = new RadioGroup(this);

                RadioButton trueButton =
                        createRadioButton("True");

                RadioButton falseButton =
                        createRadioButton("False");

                group.addView(trueButton);
                group.addView(falseButton);

                group.setOnCheckedChangeListener(
                        (radioGroup, checkedId) -> {

                            RadioButton selected =
                                    radioGroup.findViewById(checkedId);

                            if (selected != null) {
                                selectedAnswers.put(
                                        index,
                                        selected.getText().toString()
                                );
                            }
                        }
                );

                card.addView(group);

            } else if (type.equals("MCQ")
                    || type.equals("MULTIPLE_CHOICE")
                    || type.equals("SINGLE_SELECT")) {

                RadioGroup group = new RadioGroup(this);

                addOption(group, index, question.optionA);
                addOption(group, index, question.optionB);
                addOption(group, index, question.optionC);
                addOption(group, index, question.optionD);

                card.addView(group);

            } else {

                android.widget.EditText answerInput =
                        new android.widget.EditText(this);

                answerInput.setHint("Enter your answer");
                answerInput.setTextSize(16);
                answerInput.setSingleLine(false);
                answerInput.setPadding(16, 12, 16, 12);

                answerInput.setOnFocusChangeListener(
                        (v, hasFocus) -> {

                            if (!hasFocus) {
                                selectedAnswers.put(
                                        index,
                                        answerInput.getText()
                                                .toString()
                                                .trim()
                                );
                            }
                        }
                );

                answerInput.addTextChangedListener(
                        new android.text.TextWatcher() {

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
                                selectedAnswers.put(
                                        index,
                                        s.toString().trim()
                                );
                            }

                            @Override
                            public void afterTextChanged(
                                    android.text.Editable s
                            ) {
                            }
                        }
                );

                card.addView(answerInput);
            }

            questionContainer.addView(card);
        }
    }

    private RadioButton createRadioButton(String text) {

        RadioButton button = new RadioButton(this);

        button.setText(text);
        button.setTextSize(16);
        button.setTextColor(Color.rgb(31, 41, 55));
        button.setPadding(0, 8, 0, 8);

        return button;
    }

    private void addOption(
            RadioGroup group,
            int questionIndex,
            String option
    ) {

        if (option == null || option.trim().isEmpty()) {
            return;
        }

        RadioButton button = createRadioButton(option);

        button.setOnClickListener(v -> {

            RadioButton selected =
                    (RadioButton) v;

            selectedAnswers.put(
                    questionIndex,
                    selected.getText().toString().trim()
            );
        });

        group.addView(button);
    }

    private void startTimer() {

        long duration =
                durationMinutes * 60L * 1000L;

        countDownTimer = new CountDownTimer(
                duration,
                1000
        ) {

            @Override
            public void onTick(long millisUntilFinished) {

                long totalSeconds =
                        millisUntilFinished / 1000;

                long minutes =
                        totalSeconds / 60;

                long seconds =
                        totalSeconds % 60;

                timerText.setText(
                        String.format(
                                Locale.getDefault(),
                                "Time: %02d:%02d",
                                minutes,
                                seconds
                        )
                );
            }

            @Override
            public void onFinish() {

                timerText.setText("Time Over");
                submitExam();
            }

        }.start();
    }

    private void submitExam() {

        if (submitted) {
            return;
        }

        if (questions.isEmpty()) {
            Toast.makeText(
                    this,
                    "No questions to submit.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        submitted = true;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        submitButton.setEnabled(false);

        currentScore = 0;

        for (int i = 0; i < questions.size(); i++) {

            ExamQuestion question = questions.get(i);

            String selected =
                    selectedAnswers.get(i);

            if (selected == null) {
                continue;
            }

            if (isAnswerCorrect(question, selected)) {
                currentScore += question.marks;
            }
        }

        saveResult();
    }

    private boolean isAnswerCorrect(
            ExamQuestion question,
            String selected
    ) {

        if (selected == null ||
                question.answer == null) {
            return false;
        }

        String userAnswer =
                selected.trim();

        String correctAnswer =
                question.answer.trim();

        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            return true;
        }

        String type =
                question.type.toUpperCase(Locale.ROOT);

        if (type.equals("TRUE_FALSE")) {

            String normalizedUser =
                    userAnswer.equalsIgnoreCase("true")
                            ? "true"
                            : userAnswer.equalsIgnoreCase("false")
                            ? "false"
                            : userAnswer;

            String normalizedCorrect =
                    correctAnswer.equalsIgnoreCase("true")
                            ? "true"
                            : correctAnswer.equalsIgnoreCase("false")
                            ? "false"
                            : correctAnswer;

            return normalizedUser.equalsIgnoreCase(
                    normalizedCorrect
            );
        }

        try {

            double userNumber =
                    Double.parseDouble(userAnswer);

            double correctNumber =
                    Double.parseDouble(correctAnswer);

            return Math.abs(
                    userNumber - correctNumber
            ) < 0.0001;

        } catch (Exception ignored) {
        }

        return false;
    }

    private void saveResult() {

        String userId =
                auth.getCurrentUser() == null
                        ? null
                        : auth.getCurrentUser().getUid();

        if (userId == null) {

            Toast.makeText(
                    this,
                    "Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        double accuracy = 0;

        if (!questions.isEmpty()) {
            accuracy =
                    (currentScore * 100.0)
                            / totalMarks;
        }

        Map<String, Object> result =
                new HashMap<>();

        result.put("userId", userId);
        result.put("studentName", studentName);
        result.put("examId", examId);

        result.put(
                "classId",
                studentClassId == null
                        ? ""
                        : studentClassId
        );

        result.put(
                "className",
                studentClassName == null
                        ? ""
                        : studentClassName
        );

        result.put(
                "medium",
                studentMedium == null
                        ? "English"
                        : studentMedium
        );

        result.put(
                "subjectId",
                studentSubjectId == null
                        ? ""
                        : studentSubjectId
        );

        result.put(
                "subjectName",
                studentSubjectName == null
                        ? ""
                        : studentSubjectName
        );

        result.put(
                "chapterId",
                studentChapterId == null
                        ? ""
                        : studentChapterId
        );

        result.put(
                "chapterName",
                studentChapterName == null
                        ? ""
                        : studentChapterName
        );

        result.put("score", currentScore);
        result.put("totalMarks", totalMarks);
        result.put("totalQuestions", questions.size());
        result.put("accuracy", accuracy);
        result.put("completedAt",
                System.currentTimeMillis());

        db.collection("examResults")
                .add(result)
                .addOnSuccessListener(documentReference -> {

                    int earnedPoints =
                            currentScore * 5;

                    addStudentPoints(
                            userId,
                            earnedPoints
                    );

                    Toast.makeText(
                            this,
                            "Exam submitted successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    openResultScreen(
                            currentScore,
                            totalMarks,
                            questions.size(),
                            accuracy
                    );
                })
                .addOnFailureListener(e -> {

                    submitted = false;
                    submitButton.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Result save failed.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void addStudentPoints(
            String userId,
            int points
    ) {

        if (points <= 0) {
            return;
        }

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {

                    long oldPoints = 0;

                    if (document.exists()) {

                        Long value =
                                document.getLong("points");

                        if (value != null) {
                            oldPoints = value;
                        }
                    }

                    Map<String, Object> update =
                            new HashMap<>();

                    update.put(
                            "points",
                            oldPoints + points
                    );

                    db.collection("users")
                            .document(userId)
                            .set(
                                    update,
                                    com.google.firebase.firestore
                                            .SetOptions.merge()
                            );
                });
    }

    private void openResultScreen(
            int score,
            int total,
            int questionsCount,
            double accuracy
    ) {

        Intent intent =
                new Intent(
                        this,
                        ExamResultActivity.class
                );

        intent.putExtra(
                "examId",
                examId
        );

        intent.putExtra(
                "score",
                score
        );

        intent.putExtra(
                "totalMarks",
                total
        );

        intent.putExtra(
                "totalQuestions",
                questionsCount
        );

        intent.putExtra(
                "accuracy",
                accuracy
        );

        intent.putExtra(
                "studentName",
                studentName
        );

        intent.putExtra(
                "className",
                studentClassName
        );

        intent.putExtra(
                "studentMedium",
                studentMedium
        );

        startActivity(intent);
        finish();
    }

    private String safe(String value) {

        if (value == null ||
                value.trim().isEmpty()) {
            return "";
        }

        return value;
    }

    @Override
    protected void onDestroy() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        super.onDestroy();
    }

    private static class ExamQuestion {

        String id = "";
        String question = "";
        String type = "";
        String optionA = "";
        String optionB = "";
        String optionC = "";
        String optionD = "";
        String answer = "";
        String explanation = "";
        int marks = 1;
    }
}
