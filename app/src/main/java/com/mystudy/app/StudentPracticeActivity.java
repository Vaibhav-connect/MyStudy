package com.mystudy.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentPracticeActivity extends AppCompatActivity {

    private static final int BLUE = Color.rgb(45, 105, 210);
    private static final int DARK_BLUE = Color.rgb(28, 65, 130);
    private static final int YELLOW = Color.rgb(255, 205, 65);
    private static final int BG = Color.rgb(244, 248, 255);
    private static final int TEXT = Color.rgb(35, 48, 70);
    private static final int GREEN = Color.rgb(35, 155, 95);
    private static final int RED = Color.rgb(220, 70, 70);

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private LinearLayout root;
    private LinearLayout content;
    private LinearLayout questionCard;
    private LinearLayout optionsContainer;

    private TextView titleText;
    private TextView progressText;
    private TextView scoreText;
    private TextView questionText;
    private TextView feedbackText;
    private TextView explanationText;

    private ProgressBar progressBar;
    private EditText answerInput;
    private Button submitButton;
    private Button nextButton;

    private String classId, className;
    private String studentMedium, studentName;
    private String subjectId, subjectName;
    private String chapterId, chapterName;
    private String lessonId, lessonTitle;

    private final List<Map<String, Object>> questions = new ArrayList<>();
    private final List<String> questionIds = new ArrayList<>();

    private int currentIndex = 0;
    private int score = 0;
    private int answeredCount = 0;

    private boolean questionAnswered = false;
    private boolean practiceFinished = false;
    private boolean resultSaved = false;

    private String selectedAnswer = "";

    private final ExecutorService imageExecutor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        readIntentData();
        createScreen();

        if (classId.isEmpty() || chapterId.isEmpty()) {
            showError("Practice information is missing.");
        } else {
            loadQuestions();
        }
    }

    private void readIntentData() {
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

        if (classId == null) classId = "";
        if (className == null) className = "";
        if (studentMedium == null) studentMedium = "";
        if (studentName == null) studentName = "";
        if (subjectId == null) subjectId = "";
        if (subjectName == null) subjectName = "";
        if (chapterId == null) chapterId = "";
        if (chapterName == null) chapterName = "";
        if (lessonId == null) lessonId = "";
        if (lessonTitle == null) lessonTitle = "";
    }

    private void createScreen() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(20), dp(18), dp(20), dp(18));
        header.setBackground(rounded(BLUE, 0, 0, 22, 22));

        TextView brand = text("📘 MyStudy", 23, Color.WHITE, true);
        header.addView(brand);

        TextView subtitle = text("Practice • Improve • Grow", 13,
                Color.WHITE, false);
        header.addView(subtitle);

        TextView chapter = text(
                chapterName.isEmpty() ? "Practice Questions" : chapterName,
                19, Color.WHITE, true);
        chapter.setPadding(0, dp(14), 0, dp(2));
        header.addView(chapter);

        if (!subjectName.isEmpty()) {
            TextView subject = text(subjectName, 13, Color.WHITE, false);
            header.addView(subject);
        }

        root.addView(header);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18), dp(18), dp(18), dp(24));

        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1));

        setContentView(root);

        showLoading();
    }

    private void loadQuestions() {
        showLoading();

        db.collection("questions")
                .whereEqualTo("classId", classId)
                .whereEqualTo("chapterId", chapterId)
                .whereEqualTo("medium", studentMedium)
                .get()
                .addOnSuccessListener(snapshot -> {
                    questions.clear();
                    questionIds.clear();

                    for (QueryDocumentSnapshot document : snapshot) {
                        Map<String, Object> data = document.getData();

                        String question = getStringValue(
                                data, "question");

                        if (question.trim().isEmpty()) {
                            continue;
                        }

                        questions.add(data);
                        questionIds.add(document.getId());
                    }

                    if (questions.isEmpty()) {
                        showEmpty();
                    } else {
                        currentIndex = 0;
                        score = 0;
                        answeredCount = 0;
                        showQuestion();
                    }
                })
                .addOnFailureListener(e ->
                        showError("Unable to load questions. Please try again."));
    }

    private void showLoading() {
        content.removeAllViews();

        ProgressBar loader = new ProgressBar(this);
        LinearLayout.LayoutParams loaderParams =
                new LinearLayout.LayoutParams(dp(48), dp(48));
        loaderParams.gravity = Gravity.CENTER;
        loaderParams.topMargin = dp(70);
        content.addView(loader, loaderParams);

        TextView message = text("Loading practice questions...",
                15, TEXT, true);
        message.setGravity(Gravity.CENTER);
        message.setPadding(0, dp(18), 0, dp(20));
        content.addView(message);
    }

    private void showEmpty() {
        content.removeAllViews();

        addMessageCard(
                "📚",
                "No Questions Available",
                "There are no practice questions for this chapter yet."
        );

        addButton(content, "Go Back", BLUE, v -> finish());
    }

    private void showError(String message) {
        content.removeAllViews();

        addMessageCard("⚠️", "Something went wrong", message);

        addButton(content, "Try Again", BLUE, v -> loadQuestions());
        addButton(content, "Go Back", DARK_BLUE, v -> finish());
    }

    private void addMessageCard(
            String icon, String heading, String message) {

        LinearLayout card = createCard();
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(20), dp(35), dp(20), dp(35));

        TextView iconText = text(icon, 42, BLUE, true);
        iconText.setGravity(Gravity.CENTER);
        card.addView(iconText);

        TextView title = text(heading, 20, TEXT, true);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(12), 0, dp(8));
        card.addView(title);

        TextView description = text(message, 14, Color.GRAY, false);
        description.setGravity(Gravity.CENTER);
        card.addView(description);

        content.addView(card);
    }

    private void showQuestion() {
        if (currentIndex >= questions.size()) {
            showResult();
            return;
        }

        questionAnswered = false;
        selectedAnswer = "";

        content.removeAllViews();

        Map<String, Object> data = questions.get(currentIndex);

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.setGravity(Gravity.CENTER_VERTICAL);

        progressText = text(
                "Question " + (currentIndex + 1) + " / " + questions.size(),
                14, DARK_BLUE, true);

        scoreText = text("⭐ " + score + " Points", 14, BLUE, true);
        scoreText.setGravity(Gravity.END);

        stats.addView(progressText,
                new LinearLayout.LayoutParams(0, -2, 1));
        stats.addView(scoreText,
                new LinearLayout.LayoutParams(0, -2, 1));

        content.addView(stats);

        progressBar = new ProgressBar(
                this, null, android.R.attr.progressBarStyleHorizontal);

        progressBar.setMax(questions.size());
        progressBar.setProgress(currentIndex + 1);
        progressBar.setProgressTintList(
                android.content.res.ColorStateList.valueOf(BLUE));

        LinearLayout.LayoutParams progressParams =
                new LinearLayout.LayoutParams(-1, dp(8));
        progressParams.topMargin = dp(12);
        progressParams.bottomMargin = dp(18);

        content.addView(progressBar, progressParams);

        questionCard = createCard();
        questionCard.setPadding(dp(18), dp(20), dp(18), dp(20));

        TextView typeLabel = text(
                getQuestionType(data).replace("_", " "),
                12, BLUE, true);

        typeLabel.setBackground(rounded(
                Color.rgb(230, 239, 255), 12, 12, 12, 12));
        typeLabel.setPadding(dp(12), dp(7), dp(12), dp(7));

        questionCard.addView(typeLabel);

        questionText = text(
                getStringValue(data, "question"),
                19, TEXT, true);

        questionText.setPadding(0, dp(18), 0, dp(12));
        questionCard.addView(questionText);

        String imageUrl = getStringValue(data, "imageUrl");

        if (!imageUrl.isEmpty()) {
            addQuestionImage(imageUrl);
        }

        optionsContainer = new LinearLayout(this);
        optionsContainer.setOrientation(LinearLayout.VERTICAL);
        optionsContainer.setPadding(0, dp(12), 0, 0);

        questionCard.addView(optionsContainer);

        content.addView(questionCard);

        feedbackText = text("", 15, GREEN, true);
        feedbackText.setPadding(dp(8), dp(12), dp(8), dp(4));
        content.addView(feedbackText);

        explanationText = text("", 14, TEXT, false);
        explanationText.setPadding(dp(8), dp(4), dp(8), dp(12));
        content.addView(explanationText);

        showAnswerControls(data);

        nextButton = createButton("Next Question", BLUE);
        nextButton.setVisibility(View.GONE);
        nextButton.setOnClickListener(v -> {
            currentIndex++;
            showQuestion();
        });

        content.addView(nextButton);

        animateView(questionCard);
    }

    private void showAnswerControls(Map<String, Object> data) {
        String type = getQuestionType(data);

        answerInput = null;
        submitButton = null;

        if (type.equals("TRUE_FALSE")) {
            addOption("True");
            addOption("False");
        } else if (type.equals("MCQ")
                || type.equals("PICTURE_BASED")) {

            addOptionFromData(data, "optionA");
            addOptionFromData(data, "optionB");
            addOptionFromData(data, "optionC");
            addOptionFromData(data, "optionD");

        } else if (type.equals("MATCH")) {
            createTextAnswerInput(false);
        } else {
            createTextAnswerInput(
                    type.equals("NUMERICAL")
                            || type.equals("WORD_PROBLEM"));
        }
    }

    private void addOptionFromData(
            Map<String, Object> data, String key) {

        String option = getStringValue(data, key);

        if (!option.isEmpty()) {
            addOption(option);
        }
    }

    private void addOption(String option) {
        Button button = createButton(option, Color.WHITE);
        button.setTextColor(TEXT);
        button.setGravity(Gravity.CENTER_VERTICAL);
        button.setStrokeColor(BLUE);

        button.setOnClickListener(v -> {
            if (questionAnswered) return;

            selectedAnswer = option;
            checkAnswer(option);
        });

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, dp(52));

        params.topMargin = dp(7);
        params.bottomMargin = dp(7);

        optionsContainer.addView(button, params);
    }

    private void createTextAnswerInput(boolean numeric) {
        answerInput = new EditText(this);
        answerInput.setSingleLine(false);
        answerInput.setMinLines(1);
        answerInput.setMaxLines(3);
        answerInput.setTextSize(15);
        answerInput.setTextColor(TEXT);
        answerInput.setHint("Type your answer here...");
        answerInput.setPadding(dp(14), dp(12), dp(14), dp(12));
        answerInput.setBackground(rounded(Color.WHITE,
                2, 2, 2, 2));
        answerInput.setInputType(numeric
                ? InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL
                : InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        optionsContainer.addView(answerInput,
                new LinearLayout.LayoutParams(-1, dp(55)));

        submitButton = createButton("Submit Answer", BLUE);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, dp(50));
        params.topMargin = dp(14);

        optionsContainer.addView(submitButton, params);

        submitButton.setOnClickListener(v -> {
            if (questionAnswered) return;

            String answer = answerInput.getText()
                    .toString().trim();

            if (answer.isEmpty()) {
                answerInput.setError("Please enter your answer");
                return;
            }

            checkAnswer(answer);
        });
    }

    private void checkAnswer(String userAnswer) {
        if (questionAnswered || currentIndex >= questions.size()) {
            return;
        }

        questionAnswered = true;
        answeredCount++;

        Map<String, Object> data = questions.get(currentIndex);

        String correctAnswer = getStringValue(data, "answer");
        String type = getQuestionType(data);

        boolean correct = isCorrectAnswer(
                userAnswer, correctAnswer, type);

        if (correct) {
            score++;
            feedbackText.setText("🎉 Correct Answer! Well done!");
            feedbackText.setTextColor(GREEN);
        } else {
            feedbackText.setText(
                    "❌ Incorrect. Correct answer: " + correctAnswer);
            feedbackText.setTextColor(RED);
        }

        String explanation = getStringValue(data, "explanation");

        if (!explanation.isEmpty()) {
            explanationText.setText("💡 Explanation: " + explanation);
        } else {
            explanationText.setText("");
        }

        if (answerInput != null) {
            answerInput.setEnabled(false);
        }

        if (submitButton != null) {
            submitButton.setEnabled(false);
        }

        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            View child = optionsContainer.getChildAt(i);

            if (child instanceof Button) {
                child.setEnabled(false);
            }
        }

        scoreText.setText("⭐ " + score + " Points");

        if (currentIndex == questions.size() - 1) {
            nextButton.setText("View Results");
        } else {
            nextButton.setText("Next Question");
        }

        nextButton.setVisibility(View.VISIBLE);
    }

    private boolean isCorrectAnswer(
            String userAnswer, String correctAnswer, String type) {

        if (userAnswer == null || correctAnswer == null) {
            return false;
        }

        String user = userAnswer.trim();
        String correct = correctAnswer.trim();

        if (type.equals("MATCH")) {
            return normalizeMatch(user).equals(
                    normalizeMatch(correct));
        }

        if (type.equals("NUMERICAL")
                || type.equals("WORD_PROBLEM")) {

            try {
                double userNumber = Double.parseDouble(user);
                double correctNumber =
                        Double.parseDouble(correct);

                return Math.abs(userNumber - correctNumber) < 0.0001;
            } catch (Exception ignored) {
                return user.equalsIgnoreCase(correct);
            }
        }

        return user.equalsIgnoreCase(correct);
    }

    private String normalizeMatch(String value) {
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "");
    }

    private void addQuestionImage(String imageUrl) {
        ImageView image = new ImageView(this);
        image.setAdjustViewBounds(true);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setBackground(rounded(BG, 12, 12, 12, 12));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, dp(190));

        params.topMargin = dp(8);
        params.bottomMargin = dp(8);

        questionCard.addView(image, params);

        imageExecutor.execute(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(imageUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.setDoInput(true);
                connection.connect();

                Bitmap bitmap = BitmapFactory.decodeStream(
                        connection.getInputStream());

                if (bitmap != null && !isFinishing()) {
                    runOnUiThread(() -> image.setImageBitmap(bitmap));
                }

            } catch (Exception ignored) {
                runOnUiThread(() ->
                        image.setVisibility(View.GONE));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void showResult() {
        if (practiceFinished) return;

        practiceFinished = true;

        content.removeAllViews();

        LinearLayout resultCard = createCard();
        resultCard.setGravity(Gravity.CENTER);
        resultCard.setPadding(dp(22), dp(28), dp(22), dp(28));

        TextView trophy = text("🏆", 55, YELLOW, true);
        trophy.setGravity(Gravity.CENTER);
        resultCard.addView(trophy);

        TextView heading = text(
                "Practice Completed!",
                23, DARK_BLUE, true);
        heading.setGravity(Gravity.CENTER);
        heading.setPadding(0, dp(12), 0, dp(12));
        resultCard.addView(heading);

        TextView scoreResult = text(
                score + " / " + questions.size(),
                38, BLUE, true);
        scoreResult.setGravity(Gravity.CENTER);
        resultCard.addView(scoreResult);

        int percentage = questions.isEmpty()
                ? 0
                : (score * 100 / questions.size());

        TextView percentageText = text(
                percentage + "% Accuracy",
                17, GREEN, true);
        percentageText.setGravity(Gravity.CENTER);
        percentageText.setPadding(0, dp(8), 0, dp(18));
        resultCard.addView(percentageText);

        TextView answered = text(
                "Questions Answered: " + answeredCount,
                15, TEXT, false);
        answered.setGravity(Gravity.CENTER);
        resultCard.addView(answered);

        TextView points = text(
                "⭐ Points Earned: " + (score * 5),
                17, DARK_BLUE, true);
        points.setGravity(Gravity.CENTER);
        points.setPadding(0, dp(14), 0, dp(5));
        resultCard.addView(points);

        content.addView(resultCard);

        addButton(content, "Practice Again", BLUE, v -> {
            currentIndex = 0;
            score = 0;
            answeredCount = 0;
            practiceFinished = false;
            resultSaved = false;
            showQuestion();
        });

        addButton(content, "Back to Lesson", DARK_BLUE, v -> finish());

        savePracticeResult();
    }

    private void savePracticeResult() {
        if (resultSaved) return;

        resultSaved = true;

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this,
                    "Please login again to save progress.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        ProgressManager.saveQuizResult(
                uid,
                classId,
                chapterId,
                score,
                questions.size()
        );

        if (score > 0) {
            ProgressManager.addPoints(uid, score * 5);
        }

        ProgressManager.updateStreak(uid);
    }

    private String getQuestionType(Map<String, Object> data) {
        String type = getStringValue(data, "type");

        if (type.isEmpty()) {
            return "MCQ";
        }

        return type.trim().toUpperCase(Locale.ROOT);
    }

    private String getStringValue(
            Map<String, Object> data, String key) {

        Object value = data.get(key);

        return value == null ? "" : String.valueOf(value);
    }

    private LinearLayout createCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        card.setBackground(rounded(Color.WHITE,
                18, 18, 18, 18));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.bottomMargin = dp(16);
        card.setLayoutParams(params);

        return card;
    }

    private TextView text(
            String value, int size, int color, boolean bold) {

        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }

        return view;
    }

    private Button createButton(String label, int color) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextSize(14);
        button.setAllCaps(false);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextColor(color == Color.WHITE ? TEXT : Color.WHITE);

        button.setBackground(rounded(color, 12, 12, 12, 12));

        return button;
    }

    private void addButton(
            LinearLayout parent,
            String label,
            int color,
            View.OnClickListener listener) {

        Button button = createButton(label, color);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, dp(50));

        params.topMargin = dp(7);
        params.bottomMargin = dp(7);

        parent.addView(button, params);
        button.setOnClickListener(listener);
    }

    private GradientDrawable rounded(
            int color, int radius,
            int topLeft, int topRight,
            int bottomLeft) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);

        drawable.setCornerRadii(new float[]{
                dp(topLeft), dp(topLeft),
                dp(topRight), dp(topRight),
                dp(bottomLeft), dp(bottomLeft),
                dp(radius), dp(radius)
        });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        imageExecutor.shutdownNow();
    }
}
