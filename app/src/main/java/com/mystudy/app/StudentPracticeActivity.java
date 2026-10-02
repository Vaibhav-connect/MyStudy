package com.mystudy.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudentPracticeActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private LinearLayout root;
    private TextView questionText;
    private TextView progressText;
    private RadioGroup optionsGroup;
    private Button nextButton;

    private final List<QueryDocumentSnapshot> questions =
            new ArrayList<>();

    private int currentQuestion = 0;
    private int score = 0;

    private String classId;
    private String chapterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classId = getIntent().getStringExtra("classId");
        chapterId = getIntent().getStringExtra("chapterId");

        if (classId == null || chapterId == null) {
            Toast.makeText(
                    this,
                    "Practice information missing",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        createUI();
        loadQuestions();
    }

    private void createUI() {

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(
                Color.rgb(248, 250, 252)
        );

        TextView title = new TextView(this);
        title.setText("🎯 Practice");
        title.setTextSize(26);
        title.setTextColor(
                Color.rgb(17, 24, 39)
        );
        title.setTypeface(
                null,
                Typeface.BOLD
        );

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1,
                        70
                )
        );

        progressText = new TextView(this);
        progressText.setTextSize(15);
        progressText.setTextColor(
                Color.rgb(100, 116, 139)
        );

        root.addView(
                progressText,
                new LinearLayout.LayoutParams(
                        -1,
                        45
                )
        );

        questionText = new TextView(this);
        questionText.setTextSize(20);
        questionText.setTextColor(
                Color.rgb(17, 24, 39)
        );
        questionText.setTypeface(
                null,
                Typeface.BOLD
        );
        questionText.setPadding(
                0,
                20,
                0,
                20
        );

        root.addView(questionText);

        optionsGroup = new RadioGroup(this);
        optionsGroup.setOrientation(
                RadioGroup.VERTICAL
        );

        root.addView(
                optionsGroup,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        nextButton = new Button(this);
        nextButton.setText("Next →");

        nextButton.setOnClickListener(
                view -> checkAnswer()
        );

        root.addView(
                nextButton,
                new LinearLayout.LayoutParams(
                        -1,
                        60
                )
        );

        setContentView(root);
    }

    private void loadQuestions() {

        db.collection("questions")
                .whereEqualTo("classId", classId)
                .whereEqualTo("chapterId", chapterId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    questions.clear();

                    for (QueryDocumentSnapshot document :
                            snapshot) {

                        questions.add(document);
                    }

                    if (questions.isEmpty()) {

                        questionText.setText(
                                "No practice questions available yet."
                        );

                        progressText.setText("");
                        nextButton.setEnabled(false);

                        return;
                    }

                    currentQuestion = 0;
                    score = 0;

                    showQuestion();
                })
                .addOnFailureListener(error -> {

                    Toast.makeText(
                            StudentPracticeActivity.this,
                            "Unable to load questions",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void showQuestion() {

        if (currentQuestion >= questions.size()) {
            showResult();
            return;
        }

        QueryDocumentSnapshot question =
                questions.get(currentQuestion);

        String questionValue =
                question.getString("question");

        if (questionValue == null ||
                questionValue.trim().isEmpty()) {

            questionValue = "Question";
        }

        questionText.setText(
                questionValue
        );

        progressText.setText(
                "Question "
                        + (currentQuestion + 1)
                        + " of "
                        + questions.size()
        );

        optionsGroup.removeAllViews();

        addOption(
                question.getString("optionA")
        );

        addOption(
                question.getString("optionB")
        );

        addOption(
                question.getString("optionC")
        );

        addOption(
                question.getString("optionD")
        );

        nextButton.setText(
                currentQuestion ==
                        questions.size() - 1
                        ? "Finish"
                        : "Next →"
        );
    }

    private void addOption(String value) {

        if (value == null ||
                value.trim().isEmpty()) {
            return;
        }

        RadioButton option =
                new RadioButton(this);

        option.setText(value);
        option.setTextSize(17);
        option.setTextColor(
                Color.rgb(31, 41, 55)
        );
        option.setPadding(
                12,
                12,
                12,
                12
        );

        optionsGroup.addView(
                option,
                new RadioGroup.LayoutParams(
                        -1,
                        -2
                )
        );
    }

    private void checkAnswer() {

        if (currentQuestion >= questions.size()) {
            return;
        }

        if (optionsGroup.getCheckedRadioButtonId()
                == -1) {

            Toast.makeText(
                    this,
                    "Please select an answer",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        RadioButton selected =
                findViewById(
                        optionsGroup
                                .getCheckedRadioButtonId()
                );

        String selectedAnswer =
                selected.getText().toString();

        String correctAnswer =
                questions
                        .get(currentQuestion)
                        .getString("answer");

        if (correctAnswer != null &&
                selectedAnswer.equalsIgnoreCase(
                        correctAnswer.trim()
                )) {

            score++;
        }

        currentQuestion++;

        showQuestion();
    }

    private void showResult() {

        root.removeAllViews();

        TextView resultTitle =
                new TextView(this);

        resultTitle.setText(
                "🎉 Practice Complete!"
        );

        resultTitle.setTextSize(26);
        resultTitle.setTextColor(
                Color.rgb(17, 24, 39)
        );
        resultTitle.setTypeface(
                null,
                Typeface.BOLD
        );
        resultTitle.setGravity(
                Gravity.CENTER
        );

        root.addView(
                resultTitle,
                new LinearLayout.LayoutParams(
                        -1,
                        120
                )
        );

        TextView scoreText =
                new TextView(this);

        scoreText.setText(
                "Your Score: "
                        + score
                        + " / "
                        + questions.size()
        );

        scoreText.setTextSize(22);
        scoreText.setTextColor(
                Color.rgb(79, 70, 229)
        );
        scoreText.setGravity(
                Gravity.CENTER
        );

        root.addView(
                scoreText,
                new LinearLayout.LayoutParams(
                        -1,
                        100
                )
        );

        Button doneButton =
                new Button(this);

        doneButton.setText(
                "Done"
        );

        doneButton.setOnClickListener(
                view -> finish()
        );

        root.addView(
                doneButton,
                new LinearLayout.LayoutParams(
                        -1,
                        60
                )
        );
    }
}
