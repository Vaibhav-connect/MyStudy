package com.mystudy.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudentPracticeActivity extends AppCompatActivity {

    private LinearLayout questionContainer;
    private TextView scoreText;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressManager progressManager;

    private String classId;
    private String className;
    private String subjectId;
    private String subjectName;
    private String chapterId;
    private String chapterName;
    private String lessonId;
    private String lessonTitle;

    private int score = 0;
    private int answered = 0;

    private boolean practiceFinished = false;

    private final List<String> questionIds =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");
        subjectId = getIntent().getStringExtra("subjectId");
        subjectName = getIntent().getStringExtra("subjectName");
        chapterId = getIntent().getStringExtra("chapterId");
        chapterName = getIntent().getStringExtra("chapterName");
        lessonId = getIntent().getStringExtra("lessonId");
        lessonTitle = getIntent().getStringExtra("lessonTitle");

        if (classId == null ||
                classId.trim().isEmpty() ||
                chapterId == null ||
                chapterId.trim().isEmpty()) {

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

        ScrollView scrollView =
                new ScrollView(this);

        scrollView.setFillViewport(true);

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                20,
                24,
                20,
                30
        );

        root.setBackgroundColor(
                Color.rgb(248, 250, 252)
        );

        TextView title =
                new TextView(this);

        title.setText(
                "🧠 Practice"
        );

        title.setTextSize(27);
        title.setTextColor(
                Color.rgb(17, 24, 39)
        );

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        title.setGravity(
                Gravity.CENTER_VERTICAL
        );

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1,
                        65
                )
        );

        TextView subtitle =
                new TextView(this);

        if (lessonTitle != null &&
                !lessonTitle.trim().isEmpty()) {

            subtitle.setText(
                    lessonTitle +
                            " • Test your knowledge"
            );

        } else if (chapterName != null &&
                !chapterName.trim().isEmpty()) {

            subtitle.setText(
                    chapterName +
                            " • Test your knowledge"
            );

        } else {

            subtitle.setText(
                    "Test your knowledge"
            );
        }

        subtitle.setTextSize(16);

        subtitle.setTextColor(
                Color.rgb(100, 116, 139)
        );

        subtitle.setPadding(
                0,
                0,
                0,
                16
        );

        root.addView(subtitle);

        scoreText =
                new TextView(this);

        scoreText.setText(
                "Score: 0"
        );

        scoreText.setTextSize(18);

        scoreText.setTextColor(
                Color.rgb(79, 70, 229)
        );

        scoreText.setTypeface(
                null,
                Typeface.BOLD
        );

        scoreText.setGravity(
                Gravity.CENTER
        );

        GradientDrawable scoreBackground =
                new GradientDrawable();

        scoreBackground.setColor(
                Color.WHITE
        );

        scoreBackground.setCornerRadius(
                24
        );

        scoreText.setBackground(
                scoreBackground
        );

        scoreText.setElevation(3);

        root.addView(
                scoreText,
                new LinearLayout.LayoutParams(
                        -1,
                        55
                )
        );

        questionContainer =
                new LinearLayout(this);

        questionContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams questionParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

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
                .whereEqualTo(
                        "classId",
                        classId
                )
                .whereEqualTo(
                        "chapterId",
                        chapterId
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            if (querySnapshot.isEmpty()) {

                                showMessage(
                                        "No practice questions available yet."
                                );

                                return;
                            }

                            for (
                                    QueryDocumentSnapshot document :
                                    querySnapshot
                            ) {

                                String questionId =
                                        document.getId();

                                String question =
                                        document.getString(
                                                "question"
                                        );

                                String type =
                                        document.getString(
                                                "type"
                                        );

                                String optionA =
                                        document.getString(
                                                "optionA"
                                        );

                                String optionB =
                                        document.getString(
                                                "optionB"
                                        );

                                String optionC =
                                        document.getString(
                                                "optionC"
                                        );

                                String optionD =
                                        document.getString(
                                                "optionD"
                                        );

                                String answer =
                                        document.getString(
                                                "answer"
                                        );

                                if (question == null ||
                                        question.trim().isEmpty()) {

                                    continue;
                                }

                                questionIds.add(
                                        questionId
                                );

                                addQuestionCard(
                                        questionId,
                                        question,
                                        type,
                                        optionA,
                                        optionB,
                                        optionC,
                                        optionD,
                                        answer
                                );
                            }

                            if (questionIds.isEmpty()) {

                                showMessage(
                                        "No valid practice questions available."
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        error -> {

                            showMessage(
                                    "Unable to load questions."
                            );

                            Toast.makeText(
                                    StudentPracticeActivity.this,
                                    "Unable to load questions",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }

    private void addQuestionCard(
            String questionId,
            String question,
            String type,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            String answer
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                20,
                20,
                20,
                20
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.WHITE
        );

        background.setCornerRadius(
                26
        );

        card.setBackground(
                background
        );

        card.setElevation(5);

        TextView questionNumber =
                new TextView(this);

        int number =
                questionIds.indexOf(questionId) + 1;

        questionNumber.setText(
                "Question " + number
        );

        questionNumber.setTextSize(13);
        questionNumber.setTextColor(
                Color.rgb(79, 70, 229)
        );

        questionNumber.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(
                questionNumber
        );

        TextView questionText =
                new TextView(this);

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
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        questionTextParams.topMargin = 8;

        card.addView(
                questionText,
                questionTextParams
        );

        if ("TRUE_FALSE".equalsIgnoreCase(type)) {

            addOption(
                    card,
                    "True",
                    answer,
                    questionId
            );

            addOption(
                    card,
                    "False",
                    answer,
                    questionId
            );

        } else {

            if (optionA != null &&
                    !optionA.trim().isEmpty()) {

                addOption(
                        card,
                        optionA,
                        answer,
                        questionId
                );
            }

            if (optionB != null &&
                    !optionB.trim().isEmpty()) {

                addOption(
                        card,
                        optionB,
                        answer,
                        questionId
                );
            }

            if (optionC != null &&
                    !optionC.trim().isEmpty()) {

                addOption(
                        card,
                        optionC,
                        answer,
                        questionId
                );
            }

            if (optionD != null &&
                    !optionD.trim().isEmpty()) {

                addOption(
                        card,
                        optionD,
                        answer,
                        questionId
                );
            }
        }

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

        questionContainer.addView(
                card,
                params
        );

        addPressAnimation(card);
    }

    private void addOption(
            LinearLayout card,
            String option,
            String correctAnswer,
            String questionId
    ) {

        Button button =
                new Button(this);

        button.setText(
                option
        );

        button.setAllCaps(false);

        button.setTextSize(15);

        button.setTextColor(
                Color.rgb(17, 24, 39)
        );

        button.setGravity(
                Gravity.CENTER
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(241, 245, 249)
        );

        background.setCornerRadius(
                24
        );

        button.setBackground(
                background
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        55
                );

        params.topMargin = 10;

        card.addView(
                button,
                params
        );

        addPressAnimation(button);

        button.setOnClickListener(
                view -> {

                    if (!button.isEnabled() ||
                            practiceFinished) {

                        return;
                    }

                    disableQuestionOptions(
                            card
                    );

                    answered++;

                    String selected =
                            option.trim();

                    String correct =
                            correctAnswer == null
                                    ? ""
                                    : correctAnswer.trim();

                    if (selected.equalsIgnoreCase(
                            correct
                    )) {

                        score++;

                        button.setText(
                                "✓ " + option
                        );

                        setButtonBackground(
                                button,
                                Color.rgb(
                                        34,
                                        197,
                                        94
                                )
                        );

                        button.setTextColor(
                                Color.WHITE
                        );

                        Toast.makeText(
                                StudentPracticeActivity.this,
                                "Correct! 🎉",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        button.setText(
                                "✗ " + option
                        );

                        setButtonBackground(
                                button,
                                Color.rgb(
                                        239,
                                        68,
                                        68
                                )
                        );

                        button.setTextColor(
                                Color.WHITE
                        );

                        Toast.makeText(
                                StudentPracticeActivity.this,
                                "Try the next one!",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    updateScore();

                    if (answered >=
                            questionIds.size()) {

                        finishPractice();
                    }
                }
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

        background.setColor(
                color
        );

        background.setCornerRadius(
                24
        );

        button.setBackground(
                background
        );
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

        if (user != null &&
                total > 0) {

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
                                    MotionEvent.ACTION_UP ||
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

        messageView.setText(
                message
        );

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

        lessonContainerSafeAdd(
                messageView
        );
    }

    private void lessonContainerSafeAdd(
            TextView messageView
    ) {

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
