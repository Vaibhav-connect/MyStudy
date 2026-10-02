package com.mystudy.app;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
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
        data.put("completedAt",
                System.currentTimeMillis());

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

        Map<String, Object> data =
                new HashMap<>();

        data.put("userId", userId);
        data.put("classId", classId);
        data.put("chapterId", chapterId);
        data.put("score", score);
        data.put("totalQuestions", totalQuestions);

        double accuracy = 0;

        if (totalQuestions > 0) {
            accuracy =
                    (score * 100.0) /
                            totalQuestions;
        }

        data.put("accuracy", accuracy);
        data.put(
                "completedAt",
                System.currentTimeMillis()
        );

        db.collection("quizProgress")
                .add(data);
    }

    public void addPoints(
            String userId,
            int points
    ) {

        if (userId == null || userId.trim().isEmpty()) {
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
                                    com.google.firebase.firestore.SetOptions.merge()
                            );
                });
    }

    public void updateStreak(
            String userId
    ) {

        if (userId == null || userId.trim().isEmpty()) {
            return;
        }

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {

                    long streak = 0;

                    if (document.exists()) {

                        Long value =
                                document.getLong("streak");

                        if (value != null) {
                            streak = value;
                        }
                    }

                    Map<String, Object> update =
                            new HashMap<>();

                    update.put(
                            "streak",
                            streak + 1
                    );

                    db.collection("users")
                            .document(userId)
                            .set(
                                    update,
                                    com.google.firebase.firestore.SetOptions.merge()
                            );
                });
    }
}
