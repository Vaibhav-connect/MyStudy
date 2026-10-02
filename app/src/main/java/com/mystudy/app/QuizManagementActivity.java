package com.mystudy.app;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private LinearLayout rootContainer;
    private LinearLayout quizContainer;
    private LinearLayout questionContainer;

    private final List<String> classIds = new ArrayList<>();
    private final List<String> classNames = new ArrayList<>();

    private final List<String> subjectIds = new ArrayList<>();
    private final List<String> subjectNames = new ArrayList<>();

    private final List<String> chapterIds = new ArrayList<>();
    private final List<String> chapterNames = new ArrayList<>();

    private final String[] mediums = {
            "English",
            "Semi-English",
            "Marathi",
            "Hindi"
    };

    private final String[] quizTypes = {
            "CHAPTER_TEST",
            "SUBJECT_TEST",
            "PRACTICE_QUIZ",
            "FULL_EXAM"
    };

    private final String[] difficulties = {
            "easy",
            "medium",
            "hard"
    };

    private final String[] questionTypes = {
            "MCQ",
            "TRUE_FALSE",
            "FILL_BLANK",
            "NUMERICAL",
            "SHORT_ANSWER",
            "SPELLING",
            "REARRANGE",
            "WORD_PROBLEM",
            "PICTURE_BASED",
            "MATCH"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        createUI();
        loadQuizzes();
        loadQuestions();
    }

    private void createUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 30, 24, 24);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("Quiz & Question Management");
        title.setTextSize(27);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 20);

        root.addView(title);

        Button createQuizButton = new Button(this);
        createQuizButton.setText("+ Create Quiz / Exam");
        createQuizButton.setAllCaps(false);
        createQuizButton.setTextSize(16);

        createQuizButton.setOnClickListener(
                v -> showCreateQuizDialog()
        );

        root.addView(
                createQuizButton,
                new LinearLayout.LayoutParams(-1, -2)
        );

        Button addQuestionButton = new Button(this);
        addQuestionButton.setText("+ Add Question");
        addQuestionButton.setAllCaps(false);
        addQuestionButton.setTextSize(16);

        addQuestionButton.setOnClickListener(
                v -> loadClassesForQuestion(false, null)
        );

        root.addView(
                addQuestionButton,
                new LinearLayout.LayoutParams(-1, -2)
        );

        TextView quizHeading = new TextView(this);
        quizHeading.setText("Created Quizzes / Exams");
        quizHeading.setTextSize(21);
        quizHeading.setTextColor(Color.rgb(17, 24, 39));
        quizHeading.setPadding(0, 25, 0, 10);

        root.addView(quizHeading);

        ScrollView quizScroll = new ScrollView(this);

        quizContainer = new LinearLayout(this);
        quizContainer.setOrientation(LinearLayout.VERTICAL);
        quizContainer.setPadding(0, 10, 0, 20);

        quizScroll.addView(quizContainer);

        root.addView(
                quizScroll,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        TextView questionHeading = new TextView(this);
        questionHeading.setText("Question Bank");
        questionHeading.setTextSize(21);
        questionHeading.setTextColor(Color.rgb(17, 24, 39));
        questionHeading.setPadding(0, 20, 0, 10);

        root.addView(questionHeading);

        ScrollView questionScroll = new ScrollView(this);

        questionContainer = new LinearLayout(this);
        questionContainer.setOrientation(LinearLayout.VERTICAL);
        questionContainer.setPadding(0, 10, 0, 20);

        questionScroll.addView(questionContainer);

        root.addView(
                questionScroll,
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

        rootContainer = root;

        setContentView(root);
    }

    // ============================================================
    // QUIZ MANAGEMENT
    // ============================================================

    private void loadQuizzes() {

        quizContainer.removeAllViews();

        db.collection("quizzes")
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.isEmpty()) {

                        TextView empty = new TextView(this);
                        empty.setText("No quizzes or exams created yet.");
                        empty.setTextSize(16);
                        empty.setGravity(Gravity.CENTER);
                        empty.setTextColor(
                                Color.rgb(100, 116, 139)
                        );
                        empty.setPadding(0, 30, 0, 30);

                        quizContainer.addView(empty);
                        return;
                    }

                    for (DocumentSnapshot doc :
                            snapshot.getDocuments()) {

                        String title = getStringValue(doc, "title");
                        String type = getStringValue(doc, "type");
                        String className = getStringValue(doc, "className");
                        String medium = getStringValue(doc, "medium");
                        String subjectName =
                                getStringValue(doc, "subjectName");
                        String chapterName =
                                getStringValue(doc, "chapterName");
                        String difficulty =
                                getStringValue(doc, "difficulty");

                        if (title.isEmpty()) {
                            title = "Untitled Quiz";
                        }

                        if (type.isEmpty()) {
                            type = "PRACTICE_QUIZ";
                        }

                        if (className.isEmpty()) {
                            className = "Unknown Class";
                        }

                        if (medium.isEmpty()) {
                            medium = "English";
                        }

                        if (subjectName.isEmpty()) {
                            subjectName = "All Subjects";
                        }

                        if (chapterName.isEmpty()) {
                            chapterName = "All Chapters";
                        }

                        if (difficulty.isEmpty()) {
                            difficulty = "easy";
                        }

                        Boolean published =
                                doc.getBoolean("published");

                        if (published == null) {
                            published = false;
                        }

                        Long totalMarks =
                                doc.getLong("totalMarks");

                        Long duration =
                                doc.getLong("durationMinutes");

                        if (totalMarks == null) {
                            totalMarks = 0L;
                        }

                        if (duration == null) {
                            duration = 0L;
                        }

                        addQuizCard(
                                doc.getId(),
                                title,
                                type,
                                className,
                                medium,
                                subjectName,
                                chapterName,
                                difficulty,
                                published,
                                totalMarks,
                                duration
                        );
                    }
                })
                .addOnFailureListener(
                        e -> showError(
                                quizContainer,
                                "Failed to load quizzes: "
                                        + e.getMessage()
                        )
                );
    }

    private void addQuizCard(
            String id,
            String title,
            String type,
            String className,
            String medium,
            String subjectName,
            String chapterName,
            String difficulty,
            boolean published,
            long totalMarks,
            long duration
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 20, 24, 20);
        card.setBackgroundColor(Color.WHITE);

        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(19);
        titleText.setTextColor(Color.rgb(17, 24, 39));

        TextView typeText = new TextView(this);
        typeText.setText("Type: " + type);

        TextView classText = new TextView(this);
        classText.setText(
                "Class: " + className + " • " + medium
        );

        TextView subjectText = new TextView(this);
        subjectText.setText("Subject: " + subjectName);

        TextView chapterText = new TextView(this);
        chapterText.setText("Chapter: " + chapterName);

        TextView detailsText = new TextView(this);
        detailsText.setText(
                "Marks: " + totalMarks +
                        " • Time: " + duration + " min"
        );

        TextView statusText = new TextView(this);
        statusText.setText(
                "Difficulty: " + difficulty +
                        " • " +
                        (published ? "Published" : "Draft")
        );

        typeText.setTextColor(Color.rgb(79, 70, 229));
        classText.setTextColor(Color.rgb(16, 185, 129));
        subjectText.setTextColor(Color.rgb(14, 116, 144));
        chapterText.setTextColor(Color.rgb(124, 58, 237));
        detailsText.setTextColor(Color.rgb(100, 116, 139));

        statusText.setTextColor(
                published
                        ? Color.rgb(22, 163, 74)
                        : Color.rgb(234, 88, 12)
        );

        card.addView(titleText);
        card.addView(typeText);
        card.addView(classText);
        card.addView(subjectText);
        card.addView(chapterText);
        card.addView(detailsText);
        card.addView(statusText);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        Button editButton = new Button(this);
        editButton.setText("Edit");
        editButton.setAllCaps(false);

        editButton.setOnClickListener(
                v -> loadClassesForQuizEdit(id)
        );

        Button publishButton = new Button(this);
        publishButton.setText(
                published ? "Unpublish" : "Publish"
        );
        publishButton.setAllCaps(false);

        final boolean currentPublished = published;

        publishButton.setOnClickListener(v -> {

            Map<String, Object> update =
                    new HashMap<>();

            update.put(
                    "published",
                    !currentPublished
            );

            update.put(
                    "updatedAt",
                    System.currentTimeMillis()
            );

            db.collection("quizzes")
                    .document(id)
                    .update(update)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                this,
                                currentPublished
                                        ? "Quiz unpublished"
                                        : "Quiz published",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadQuizzes();
                    })
                    .addOnFailureListener(
                            e -> Toast.makeText(
                                    this,
                                    "Failed: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show()
                    );
        });

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setAllCaps(false);

        deleteButton.setOnClickListener(
                v -> confirmDeleteQuiz(id, title)
        );

        row.addView(
                editButton,
                new LinearLayout.LayoutParams(0, -2, 1)
        );

        row.addView(
                publishButton,
                new LinearLayout.LayoutParams(0, -2, 1)
        );

        row.addView(
                deleteButton,
                new LinearLayout.LayoutParams(0, -2, 1)
        );

        card.addView(row);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 0, 0, 18);

        quizContainer.addView(card, params);
    }

    private void showCreateQuizDialog() {
        loadClassesForQuiz(false, null);
    }

    private void loadClassesForQuiz(
            boolean editMode,
            QuizEditData editData
    ) {

        db.collection("classes")
                .get()
                .addOnSuccessListener(snapshot -> {

                    classIds.clear();
                    classNames.clear();

                    for (DocumentSnapshot doc :
                            snapshot.getDocuments()) {

                        classIds.add(doc.getId());

                        String name =
                                doc.getString("name");

                        if (name == null) {
                            name = "Unnamed Class";
                        }

                        classNames.add(name);
                    }

                    if (classNames.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Please add a class first.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    showQuizDialog(
                            editMode,
                            editData
                    );
                })
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                "Failed to load classes: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void loadClassesForQuizEdit(String quizId) {

        db.collection("quizzes")
                .document(quizId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {

                        Toast.makeText(
                                this,
                                "Quiz not found.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    QuizEditData data =
                            new QuizEditData();

                    data.documentId = quizId;
                    data.title = getStringValue(doc, "title");
                    data.type = getStringValue(doc, "type");
                    data.className =
                            getStringValue(doc, "className");
                    data.medium =
                            getStringValue(doc, "medium");
                    data.subjectName =
                            getStringValue(doc, "subjectName");
                    data.chapterName =
                            getStringValue(doc, "chapterName");
                    data.difficulty =
                            getStringValue(doc, "difficulty");

                    Long marks =
                            doc.getLong("totalMarks");

                    Long duration =
                            doc.getLong("durationMinutes");

                    Long questionCount =
                            doc.getLong("questionCount");

                    data.totalMarks =
                            marks == null ? 0 : marks;

                    data.duration =
                            duration == null ? 10 : duration;

                    data.questionCount =
                            questionCount == null
                                    ? 10
                                    : questionCount.intValue();

                    Boolean published =
                            doc.getBoolean("published");

                    data.published =
                            published != null && published;

                    loadClassesForQuiz(
                            true,
                            data
                    );
                })
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                "Failed to load quiz: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void showQuizDialog(
            boolean editMode,
            QuizEditData editData
    ) {

        LinearLayout form =
                new LinearLayout(this);

        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(40, 10, 40, 10);

        ScrollView scrollView =
                new ScrollView(this);

        EditText titleInput =
                new EditText(this);

        titleInput.setHint("Quiz / Exam Title");

        Spinner typeSpinner =
                createSpinner(quizTypes);

        Spinner classSpinner =
                createSpinner(
                        classNames.toArray(new String[0])
                );

        Spinner mediumSpinner =
                createSpinner(mediums);

        Spinner subjectSpinner =
                new Spinner(this);

        Spinner chapterSpinner =
                new Spinner(this);

        EditText questionCountInput =
                new EditText(this);

        questionCountInput.setHint("Number of Questions");
        questionCountInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        EditText totalMarksInput =
                new EditText(this);

        totalMarksInput.setHint("Total Marks");
        totalMarksInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        EditText durationInput =
                new EditText(this);

        durationInput.setHint("Time in Minutes");
        durationInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        Spinner difficultySpinner =
                createSpinner(difficulties);

        form.addView(label("Title"));
        form.addView(titleInput);

        form.addView(label("Quiz Type"));
        form.addView(typeSpinner);

        form.addView(label("Class"));
        form.addView(classSpinner);

        form.addView(label("Medium"));
        form.addView(mediumSpinner);

        form.addView(label("Subject"));
        form.addView(subjectSpinner);

        form.addView(label("Chapter"));
        form.addView(chapterSpinner);

        form.addView(label("Number of Questions"));
        form.addView(questionCountInput);

        form.addView(label("Total Marks"));
        form.addView(totalMarksInput);

        form.addView(label("Time Limit"));
        form.addView(durationInput);

        form.addView(label("Difficulty"));
        form.addView(difficultySpinner);

        TextView info = new TextView(this);

        info.setText(
                "Quiz questions are taken from the Question Bank using Class, Medium, Subject and Chapter."
        );

        info.setTextSize(13);
        info.setTextColor(Color.rgb(100, 116, 139));
        info.setPadding(0, 15, 0, 15);

        form.addView(info);

        scrollView.addView(form);

        if (editMode && editData != null) {

            titleInput.setText(editData.title);

            questionCountInput.setText(
                    String.valueOf(editData.questionCount)
            );

            totalMarksInput.setText(
                    String.valueOf(editData.totalMarks)
            );

            durationInput.setText(
                    String.valueOf(editData.duration)
            );

            typeSpinner.setSelection(
                    findPosition(
                            quizTypes,
                            editData.type
                    )
            );

            mediumSpinner.setSelection(
                    findPosition(
                            mediums,
                            editData.medium
                    )
            );

            difficultySpinner.setSelection(
                    findPosition(
                            difficulties,
                            editData.difficulty
                    )
            );
        }

        classSpinner.setOnItemSelectedListener(
                new SimpleSelectionListener() {

                    @Override
                    public void selected(int position) {

                        if (position >= 0 &&
                                position < classIds.size()) {

                            loadSubjectsForQuiz(
                                    classIds.get(position),
                                    mediumSpinner,
                                    subjectSpinner,
                                    chapterSpinner,
                                    editMode,
                                    editData
                            );
                        }
                    }
                }
        );

        mediumSpinner.setOnItemSelectedListener(
                new SimpleSelectionListener() {

                    @Override
                    public void selected(int position) {

                        int classPosition =
                                classSpinner
                                        .getSelectedItemPosition();

                        if (classPosition >= 0 &&
                                classPosition <
                                        classIds.size()) {

                            loadSubjectsForQuiz(
                                    classIds.get(classPosition),
                                    mediumSpinner,
                                    subjectSpinner,
                                    chapterSpinner,
                                    editMode,
                                    editData
                            );
                        }
                    }
                }
        );

        subjectSpinner.setOnItemSelectedListener(
                new SimpleSelectionListener() {

                    @Override
                    public void selected(int position) {

                        if (position >= 0 &&
                                position < subjectIds.size()) {

                            loadChaptersForQuiz(
                                    classSpinner,
                                    subjectIds.get(position),
                                    mediumSpinner,
                                    chapterSpinner,
                                    editMode,
                                    editData
                            );
                        }
                    }
                }
        );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                editMode
                                        ? "Edit Quiz / Exam"
                                        : "Create Quiz / Exam"
                        )
                        .setView(scrollView)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                editMode ? "Save" : "Create",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                ignored -> {

                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener(v -> {

                        String title =
                                titleInput.getText()
                                        .toString()
                                        .trim();

                        if (title.isEmpty()) {

                            titleInput.setError(
                                    "Enter title"
                            );

                            return;
                        }

                        String questionCountText =
                                questionCountInput.getText()
                                        .toString()
                                        .trim();

                        String marksText =
                                totalMarksInput.getText()
                                        .toString()
                                        .trim();

                        String durationText =
                                durationInput.getText()
                                        .toString()
                                        .trim();

                        if (questionCountText.isEmpty() ||
                                marksText.isEmpty() ||
                                durationText.isEmpty()) {

                            Toast.makeText(
                                    this,
                                    "Fill all numeric fields.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        int questionCount;
                        long totalMarks;
                        long duration;

                        try {

                            questionCount =
                                    Integer.parseInt(
                                            questionCountText
                                    );

                            totalMarks =
                                    Long.parseLong(
                                            marksText
                                    );

                            duration =
                                    Long.parseLong(
                                            durationText
                                    );

                        } catch (Exception e) {

                            Toast.makeText(
                                    this,
                                    "Enter valid numbers.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        if (questionCount <= 0 ||
                                totalMarks <= 0 ||
                                duration <= 0) {

                            Toast.makeText(
                                    this,
                                    "Numbers must be greater than 0.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        int classPosition =
                                classSpinner
                                        .getSelectedItemPosition();

                        int subjectPosition =
                                subjectSpinner
                                        .getSelectedItemPosition();

                        int chapterPosition =
                                chapterSpinner
                                        .getSelectedItemPosition();

                        if (classPosition < 0 ||
                                classPosition >=
                                        classIds.size()) {

                            Toast.makeText(
                                    this,
                                    "Select class.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        String selectedClassId =
                                classIds.get(classPosition);

                        String selectedClassName =
                                classNames.get(classPosition);

                        String selectedMedium =
                                mediumSpinner
                                        .getSelectedItem()
                                        .toString();

                        String selectedSubjectId = "";
                        String selectedSubjectName =
                                "All Subjects";

                        if (subjectPosition >= 0 &&
                                subjectPosition <
                                        subjectIds.size()) {

                            selectedSubjectId =
                                    subjectIds.get(
                                            subjectPosition
                                    );

                            selectedSubjectName =
                                    subjectNames.get(
                                            subjectPosition
                                    );
                        }

                        String selectedChapterId = "";
                        String selectedChapterName =
                                "All Chapters";

                        if (chapterPosition >= 0 &&
                                chapterPosition <
                                        chapterIds.size()) {

                            selectedChapterId =
                                    chapterIds.get(
                                            chapterPosition
                                    );

                            selectedChapterName =
                                    chapterNames.get(
                                            chapterPosition
                                    );
                        }

                        Map<String, Object> data =
                                new HashMap<>();

                        data.put(
                                "title",
                                title
                        );

                        data.put(
                                "type",
                                typeSpinner
                                        .getSelectedItem()
                                        .toString()
                        );

                        data.put(
                                "classId",
                                selectedClassId
                        );

                        data.put(
                                "className",
                                selectedClassName
                        );

                        data.put(
                                "medium",
                                selectedMedium
                        );

                        data.put(
                                "subjectId",
                                selectedSubjectId
                        );

                        data.put(
                                "subjectName",
                                selectedSubjectName
                        );

                        data.put(
                                "chapterId",
                                selectedChapterId
                        );

                        data.put(
                                "chapterName",
                                selectedChapterName
                        );

                        data.put(
                                "questionCount",
                                questionCount
                        );

                        data.put(
                                "totalMarks",
                                totalMarks
                        );

                        data.put(
                                "durationMinutes",
                                duration
                        );

                        data.put(
                                "difficulty",
                                difficultySpinner
                                        .getSelectedItem()
                                        .toString()
                        );

                        data.put(
                                "published",
                                editData != null &&
                                        editData.published
                        );

                        data.put(
                                "updatedAt",
                                System.currentTimeMillis()
                        );

                        if (!editMode) {

                            data.put(
                                    "createdAt",
                                    System.currentTimeMillis()
                            );

                            db.collection("quizzes")
                                    .add(data)
                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        this,
                                                        "Quiz created successfully",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                dialog.dismiss();

                                                loadQuizzes();
                                            }
                                    )
                                    .addOnFailureListener(
                                            e -> Toast.makeText(
                                                    this,
                                                    "Failed: "
                                                            + e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show()
                                    );

                        } else {

                            if (editData == null) {
                                return;
                            }

                            db.collection("quizzes")
                                    .document(
                                            editData.documentId
                                    )
                                    .update(data)
                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        this,
                                                        "Quiz updated successfully",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                dialog.dismiss();

                                                loadQuizzes();
                                            }
                                    )
                                    .addOnFailureListener(
                                            e -> Toast.makeText(
                                                    this,
                                                    "Failed: "
                                                            + e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show()
                                    );
                        }
                    });
                }
        );

        dialog.show();
    }

    private void loadSubjectsForQuiz(
            String classId,
            Spinner mediumSpinner,
            Spinner subjectSpinner,
            Spinner chapterSpinner,
            boolean editMode,
            QuizEditData editData
    ) {

        int mediumPosition =
                mediumSpinner.getSelectedItemPosition();

        if (mediumPosition < 0 ||
                mediumPosition >= mediums.length) {
            return;
        }

        String medium = mediums[mediumPosition];

        db.collection("subjects")
                .whereEqualTo("classId", classId)
                .whereEqualTo("medium", medium)
                .get()
                .addOnSuccessListener(snapshot -> {

                    subjectIds.clear();
                    subjectNames.clear();

                    for (DocumentSnapshot doc :
                            snapshot.getDocuments()) {

                        subjectIds.add(doc.getId());

                        String name =
                                doc.getString("name");

                        if (name == null) {
                            name = "Unnamed Subject";
                        }

                        subjectNames.add(name);
                    }

                    setSpinner(
                            subjectSpinner,
                            subjectNames
                    );

                    if (editMode &&
                            editData != null) {

                        int position =
                                subjectNames.indexOf(
                                        editData.subjectName
                                );

                        if (position >= 0) {
                            subjectSpinner
                                    .setSelection(position);
                        }
                    }

                    if (subjectNames.isEmpty()) {

                        chapterIds.clear();
                        chapterNames.clear();

                        setSpinner(
                                chapterSpinner,
                                chapterNames
                        );
                    }
                })
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                "Failed to load subjects: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void loadChaptersForQuiz(
            Spinner classSpinner,
            String subjectId,
            Spinner mediumSpinner,
            Spinner chapterSpinner,
            boolean editMode,
            QuizEditData editData
    ) {

        int classPosition =
                classSpinner.getSelectedItemPosition();

        int mediumPosition =
                mediumSpinner.getSelectedItemPosition();

        if (classPosition < 0 ||
                classPosition >= classIds.size() ||
                mediumPosition < 0 ||
                mediumPosition >= mediums.length) {
            return;
        }

        String classId =
                classIds.get(classPosition);

        String medium =
                mediums[mediumPosition];

        db.collection("chapters")
                .whereEqualTo("classId", classId)
                .whereEqualTo("subjectId", subjectId)
                .whereEqualTo("medium", medium)
                .get()
                .addOnSuccessListener(snapshot -> {

                    chapterIds.clear();
                    chapterNames.clear();

                    for (DocumentSnapshot doc :
                            snapshot.getDocuments()) {

                        chapterIds.add(doc.getId());

                        String name =
                                doc.getString("name");

                        if (name == null) {
                            name = "Unnamed Chapter";
                        }

                        chapterNames.add(name);
                    }

                    setSpinner(
                            chapterSpinner,
                            chapterNames
                    );

                    if (editMode &&
                            editData != null) {

                        int position =
                                chapterNames.indexOf(
                                        editData.chapterName
                                );

                        if (position >= 0) {
                            chapterSpinner
                                    .setSelection(position);
                        }
                    }
                })
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                "Failed to load chapters: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    // ============================================================
    // QUESTION BANK
    // ============================================================

    private void loadQuestions() {

        questionContainer.removeAllViews();

        db.collection("questions")
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.isEmpty()) {

                        TextView empty = new TextView(this);

                        empty.setText(
                                "No questions in Question Bank yet."
                        );

                        empty.setTextSize(16);
                        empty.setGravity(Gravity.CENTER);
                        empty.setTextColor(
                                Color.rgb(100, 116, 139)
                        );

                        empty.setPadding(0, 30, 0, 30);

                        questionContainer.addView(empty);

                        return;
                    }

                    for (DocumentSnapshot doc :
                            snapshot.getDocuments()) {

                        addQuestionCard(doc);
                    }
                })
                .addOnFailureListener(
                        e -> showError(
                                questionContainer,
                                "Failed to load questions: "
                                        + e.getMessage()
                        )
                );
    }

    private void addQuestionCard(
            DocumentSnapshot doc
    ) {

        String id = doc.getId();

        String question =
                getStringValue(doc, "question");

        String type =
                getStringValue(doc, "type");

        String className =
                getStringValue(doc, "className");

        String medium =
                getStringValue(doc, "medium");

        String subjectName =
                getStringValue(doc, "subjectName");

        String chapterName =
                getStringValue(doc, "chapterName");

        String difficulty =
                getStringValue(doc, "difficulty");

        if (question.isEmpty()) {
            question = "Untitled Question";
        }

        if (type.isEmpty()) {
            type = "MCQ";
        }

        // IMPORTANT:
        // question is modified above, so create a final copy
        // before using it inside the lambda below.
        final String questionForDelete = question;

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 20, 24, 20);
        card.setBackgroundColor(Color.WHITE);

        TextView questionText =
                new TextView(this);

        questionText.setText(
                question
        );

        questionText.setTextSize(17);
        questionText.setTextColor(
                Color.rgb(17, 24, 39)
        );

        TextView details =
                new TextView(this);

        details.setText(
                "Type: " + type +
                        "\nClass: " + className +
                        " • " + medium +
                        "\nSubject: " + subjectName +
                        "\nChapter: " + chapterName +
                        "\nDifficulty: " + difficulty
        );

        details.setTextColor(
                Color.rgb(71, 85, 105)
        );

        details.setPadding(0, 10, 0, 10);

        card.addView(questionText);
        card.addView(details);

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        Button editButton =
                new Button(this);

        editButton.setText("Edit");
        editButton.setAllCaps(false);

        editButton.setOnClickListener(
                v -> loadClassesForQuestionEdit(id)
        );

        Button deleteButton =
                new Button(this);

        deleteButton.setText("Delete");
        deleteButton.setAllCaps(false);

        deleteButton.setOnClickListener(
                v -> confirmDeleteQuestion(
                        id,
                        questionForDelete
                )
        );

        row.addView(
                editButton,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        row.addView(
                deleteButton,
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                )
        );

        card.addView(row);

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
    }

    private void loadClassesForQuestion(
            boolean editMode,
            QuestionEditData editData
    ) {

        db.collection("classes")
                .get()
                .addOnSuccessListener(snapshot -> {

                    classIds.clear();
                    classNames.clear();

                    for (DocumentSnapshot doc :
                            snapshot.getDocuments()) {

                        classIds.add(doc.getId());

                        String name =
                                doc.getString("name");

                        if (name == null) {
                            name = "Unnamed Class";
                        }

                        classNames.add(name);
                    }

                    if (classNames.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Please add a class first.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    showQuestionDialog(
                            editMode,
                            editData
                    );
                })
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                "Failed to load classes: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void loadClassesForQuestionEdit(
            String questionId
    ) {

        db.collection("questions")
                .document(questionId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {

                        Toast.makeText(
                                this,
                                "Question not found.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    QuestionEditData data =
                            new QuestionEditData();

                    data.documentId = questionId;
                    data.question =
                            getStringValue(
                                    doc,
                                    "question"
                            );

                    data.type =
                            getStringValue(
                                    doc,
                                    "type"
                            );

                    data.className =
                            getStringValue(
                                    doc,
                                    "className"
                            );

                    data.medium =
                            getStringValue(
                                    doc,
                                    "medium"
                            );

                    data.subjectName =
                            getStringValue(
                                    doc,
                                    "subjectName"
                            );

                    data.chapterName =
                            getStringValue(
                                    doc,
                                    "chapterName"
                            );

                    data.difficulty =
                            getStringValue(
                                    doc,
                                    "difficulty"
                            );

                    data.optionA =
                            getStringValue(doc, "optionA");

                    data.optionB =
                            getStringValue(doc, "optionB");

                    data.optionC =
                            getStringValue(doc, "optionC");

                    data.optionD =
                            getStringValue(doc, "optionD");

                    data.correctAnswer =
                            getStringValue(
                                    doc,
                                    "correctAnswer"
                            );

                    data.answer =
                            getStringValue(
                                    doc,
                                    "answer"
                            );

                    data.explanation =
                            getStringValue(
                                    doc,
                                    "explanation"
                            );

                    data.imageUrl =
                            getStringValue(
                                    doc,
                                    "imageUrl"
                            );

                    loadClassesForQuestion(
                            true,
                            data
                    );
                })
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                "Failed to load question: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void showQuestionDialog(
            boolean editMode,
            QuestionEditData editData
    ) {

        LinearLayout form =
                new LinearLayout(this);

        form.setOrientation(
                LinearLayout.VERTICAL
        );

        form.setPadding(
                40,
                10,
                40,
                10
        );

        ScrollView scroll =
                new ScrollView(this);

        EditText questionInput =
                new EditText(this);

        questionInput.setHint(
                "Enter question"
        );

        questionInput.setGravity(
                Gravity.TOP
        );

        questionInput.setMinLines(3);

        Spinner typeSpinner =
                createSpinner(questionTypes);

        Spinner classSpinner =
                createSpinner(
                        classNames.toArray(
                                new String[0]
                        )
                );

        Spinner mediumSpinner =
                createSpinner(mediums);

        Spinner subjectSpinner =
                new Spinner(this);

        Spinner chapterSpinner =
                new Spinner(this);

        EditText optionA =
                createInput("Option A");

        EditText optionB =
                createInput("Option B");

        EditText optionC =
                createInput("Option C");

        EditText optionD =
                createInput("Option D");

        EditText correctAnswer =
                createInput(
                        "Correct Answer"
                );

        EditText answer =
                createInput(
                        "Answer / Solution"
                );

        EditText explanation =
                createInput(
                        "Explanation"
                );

        explanation.setGravity(
                Gravity.TOP
        );

        explanation.setMinLines(3);

        EditText imageUrl =
                createInput(
                        "Image URL (optional)"
                );

        EditText difficultyInput =
                createInput(
                        "Difficulty"
                );

        difficultyInput.setText("easy");

        form.addView(
                label("Question")
        );

        form.addView(questionInput);

        form.addView(
                label("Question Type")
        );

        form.addView(typeSpinner);

        form.addView(
                label("Class")
        );

        form.addView(classSpinner);

        form.addView(
                label("Medium")
        );

        form.addView(mediumSpinner);

        form.addView(
                label("Subject")
        );

        form.addView(subjectSpinner);

        form.addView(
                label("Chapter")
        );

        form.addView(chapterSpinner);

        form.addView(
                label("Option A")
        );

        form.addView(optionA);

        form.addView(
                label("Option B")
        );

        form.addView(optionB);

        form.addView(
                label("Option C")
        );

        form.addView(optionC);

        form.addView(
                label("Option D")
        );

        form.addView(optionD);

        form.addView(
                label("Correct Answer")
        );

        form.addView(correctAnswer);

        form.addView(
                label("Answer / Solution")
        );

        form.addView(answer);

        form.addView(
                label("Explanation")
        );

        form.addView(explanation);

        form.addView(
                label("Image URL")
        );

        form.addView(imageUrl);

        form.addView(
                label("Difficulty")
        );

        form.addView(difficultyInput);

        TextView info =
                new TextView(this);

        info.setText(
                "For MCQ use Options A-D and Correct Answer. Other question types can use Answer / Solution."
        );

        info.setTextSize(13);

        info.setTextColor(
                Color.rgb(100, 116, 139)
        );

        info.setPadding(
                0,
                15,
                0,
                15
        );

        form.addView(info);

        scroll.addView(form);

        if (editMode &&
                editData != null) {

            questionInput.setText(
                    editData.question
            );

            optionA.setText(
                    editData.optionA
            );

            optionB.setText(
                    editData.optionB
            );

            optionC.setText(
                    editData.optionC
            );

            optionD.setText(
                    editData.optionD
            );

            correctAnswer.setText(
                    editData.correctAnswer
            );

            answer.setText(
                    editData.answer
            );

            explanation.setText(
                    editData.explanation
            );

            imageUrl.setText(
                    editData.imageUrl
            );

            difficultyInput.setText(
                    editData.difficulty
            );

            int typePosition =
                    findPosition(
                            questionTypes,
                            editData.type
                    );

            if (typePosition >= 0) {
                typeSpinner.setSelection(
                        typePosition
                );
            }

            int mediumPosition =
                    findPosition(
                            mediums,
                            editData.medium
                    );

            if (mediumPosition >= 0) {
                mediumSpinner.setSelection(
                        mediumPosition
                );
            }
        }

        classSpinner.setOnItemSelectedListener(
                new SimpleSelectionListener() {

                    @Override
                    public void selected(
                            int position
                    ) {

                        if (position >= 0 &&
                                position <
                                        classIds.size()) {

                            loadQuestionSubjects(
                                    classIds.get(position),
                                    mediumSpinner,
                                    subjectSpinner,
                                    chapterSpinner,
                                    editMode,
                                    editData
                            );
                        }
                    }
                }
        );

        mediumSpinner.setOnItemSelectedListener(
                new SimpleSelectionListener() {

                    @Override
                    public void selected(
                            int position
                    ) {

                        int classPosition =
                                classSpinner
                                        .getSelectedItemPosition();

                        if (classPosition >= 0 &&
                                classPosition <
                                        classIds.size()) {

                            loadQuestionSubjects(
                                    classIds.get(
                                            classPosition
                                    ),
                                    mediumSpinner,
                                    subjectSpinner,
                                    chapterSpinner,
                                    editMode,
                                    editData
                            );
                        }
                    }
                }
        );

        subjectSpinner.setOnItemSelectedListener(
                new SimpleSelectionListener() {

                    @Override
                    public void selected(
                            int position
                    ) {

                        if (position >= 0 &&
                                position <
                                        subjectIds.size()) {

                            loadQuestionChapters(
                                    classSpinner,
                                    subjectIds.get(
                                            position
                                    ),
                                    mediumSpinner,
                                    chapterSpinner,
                                    editMode,
                                    editData
                            );
                        }
                    }
                }
        );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                editMode
                                        ? "Edit Question"
                                        : "Add Question"
                        )
                        .setView(scroll)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                editMode
                                        ? "Save"
                                        : "Add",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                ignored -> {

                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener(v -> {

                        String question =
                                questionInput
                                        .getText()
                                        .toString()
                                        .trim();

                        if (question.isEmpty()) {

                            questionInput.setError(
                                    "Enter question"
                            );

                            return;
                        }

                        int classPosition =
                                classSpinner
                                        .getSelectedItemPosition();

                        if (classPosition < 0 ||
                                classPosition >=
                                        classIds.size()) {

                            Toast.makeText(
                                    this,
                                    "Select class.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        int subjectPosition =
                                subjectSpinner
                                        .getSelectedItemPosition();

                        int chapterPosition =
                                chapterSpinner
                                        .getSelectedItemPosition();

                        String selectedClassId =
                                classIds.get(
                                        classPosition
                                );

                        String selectedClassName =
                                classNames.get(
                                        classPosition
                                );

                        String selectedMedium =
                                mediumSpinner
                                        .getSelectedItem()
                                        .toString();

                        String selectedSubjectId =
                                "";

                        String selectedSubjectName =
                                "";

                        if (subjectPosition >= 0 &&
                                subjectPosition <
                                        subjectIds.size()) {

                            selectedSubjectId =
                                    subjectIds.get(
                                            subjectPosition
                                    );

                            selectedSubjectName =
                                    subjectNames.get(
                                            subjectPosition
                                    );
                        }

                        String selectedChapterId =
                                "";

                        String selectedChapterName =
                                "";

                        if (chapterPosition >= 0 &&
                                chapterPosition <
                                        chapterIds.size()) {

                            selectedChapterId =
                                    chapterIds.get(
                                            chapterPosition
                                    );

                            selectedChapterName =
                                    chapterNames.get(
                                            chapterPosition
                                    );
                        }

                        String selectedType =
                                typeSpinner
                                        .getSelectedItem()
                                        .toString();

                        Map<String, Object> data =
                                new HashMap<>();

                        data.put(
                                "question",
                                question
                        );

                        data.put(
                                "type",
                                selectedType
                        );

                        data.put(
                                "classId",
                                selectedClassId
                        );

                        data.put(
                                "className",
                                selectedClassName
                        );

                        data.put(
                                "medium",
                                selectedMedium
                        );

                        data.put(
                                "subjectId",
                                selectedSubjectId
                        );

                        data.put(
                                "subjectName",
                                selectedSubjectName
                        );

                        data.put(
                                "chapterId",
                                selectedChapterId
                        );

                        data.put(
                                "chapterName",
                                selectedChapterName
                        );

                        data.put(
                                "optionA",
                                optionA.getText()
                                        .toString()
                                        .trim()
                        );

                        data.put(
                                "optionB",
                                optionB.getText()
                                        .toString()
                                        .trim()
                        );

                        data.put(
                                "optionC",
                                optionC.getText()
                                        .toString()
                                        .trim()
                        );

                        data.put(
                                "optionD",
                                optionD.getText()
                                        .toString()
                                        .trim()
                        );

                        data.put(
                                "correctAnswer",
                                correctAnswer
                                        .getText()
                                        .toString()
                                        .trim()
                        );

                        data.put(
                                "answer",
                                answer.getText()
                                        .toString()
                                        .trim()
                        );

                        data.put(
                                "explanation",
                                explanation
                                        .getText()
                                        .toString()
                                        .trim()
                        );

                        data.put(
                                "imageUrl",
                                imageUrl.getText()
                                        .toString()
                                        .trim()
                        );

                        data.put(
                                "difficulty",
                                difficultyInput
                                        .getText()
                                        .toString()
                                        .trim()
                        );

                        data.put(
                                "updatedAt",
                                System.currentTimeMillis()
                        );

                        if (!editMode) {

                            data.put(
                                    "createdAt",
                                    System.currentTimeMillis()
                            );

                            db.collection("questions")
                                    .add(data)
                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        this,
                                                        "Question added successfully",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                dialog.dismiss();

                                                loadQuestions();
                                            }
                                    )
                                    .addOnFailureListener(
                                            e ->
                                                    Toast.makeText(
                                                            this,
                                                            "Failed: "
                                                                    + e.getMessage(),
                                                            Toast.LENGTH_LONG
                                                    ).show()
                                    );

                        } else {

                            if (editData == null) {
                                return;
                            }

                            db.collection("questions")
                                    .document(
                                            editData.documentId
                                    )
                                    .update(data)
                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        this,
                                                        "Question updated successfully",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                dialog.dismiss();

                                                loadQuestions();
                                            }
                                    )
                                    .addOnFailureListener(
                                            e ->
                                                    Toast.makeText(
                                                            this,
                                                            "Failed: "
                                                                    + e.getMessage(),
                                                            Toast.LENGTH_LONG
                                                    ).show()
                                    );
                        }
                    });
                }
        );

        dialog.show();
    }

    private void loadQuestionSubjects(
            String classId,
            Spinner mediumSpinner,
            Spinner subjectSpinner,
            Spinner chapterSpinner,
            boolean editMode,
            QuestionEditData editData
    ) {

        int mediumPosition =
                mediumSpinner
                        .getSelectedItemPosition();

        if (mediumPosition < 0 ||
                mediumPosition >= mediums.length) {
            return;
        }

        String medium =
                mediums[mediumPosition];

        db.collection("subjects")
                .whereEqualTo(
                        "classId",
                        classId
                )
                .whereEqualTo(
                        "medium",
                        medium
                )
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            subjectIds.clear();
                            subjectNames.clear();

                            for (DocumentSnapshot doc :
                                    snapshot.getDocuments()) {

                                subjectIds.add(
                                        doc.getId()
                                );

                                String name =
                                        doc.getString(
                                                "name"
                                        );

                                if (name == null) {
                                    name =
                                            "Unnamed Subject";
                                }

                                subjectNames.add(name);
                            }

                            setSpinner(
                                    subjectSpinner,
                                    subjectNames
                            );

                            if (editMode &&
                                    editData != null) {

                                int position =
                                        subjectNames.indexOf(
                                                editData.subjectName
                                        );

                                if (position >= 0) {

                                    subjectSpinner
                                            .setSelection(
                                                    position
                                            );
                                }
                            }

                            if (subjectNames.isEmpty()) {

                                chapterIds.clear();
                                chapterNames.clear();

                                setSpinner(
                                        chapterSpinner,
                                        chapterNames
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                "Failed to load subjects: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void loadQuestionChapters(
            Spinner classSpinner,
            String subjectId,
            Spinner mediumSpinner,
            Spinner chapterSpinner,
            boolean editMode,
            QuestionEditData editData
    ) {

        int classPosition =
                classSpinner.getSelectedItemPosition();

        int mediumPosition =
                mediumSpinner.getSelectedItemPosition();

        if (classPosition < 0 ||
                classPosition >= classIds.size() ||
                mediumPosition < 0 ||
                mediumPosition >= mediums.length) {
            return;
        }

        String classId =
                classIds.get(classPosition);

        String medium =
                mediums[mediumPosition];

        db.collection("chapters")
                .whereEqualTo(
                        "classId",
                        classId
                )
                .whereEqualTo(
                        "subjectId",
                        subjectId
                )
                .whereEqualTo(
                        "medium",
                        medium
                )
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            chapterIds.clear();
                            chapterNames.clear();

                            for (DocumentSnapshot doc :
                                    snapshot.getDocuments()) {

                                chapterIds.add(
                                        doc.getId()
                                );

                                String name =
                                        doc.getString(
                                                "name"
                                        );

                                if (name == null) {
                                    name =
                                            "Unnamed Chapter";
                                }

                                chapterNames.add(name);
                            }

                            setSpinner(
                                    chapterSpinner,
                                    chapterNames
                            );

                            if (editMode &&
                                    editData != null) {

                                int position =
                                        chapterNames.indexOf(
                                                editData.chapterName
                                        );

                                if (position >= 0) {

                                    chapterSpinner
                                            .setSelection(
                                                    position
                                            );
                                }
                            }
                        }
                )
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                "Failed to load chapters: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    // ============================================================
    // DELETE
    // ============================================================

    private void confirmDeleteQuiz(
            String id,
            String title
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Quiz / Exam")
                .setMessage(
                        "Delete \"" + title + "\"?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            db.collection("quizzes")
                                    .document(id)
                                    .delete()
                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        this,
                                                        "Quiz deleted",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                loadQuizzes();
                                            }
                                    )
                                    .addOnFailureListener(
                                            e -> Toast.makeText(
                                                    this,
                                                    "Delete failed: "
                                                            + e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show()
                                    );
                        }
                )
                .show();
    }

    private void confirmDeleteQuestion(
            String id,
            final String question
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Question")
                .setMessage(
                        "Delete this question?\n\n"
                                + question
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            db.collection("questions")
                                    .document(id)
                                    .delete()
                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        this,
                                                        "Question deleted",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                loadQuestions();
                                            }
                                    )
                                    .addOnFailureListener(
                                            e -> Toast.makeText(
                                                    this,
                                                    "Delete failed: "
                                                            + e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show()
                                    );
                        }
                )
                .show();
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private Spinner createSpinner(
            String[] values
    ) {

        Spinner spinner =
                new Spinner(this);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        values
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);

        return spinner;
    }

    private void setSpinner(
            Spinner spinner,
            List<String> values
    ) {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        values
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }

    private EditText createInput(
            String hint
    ) {

        EditText input =
                new EditText(this);

        input.setHint(hint);

        return input;
    }

    private TextView label(
            String text
    ) {

        TextView label =
                new TextView(this);

        label.setText(text);
        label.setTextSize(13);
        label.setTextColor(
                Color.rgb(71, 85, 105)
        );

        label.setPadding(
                0,
                12,
                0,
                4
        );

        return label;
    }

    private String getStringValue(
            DocumentSnapshot doc,
            String field
    ) {

        String value =
                doc.getString(field);

        return value == null ? "" : value;
    }

    private int findPosition(
            String[] values,
            String target
    ) {

        if (target == null) {
            return -1;
        }

        for (int i = 0;
             i < values.length;
             i++) {

            if (values[i].equalsIgnoreCase(target)) {
                return i;
            }
        }

        return -1;
    }

    private void showError(
            LinearLayout container,
            String message
    ) {

        container.removeAllViews();

        TextView error =
                new TextView(this);

        error.setText(message);
        error.setTextSize(15);
        error.setTextColor(
                Color.rgb(220, 38, 38)
        );

        error.setGravity(Gravity.CENTER);
        error.setPadding(20, 30, 20, 30);

        container.addView(error);
    }

    private abstract static class SimpleSelectionListener
            implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(
                AdapterView<?> parent,
                View view,
                int position,
                long id
        ) {
            selected(position);
        }

        @Override
        public void onNothingSelected(
                AdapterView<?> parent
        ) {
        }

        public abstract void selected(
                int position
        );
    }

    private static class QuizEditData {

        String documentId;
        String title;
        String type;
        String className;
        String medium;
        String subjectName;
        String chapterName;
        String difficulty;

        int questionCount;
        long totalMarks;
        long duration;

        boolean published;
    }

    private static class QuestionEditData {

        String documentId;
        String question;
        String type;

        String className;
        String medium;
        String subjectName;
        String chapterName;
        String difficulty;

        String optionA;
        String optionB;
        String optionC;
        String optionD;

        String correctAnswer;
        String answer;
        String explanation;
        String imageUrl;
    }
}
