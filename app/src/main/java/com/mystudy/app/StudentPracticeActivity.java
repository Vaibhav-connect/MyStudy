package com.mystudy.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentPracticeActivity extends AppCompatActivity {

    private LinearLayout questionContainer;
    private TextView scoreText;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressManager progressManager;

    private String classId;
    private String className;
    private String studentMedium;
    private String studentName;
    private String subjectId;
    private String subjectName;
    private String chapterId;
    private String chapterName;
    private String lessonId;
    private String lessonTitle;

    private int score = 0;
    private int answered = 0;

    private boolean practiceFinished = false;

    private final List<String> questionIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");
        studentMedium = getIntent().getStringExtra("studentMedium");
        studentName = getIntent().getStringExtra("studentName");
        subjectId = getIntent().getStringExtra("subjectId");
        subjectName = getIntent().getStringExtra("subjectName");
        chapterId = getIntent().getStringExtra("chapterId");
        chapterName = getIntent().getStringExtra("chapterName");
        lessonId = getIntent().getStringExtra("lessonId");
        lessonTitle = getIntent().getStringExtra("lessonTitle");

        if (studentMedium == null || studentMedium.trim().isEmpty()) {
            studentMedium = "English";
        }

        if (classId == null || classId.trim().isEmpty()
                || chapterId == null || chapterId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Practice information missing",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        progressManager = new ProgressManager();

        createUI();
        loadQuestions();
    }

    private void createUI() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 24, 20, 30);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("🧠 Practice");
        title.setTextSize(27);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER_VERTICAL);

        root.addView(
                title,
                new LinearLayout.LayoutParams(-1, 65)
        );

        TextView subtitle = new TextView(this);

        if (lessonTitle != null && !lessonTitle.trim().isEmpty()) {
            subtitle.setText(
                    lessonTitle + " • " +
                            studentMedium +
                            " • Test your knowledge"
            );
        } else if (chapterName != null && !chapterName.trim().isEmpty()) {
            subtitle.setText(
                    chapterName + " • " +
                            studentMedium +
                            " • Test your knowledge"
            );
        } else {
            subtitle.setText(
                    studentMedium +
                            " • Test your knowledge"
            );
        }

        subtitle.setTextSize(16);
        subtitle.setTextColor(Color.rgb(100, 116, 139));
        subtitle.setPadding(0, 0, 0, 16);

        root.addView(subtitle);

        scoreText = new TextView(this);
        scoreText.setText("⭐ Score: 0");
        scoreText.setTextSize(18);
        scoreText.setTextColor(Color.rgb(79, 70, 229));
        scoreText.setTypeface(null, Typeface.BOLD);
        scoreText.setGravity(Gravity.CENTER);

        GradientDrawable scoreBackground = new GradientDrawable();
        scoreBackground.setColor(Color.WHITE);
        scoreBackground.setCornerRadius(24);

        scoreText.setBackground(scoreBackground);
        scoreText.setElevation(3);

        root.addView(
                scoreText,
                new LinearLayout.LayoutParams(-1, 55)
        );

        questionContainer = new LinearLayout(this);
        questionContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams questionParams =
                new LinearLayout.LayoutParams(-1, -2);

        questionParams.topMargin = 18;

        root.addView(
                questionContainer,
                questionParams
        );

        scrollView.addView(root);
        setContentView(scrollView);
    }

    private void loadQuestions() {

        questionContainer.removeAllViews();
        questionIds.clear();

        score = 0;
        answered = 0;
        practiceFinished = false;

        updateScore();

        db.collection("questions")
                .whereEqualTo("classId", classId)
                .whereEqualTo("chapterId", chapterId)
                .whereEqualTo("medium", studentMedium)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        showMessage(
                                "No practice questions available for " +
                                        studentMedium +
                                        " medium yet."
                        );

                        return;
                    }

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String questionId = document.getId();

                        String question =
                                document.getString("question");

                        String type =
                                document.getString("type");

                        String optionA =
                                document.getString("optionA");

                        String optionB =
                                document.getString("optionB");

                        String optionC =
                                document.getString("optionC");

                        String optionD =
                                document.getString("optionD");

                        String answer =
                                document.getString("answer");

                        String explanation =
                                document.getString("explanation");

                        String imageUrl =
                                document.getString("imageUrl");

                        if (question == null ||
                                question.trim().isEmpty()) {
                            continue;
                        }

                        if (type == null ||
                                type.trim().isEmpty()) {
                            type = "MCQ";
                        }

                        questionIds.add(questionId);

                        addQuestionCard(
                                questionId,
                                question,
                                type,
                                optionA,
                                optionB,
                                optionC,
                                optionD,
                                answer,
                                explanation,
                                imageUrl
                        );
                    }

                    if (questionIds.isEmpty()) {

                        showMessage(
                                "No valid practice questions available."
                        );

                    } else {

                        updateScore();
                    }
                })
                .addOnFailureListener(error -> {

                    showMessage(
                            "Unable to load questions."
                    );

                    Toast.makeText(
                            StudentPracticeActivity.this,
                            "Unable to load questions",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void addQuestionCard(
            String questionId,
            String question,
            String type,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            String answer,
            String explanation,
            String imageUrl
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 20, 20, 20);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(26);

        card.setBackground(background);
        card.setElevation(5);

        TextView questionNumber = new TextView(this);

        int number =
                questionIds.indexOf(questionId) + 1;

        questionNumber.setText(
                "Question " +
                        number +
                        " • " +
                        type.replace("_", " ")
        );

        questionNumber.setTextSize(13);
        questionNumber.setTextColor(
                Color.rgb(79, 70, 229)
        );

        questionNumber.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(questionNumber);

        TextView questionText = new TextView(this);

        questionText.setText(
                "❓ " + question
        );

        questionText.setTextSize(17);
        questionText.setTextColor(
                Color.rgb(17, 24, 39)
        );

        questionText.setTypeface(
                null,
                Typeface.BOLD
        );

        questionText.setLineSpacing(
                2,
                1.05f
        );

        LinearLayout.LayoutParams questionTextParams =
                new LinearLayout.LayoutParams(-1, -2);

        questionTextParams.topMargin = 8;

        card.addView(
                questionText,
                questionTextParams
        );

        if ("PICTURE_BASED".equalsIgnoreCase(type)
                && imageUrl != null
                && !imageUrl.trim().isEmpty()) {

            addQuestionImage(card, imageUrl);
        }

        if ("TRUE_FALSE".equalsIgnoreCase(type)) {

            addOption(
                    card,
                    "True",
                    answer,
                    questionId,
                    explanation
            );

            addOption(
                    card,
                    "False",
                    answer,
                    questionId,
                    explanation
            );

        } else if (
                "FILL_BLANK".equalsIgnoreCase(type)
                        || "NUMERICAL".equalsIgnoreCase(type)
                        || "SHORT_ANSWER".equalsIgnoreCase(type)
                        || "SPELLING".equalsIgnoreCase(type)
                        || "REARRANGE".equalsIgnoreCase(type)
                        || "WORD_PROBLEM".equalsIgnoreCase(type)
                        || "PICTURE_BASED".equalsIgnoreCase(type)
        ) {

            String hint;

            if ("SPELLING".equalsIgnoreCase(type)) {
                hint = "Type the correct spelling";
            } else if ("REARRANGE".equalsIgnoreCase(type)) {
                hint = "Arrange/type the words in the correct order";
            } else if ("WORD_PROBLEM".equalsIgnoreCase(type)) {
                hint = "Solve the problem and type your answer";
            } else if ("PICTURE_BASED".equalsIgnoreCase(type)) {
                hint = "Type the answer related to the picture";
            } else {
                hint = "Type your answer";
            }

            addTextAnswer(
                    card,
                    answer,
                    questionId,
                    explanation,
                    type,
                    hint
            );

        } else if ("MATCH".equalsIgnoreCase(type)) {

            addMatchQuestion(
                    card,
                    optionA,
                    optionB,
                    optionC,
                    optionD,
                    answer,
                    questionId,
                    explanation
            );

        } else {

            if (optionA != null &&
                    !optionA.trim().isEmpty()) {

                addOption(
                        card,
                        optionA,
                        answer,
                        questionId,
                        explanation
                );
            }

            if (optionB != null &&
                    !optionB.trim().isEmpty()) {

                addOption(
                        card,
                        optionB,
                        answer,
                        questionId,
                        explanation
                );
            }

            if (optionC != null &&
                    !optionC.trim().isEmpty()) {

                addOption(
                        card,
                        optionC,
                        answer,
                        questionId,
                        explanation
                );
            }

            if (optionD != null &&
                    !optionD.trim().isEmpty()) {

                addOption(
                        card,
                        optionD,
                        answer,
                        questionId,
                        explanation
                );
            }
        }

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 0, 0, 18);

        questionContainer.addView(card, params);

        addPressAnimation(card);
    }

    private void addQuestionImage(
            LinearLayout card,
            String imageUrl
    ) {

        ImageView imageView = new ImageView(this);

        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );

        GradientDrawable imageBackground =
                new GradientDrawable();

        imageBackground.setColor(
                Color.rgb(248, 250, 252)
        );

        imageBackground.setCornerRadius(20);

        imageView.setBackground(imageBackground);

        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(
                        -1,
                        300
                );

        imageParams.topMargin = 12;

        card.addView(
                imageView,
                imageParams
        );

        loadImageFromUrl(
                imageUrl,
                imageView
        );
    }

    private void loadImageFromUrl(
            String imageUrl,
            ImageView imageView
    ) {

        new Thread(() -> {

            try {

                URL url =
                        new URL(imageUrl);

                HttpURLConnection connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setDoInput(true);

                connection.connect();

                InputStream inputStream =
                        connection.getInputStream();

                Bitmap bitmap =
                        BitmapFactory.decodeStream(
                                inputStream
                        );

                inputStream.close();
                connection.disconnect();

                runOnUiThread(() -> {

                    if (bitmap != null) {

                        imageView.setImageBitmap(
                                bitmap
                        );

                    } else {

                        imageView.setVisibility(
                                View.GONE
                        );
                    }
                });

            } catch (Exception error) {

                runOnUiThread(() ->
                        imageView.setVisibility(
                                View.GONE
                        )
                );
            }

        }).start();
    }

    private void addMatchQuestion(
            LinearLayout card,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            String correctAnswer,
            String questionId,
            String explanation
    ) {

        TextView instruction =
                new TextView(this);

        instruction.setText(
                "Match the pairs using the format shown in the answer."
        );

        instruction.setTextSize(14);

        instruction.setTextColor(
                Color.rgb(100, 116, 139)
        );

        instruction.setPadding(
                0,
                12,
                0,
                4
        );

        card.addView(instruction);

        if (optionA != null &&
                !optionA.trim().isEmpty()) {

            addMatchRow(
                    card,
                    "A",
                    optionA
            );
        }

        if (optionB != null &&
                !optionB.trim().isEmpty()) {

            addMatchRow(
                    card,
                    "B",
                    optionB
            );
        }

        if (optionC != null &&
                !optionC.trim().isEmpty()) {

            addMatchRow(
                    card,
                    "C",
                    optionC
            );
        }

        if (optionD != null &&
                !optionD.trim().isEmpty()) {

            addMatchRow(
                    card,
                    "D",
                    optionD
            );
        }

        EditText matchInput =
                new EditText(this);

        matchInput.setHint(
                "Example: A-1, B-2, C-3"
        );

        matchInput.setTextSize(16);
        matchInput.setSingleLine(true);
        matchInput.setInputType(
                InputType.TYPE_CLASS_TEXT
        );

        GradientDrawable inputBackground =
                new GradientDrawable();

        inputBackground.setColor(
                Color.rgb(248, 250, 252)
        );

        inputBackground.setCornerRadius(20);

        inputBackground.setStroke(
                2,
                Color.rgb(226, 232, 240)
        );

        matchInput.setBackground(
                inputBackground
        );

        LinearLayout.LayoutParams inputParams =
                new LinearLayout.LayoutParams(
                        -1,
                        58
                );

        inputParams.topMargin = 10;

        card.addView(
                matchInput,
                inputParams
        );

        Button submitButton =
                createSubmitButton();

        card.addView(
                submitButton,
                new LinearLayout.LayoutParams(
                        -1,
                        55
                )
        );

        addPressAnimation(submitButton);

        submitButton.setOnClickListener(view -> {

            if (!submitButton.isEnabled()
                    || practiceFinished) {
                return;
            }

            String entered =
                    matchInput.getText()
                            .toString()
                            .trim();

            if (entered.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please enter the matching answer.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            submitButton.setEnabled(false);
            matchInput.setEnabled(false);

            answered++;

            boolean isCorrect =
                    checkMatchAnswer(
                            entered,
                            correctAnswer
                    );

            if (isCorrect) {

                score++;

                setButtonBackground(
                        submitButton,
                        Color.rgb(34, 197, 94)
                );

                submitButton.setText(
                        "✓ Correct"
                );

                Toast.makeText(
                        this,
                        "Correct! 🎉",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                setButtonBackground(
                        submitButton,
                        Color.rgb(239, 68, 68)
                );

                submitButton.setText(
                        "✗ Wrong"
                );

                Toast.makeText(
                        this,
                        "Wrong matching.",
                        Toast.LENGTH_SHORT
                ).show();
            }

            addExplanation(
                    card,
                    explanation
            );

            updateScore();

            if (answered >= questionIds.size()) {
                finishPractice();
            }
        });
    }

    private void addMatchRow(
            LinearLayout card,
            String label,
            String value
    ) {

        TextView row =
                new TextView(this);

        row.setText(
                label + "  →  " + value
        );

        row.setTextSize(16);
        row.setTextColor(
                Color.rgb(17, 24, 39)
        );

        row.setPadding(
                14,
                12,
                14,
                12
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(248, 250, 252)
        );

        background.setCornerRadius(16);

        row.setBackground(background);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.topMargin = 6;

        card.addView(row, params);
    }

    private boolean checkMatchAnswer(
            String entered,
            String correctAnswer
    ) {

        if (correctAnswer == null ||
                correctAnswer.trim().isEmpty()) {
            return false;
        }

        String enteredNormalized =
                normalizeMatch(entered);

        String expectedNormalized =
                normalizeMatch(correctAnswer);

        return enteredNormalized.equals(
                expectedNormalized
        );
    }

    private String normalizeMatch(
            String value
    ) {

        return value
                .toLowerCase(Locale.ROOT)
                .replace(" ", "")
                .replace("→", "-")
                .replace(":", "-")
                .replace(";", ",")
                .trim();
    }

    private void addOption(
            LinearLayout card,
            String option,
            String correctAnswer,
            String questionId,
            String explanation
    ) {

        Button button =
                new Button(this);

        button.setText(option);
        button.setAllCaps(false);
        button.setTextSize(15);
        button.setTextColor(
                Color.rgb(17, 24, 39)
        );

        button.setGravity(Gravity.CENTER);

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(241, 245, 249)
        );

        background.setCornerRadius(24);

        button.setBackground(background);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        55
                );

        params.topMargin = 10;

        card.addView(button, params);

        addPressAnimation(button);

        button.setOnClickListener(view -> {

            if (!button.isEnabled()
                    || practiceFinished) {
                return;
            }

            disableQuestionOptions(card);

            answered++;

            String selected =
                    option.trim();

            String correct =
                    correctAnswer == null
                            ? ""
                            : correctAnswer.trim();

            boolean isCorrect =
                    selected.equalsIgnoreCase(
                            correct
                    );

            showAnswerResult(
                    button,
                    isCorrect,
                    explanation
            );

            if (isCorrect) {

                score++;

                Toast.makeText(
                        this,
                        "Correct! 🎉",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                Toast.makeText(
                        this,
                        "Try the next one!",
                        Toast.LENGTH_SHORT
                ).show();
            }

            updateScore();

            if (answered >= questionIds.size()) {
                finishPractice();
            }
        });
    }

    private void addTextAnswer(
            LinearLayout card,
            String correctAnswer,
            String questionId,
            String explanation,
            String type,
            String hint
    ) {

        EditText answerInput =
                new EditText(this);

        answerInput.setHint(hint);
        answerInput.setTextSize(16);
        answerInput.setSingleLine(true);
        answerInput.setPadding(
                16,
                0,
                16,
                0
        );

        if ("NUMERICAL".equalsIgnoreCase(type)
                || "WORD_PROBLEM".equalsIgnoreCase(type)) {

            answerInput.setInputType(
                    InputType.TYPE_CLASS_NUMBER
                            | InputType.TYPE_NUMBER_FLAG_DECIMAL
                            | InputType.TYPE_NUMBER_FLAG_SIGNED
            );

        } else {

            answerInput.setInputType(
                    InputType.TYPE_CLASS_TEXT
            );
        }

        GradientDrawable inputBackground =
                new GradientDrawable();

        inputBackground.setColor(
                Color.rgb(248, 250, 252)
        );

        inputBackground.setCornerRadius(20);

        inputBackground.setStroke(
                2,
                Color.rgb(226, 232, 240)
        );

        answerInput.setBackground(inputBackground);

        LinearLayout.LayoutParams inputParams =
                new LinearLayout.LayoutParams(
                        -1,
                        58
                );

        inputParams.topMargin = 12;

        card.addView(
                answerInput,
                inputParams
        );

        Button submitButton =
                createSubmitButton();

        LinearLayout.LayoutParams submitParams =
                new LinearLayout.LayoutParams(
                        -1,
                        55
                );

        submitParams.topMargin = 10;

        card.addView(
                submitButton,
                submitParams
        );

        addPressAnimation(submitButton);

        submitButton.setOnClickListener(view -> {

            if (!submitButton.isEnabled()
                    || practiceFinished) {
                return;
            }

            String entered =
                    answerInput.getText()
                            .toString()
                            .trim();

            if (entered.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please enter an answer.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            submitButton.setEnabled(false);
            answerInput.setEnabled(false);

            answered++;

            boolean isCorrect =
                    checkTextAnswer(
                            entered,
                            correctAnswer,
                            type
                    );

            if (isCorrect) {

                score++;

                setButtonBackground(
                        submitButton,
                        Color.rgb(34, 197, 94)
                );

                submitButton.setText(
                        "✓ Correct"
                );

                Toast.makeText(
                        this,
                        "Correct! 🎉",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                setButtonBackground(
                        submitButton,
                        Color.rgb(239, 68, 68)
                );

                submitButton.setText(
                        "✗ Wrong"
                );

                Toast.makeText(
                        this,
                        "Wrong answer.",
                        Toast.LENGTH_SHORT
                ).show();
            }

            addExplanation(
                    card,
                    explanation
            );

            updateScore();

            if (answered >= questionIds.size()) {
                finishPractice();
            }
        });
    }

    private Button createSubmitButton() {

        Button button =
                new Button(this);

        button.setText(
                "Submit Answer"
        );

        button.setAllCaps(false);
        button.setTextSize(15);
        button.setTextColor(Color.WHITE);

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(79, 70, 229)
        );

        background.setCornerRadius(24);

        button.setBackground(background);

        return button;
    }

    private boolean checkTextAnswer(
            String entered,
            String correctAnswer,
            String type
    ) {

        if (correctAnswer == null ||
                correctAnswer.trim().isEmpty()) {
            return false;
        }

        String expected =
                correctAnswer.trim();

        if ("NUMERICAL".equalsIgnoreCase(type)
                || "WORD_PROBLEM".equalsIgnoreCase(type)) {

            try {

                double enteredNumber =
                        Double.parseDouble(
                                entered
                        );

                double expectedNumber =
                        Double.parseDouble(
                                expected
                        );

                return Math.abs(
                        enteredNumber -
                                expectedNumber
                ) < 0.000001;

            } catch (Exception error) {

                return normalizeText(entered)
                        .equals(
                                normalizeText(expected)
                        );
            }
        }

        if ("REARRANGE".equalsIgnoreCase(type)) {

            return normalizeRearrange(
                    entered
            ).equals(
                    normalizeRearrange(
                            expected
                    )
            );
        }

        return normalizeText(entered)
                .equals(
                        normalizeText(expected)
                );
    }

    private String normalizeText(
            String value
    ) {

        return value
                .trim()
                .replaceAll(
                        "\\s+",
                        " "
                )
                .toLowerCase(
                        Locale.ROOT
                );
    }

    private String normalizeRearrange(
            String value
    ) {

        return value
                .trim()
                .replaceAll(
                        "\\s+",
                        " "
                )
                .toLowerCase(
                        Locale.ROOT
                );
    }

    private void showAnswerResult(
            Button button,
            boolean isCorrect,
            String explanation
    ) {

        if (isCorrect) {

            button.setText(
                    "✓ " + button.getText()
            );

            setButtonBackground(
                    button,
                    Color.rgb(34, 197, 94)
            );

            button.setTextColor(Color.WHITE);

        } else {

            button.setText(
                    "✗ " + button.getText()
            );

            setButtonBackground(
                    button,
                    Color.rgb(239, 68, 68)
            );

            button.setTextColor(Color.WHITE);
        }

        if (explanation != null &&
                !explanation.trim().isEmpty()) {

            TextView explanationView =
                    new TextView(this);

            explanationView.setText(
                    "💡 " + explanation
            );

            explanationView.setTextSize(14);

            explanationView.setTextColor(
                    Color.rgb(71, 85, 105)
            );

            explanationView.setPadding(
                    0,
                    10,
                    0,
                    0
            );

            View parent =
                    (View) button.getParent();

            if (parent instanceof LinearLayout) {

                ((LinearLayout) parent)
                        .addView(
                                explanationView
                        );
            }
        }
    }

    private void addExplanation(
            LinearLayout card,
            String explanation
    ) {

        if (explanation == null ||
                explanation.trim().isEmpty()) {
            return;
        }

        TextView explanationView =
                new TextView(this);

        explanationView.setText(
                "💡 " + explanation
        );

        explanationView.setTextSize(14);

        explanationView.setTextColor(
                Color.rgb(71, 85, 105)
        );

        explanationView.setPadding(
                0,
                10,
                0,
                0
        );

        card.addView(
                explanationView
        );
    }

    private void disableQuestionOptions(
            LinearLayout card
    ) {

        for (int i = 0;
             i < card.getChildCount();
             i++) {

            View child =
                    card.getChildAt(i);

            if (child instanceof Button) {
                child.setEnabled(false);
            }
        }
    }

    private void setButtonBackground(
            Button button,
            int color
    ) {

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(color);
        background.setCornerRadius(24);

        button.setBackground(background);
    }

    private void updateScore() {

        if (scoreText == null) {
            return;
        }

        scoreText.setText(
                "⭐ Score: " +
                        score +
                        " / " +
                        questionIds.size()
        );
    }

    private void finishPractice() {

        if (practiceFinished) {
            return;
        }

        practiceFinished = true;

        int total =
                questionIds.size();

        FirebaseUser user =
                auth.getCurrentUser();

        if (user != null && total > 0) {

            progressManager.saveQuizResult(
                    user.getUid(),
                    classId,
                    chapterId,
                    score,
                    total
            );

            progressManager.addPoints(
                    user.getUid(),
                    score * 5
            );

            progressManager.updateStreak(
                    user.getUid()
            );
        }

        Toast.makeText(
                this,
                "Practice complete! Score: " +
                        score +
                        "/" +
                        total +
                        " 🎉",
                Toast.LENGTH_LONG
        ).show();
    }

    private void addPressAnimation(
            View view
    ) {

        view.setOnTouchListener(
                (v, event) -> {

                    if (event.getAction() ==
                            MotionEvent.ACTION_DOWN) {

                        v.animate()
                                .scaleX(0.97f)
                                .scaleY(0.97f)
                                .setDuration(100)
                                .start();

                    } else if (
                            event.getAction() ==
                                    MotionEvent.ACTION_UP
                                    ||
                                    event.getAction() ==
                                            MotionEvent.ACTION_CANCEL
                    ) {

                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                    }

                    return false;
                }
        );
    }

    private void showMessage(
            String message
    ) {

        TextView messageView =
                new TextView(this);

        messageView.setText(message);
        messageView.setTextSize(16);

        messageView.setTextColor(
                Color.rgb(100, 116, 139)
        );

        messageView.setGravity(
                Gravity.CENTER
        );

        messageView.setPadding(
                20,
                50,
                20,
                50
        );

        if (questionContainer != null) {

            questionContainer.addView(
                    messageView,
                    new LinearLayout.LayoutParams(
                            -1,
                            -2
                    )
            );
        }
    }
}
