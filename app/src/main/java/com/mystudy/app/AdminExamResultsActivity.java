package com.mystudy.app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminExamResultsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout resultContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        createUI();
        loadExamResults();
    }

    private void createUI() {

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                24,
                30,
                24,
                24
        );

        root.setBackgroundColor(
                Color.rgb(248, 250, 252)
        );

        TextView title =
                new TextView(this);

        title.setText(
                "📊 Exam Results"
        );

        title.setTextSize(28);

        title.setTextColor(
                Color.rgb(17, 24, 39)
        );

        title.setGravity(
                Gravity.CENTER
        );

        title.setPadding(
                0,
                0,
                0,
                8
        );

        root.addView(title);

        TextView subtitle =
                new TextView(this);

        subtitle.setText(
                "Student exam performance"
        );

        subtitle.setTextSize(15);

        subtitle.setTextColor(
                Color.rgb(100, 116, 139)
        );

        subtitle.setGravity(
                Gravity.CENTER
        );

        subtitle.setPadding(
                0,
                0,
                0,
                20
        );

        root.addView(subtitle);

        ScrollView scrollView =
                new ScrollView(this);

        resultContainer =
                new LinearLayout(this);

        resultContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        resultContainer.setPadding(
                0,
                10,
                0,
                20
        );

        scrollView.addView(
                resultContainer
        );

        root.addView(
                scrollView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        Button backButton =
                new Button(this);

        backButton.setText(
                "Back"
        );

        backButton.setAllCaps(false);

        backButton.setOnClickListener(
                v -> finish()
        );

        root.addView(
                backButton
        );

        setContentView(root);
    }

    private void loadExamResults() {

        resultContainer.removeAllViews();

        db.collection("examResults")
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            if (snapshot.isEmpty()) {

                                showEmptyMessage(
                                        "No exam results available yet."
                                );

                                return;
                            }

                            for (
                                    DocumentSnapshot document :
                                    snapshot.getDocuments()
                            ) {

                                addResultCard(
                                        document
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    this,
                                    "Failed to load results: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }

    private void addResultCard(
            DocumentSnapshot document
    ) {

        String studentName =
                getStringValue(
                        document,
                        "studentName",
                        "Unknown Student"
                );

        String quizTitle =
                getStringValue(
                        document,
                        "quizTitle",
                        "Exam"
                );

        String quizId =
                getStringValue(
                        document,
                        "quizId",
                        ""
                );

        String userId =
                getStringValue(
                        document,
                        "userId",
                        ""
                );

        long score =
                getLongValue(
                        document,
                        "score",
                        0
                );

        long totalMarks =
                getLongValue(
                        document,
                        "totalMarks",
                        0
                );

        long correctAnswers =
                getLongValue(
                        document,
                        "correctAnswers",
                        0
                );

        long wrongAnswers =
                getLongValue(
                        document,
                        "wrongAnswers",
                        0
                );

        long totalQuestions =
                getLongValue(
                        document,
                        "totalQuestions",
                        0
                );

        long percentage = 0;

        if (totalMarks > 0) {

            percentage =
                    Math.round(
                            (score * 100.0)
                                    / totalMarks
                    );
        }

        long submittedAt =
                getLongValue(
                        document,
                        "submittedAt",
                        0
                );

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                24,
                20,
                24,
                20
        );

        card.setBackgroundColor(
                Color.WHITE
        );

        TextView studentText =
                new TextView(this);

        studentText.setText(
                "👨‍🎓 " +
                        studentName
        );

        studentText.setTextSize(20);

        studentText.setTextColor(
                Color.rgb(17, 24, 39)
        );

        TextView examText =
                new TextView(this);

        examText.setText(
                "📝 " +
                        quizTitle
        );

        examText.setTextSize(16);

        examText.setTextColor(
                Color.rgb(79, 70, 229)
        );

        examText.setPadding(
                0,
                8,
                0,
                8
        );

        TextView scoreText =
                new TextView(this);

        scoreText.setText(
                "Score: " +
                        score +
                        " / " +
                        totalMarks
        );

        scoreText.setTextSize(16);

        scoreText.setTextColor(
                Color.rgb(22, 101, 52)
        );

        TextView percentageText =
                new TextView(this);

        percentageText.setText(
                "Percentage: " +
                        percentage +
                        "%"
        );

        percentageText.setTextSize(16);

        percentageText.setTextColor(
                Color.rgb(17, 24, 39)
        );

        TextView answersText =
                new TextView(this);

        answersText.setText(
                "Correct: " +
                        correctAnswers +
                        "   Wrong: " +
                        wrongAnswers
        );

        answersText.setTextSize(14);

        answersText.setTextColor(
                Color.rgb(71, 85, 105)
        );

        TextView questionsText =
                new TextView(this);

        questionsText.setText(
                "Questions: " +
                        totalQuestions
        );

        questionsText.setTextSize(14);

        questionsText.setTextColor(
                Color.rgb(71, 85, 105)
        );

        TextView dateText =
                new TextView(this);

        dateText.setText(
                "Submitted: " +
                        formatDate(submittedAt)
        );

        dateText.setTextSize(13);

        dateText.setTextColor(
                Color.rgb(100, 116, 139)
        );

        card.addView(studentText);
        card.addView(examText);
        card.addView(scoreText);
        card.addView(percentageText);
        card.addView(answersText);
        card.addView(questionsText);
        card.addView(dateText);

        if (!quizId.isEmpty()) {

            TextView quizIdText =
                    new TextView(this);

            quizIdText.setText(
                    "Quiz ID: " +
                            quizId
            );

            quizIdText.setTextSize(11);

            quizIdText.setTextColor(
                    Color.rgb(148, 163, 184)
            );

            quizIdText.setPadding(
                    0,
                    6,
                    0,
                    0
            );

            card.addView(
                    quizIdText
            );
        }

        if (!userId.isEmpty()) {

            TextView userIdText =
                    new TextView(this);

            userIdText.setText(
                    "Student ID: " +
                            userId
            );

            userIdText.setTextSize(10);

            userIdText.setTextColor(
                    Color.rgb(148, 163, 184)
            );

            userIdText.setPadding(
                    0,
                    4,
                    0,
                    0
            );

            card.addView(
                    userIdText
            );
        }

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                0,
                0,
                18
        );

        resultContainer.addView(
                card,
                params
        );
    }

    private void showEmptyMessage(
            String message
    ) {

        TextView empty =
                new TextView(this);

        empty.setText(
                message
        );

        empty.setTextSize(16);

        empty.setTextColor(
                Color.rgb(100, 116, 139)
        );

        empty.setGravity(
                Gravity.CENTER
        );

        empty.setPadding(
                0,
                50,
                0,
                50
        );

        resultContainer.addView(
                empty
        );
    }

    private String getStringValue(
            DocumentSnapshot document,
            String field,
            String defaultValue
    ) {

        String value =
                document.getString(
                        field
                );

        if (value == null ||
                value.trim().isEmpty()) {

            return defaultValue;
        }

        return value;
    }

    private long getLongValue(
            DocumentSnapshot document,
            String field,
            long defaultValue
    ) {

        Long value =
                document.getLong(
                        field
                );

        if (value == null) {

            return defaultValue;
        }

        return value;
    }

    private String formatDate(
            long timestamp
    ) {

        if (timestamp <= 0) {

            return "Unknown";
        }

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "dd MMM yyyy, hh:mm a",
                        Locale.getDefault()
                );

        return format.format(
                new Date(timestamp)
        );
    }
}
