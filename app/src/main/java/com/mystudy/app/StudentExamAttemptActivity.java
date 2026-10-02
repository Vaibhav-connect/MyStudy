package com.mystudy.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StudentExamAttemptActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private LinearLayout container;
    private TextView progressText;
    private TextView timerText;

    private final List<QuestionData> questions = new ArrayList<>();

    private int currentIndex = 0;
    private int score = 0;
    private int answered = 0;

    private String quizId = "";
    private String quizTitle = "";
    private String quizType = "";

    private String classId = "";
    private String className = "";
    private String studentMedium = "";
    private String studentName = "";

    private String subjectId = "";
    private String subjectName = "";
    private String chapterId = "";
    private String chapterName = "";

    private long durationMinutes = 10;
    private long totalMarks = 0;

    private CountDownTimer countDownTimer;
    private boolean submitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        readIntentData();
        createUI();
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

        durationMinutes =
                getIntent().getLongExtra(
                        "durationMinutes",
                        10
                );

        totalMarks =
                getIntent().getLongExtra(
                        "totalMarks",
                        0
                );

        if (studentMedium == null ||
                studentMedium.trim().isEmpty()) {

            studentMedium = "English";
        }

        if (quizTitle == null) {
            quizTitle = "Quiz";
        }
    }

    private void createUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(
                Color.rgb(248, 250, 252)
        );

        LinearLayout header =
                new LinearLayout(this);

        header.setOrientation(
                LinearLayout.VERTICAL
        );

        header.setPadding(
                24,
                24,
                24,
                18
        );

        TextView title =
                new TextView(this);

        title.setText(quizTitle);
        title.setTextSize(24);
        title.setTextColor(
                Color.rgb(17, 24, 39)
        );

        timerText =
                new TextView(this);

        timerText.setTextSize(17);
        timerText.setTextColor(
                Color.rgb(220, 38, 38)
        );

        timerText.setPadding(
                0,
                8,
                0,
                0
        );

        progressText =
                new TextView(this);

        progressText.setTextSize(14);
        progressText.setTextColor(
                Color.rgb(100, 116, 139)
        );

        progressText.setPadding(
                0,
                6,
                0,
                0
        );

        header.addView(title);
        header.addView(timerText);
        header.addView(progressText);

        root.addView(header);

        ScrollView scrollView =
                new ScrollView(this);

        container =
                new LinearLayout(this);

        container.setOrientation(
                LinearLayout.VERTICAL
        );

        container.setPadding(
                24,
                10,
                24,
                30
        );

        scrollView.addView(container);

        root.addView(
                scrollView,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        setContentView(root);
    }

    private void loadQuestions() {

        ArrayList<String> questionIds =
                getIntent().getStringArrayListExtra(
                        "questionIds"
                );

        if (questionIds == null ||
                questionIds.isEmpty()) {

            Toast.makeText(
                    this,
                    "No questions available.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        loadQuestionByIndex(
                questionIds,
                0
        );

        startTimer();
    }

    private void loadQuestionByIndex(
            ArrayList<String> ids,
            int index
    ) {

        if (index >= ids.size()) {

            showQuestionScreen();
            return;
        }

        String id = ids.get(index);

        db.collection("questions")
                .document(id)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (document.exists()) {

                                QuestionData question =
                                        new QuestionData();

                                question.id =
                                        document.getId();

                                question.question =
                                        value(
                                                document.getString(
                                                        "question"
                                                ),
                                                ""
                                        );

                                question.type =
                                        value(
                                                document.getString(
                                                        "type"
                                                ),
                                                "MCQ"
                                        );

                                question.optionA =
                                        value(
                                                document.getString(
                                                        "optionA"
                                                ),
                                                ""
                                        );

                                question.optionB =
                                        value(
                                                document.getString(
                                                        "optionB"
                                                ),
                                                ""
                                        );

                                question.optionC =
                                        value(
                                                document.getString(
                                                        "optionC"
                                                ),
                                                ""
                                        );

                                question.optionD =
                                        value(
                                                document.getString(
                                                        "optionD"
                                                ),
                                                ""
                                        );

                                question.answer =
                                        value(
                                                document.getString(
                                                        "answer"
                                                ),
                                                ""
                                        );

                                question.explanation =
                                        value(
                                                document.getString(
                                                        "explanation"
                                                ),
                                                ""
                                        );

                                Long marks =
                                        document.getLong(
                                                "marks"
                                        );

                                question.marks =
                                        marks != null
                                                ? marks.intValue()
                                                : 1;

                                questions.add(
                                        question
                                );
                            }

                            loadQuestionByIndex(
                                    ids,
                                    index + 1
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    this,
                                    "Failed to load question.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadQuestionByIndex(
                                    ids,
                                    index + 1
                            );
                        }
                );
    }

    private void showQuestionScreen() {

        if (questions.isEmpty()) {

            Toast.makeText(
                    this,
                    "No valid questions found.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        showCurrentQuestion();
    }

    private void showCurrentQuestion() {

        container.removeAllViews();

        if (currentIndex >= questions.size()) {
            submitExam();
            return;
        }

        QuestionData q =
                questions.get(currentIndex);

        progressText.setText(
                "Question " +
                        (currentIndex + 1) +
                        " of " +
                        questions.size()
        );

        TextView questionText =
                new TextView(this);

        questionText.setText(
                (currentIndex + 1) +
                        ". " +
                        q.question
        );

        questionText.setTextSize(20);
        questionText.setTextColor(
                Color.rgb(17, 24, 39)
        );

        questionText.setPadding(
                0,
                20,
                0,
                22
        );

        container.addView(
                questionText
        );

        String type =
                q.type.toUpperCase(
                        Locale.US
                );

        if (type.equals("TRUE_FALSE")) {

            addTrueFalse(q);

        } else if (
                type.equals("FILL_BLANK") ||
                type.equals("NUMERICAL") ||
                type.equals("SHORT_ANSWER") ||
                type.equals("SPELLING") ||
                type.equals("REARRANGE") ||
                type.equals("WORD_PROBLEM")
        ) {

            addTextAnswer(q);

        } else {

            addMCQ(q);
        }

        Button nextButton =
                new Button(this);

        if (currentIndex ==
                questions.size() - 1) {

            nextButton.setText(
                    "Submit Exam"
            );

        } else {

            nextButton.setText(
                    "Next Question"
            );
        }

        nextButton.setAllCaps(false);

        nextButton.setOnClickListener(
                v -> processAnswer(q)
        );

        container.addView(
                nextButton
        );
    }

    private RadioGroup currentRadioGroup;
    private EditText currentEditText;

    private void addMCQ(
            QuestionData q
    ) {

        currentRadioGroup =
                new RadioGroup(this);

        addRadioOption(
                q.optionA,
                "A"
        );

        addRadioOption(
                q.optionB,
                "B"
        );

        addRadioOption(
                q.optionC,
                "C"
        );

        addRadioOption(
                q.optionD,
                "D"
        );

        container.addView(
                currentRadioGroup
        );
    }

    private void addTrueFalse(
            QuestionData q
    ) {

        currentRadioGroup =
                new RadioGroup(this);

        addRadioOption(
                "True",
                "True"
        );

        addRadioOption(
                "False",
                "False"
        );

        container.addView(
                currentRadioGroup
        );
    }

    private void addRadioOption(
            String text,
            String value
    ) {

        if (text == null ||
                text.trim().isEmpty()) {
            return;
        }

        RadioButton radio =
                new RadioButton(this);

        radio.setText(text);
        radio.setTag(value);
        radio.setTextSize(17);
        radio.setPadding(
                0,
                10,
                0,
                10
        );

        currentRadioGroup.addView(
                radio
        );
    }

    private void addTextAnswer(
            QuestionData q
    ) {

        currentEditText =
                new EditText(this);

        currentEditText.setHint(
                "Enter your answer"
        );

        currentEditText.setTextSize(17);

        currentEditText.setMinLines(2);

        currentEditText.setGravity(
                Gravity.TOP
        );

        container.addView(
                currentEditText,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
    }

    private void processAnswer(
            QuestionData q
    ) {

        if (submitted) {
            return;
        }

        String userAnswer = "";

        if (currentRadioGroup != null) {

            int checkedId =
                    currentRadioGroup
                            .getCheckedRadioButtonId();

            if (checkedId != -1) {

                RadioButton selected =
                        currentRadioGroup
                                .findViewById(
                                        checkedId
                                );

                if (selected != null) {

                    Object tag =
                            selected.getTag();

                    if (tag != null) {
                        userAnswer =
                                tag.toString();
                    } else {
                        userAnswer =
                                selected
                                        .getText()
                                        .toString();
                    }
                }
            }

        } else if (currentEditText != null) {

            userAnswer =
                    currentEditText
                            .getText()
                            .toString()
                            .trim();
        }

        if (userAnswer.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Please answer the question.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        answered++;

        if (isCorrect(
                userAnswer,
                q.answer,
                q.type
        )) {

            score += q.marks;

            showAnswerMessage(
                    true,
                    q.explanation
            );

        } else {

            showAnswerMessage(
                    false,
                    q.explanation
            );
        }

        currentRadioGroup = null;
        currentEditText = null;

        currentIndex++;

        container.postDelayed(
                this::showCurrentQuestion,
                900
        );
    }

    private boolean isCorrect(
            String userAnswer,
            String correctAnswer,
            String type
    ) {

        if (correctAnswer == null) {
            return false;
        }

        String user =
                userAnswer
                        .trim()
                        .toLowerCase(
                                Locale.US
                        );

        String correct =
                correctAnswer
                        .trim()
                        .toLowerCase(
                                Locale.US
                        );

        if (type != null &&
                type.equalsIgnoreCase(
                        "NUMERICAL"
                )) {

            try {

                double u =
                        Double.parseDouble(
                                user
                        );

                double c =
                        Double.parseDouble(
                                correct
                        );

                return Math.abs(
                        u - c
                ) < 0.0001;

            } catch (Exception ignored) {
                return user.equals(correct);
            }
        }

        return user.equals(correct) ||
                normalize(user).equals(
                        normalize(correct)
                );
    }

    private String normalize(
            String value
    ) {

        return value
                .replaceAll(
                        "\\s+",
                        " "
                )
                .trim()
                .toLowerCase(
                        Locale.US
                );
    }

    private void showAnswerMessage(
            boolean correct,
            String explanation
    ) {

        TextView result =
                new TextView(this);

        if (correct) {

            result.setText(
                    "Correct! +" +
                            questions
                                    .get(currentIndex)
                                    .marks +
                            " marks"
            );

            result.setTextColor(
                    Color.rgb(22, 163, 74)
            );

        } else {

            result.setText(
                    "Wrong answer"
            );

            result.setTextColor(
                    Color.rgb(220, 38, 38)
            );
        }

        result.setTextSize(18);

        result.setPadding(
                0,
                18,
                0,
                8
        );

        container.addView(
                result,
                0
        );

        if (explanation != null &&
                !explanation.trim().isEmpty()) {

            TextView explanationText =
                    new TextView(this);

            explanationText.setText(
                    "Explanation: " +
                            explanation
            );

            explanationText.setTextSize(14);

            explanationText.setTextColor(
                    Color.rgb(100, 116, 139)
            );

            explanationText.setPadding(
                    0,
                    5,
                    0,
                    12
            );

            container.addView(
                    explanationText,
                    1
            );
        }
    }

    private void startTimer() {

        long millis =
                durationMinutes *
                        60L *
                        1000L;

        countDownTimer =
                new CountDownTimer(
                        millis,
                        1000
                ) {

                    @Override
                    public void onTick(
                            long remaining
                    ) {

                        long totalSeconds =
                                remaining / 1000;

                        long minutes =
                                totalSeconds / 60;

                        long seconds =
                                totalSeconds % 60;

                        timerText.setText(
                                String.format(
                                        Locale.US,
                                        "Time: %02d:%02d",
                                        minutes,
                                        seconds
                                )
                        );
                    }

                    @Override
                    public void onFinish() {

                        timerText.setText(
                                "Time's up!"
                        );

                        if (!submitted) {
                            submitExam();
                        }
                    }
                }
                        .start();
    }

    private void submitExam() {

        if (submitted) {
            return;
        }

        submitted = true;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        saveResult();

        showResult();
    }

    private void saveResult() {

        if (auth.getCurrentUser() == null) {
            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        Map<String, Object> result =
                new HashMap<>();

        result.put(
                "userId",
                userId
        );

        result.put(
                "quizId",
                quizId
        );

        result.put(
                "quizTitle",
                quizTitle
        );

        result.put(
                "quizType",
                quizType
        );

        result.put(
                "classId",
                classId
        );

        result.put(
                "className",
                className
        );

        result.put(
                "medium",
                studentMedium
        );

        result.put(
                "studentName",
                studentName
        );

        result.put(
                "subjectId",
                subjectId
        );

        result.put(
                "subjectName",
                subjectName
        );

        result.put(
                "chapterId",
                chapterId
        );

        result.put(
                "chapterName",
                chapterName
        );

        result.put(
                "score",
                score
        );

        result.put(
                "answered",
                answered
        );

        result.put(
                "totalQuestions",
                questions.size()
        );

        result.put(
                "totalMarks",
                totalMarks
        );

        double accuracy = 0;

        if (answered > 0) {

            accuracy =
                    (score * 100.0) /
                            Math.max(
                                    totalMarks > 0
                                            ? totalMarks
                                            : questions.size(),
                                    1
                            );
        }

        result.put(
                "accuracy",
                accuracy
        );

        result.put(
                "completedAt",
                System.currentTimeMillis()
        );

        db.collection("examResults")
                .add(result);

        long points =
                Math.max(
                        0,
                        score * 5L
                );

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(
                        document -> {

                            Long oldPoints =
                                    document.getLong(
                                            "points"
                                    );

                            long currentPoints =
                                    oldPoints != null
                                            ? oldPoints
                                            : 0;

                            Map<String, Object> update =
                                    new HashMap<>();

                            update.put(
                                    "points",
                                    currentPoints +
                                            points
                            );

                            db.collection("users")
                                    .document(userId)
                                    .set(
                                            update,
                                            com.google.firebase.firestore.SetOptions
                                                    .merge()
                                    );
                        }
                );
    }

    private void showResult() {

        container.removeAllViews();

        TextView resultTitle =
                new TextView(this);

        resultTitle.setText(
                "Exam Completed!"
        );

        resultTitle.setTextSize(26);

        resultTitle.setGravity(
                Gravity.CENTER
        );

        resultTitle.setTextColor(
                Color.rgb(22, 163, 74)
        );

        resultTitle.setPadding(
                0,
                40,
                0,
                25
        );

        container.addView(
                resultTitle
        );

        TextView scoreText =
                new TextView(this);

        scoreText.setText(
                "Score: " +
                        score +
                        " / " +
                        totalMarks
        );

        scoreText.setTextSize(22);

        scoreText.setGravity(
                Gravity.CENTER
        );

        scoreText.setTextColor(
                Color.rgb(17, 24, 39)
        );

        container.addView(
                scoreText
        );

        TextView answeredText =
                new TextView(this);

        answeredText.setText(
                "Answered: " +
                        answered +
                        " / " +
                        questions.size()
        );

        answeredText.setTextSize(17);

        answeredText.setGravity(
                Gravity.CENTER
        );

        answeredText.setTextColor(
                Color.rgb(100, 116, 139)
        );

        answeredText.setPadding(
                0,
                15,
                0,
                10
        );

        container.addView(
                answeredText
        );

        double accuracy = 0;

        if (totalMarks > 0) {

            accuracy =
                    (score * 100.0) /
                            totalMarks;
        }

        TextView accuracyText =
                new TextView(this);

        accuracyText.setText(
                String.format(
                        Locale.US,
                        "Accuracy: %.1f%%",
                        accuracy
                )
        );

        accuracyText.setTextSize(18);

        accuracyText.setGravity(
                Gravity.CENTER
        );

        accuracyText.setTextColor(
                Color.rgb(79, 70, 229)
        );

        accuracyText.setPadding(
                0,
                10,
                0,
                25
        );

        container.addView(
                accuracyText
        );

        Button done =
                new Button(this);

        done.setText(
                "Back to Exams"
        );

        done.setAllCaps(false);

        done.setOnClickListener(
                v -> finish()
        );

        container.addView(
                done
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
