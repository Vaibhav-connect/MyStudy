package com.mystudy.app;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StudentExamAttemptActivity extends AppCompatActivity {

    private static final int BLUE = Color.rgb(37, 99, 235);
    private static final int DARK = Color.rgb(17, 24, 39);
    private static final int MUTED = Color.rgb(100, 116, 139);
    private static final int BG = Color.rgb(248, 250, 252);
    private static final int GREEN = Color.rgb(22, 163, 74);
    private static final int RED = Color.rgb(220, 38, 38);
    private static final int YELLOW = Color.rgb(250, 204, 21);

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private LinearLayout root;
    private LinearLayout container;
    private TextView timerText;
    private TextView progressText;

    private RadioGroup currentRadioGroup;
    private EditText currentEditText;

    private final List<QuestionData> questions = new ArrayList<>();

    private int currentIndex = 0;
    private int score = 0;
    private int answered = 0;

    private String quizId = "";
    private String quizTitle = "Quiz";
    private String quizType = "";

    private String classId = "";
    private String className = "";
    private String studentMedium = "English";
    private String studentName = "";

    private String subjectId = "";
    private String subjectName = "";
    private String chapterId = "";
    private String chapterName = "";

    private long durationMinutes = 10;
    private long totalMarks = 0;

    private CountDownTimer countDownTimer;

    private boolean submitted = false;
    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        readIntentData();
        createBaseUI();
        showLoading();
        loadQuestions();
    }

    private void readIntentData() {

        quizId = getIntent().getStringExtra("quizId");
        quizTitle = getIntent().getStringExtra("quizTitle");
        quizType = getIntent().getStringExtra("quizType");

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");

        studentMedium = getIntent().getStringExtra("studentMedium");
        studentName = getIntent().getStringExtra("studentName");

        subjectId = getIntent().getStringExtra("subjectId");
        subjectName = getIntent().getStringExtra("subjectName");

        chapterId = getIntent().getStringExtra("chapterId");
        chapterName = getIntent().getStringExtra("chapterName");

        durationMinutes = getIntent().getLongExtra(
                "durationMinutes", 10
        );

        totalMarks = getIntent().getLongExtra(
                "totalMarks", 0
        );

        if (quizTitle == null || quizTitle.trim().isEmpty()) {
            quizTitle = "Quiz";
        }

        if (studentMedium == null || studentMedium.trim().isEmpty()) {
            studentMedium = "English";
        }

        if (durationMinutes <= 0) {
            durationMinutes = 10;
        }
    }

    private void createBaseUI() {

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(22), dp(20), dp(22), dp(18));
        header.setBackgroundColor(Color.WHITE);

        TextView brand = new TextView(this);
        brand.setText("📘 MyStudy");
        brand.setTextSize(18);
        brand.setTypeface(null, Typeface.BOLD);
        brand.setTextColor(BLUE);

        TextView title = new TextView(this);
        title.setText(quizTitle);
        title.setTextSize(23);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(DARK);
        title.setPadding(0, dp(12), 0, dp(8));

        timerText = new TextView(this);
        timerText.setText("Time: --:--");
        timerText.setTextSize(16);
        timerText.setTypeface(null, Typeface.BOLD);
        timerText.setTextColor(RED);

        progressText = new TextView(this);
        progressText.setText("Preparing your exam...");
        progressText.setTextSize(14);
        progressText.setTextColor(MUTED);
        progressText.setPadding(0, dp(7), 0, 0);

        header.addView(brand);
        header.addView(title);
        header.addView(timerText);
        header.addView(progressText);

        root.addView(header);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dp(20), dp(18), dp(20), dp(30));

        scrollView.addView(container);

        root.addView(
                scrollView,
                new LinearLayout.LayoutParams(
                        -1, 0, 1
                )
        );

        setContentView(root);
    }

    private void loadQuestions() {

        ArrayList<String> questionIds =
                getIntent().getStringArrayListExtra("questionIds");

        if (questionIds == null || questionIds.isEmpty()) {
            showError("No questions available for this exam.");
            return;
        }

        loading = true;
        loadQuestionByIndex(questionIds, 0);
    }

    private void loadQuestionByIndex(
            ArrayList<String> ids,
            int index
    ) {

        if (isFinishing() || isDestroyed()) {
            return;
        }

        if (index >= ids.size()) {
            finishLoadingQuestions();
            return;
        }

        String id = ids.get(index);

        db.collection("questions")
                .document(id)
                .get()
                .addOnSuccessListener(document -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    if (document.exists()) {

                        QuestionData question = new QuestionData();

                        question.id = document.getId();

                        question.question = value(
                                document.getString("question"), ""
                        );

                        question.type = value(
                                document.getString("type"), "MCQ"
                        );

                        question.optionA = value(
                                document.getString("optionA"), ""
                        );

                        question.optionB = value(
                                document.getString("optionB"), ""
                        );

                        question.optionC = value(
                                document.getString("optionC"), ""
                        );

                        question.optionD = value(
                                document.getString("optionD"), ""
                        );

                        question.answer = value(
                                document.getString("answer"), ""
                        );

                        question.explanation = value(
                                document.getString("explanation"), ""
                        );

                        Long marks = document.getLong("marks");

                        question.marks = marks != null && marks > 0
                                ? marks.intValue()
                                : 1;

                        questions.add(question);
                    }

                    loadQuestionByIndex(ids, index + 1);
                })
                .addOnFailureListener(e -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    loadQuestionByIndex(ids, index + 1);
                });
    }

    private void finishLoadingQuestions() {

        loading = false;

        if (questions.isEmpty()) {
            showError("No valid questions found. Please try again.");
            return;
        }

        if (totalMarks <= 0) {
            totalMarks = 0;

            for (QuestionData q : questions) {
                totalMarks += q.marks;
            }
        }

        showCurrentQuestion();
        startTimer();
    }

    private void showLoading() {

        container.removeAllViews();

        TextView text = createText(
                "Preparing your exam...",
                18,
                DARK,
                true
        );

        text.setGravity(Gravity.CENTER);
        text.setPadding(0, dp(80), 0, dp(20));

        container.addView(text);
    }

    private void showError(String message) {

        container.removeAllViews();

        TextView error = createText(
                "Unable to start exam",
                22,
                RED,
                true
        );

        error.setGravity(Gravity.CENTER);
        error.setPadding(0, dp(50), 0, dp(15));

        TextView details = createText(
                message,
                15,
                MUTED,
                false
        );

        details.setGravity(Gravity.CENTER);

        Button back = createButton("Back to Exams");

        back.setOnClickListener(v -> finish());

        container.addView(error);
        container.addView(details);
        container.addView(back);
    }

    private void showCurrentQuestion() {

        if (submitted || isFinishing() || isDestroyed()) {
            return;
        }

        if (currentIndex >= questions.size()) {
            submitExam();
            return;
        }

        container.removeAllViews();

        currentRadioGroup = null;
        currentEditText = null;

        QuestionData q = questions.get(currentIndex);

        progressText.setText(
                "Question " + (currentIndex + 1) +
                        " of " + questions.size()
        );

        LinearLayout card = createCard();

        TextView questionNumber = createText(
                "QUESTION " + (currentIndex + 1),
                13,
                BLUE,
                true
        );

        TextView questionText = createText(
                q.question,
                20,
                DARK,
                true
        );

        questionText.setPadding(0, dp(12), 0, dp(18));

        TextView marksText = createText(
                "Marks: " + q.marks,
                13,
                MUTED,
                false
        );

        card.addView(questionNumber);
        card.addView(questionText);
        card.addView(marksText);

        container.addView(card);

        String type = q.type.toUpperCase(Locale.US);

        if (type.equals("TRUE_FALSE")) {

            addTrueFalse(card);

        } else if (
                type.equals("FILL_BLANK") ||
                type.equals("NUMERICAL") ||
                type.equals("SHORT_ANSWER") ||
                type.equals("SPELLING") ||
                type.equals("REARRANGE") ||
                type.equals("WORD_PROBLEM") ||
                type.equals("MATCH")
        ) {

            addTextAnswer(card);

        } else {

            addMCQ(card, q);
        }

        Button nextButton = createButton(
                currentIndex == questions.size() - 1
                        ? "Submit Exam"
                        : "Next Question"
        );

        nextButton.setOnClickListener(v -> processAnswer(q));

        container.addView(nextButton);

        TextView note = createText(
                "Please check your answer before moving ahead.",
                13,
                MUTED,
                false
        );

        note.setGravity(Gravity.CENTER);
        note.setPadding(0, dp(15), 0, 0);

        container.addView(note);
    }

    private void addMCQ(
            LinearLayout card,
            QuestionData q
    ) {

        currentRadioGroup = new RadioGroup(this);
        currentRadioGroup.setOrientation(LinearLayout.VERTICAL);

        addRadioOption(card, q.optionA, "A");
        addRadioOption(card, q.optionB, "B");
        addRadioOption(card, q.optionC, "C");
        addRadioOption(card, q.optionD, "D");
    }

    private void addTrueFalse(LinearLayout card) {

        currentRadioGroup = new RadioGroup(this);
        currentRadioGroup.setOrientation(LinearLayout.VERTICAL);

        addRadioOption(card, "True", "True");
        addRadioOption(card, "False", "False");
    }

    private void addRadioOption(
            LinearLayout card,
            String text,
            String answerValue
    ) {

        if (text == null || text.trim().isEmpty()) {
            return;
        }

        RadioButton radio = new RadioButton(this);

        radio.setText(text);
        radio.setTag(answerValue);
        radio.setTextSize(16);
        radio.setTextColor(DARK);
        radio.setPadding(dp(8), dp(10), dp(8), dp(10));

        if (currentRadioGroup != null) {
            currentRadioGroup.addView(radio);
        }

        if (currentRadioGroup != null &&
                currentRadioGroup.getParent() == null) {
            card.addView(currentRadioGroup);
        }
    }

    private void addTextAnswer(LinearLayout card) {

        currentEditText = new EditText(this);

        currentEditText.setHint("Enter your answer");
        currentEditText.setTextSize(16);
        currentEditText.setTextColor(DARK);
        currentEditText.setHintTextColor(MUTED);
        currentEditText.setMinLines(2);
        currentEditText.setPadding(
                dp(14), dp(12), dp(14), dp(12)
        );

        currentEditText.setGravity(Gravity.TOP);

        currentEditText.setBackground(
                rounded(Color.WHITE, 14, BLUE)
        );

        card.addView(
                currentEditText,
                new LinearLayout.LayoutParams(
                        -1, -2
                )
        );
    }

    private void processAnswer(QuestionData q) {

        if (submitted || loading) {
            return;
        }

        String userAnswer = "";

        if (currentRadioGroup != null) {

            int checkedId =
                    currentRadioGroup.getCheckedRadioButtonId();

            if (checkedId != -1) {

                RadioButton selected =
                        currentRadioGroup.findViewById(checkedId);

                if (selected != null) {
                    Object tag = selected.getTag();

                    userAnswer = tag != null
                            ? tag.toString()
                            : selected.getText().toString();
                }
            }

        } else if (currentEditText != null) {

            userAnswer = currentEditText.getText()
                    .toString().trim();
        }

        if (userAnswer.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Please answer the question.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        hideKeyboard();

        answered++;

        boolean correct = isCorrect(
                userAnswer,
                q.answer,
                q.type,
                q
        );

        if (correct) {
            score += q.marks;
        }

        showAnswerMessage(correct, q);

        currentRadioGroup = null;
        currentEditText = null;

        currentIndex++;

        container.postDelayed(() -> {

            if (!submitted && !isFinishing()) {
                showCurrentQuestion();
            }

        }, 900);
    }

    private boolean isCorrect(
            String userAnswer,
            String correctAnswer,
            String type,
            QuestionData q
    ) {

        if (correctAnswer == null) {
            return false;
        }

        String user = normalize(userAnswer);
        String correct = normalize(correctAnswer);

        if (type != null &&
                type.equalsIgnoreCase("NUMERICAL")) {

            try {

                double u = Double.parseDouble(user);
                double c = Double.parseDouble(correct);

                return Math.abs(u - c) < 0.0001;

            } catch (Exception ignored) {
                return user.equals(correct);
            }
        }

        if (type != null &&
                (type.equalsIgnoreCase("MCQ") ||
                 type.equalsIgnoreCase("PICTURE_BASED"))) {

            String option = getOptionForAnswer(q, correct);

            if (!option.isEmpty()) {
                return user.equals(correct) ||
                        user.equals(normalize(option));
            }
        }

        return user.equals(correct);
    }

    private String getOptionForAnswer(
            QuestionData q,
            String answer
    ) {

        if (answer.equals("a")) return q.optionA;
        if (answer.equals("b")) return q.optionB;
        if (answer.equals("c")) return q.optionC;
        if (answer.equals("d")) return q.optionD;

        return "";
    }

    private String normalize(String value) {

        if (value == null) {
            return "";
        }

        return value.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.US);
    }

    private void showAnswerMessage(
            boolean correct,
            QuestionData q
    ) {

        container.removeAllViews();

        LinearLayout card = createCard();

        TextView result = createText(
                correct ? "✓ Correct Answer!" : "✗ Incorrect Answer",
                22,
                correct ? GREEN : RED,
                true
        );

        result.setGravity(Gravity.CENTER);
        result.setPadding(0, dp(25), 0, dp(15));

        card.addView(result);

        if (correct) {

            TextView marks = createText(
                    "+" + q.marks + " marks earned",
                    16,
                    GREEN,
                    true
            );

            marks.setGravity(Gravity.CENTER);
            card.addView(marks);

        } else {

            TextView correctAnswer = createText(
                    "Correct Answer: " + q.answer,
                    16,
                    DARK,
                    true
            );

            correctAnswer.setPadding(0, dp(10), 0, dp(10));
            card.addView(correctAnswer);
        }

        if (q.explanation != null &&
                !q.explanation.trim().isEmpty()) {

            TextView explanation = createText(
                    "Explanation\n" + q.explanation,
                    15,
                    MUTED,
                    false
            );

            explanation.setPadding(0, dp(15), 0, dp(20));
            card.addView(explanation);
        }

        container.addView(card);
    }

    private void startTimer() {

        if (submitted) {
            return;
        }

        long millis = durationMinutes * 60L * 1000L;

        countDownTimer = new CountDownTimer(millis, 1000) {

            @Override
            public void onTick(long remaining) {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                long totalSeconds = remaining / 1000;
                long minutes = totalSeconds / 60;
                long seconds = totalSeconds % 60;

                timerText.setText(
                        String.format(
                                Locale.US,
                                "⏱ Time: %02d:%02d",
                                minutes,
                                seconds
                        )
                );

                if (remaining <= 60000) {
                    timerText.setTextColor(RED);
                }
            }

            @Override
            public void onFinish() {

                timerText.setText("Time's up!");

                if (!submitted) {
                    Toast.makeText(
                            StudentExamAttemptActivity.this,
                            "Time is over. Submitting exam.",
                            Toast.LENGTH_LONG
                    ).show();

                    submitExam();
                }
            }

        }.start();
    }

    private void submitExam() {

        if (submitted) {
            return;
        }

        submitted = true;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        currentRadioGroup = null;
        currentEditText = null;

        saveResult();
        showResult();
    }

    private void saveResult() {

        if (auth.getCurrentUser() == null) {
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> result = new HashMap<>();

        result.put("userId", userId);
        result.put("quizId", quizId);
        result.put("quizTitle", quizTitle);
        result.put("quizType", quizType);

        result.put("classId", classId);
        result.put("className", className);
        result.put("medium", studentMedium);
        result.put("studentName", studentName);

        result.put("subjectId", subjectId);
        result.put("subjectName", subjectName);

        result.put("chapterId", chapterId);
        result.put("chapterName", chapterName);

        result.put("score", score);
        result.put("answered", answered);
        result.put("totalQuestions", questions.size());
        result.put("totalMarks", totalMarks);

        double accuracy = totalMarks > 0
                ? (score * 100.0) / totalMarks
                : 0;

        result.put("accuracy", accuracy);
        result.put("completedAt", System.currentTimeMillis());

        db.collection("examResults")
                .add(result)
                .addOnFailureListener(e -> {

                    if (!isFinishing()) {
                        Toast.makeText(
                                this,
                                "Could not save exam result.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

        long points = Math.max(0, score * 5L);

        if (points > 0) {

            Map<String, Object> update = new HashMap<>();

            update.put("points", FieldValue.increment(points));

            db.collection("users")
                    .document(userId)
                    .set(update, SetOptions.merge());
        }
    }

    private void showResult() {

        container.removeAllViews();

        LinearLayout card = createCard();

        TextView icon = createText(
                "🏆",
                48,
                YELLOW,
                true
        );

        icon.setGravity(Gravity.CENTER);
        icon.setPadding(0, dp(25), 0, dp(10));

        TextView title = createText(
                "Exam Completed!",
                26,
                GREEN,
                true
        );

        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(20));

        TextView scoreText = createText(
                "Score: " + score + " / " + totalMarks,
                23,
                DARK,
                true
        );

        scoreText.setGravity(Gravity.CENTER);

        TextView answeredText = createText(
                "Answered: " + answered + " / " + questions.size(),
                16,
                MUTED,
                false
        );

        answeredText.setGravity(Gravity.CENTER);
        answeredText.setPadding(0, dp(15), 0, dp(15));

        double accuracy = totalMarks > 0
                ? (score * 100.0) / totalMarks
                : 0;

        TextView accuracyText = createText(
                String.format(
                        Locale.US,
                        "Accuracy: %.1f%%",
                        accuracy
                ),
                19,
                BLUE,
                true
        );

        accuracyText.setGravity(Gravity.CENTER);
        accuracyText.setPadding(0, dp(10), 0, dp(25));

        card.addView(icon);
        card.addView(title);
        card.addView(scoreText);
        card.addView(answeredText);
        card.addView(accuracyText);

        container.addView(card);

        Button done = createButton("Back to Exams");

        done.setOnClickListener(v -> finish());

        container.addView(done);
    }

    private LinearLayout createCard() {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));

        card.setBackground(rounded(Color.WHITE, 20, Color.rgb(226, 232, 240)));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.bottomMargin = dp(18);

        card.setLayoutParams(params);

        return card;
    }

    private TextView createText(
            String text,
            int size,
            int color,
            boolean bold
    ) {

        TextView view = new TextView(this);

        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }

        return view;
    }

    private Button createButton(String text) {

        Button button = new Button(this);

        button.setText(text);
        button.setTextSize(16);
        button.setAllCaps(false);
        button.setTextColor(Color.WHITE);
        button.setTypeface(null, Typeface.BOLD);

        button.setBackground(rounded(BLUE, 14, BLUE));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, dp(54));

        params.topMargin = dp(12);
        params.bottomMargin = dp(10);

        button.setLayoutParams(params);

        return button;
    }

    private GradientDrawable rounded(
            int background,
            int radius,
            int stroke
    ) {

        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(background);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), stroke);

        return drawable;
    }

    private int dp(int value) {

        return (int) (
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    private String value(
            String value,
            String fallback
    ) {

        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value;
    }

    private void hideKeyboard() {

        try {

            InputMethodManager manager =
                    (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE
                    );

            if (manager != null && getCurrentFocus() != null) {

                manager.hideSoftInputFromWindow(
                        getCurrentFocus().getWindowToken(),
                        0
                );
            }

        } catch (Exception ignored) {
        }
    }

    @Override
    public void onBackPressed() {

        if (submitted) {
            super.onBackPressed();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Submit Exam?")
                .setMessage(
                        "Are you sure you want to leave? " +
                        "Your exam will be submitted with your current score."
                )
                .setNegativeButton("Continue Exam", (dialog, which) ->
                        dialog.dismiss()
                )
                .setPositiveButton("Submit Exam", (dialog, which) ->
                        submitExam()
                )
                .show();
    }

    @Override
    protected void onDestroy() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        super.onDestroy();
    }

    private static class QuestionData {

        String id = "";
        String question = "";
        String type = "MCQ";

        String optionA = "";
        String optionB = "";
        String optionC = "";
        String optionD = "";

        String answer = "";
        String explanation = "";

        int marks = 1;
    }
}
