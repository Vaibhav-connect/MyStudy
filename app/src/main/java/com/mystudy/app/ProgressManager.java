package com.mystudy.app;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProgressManager {

    private final FirebaseFirestore db;

    public ProgressManager() {
        db = FirebaseFirestore.getInstance();
    }

    public void saveLessonCompleted(
            String userId,
            String classId,
            String subjectId,
            String chapterId,
            String lessonId
    ) {

        if (userId == null || userId.trim().isEmpty()) {
            return;
        }

        String progressId =
                userId + "_" + lessonId;

        Map<String, Object> data =
                new HashMap<>();

        data.put("userId", userId);
        data.put("classId", classId);
        data.put("subjectId", subjectId);
        data.put("chapterId", chapterId);
        data.put("lessonId", lessonId);
        data.put("completed", true);
        data.put(
                "completedAt",
                System.currentTimeMillis()
        );

        db.collection("lessonProgress")
                .document(progressId)
                .set(data);
    }

    public void saveQuizResult(
            String userId,
            String classId,
            String chapterId,
            int score,
            int totalQuestions
    ) {

        if (userId == null || userId.trim().isEmpty()) {
            return;
        }

        double accuracy = 0;

        if (totalQuestions > 0) {
            accuracy =
                    (score * 100.0) /
                            totalQuestions;
        }

        Map<String, Object> data =
                new HashMap<>();

        data.put("userId", userId);
        data.put("classId", classId);
        data.put("chapterId", chapterId);
        data.put("score", score);
        data.put("totalQuestions", totalQuestions);
        data.put("accuracy", accuracy);
        data.put(
                "completedAt",
                System.currentTimeMillis()
        );

        db.collection("quizProgress")
                .add(data);

        updateAccuracyAndWeakTopic(
                userId,
                chapterId,
                score,
                totalQuestions,
                accuracy
        );
    }

    private void updateAccuracyAndWeakTopic(
            String userId,
            String chapterId,
            int score,
            int totalQuestions,
            double quizAccuracy
    ) {

        if (userId == null ||
                userId.trim().isEmpty()) {
            return;
        }

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {

                    long totalCorrect = 0;
                    long totalQuestionsDone = 0;
                    long totalQuizAttempts = 0;

                    if (document.exists()) {

                        Long correct =
                                document.getLong(
                                        "totalCorrectAnswers"
                                );

                        Long questions =
                                document.getLong(
                                        "totalQuestionsAnswered"
                                );

                        Long attempts =
                                document.getLong(
                                        "totalQuizAttempts"
                                );

                        if (correct != null) {
                            totalCorrect = correct;
                        }

                        if (questions != null) {
                            totalQuestionsDone = questions;
                        }

                        if (attempts != null) {
                            totalQuizAttempts = attempts;
                        }
                    }

                    totalCorrect += score;
                    totalQuestionsDone += totalQuestions;
                    totalQuizAttempts++;

                    double overallAccuracy = 0;

                    if (totalQuestionsDone > 0) {
                        overallAccuracy =
                                (totalCorrect * 100.0)
                                        / totalQuestionsDone;
                    }

                    Map<String, Object> update =
                            new HashMap<>();

                    update.put(
                            "totalCorrectAnswers",
                            totalCorrect
                    );

                    update.put(
                            "totalQuestionsAnswered",
                            totalQuestionsDone
                    );

                    update.put(
                            "totalQuizAttempts",
                            totalQuizAttempts
                    );

                    update.put(
                            "overallAccuracy",
                            overallAccuracy
                    );

                    update.put(
                            "lastQuizAccuracy",
                            quizAccuracy
                    );

                    if (chapterId != null &&
                            !chapterId.trim().isEmpty()) {

                        String weakStatus;

                        if (quizAccuracy < 60) {
                            weakStatus = "weak";
                        } else if (quizAccuracy < 80) {
                            weakStatus = "needs_practice";
                        } else {
                            weakStatus = "good";
                        }

                        Map<String, Object> weakTopics =
                                new HashMap<>();

                        Object existing =
                                document.get("weakTopics");

                        if (existing instanceof Map) {

                            Map<?, ?> existingMap =
                                    (Map<?, ?>) existing;

                            for (Map.Entry<?, ?> entry :
                                    existingMap.entrySet()) {

                                if (entry.getKey() != null &&
                                        entry.getValue() != null) {

                                    weakTopics.put(
                                            String.valueOf(
                                                    entry.getKey()
                                            ),
                                            entry.getValue()
                                    );
                                }
                            }
                        }

                        Map<String, Object> topicData =
                                new HashMap<>();

                        topicData.put(
                                "accuracy",
                                quizAccuracy
                        );

                        topicData.put(
                                "status",
                                weakStatus
                        );

                        topicData.put(
                                "lastAttemptAt",
                                System.currentTimeMillis()
                        );

                        weakTopics.put(
                                chapterId,
                                topicData
                        );

                        update.put(
                                "weakTopics",
                                weakTopics
                        );
                    }

                    db.collection("users")
                            .document(userId)
                            .set(
                                    update,
                                    SetOptions.merge()
                            );
                });
    }

    public void addPoints(
            String userId,
            int points
    ) {

        if (userId == null ||
                userId.trim().isEmpty()) {
            return;
        }

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {

                    long currentPoints = 0;

                    if (document.exists()) {

                        Long value =
                                document.getLong("points");

                        if (value != null) {
                            currentPoints = value;
                        }
                    }

                    Map<String, Object> update =
                            new HashMap<>();

                    update.put(
                            "points",
                            currentPoints + points
                    );

                    db.collection("users")
                            .document(userId)
                            .set(
                                    update,
                                    SetOptions.merge()
                            );
                });
    }

    public void updateStreak(
            String userId
    ) {

        if (userId == null ||
                userId.trim().isEmpty()) {
            return;
        }

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {

                    String today =
                            new SimpleDateFormat(
                                    "yyyy-MM-dd",
                                    Locale.getDefault()
                            ).format(
                                    new Date()
                            );

                    String lastActiveDate = null;
                    long streak = 0;

                    if (document.exists()) {

                        lastActiveDate =
                                document.getString(
                                        "lastActiveDate"
                                );

                        Long value =
                                document.getLong("streak");

                        if (value != null) {
                            streak = value;
                        }
                    }

                    Map<String, Object> update =
                            new HashMap<>();

                    if (today.equals(lastActiveDate)) {

                        update.put(
                                "lastActiveDate",
                                today
                        );

                    } else {

                        boolean continueStreak = false;

                        if (lastActiveDate != null) {

                            try {

                                Date lastDate =
                                        new SimpleDateFormat(
                                                "yyyy-MM-dd",
                                                Locale.getDefault()
                                        ).parse(
                                                lastActiveDate
                                        );

                                Date todayDate =
                                        new SimpleDateFormat(
                                                "yyyy-MM-dd",
                                                Locale.getDefault()
                                        ).parse(
                                                today
                                        );

                                long difference =
                                        todayDate.getTime()
                                                - lastDate.getTime();

                                long days =
                                        difference /
                                                (1000L * 60L * 60L * 24L);

                                if (days == 1) {
                                    continueStreak = true;
                                }

                            } catch (Exception ignored) {
                            }
                        }

                        if (continueStreak) {
                            streak++;
                        } else {
                            streak = 1;
                        }

                        update.put(
                                "streak",
                                streak
                        );

                        update.put(
                                "lastActiveDate",
                                today
                        );
                    }

                    db.collection("users")
                            .document(userId)
                            .set(
                                    update,
                                    SetOptions.merge()
                            );
                });
    }
}
