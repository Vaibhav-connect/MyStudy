package com.mystudy.app;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
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
    private LinearLayout quizContainer;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        createUI();
        loadQuizzes();
    }

    private void createUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 30, 24, 24);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("Quiz & Exam Management");
        title.setTextSize(27);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 20);

        root.addView(title);

        Button addButton = new Button(this);
        addButton.setText("+ Create Quiz / Exam");
        addButton.setAllCaps(false);
        addButton.setTextSize(16);

        addButton.setOnClickListener(v ->
                showCreateQuizDialog()
        );

        root.addView(
                addButton,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        ScrollView scrollView = new ScrollView(this);

        quizContainer = new LinearLayout(this);
        quizContainer.setOrientation(
                LinearLayout.VERTICAL
        );
        quizContainer.setPadding(
                0,
                20,
                0,
                20
        );

        scrollView.addView(quizContainer);

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

        backButton.setOnClickListener(v ->
                finish()
        );

        root.addView(backButton);

        setContentView(root);
    }

    private void loadQuizzes() {

        quizContainer.removeAllViews();

        db.collection("quizzes")
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.isEmpty()) {

                        TextView empty =
                                new TextView(this);

                        empty.setText(
                                "No quizzes or exams created yet."
                        );

                        empty.setTextSize(16);
                        empty.setGravity(Gravity.CENTER);
                        empty.setTextColor(
                                Color.rgb(100, 116, 139)
                        );

                        empty.setPadding(
                                0,
                                50,
                                0,
                                50
                        );

                        quizContainer.addView(
                                empty
                        );

                        return;
                    }

                    for (DocumentSnapshot doc :
                            snapshot.getDocuments()) {

                        String title =
                                doc.getString("title");

                        String type =
                                doc.getString("type");

                        String className =
                                doc.getString("className");

                        String medium =
                                doc.getString("medium");

                        String subjectName =
                                doc.getString("subjectName");

                        String chapterName =
                                doc.getString("chapterName");

                        String difficulty =
                                doc.getString("difficulty");

                        Boolean published =
                                doc.getBoolean("published");

                        Long totalMarks =
                                doc.getLong("totalMarks");

                        Long duration =
                                doc.getLong("durationMinutes");

                        if (title == null) {
                            title = "Untitled Quiz";
                        }

                        if (type == null) {
                            type = "PRACTICE_QUIZ";
                        }

                        if (className == null) {
                            className = "Unknown Class";
                        }

                        if (medium == null) {
                            medium = "English";
                        }

                        if (subjectName == null) {
                            subjectName = "All Subjects";
                        }

                        if (chapterName == null) {
                            chapterName = "All Chapters";
                        }

                        if (difficulty == null) {
                            difficulty = "easy";
                        }

                        if (published == null) {
                            published = false;
                        }

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
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to load quizzes: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
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

        TextView titleText =
                new TextView(this);

        titleText.setText(title);
        titleText.setTextSize(19);
        titleText.setTextColor(
                Color.rgb(17, 24, 39)
        );

        TextView typeText =
                new TextView(this);

        typeText.setText(
                "Type: " + type
        );

        TextView classText =
                new TextView(this);

        classText.setText(
                "Class: " + className +
                        " • " + medium
        );

        TextView subjectText =
                new TextView(this);

        subjectText.setText(
                "Subject: " + subjectName
        );

        TextView chapterText =
                new TextView(this);

        chapterText.setText(
                "Chapter: " + chapterName
        );

        TextView detailsText =
                new TextView(this);

        detailsText.setText(
                "Marks: " + totalMarks +
                        " • Time: " + duration +
                        " min"
        );

        TextView statusText =
                new TextView(this);

        statusText.setText(
                "Difficulty: " + difficulty +
                        " • " +
                        (published
                                ? "Published"
                                : "Draft")
        );

        typeText.setTextColor(
                Color.rgb(79, 70, 229)
        );

        classText.setTextColor(
                Color.rgb(16, 185, 129)
        );

        subjectText.setTextColor(
                Color.rgb(14, 116, 144)
        );

        chapterText.setTextColor(
                Color.rgb(124, 58, 237)
        );

        detailsText.setTextColor(
                Color.rgb(100, 116, 139)
        );

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

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        Button editButton =
                new Button(this);

        editButton.setText("Edit");
        editButton.setAllCaps(false);

        editButton.setOnClickListener(v ->
                loadClassesForEdit(id)
        );

        Button publishButton =
                new Button(this);

        publishButton.setText(
                published
                        ? "Unpublish"
                        : "Publish"
        );

        publishButton.setAllCaps(false);

        final boolean currentPublished =
                published;

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
                    .addOnSuccessListener(
                            unused -> {

                                Toast.makeText(
                                        this,
                                        currentPublished
                                                ? "Quiz unpublished"
                                                : "Quiz published",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadQuizzes();
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
        });

        Button deleteButton =
                new Button(this);

        deleteButton.setText("Delete");
        deleteButton.setAllCaps(false);

        deleteButton.setOnClickListener(v ->
                confirmDelete(
                        id,
                        title
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
                publishButton,
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

        quizContainer.addView(
                card,
                params
        );
    }

    private void showCreateQuizDialog() {
        loadClassesForCreate();
    }

    private void loadClassesForCreate() {

        db.collection("classes")
                .get()
                .addOnSuccessListener(snapshot -> {

                    classIds.clear();
                    classNames.clear();

                    for (DocumentSnapshot doc :
                            snapshot.getDocuments()) {

                        classIds.add(
                                doc.getId()
                        );

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
                            false,
                            null
                    );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to load classes: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void loadClassesForEdit(
            String quizId
    ) {

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

                    data.documentId =
                            quizId;

                    data.title =
                            getStringValue(
                                    doc,
                                    "title"
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

                    Long marks =
                            doc.getLong(
                                    "totalMarks"
                            );

                    Long duration =
                            doc.getLong(
                                    "durationMinutes"
                            );

                    Long questionCount =
                            doc.getLong(
                                    "questionCount"
                            );

                    data.totalMarks =
                            marks != null
                                    ? marks
                                    : 0;

                    data.duration =
                            duration != null
                                    ? duration
                                    : 10;

                    data.questionCount =
                            questionCount != null
                                    ? questionCount.intValue()
                                    : 10;

                    Boolean published =
                            doc.getBoolean(
                                    "published"
                            );

                    data.published =
                            published != null &&
                                    published;

                    db.collection("classes")
                            .get()
                            .addOnSuccessListener(
                                    snapshot -> {

                                        classIds.clear();
                                        classNames.clear();

                                        for (
                                                DocumentSnapshot classDoc :
                                                snapshot.getDocuments()
                                        ) {

                                            classIds.add(
                                                    classDoc.getId()
                                            );

                                            String name =
                                                    classDoc.getString(
                                                            "name"
                                                    );

                                            if (name == null) {
                                                name =
                                                        "Unnamed Class";
                                            }

                                            classNames.add(
                                                    name
                                            );
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
                                                true,
                                                data
                                        );
                                    }
                            );
                })
                .addOnFailureListener(
                        e ->
                                Toast.makeText(
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

        form.setOrientation(
                LinearLayout.VERTICAL
        );

        form.setPadding(
                40,
                10,
                40,
                10
        );

        ScrollView scrollView =
                new ScrollView(this);

        EditText titleInput =
                new EditText(this);

        titleInput.setHint(
                "Quiz / Exam Title"
        );

        Spinner typeSpinner =
                new Spinner(this);

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        quizTypes
                );

        typeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        typeSpinner.setAdapter(
                typeAdapter
        );

        Spinner classSpinner =
                new Spinner(this);

        ArrayAdapter<String> classAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        classNames
                );

        classAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        classSpinner.setAdapter(
                classAdapter
        );

        Spinner mediumSpinner =
                new Spinner(this);

        ArrayAdapter<String> mediumAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        mediums
                );

        mediumAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        mediumSpinner.setAdapter(
                mediumAdapter
        );

        Spinner subjectSpinner =
                new Spinner(this);

        Spinner chapterSpinner =
                new Spinner(this);

        EditText questionCountInput =
                new EditText(this);

        questionCountInput.setHint(
                "Number of Questions"
        );

        questionCountInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        EditText totalMarksInput =
                new EditText(this);

        totalMarksInput.setHint(
                "Total Marks"
        );

        totalMarksInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        EditText durationInput =
                new EditText(this);

        durationInput.setHint(
                "Time in Minutes"
        );

        durationInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        Spinner difficultySpinner =
                new Spinner(this);

        ArrayAdapter<String> difficultyAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        difficulties
                );

        difficultyAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        difficultySpinner.setAdapter(
                difficultyAdapter
        );

        TextView info =
                new TextView(this);

        info.setText(
                "Questions will be selected from the Question Bank using the selected Class, Medium, Subject and Chapter."
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

        form.addView(info);

        scrollView.addView(form);

        if (editMode && editData != null) {

            titleInput.setText(
                    editData.title
            );

            questionCountInput.setText(
                    String.valueOf(
                            editData.questionCount
                    )
            );

            totalMarksInput.setText(
                    String.valueOf(
                            editData.totalMarks
                    )
            );

            durationInput.setText(
                    String.valueOf(
                            editData.duration
                    )
            );

            int typePosition =
                    findPosition(
                            quizTypes,
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

            int difficultyPosition =
                    findPosition(
                            difficulties,
                            editData.difficulty
                    );

            if (difficultyPosition >= 0) {
                difficultySpinner.setSelection(
                        difficultyPosition
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
                                position < classIds.size()) {

                            loadSubjects(
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

                            loadSubjects(
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
                    public void selected(
                            int position
                    ) {

                        if (position >= 0 &&
                                position <
                                        subjectIds.size()) {

                            loadChapters(
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
                                editMode
                                        ? "Save"
                                        : "Create",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                ignored -> {

                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener(v -> {

                        String title =
                                titleInput
                                        .getText()
                                        .toString()
                                        .trim();

                        String type =
                                typeSpinner
                                        .getSelectedItem()
                                        .toString();

                        String medium =
                                mediumSpinner
                                        .getSelectedItem()
                                        .toString();

                        String questionCountText =
                                questionCountInput
                                        .getText()
                                        .toString()
                                        .trim();

                        String marksText =
                                totalMarksInput
                                        .getText()
                                        .toString()
                                        .trim();

                        String durationText =
                                durationInput
                                        .getText()
                                        .toString()
                                        .trim();

                        String difficulty =
                                difficultySpinner
                                        .getSelectedItem()
                                        .toString();

                        if (title.isEmpty()) {

                            titleInput.setError(
                                    "Enter title"
                            );

                            return;
                        }

                        if (questionCountText.isEmpty()) {

                            questionCountInput.setError(
                                    "Enter question count"
                            );

                            return;
                        }

                        if (marksText.isEmpty()) {

                            totalMarksInput.setError(
                                    "Enter total marks"
                            );

                            return;
                        }

                        if (durationText.isEmpty()) {

                            durationInput.setError(
                                    "Enter duration"
                            );

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

                        if (questionCount <= 0) {

                            questionCountInput.setError(
                                    "Must be greater than 0"
                            );

                            return;
                        }

                        if (totalMarks <= 0) {

                            totalMarksInput.setError(
                                    "Must be greater than 0"
                            );

                            return;
                        }

                        if (duration <= 0) {

                            durationInput.setError(
                                    "Must be greater than 0"
                            );

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
                                classIds.get(
                                        classPosition
                                );

                        String selectedClassName =
                                classNames.get(
                                        classPosition
                                );

                        String selectedSubjectId =
                                "";

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

                        String selectedChapterId =
                                "";

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
                                type
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
                                medium
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
                                difficulty
                        );

                        data.put(
                                "published",
                                editData != null &&
                                        editData.published
                        );

                        if (!editMode) {

                            data.put(
                                    "createdAt",
                                    System.currentTimeMillis()
                            );

                            data.put(
                                    "updatedAt",
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

                            data.put(
                                    "updatedAt",
                                    System.currentTimeMillis()
                            );

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

    private void loadSubjects(
            String classId,
            Spinner mediumSpinner,
            Spinner subjectSpinner,
            Spinner chapterSpinner,
            boolean editMode,
            QuizEditData editData
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

                                subjectNames.add(
                                        name
                                );
                            }

                            ArrayAdapter<String>
                                    adapter =
                                    new ArrayAdapter<>(
                                            this,
                                            android.R.layout.simple_spinner_item,
                                            subjectNames
                                    );

                            adapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                            );

                            subjectSpinner.setAdapter(
                                    adapter
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

                                ArrayAdapter<String>
                                        emptyAdapter =
                                        new ArrayAdapter<>(
                                                this,
                                                android.R.layout.simple_spinner_item,
                                                chapterNames
                                        );

                                chapterSpinner.setAdapter(
                                        emptyAdapter
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        e ->
                                Toast.makeText(
                                        this,
                                        "Failed to load subjects: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show()
                );
    }

    private void loadChapters(
            String subjectId,
            Spinner mediumSpinner,
            Spinner chapterSpinner,
            boolean editMode,
            QuizEditData editData
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

        db.collection("chapters")
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

                                chapterNames.add(
                                        name
                                );
                            }

                            ArrayAdapter<String>
                                    adapter =
                                    new ArrayAdapter<>(
                                            this,
                                            android.R.layout.simple_spinner_item,
                                            chapterNames
                                    );

                            adapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                            );

                            chapterSpinner.setAdapter(
                                    adapter
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
                        e ->
                                Toast.makeText(
                                        this,
                                        "Failed to load chapters: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show()
                );
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

            if (values[i].equalsIgnoreCase(
                    target
            )) {
                return i;
            }
        }

        return -1;
    }

    private String getStringValue(
            DocumentSnapshot doc,
            String field
    ) {

        String value =
                doc.getString(field);

        return value == null
                ? ""
                : value;
    }

    private void confirmDelete(
            String id,
            String title
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Quiz / Exam"
                )
                .setMessage(
                        "Delete \"" +
                                title +
                                "\"?"
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
                                            e ->
                                                    Toast.makeText(
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

    private abstract static class SimpleSelectionListener
            implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(
                AdapterView<?> parent,
                android.view.View view,
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
}
