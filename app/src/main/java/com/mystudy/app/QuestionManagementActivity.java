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

public class QuestionManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout questionContainer;

    private final List<String> classIds = new ArrayList<>();
    private final List<String> classNames = new ArrayList<>();

    private final List<String> chapterIds = new ArrayList<>();
    private final List<String> chapterNames = new ArrayList<>();

    private final List<String> questionTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        createUI();
        loadQuestions();
    }

    private void createUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 30, 24, 24);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("Question Management");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 20);

        root.addView(title);

        Button addButton = new Button(this);
        addButton.setText("+ Add Question");
        addButton.setAllCaps(false);
        addButton.setTextSize(16);

        addButton.setOnClickListener(v ->
                showAddQuestionDialog()
        );

        root.addView(
                addButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        ScrollView scrollView = new ScrollView(this);

        questionContainer = new LinearLayout(this);
        questionContainer.setOrientation(LinearLayout.VERTICAL);
        questionContainer.setPadding(0, 20, 0, 20);

        scrollView.addView(questionContainer);

        root.addView(
                scrollView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
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

    private void loadQuestions() {

        questionContainer.removeAllViews();

        db.collection("questions")
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.isEmpty()) {

                        TextView empty = new TextView(this);
                        empty.setText("No questions added yet.");
                        empty.setTextSize(16);
                        empty.setTextColor(
                                Color.rgb(100, 116, 139)
                        );
                        empty.setGravity(Gravity.CENTER);
                        empty.setPadding(0, 40, 0, 40);

                        questionContainer.addView(empty);
                        return;
                    }

                    for (DocumentSnapshot document :
                            snapshot.getDocuments()) {

                        final String id =
                                document.getId();

                        String question =
                                document.getString("question");

                        String type =
                                document.getString("type");

                        String answer =
                                document.getString("answer");

                        String className =
                                document.getString("className");

                        String chapterName =
                                document.getString("chapterName");

                        Long marksValue =
                                document.getLong("marks");

                        if (question == null) {
                            question = "Unnamed Question";
                        }

                        if (type == null) {
                            type = "MCQ";
                        }

                        if (answer == null) {
                            answer = "";
                        }

                        if (className == null) {
                            className = "Unknown Class";
                        }

                        if (chapterName == null) {
                            chapterName = "Unknown Chapter";
                        }

                        long marks =
                                marksValue != null
                                        ? marksValue
                                        : 1;

                        final String finalQuestion =
                                question;

                        final String finalType =
                                type;

                        final String finalAnswer =
                                answer;

                        final String finalClassName =
                                className;

                        final String finalChapterName =
                                chapterName;

                        final long finalMarks =
                                marks;

                        addQuestionCard(
                                id,
                                finalQuestion,
                                finalType,
                                finalAnswer,
                                finalClassName,
                                finalChapterName,
                                finalMarks
                        );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to load questions: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void addQuestionCard(
            String documentId,
            String question,
            String type,
            String answer,
            String className,
            String chapterName,
            long marks
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 20, 24, 20);
        card.setBackgroundColor(Color.WHITE);

        TextView questionText = new TextView(this);
        questionText.setText(question);
        questionText.setTextSize(18);
        questionText.setTextColor(Color.rgb(17, 24, 39));

        TextView typeText = new TextView(this);
        typeText.setText("Type: " + type);
        typeText.setTextSize(14);
        typeText.setTextColor(Color.rgb(79, 70, 229));

        TextView classText = new TextView(this);
        classText.setText("Class: " + className);
        classText.setTextSize(14);
        classText.setTextColor(Color.rgb(16, 185, 129));

        TextView chapterText = new TextView(this);
        chapterText.setText("Chapter: " + chapterName);
        chapterText.setTextSize(14);
        chapterText.setTextColor(Color.rgb(14, 116, 144));

        TextView marksText = new TextView(this);
        marksText.setText("Marks: " + marks);
        marksText.setTextSize(14);
        marksText.setTextColor(Color.rgb(100, 116, 139));

        card.addView(questionText);
        card.addView(typeText);
        card.addView(classText);
        card.addView(chapterText);
        card.addView(marksText);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        Button editButton = new Button(this);
        editButton.setText("Edit");
        editButton.setAllCaps(false);

        editButton.setOnClickListener(v ->
                showEditQuestionDialog(
                        documentId,
                        question,
                        type,
                        answer,
                        className,
                        chapterName,
                        marks
                )
        );

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setAllCaps(false);

        deleteButton.setOnClickListener(v ->
                confirmDelete(
                        documentId,
                        question
                )
        );

        row.addView(
                editButton,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        row.addView(
                deleteButton,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        card.addView(row);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, 18);

        questionContainer.addView(
                card,
                params
        );
    }

    private void showAddQuestionDialog() {

        loadClasses(false, null);
    }

    private void showEditQuestionDialog(
            String documentId,
            String oldQuestion,
            String oldType,
            String oldAnswer,
            String oldClassName,
            String oldChapterName,
            long oldMarks
    ) {

        QuestionEditData data =
                new QuestionEditData();

        data.documentId = documentId;
        data.question = oldQuestion;
        data.type = oldType;
        data.answer = oldAnswer;
        data.className = oldClassName;
        data.chapterName = oldChapterName;
        data.marks = oldMarks;

        loadClasses(true, data);
    }

    private void loadClasses(
            boolean editMode,
            QuestionEditData editData
    ) {

        db.collection("classes")
                .get()
                .addOnSuccessListener(snapshot -> {

                    classIds.clear();
                    classNames.clear();

                    for (DocumentSnapshot document :
                            snapshot.getDocuments()) {

                        classIds.add(
                                document.getId()
                        );

                        String name =
                                document.getString("name");

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
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to load classes: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void showQuestionDialog(
            boolean editMode,
            QuestionEditData editData
    ) {

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                40,
                10,
                40,
                10
        );

        EditText questionInput =
                new EditText(this);

        questionInput.setHint(
                "Question"
        );

        questionInput.setSingleLine(false);

        Spinner typeSpinner =
                new Spinner(this);

        questionTypes.clear();

        questionTypes.add("MCQ");
        questionTypes.add("TRUE_FALSE");
        questionTypes.add("FILL_BLANK");
        questionTypes.add("NUMERICAL");
        questionTypes.add("SHORT_ANSWER");

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        questionTypes
                );

        typeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        typeSpinner.setAdapter(typeAdapter);

        EditText optionAInput =
                new EditText(this);

        optionAInput.setHint("Option A");

        EditText optionBInput =
                new EditText(this);

        optionBInput.setHint("Option B");

        EditText optionCInput =
                new EditText(this);

        optionCInput.setHint("Option C");

        EditText optionDInput =
                new EditText(this);

        optionDInput.setHint("Option D");

        EditText answerInput =
                new EditText(this);

        answerInput.setHint(
                "Correct Answer"
        );

        EditText marksInput =
                new EditText(this);

        marksInput.setHint("Marks e.g. 1");

        marksInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
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

        Spinner chapterSpinner =
                new Spinner(this);

        layout.addView(questionInput);
        layout.addView(typeSpinner);
        layout.addView(optionAInput);
        layout.addView(optionBInput);
        layout.addView(optionCInput);
        layout.addView(optionDInput);
        layout.addView(answerInput);
        layout.addView(marksInput);
        layout.addView(classSpinner);
        layout.addView(chapterSpinner);

        if (editMode &&
                editData != null) {

            questionInput.setText(
                    editData.question
            );

            answerInput.setText(
                    editData.answer
            );

            marksInput.setText(
                    String.valueOf(
                            editData.marks
                    )
            );

            int typePosition =
                    questionTypes.indexOf(
                            editData.type
                    );

            if (typePosition >= 0) {

                typeSpinner.setSelection(
                        typePosition
                );
            }
        }

        classSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        if (position >= 0 &&
                                position < classIds.size()) {

                            loadChapters(
                                    classIds.get(position),
                                    chapterSpinner,
                                    editMode,
                                    editData
                            );
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
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
                        .setView(layout)
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

                        String type =
                                typeSpinner
                                        .getSelectedItem()
                                        .toString();

                        String optionA =
                                optionAInput
                                        .getText()
                                        .toString()
                                        .trim();

                        String optionB =
                                optionBInput
                                        .getText()
                                        .toString()
                                        .trim();

                        String optionC =
                                optionCInput
                                        .getText()
                                        .toString()
                                        .trim();

                        String optionD =
                                optionDInput
                                        .getText()
                                        .toString()
                                        .trim();

                        String answer =
                                answerInput
                                        .getText()
                                        .toString()
                                        .trim();

                        String marksString =
                                marksInput
                                        .getText()
                                        .toString()
                                        .trim();

                        if (question.isEmpty()) {

                            questionInput.setError(
                                    "Enter question"
                            );

                            return;
                        }

                        if (answer.isEmpty()) {

                            answerInput.setError(
                                    "Enter correct answer"
                            );

                            return;
                        }

                        if (marksString.isEmpty()) {

                            marksInput.setError(
                                    "Enter marks"
                            );

                            return;
                        }

                        long marks;

                        try {

                            marks =
                                    Long.parseLong(
                                            marksString
                                    );

                        } catch (Exception e) {

                            marksInput.setError(
                                    "Enter valid marks"
                            );

                            return;
                        }

                        int classPosition =
                                classSpinner
                                        .getSelectedItemPosition();

                        int chapterPosition =
                                chapterSpinner
                                        .getSelectedItemPosition();

                        if (classPosition < 0 ||
                                classPosition >=
                                        classIds.size()) {

                            Toast.makeText(
                                    this,
                                    "Select a class.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        if (chapterPosition < 0 ||
                                chapterPosition >=
                                        chapterIds.size()) {

                            Toast.makeText(
                                    this,
                                    "Select a chapter.",
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

                        String selectedChapterId =
                                chapterIds.get(
                                        chapterPosition
                                );

                        String selectedChapterName =
                                chapterNames.get(
                                        chapterPosition
                                );

                        Map<String, Object> data =
                                new HashMap<>();

                        data.put(
                                "question",
                                question
                        );

                        data.put(
                                "type",
                                type
                        );

                        data.put(
                                "optionA",
                                optionA
                        );

                        data.put(
                                "optionB",
                                optionB
                        );

                        data.put(
                                "optionC",
                                optionC
                        );

                        data.put(
                                "optionD",
                                optionD
                        );

                        data.put(
                                "answer",
                                answer
                        );

                        data.put(
                                "marks",
                                marks
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
                                "chapterId",
                                selectedChapterId
                        );

                        data.put(
                                "chapterName",
                                selectedChapterName
                        );

                        data.put(
                                "difficulty",
                                "easy"
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

    private void loadChapters(
            String classId,
            Spinner chapterSpinner,
            boolean editMode,
            QuestionEditData editData
    ) {

        db.collection("chapters")
                .whereEqualTo(
                        "classId",
                        classId
                )
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            chapterIds.clear();
                            chapterNames.clear();

                            for (DocumentSnapshot document :
                                    snapshot.getDocuments()) {

                                chapterIds.add(
                                        document.getId()
                                );

                                String name =
                                        document.getString(
                                                "name"
                                        );

                                if (name == null) {
                                    name = "Unnamed Chapter";
                                }

                                chapterNames.add(name);
                            }

                            ArrayAdapter<String> adapter =
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

    private void confirmDelete(
            String documentId,
            String question
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Question"
                )
                .setMessage(
                        "Delete this question?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            db.collection("questions")
                                    .document(
                                            documentId
                                    )
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

    private static class QuestionEditData {

        String documentId;
        String question;
        String type;
        String answer;
        String className;
        String chapterName;
        long marks;
    }
}
