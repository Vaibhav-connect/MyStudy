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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentExamHistoryActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private LinearLayout container;

    private final List<ResultData> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        createUI();
        loadHistory();
    }

    private void createUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 28, 24, 24);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("Exam History");
        title.setTextSize(27);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 8);

        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Your previous exam and quiz results");
        subtitle.setTextSize(14);
        subtitle.setTextColor(Color.rgb(100, 116, 139));
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 0, 0, 20);

        root.addView(subtitle);

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

    private void loadHistory() {

        container.removeAllViews();
        results.clear();

        if (auth.getCurrentUser() == null) {

            showMessage("Please login again.");
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        db.collection("examResults")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        ResultData result = new ResultData();

                        result.id = doc.getId();

                        result.quizTitle = value(
                                doc.getString("quizTitle"),
                                "Quiz"
                        );

                        result.className = value(
                                doc.getString("className"),
                                ""
                        );

                        result.medium = value(
                                doc.getString("medium"),
                                "English"
                        );

                        result.subjectName = value(
                                doc.getString("subjectName"),
                                "Subject"
                        );

                        result.chapterName = value(
                                doc.getString("chapterName"),
                                "Chapter"
                        );

                        Long score = doc.getLong("score");
                        Long totalMarks = doc.getLong("totalMarks");
                        Long answered = doc.getLong("answered");
                        Long totalQuestions = doc.getLong("totalQuestions");
                        Double accuracy = doc.getDouble("accuracy");
                        Long completedAt = doc.getLong("completedAt");

                        result.score =
                                score != null ? score : 0L;

                        result.totalMarks =
                                totalMarks != null ? totalMarks : 0L;

                        result.answered =
                                answered != null ? answered : 0L;

                        result.totalQuestions =
                                totalQuestions != null
                                        ? totalQuestions
                                        : 0L;

                        result.accuracy =
                                accuracy != null ? accuracy : 0.0;

                        result.completedAt =
                                completedAt != null
                                        ? completedAt
                                        : 0L;

                        results.add(result);
                    }

                    results.sort(
                            (a, b) ->
                                    Long.compare(
                                            b.completedAt,
                                            a.completedAt
                                    )
                    );

                    if (results.isEmpty()) {

                        showMessage(
                                "No exam history available yet."
                        );

                        return;
                    }

                    addSummary();

                    for (ResultData result : results) {
                        addResultCard(result);
                    }
                })
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                "Failed to load history: " +
                                        e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void addSummary() {

        int totalAttempts = results.size();

        long totalScore = 0;
        long totalMarks = 0;

        double accuracyTotal = 0;

        for (ResultData result : results) {

            totalScore += result.score;
            totalMarks += result.totalMarks;
            accuracyTotal += result.accuracy;
        }

        double averageAccuracy =
                totalAttempts > 0
                        ? accuracyTotal / totalAttempts
                        : 0;

        LinearLayout card = createCard();

        TextView heading = new TextView(this);
        heading.setText("Overall Performance");
        heading.setTextSize(20);
        heading.setTextColor(Color.rgb(17, 24, 39));

        TextView attempts = createInfoText(
                "Total Attempts: " + totalAttempts
        );

        TextView score = createInfoText(
                "Total Score: " +
                        totalScore +
                        " / " +
                        totalMarks
        );

        TextView accuracy = createInfoText(
                String.format(
                        Locale.US,
                        "Average Accuracy: %.1f%%",
                        averageAccuracy
                )
        );

        card.addView(heading);
        card.addView(attempts);
        card.addView(score);
        card.addView(accuracy);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.setMargins(0, 0, 0, 18);

        container.addView(card, params);
    }

    private void addResultCard(ResultData result) {

        LinearLayout card = createCard();

        TextView title = new TextView(this);
        title.setText(result.quizTitle);
        title.setTextSize(19);
        title.setTextColor(Color.rgb(17, 24, 39));

        TextView subject = createInfoText(
                "Subject: " + result.subjectName
        );

        TextView chapter = createInfoText(
                "Chapter: " + result.chapterName
        );

        TextView classInfo = createInfoText(
                "Class: " +
                        result.className +
                        " • Medium: " +
                        result.medium
        );

        TextView score = createInfoText(
                "Score: " +
                        result.score +
                        " / " +
                        result.totalMarks
        );

        score.setTextColor(
                Color.rgb(79, 70, 229)
        );

        TextView questions = createInfoText(
                "Answered: " +
                        result.answered +
                        " / " +
                        result.totalQuestions
        );

        TextView accuracy = createInfoText(
                String.format(
                        Locale.US,
                        "Accuracy: %.1f%%",
                        result.accuracy
                )
        );

        if (result.accuracy >= 80) {

            accuracy.setTextColor(
                    Color.rgb(22, 163, 74)
            );

        } else if (result.accuracy >= 50) {

            accuracy.setTextColor(
                    Color.rgb(202, 138, 4)
            );

        } else {

            accuracy.setTextColor(
                    Color.rgb(220, 38, 38)
            );
        }

        TextView date = createInfoText(
                "Completed: " +
                        formatDate(result.completedAt)
        );

        card.addView(title);
        card.addView(subject);
        card.addView(chapter);
        card.addView(classInfo);
        card.addView(score);
        card.addView(questions);
        card.addView(accuracy);
        card.addView(date);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.setMargins(0, 0, 0, 16);

        container.addView(card, params);
    }

    private LinearLayout createCard() {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                22,
                20,
                22,
                20
        );

        card.setBackgroundColor(
                Color.WHITE
        );

        return card;
    }

    private TextView createInfoText(
            String text
    ) {

        TextView view = new TextView(this);

        view.setText(text);
        view.setTextSize(14);
        view.setTextColor(
                Color.rgb(100, 116, 139)
        );

        view.setPadding(
                0,
                6,
                0,
                2
        );

        return view;
    }

    private void showMessage(
            String message
    ) {

        TextView text = new TextView(this);

        text.setText(message);
        text.setTextSize(16);
        text.setTextColor(
                Color.rgb(100, 116, 139)
        );

        text.setGravity(Gravity.CENTER);

        text.setPadding(
                20,
                80,
                20,
                80
        );

        container.addView(
                text,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );
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

    private static class ResultData {

        String id = "";
        String quizTitle = "";
        String className = "";
        String medium = "";
        String subjectName = "";
        String chapterName = "";

        long score = 0;
        long totalMarks = 0;
        long answered = 0;
        long totalQuestions = 0;

        double accuracy = 0;

        long completedAt = 0;
    }
}
