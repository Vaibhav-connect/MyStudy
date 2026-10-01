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

public class LessonManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout lessonContainer;

    private final List<String> classIds = new ArrayList<>();
    private final List<String> classNames = new ArrayList<>();

    private final List<String> chapterIds = new ArrayList<>();
    private final List<String> chapterNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        createUI();
        loadLessons();
    }

    private void createUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 30, 24, 24);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("Lesson Management");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 20);

        root.addView(title);

        Button addButton = new Button(this);
        addButton.setText("+ Add Lesson");
        addButton.setAllCaps(false);
        addButton.setTextSize(16);

        addButton.setOnClickListener(v ->
                showAddLessonDialog()
        );

        root.addView(
                addButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        ScrollView scrollView = new ScrollView(this);

        lessonContainer = new LinearLayout(this);
        lessonContainer.setOrientation(LinearLayout.VERTICAL);
        lessonContainer.setPadding(0, 20, 0, 20);

        scrollView.addView(lessonContainer);

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

    private void loadLessons() {

        if (lessonContainer == null) {
            return;
        }

        lessonContainer.removeAllViews();

        db.collection("lessons")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        TextView empty = new TextView(this);
                        empty.setText("No lessons added yet.");
                        empty.setTextSize(16);
                        empty.setTextColor(
                                Color.rgb(100, 116, 139)
                        );
                        empty.setGravity(Gravity.CENTER);
                        empty.setPadding(0, 40, 0, 40);

                        lessonContainer.addView(empty);
                        return;
                    }

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        final String documentId =
                                document.getId();

                        String title =
                                document.getString("title");

                        String description =
                                document.getString("description");

                        String content =
                                document.getString("content");

                        String classId =
                                document.getString("classId");

                        String className =
                                document.getString("className");

                        String chapterId =
                                document.getString("chapterId");

                        String chapterName =
                                document.getString("chapterName");

                        Long orderValue =
                                document.getLong("order");

                        if (title == null) {
                            title = "Untitled Lesson";
                        }

                        if (description == null) {
                            description = "";
                        }

                        if (content == null) {
                            content = "";
                        }

                        if (classId == null) {
                            classId = "";
                        }

                        if (className == null) {
                            className = "Unknown Class";
                        }

                        if (chapterId == null) {
                            chapterId = "";
                        }

                        if (chapterName == null) {
                            chapterName = "Unknown Chapter";
                        }

                        final String finalTitle = title;
                        final String finalDescription = description;
                        final String finalContent = content;
                        final String finalClassId = classId;
                        final String finalClassName = className;
                        final String finalChapterId = chapterId;
                        final String finalChapterName = chapterName;

                        final long finalOrder =
                                orderValue != null
                                        ? orderValue
                                        : 0;

                        addLessonCard(
                                documentId,
                                finalTitle,
                                finalDescription,
                                finalContent,
                                finalClassId,
                                finalClassName,
                                finalChapterId,
                                finalChapterName,
                                finalOrder
                        );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to load lessons: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void addLessonCard(
            String documentId,
            String title,
            String description,
            String content,
            String classId,
            String className,
            String chapterId,
            String chapterName,
            long order
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 20, 24, 20);
        card.setBackgroundColor(Color.WHITE);

        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(20);
        titleText.setTextColor(Color.rgb(17, 24, 39));

        TextView classText = new TextView(this);
        classText.setText(
                "Class: " + className
        );
        classText.setTextSize(14);
        classText.setTextColor(Color.rgb(79, 70, 229));

        TextView chapterText = new TextView(this);
        chapterText.setText(
                "Chapter: " + chapterName
        );
        chapterText.setTextSize(15);
        chapterText.setTextColor(Color.rgb(16, 185, 129));

        TextView descriptionText = new TextView(this);
        descriptionText.setText(
                description.isEmpty()
                        ? "No description"
                        : description
        );
        descriptionText.setTextSize(14);
        descriptionText.setTextColor(
                Color.rgb(71, 85, 105)
        );
        descriptionText.setPadding(0, 8, 0, 4);

        TextView orderText = new TextView(this);
        orderText.setText(
                "Order: " + order
        );
        orderText.setTextSize(14);
        orderText.setTextColor(
                Color.rgb(100, 116, 139)
        );

        card.addView(titleText);
        card.addView(classText);
        card.addView(chapterText);
        card.addView(descriptionText);
        card.addView(orderText);

        LinearLayout buttonRow =
                new LinearLayout(this);

        buttonRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        Button editButton = new Button(this);
        editButton.setText("Edit");
        editButton.setAllCaps(false);

        editButton.setOnClickListener(v ->
                showEditLessonDialog(
                        documentId,
                        title,
                        description,
                        content,
                        classId,
                        className,
                        chapterId,
                        chapterName,
                        order
                )
        );

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setAllCaps(false);

        deleteButton.setOnClickListener(v ->
                confirmDelete(
                        documentId,
                        title
                )
        );

        buttonRow.addView(
                editButton,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        buttonRow.addView(
                deleteButton,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        card.addView(buttonRow);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                18
        );

        lessonContainer.addView(
                card,
                cardParams
        );
    }

    private void showAddLessonDialog() {

        loadClassesForDialog(
                false,
                null
        );
    }

    private void showEditLessonDialog(
            String documentId,
            String oldTitle,
            String oldDescription,
            String oldContent,
            String oldClassId,
            String oldClassName,
            String oldChapterId,
            String oldChapterName,
            long oldOrder
    ) {

        LessonEditData data =
                new LessonEditData();

        data.documentId = documentId;
        data.title = oldTitle;
        data.description = oldDescription;
        data.content = oldContent;
        data.classId = oldClassId;
        data.className = oldClassName;
        data.chapterId = oldChapterId;
        data.chapterName = oldChapterName;
        data.order = oldOrder;

        loadClassesForDialog(
                true,
                data
        );
    }

    private void loadClassesForDialog(
            boolean editMode,
            LessonEditData editData
    ) {

        db.collection("classes")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    classIds.clear();
                    classNames.clear();

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

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

                    showLessonDialog(
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

    private void showLessonDialog(
            boolean editMode,
            LessonEditData editData
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

        EditText titleInput =
                new EditText(this);

        titleInput.setHint(
                "Lesson title"
        );

        titleInput.setSingleLine(true);

        EditText descriptionInput =
                new EditText(this);

        descriptionInput.setHint(
                "Short description"
        );

        descriptionInput.setSingleLine(false);

        EditText contentInput =
                new EditText(this);

        contentInput.setHint(
                "Lesson content"
        );

        contentInput.setSingleLine(false);

        contentInput.setGravity(
                Gravity.TOP
        );

        contentInput.setMinLines(5);

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

        EditText orderInput =
                new EditText(this);

        orderInput.setHint(
                "Order e.g. 1"
        );

        orderInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        orderInput.setSingleLine(true);

        layout.addView(titleInput);
        layout.addView(descriptionInput);
        layout.addView(contentInput);
        layout.addView(classSpinner);
        layout.addView(chapterSpinner);
        layout.addView(orderInput);

        if (editMode &&
                editData != null) {

            titleInput.setText(
                    editData.title
            );

            descriptionInput.setText(
                    editData.description
            );

            contentInput.setText(
                    editData.content
            );

            orderInput.setText(
                    String.valueOf(
                            editData.order
                    )
            );

            int classPosition =
                    classIds.indexOf(
                            editData.classId
                    );

            if (classPosition >= 0) {

                classSpinner.setSelection(
                        classPosition
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

                            String selectedClassId =
                                    classIds.get(position);

                            loadChaptersForClass(
                                    selectedClassId,
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
                                        ? "Edit Lesson"
                                        : "Add Lesson"
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

                        String title =
                                titleInput
                                        .getText()
                                        .toString()
                                        .trim();

                        String description =
                                descriptionInput
                                        .getText()
                                        .toString()
                                        .trim();

                        String content =
                                contentInput
                                        .getText()
                                        .toString()
                                        .trim();

                        String orderString =
                                orderInput
                                        .getText()
                                        .toString()
                                        .trim();

                        if (title.isEmpty()) {

                            titleInput.setError(
                                    "Enter lesson title"
                            );

                            return;
                        }

                        if (content.isEmpty()) {

                            contentInput.setError(
                                    "Enter lesson content"
                            );

                            return;
                        }

                        if (orderString.isEmpty()) {

                            orderInput.setError(
                                    "Enter order"
                            );

                            return;
                        }

                        long order;

                        try {

                            order =
                                    Long.parseLong(
                                            orderString
                                    );

                        } catch (Exception e) {

                            orderInput.setError(
                                    "Enter valid number"
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
                                "title",
                                title
                        );

                        data.put(
                                "description",
                                description
                        );

                        data.put(
                                "content",
                                content
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
                                "order",
                                order
                        );

                        data.put(
                                "contentType",
                                "text"
                        );

                        if (!editMode) {

                            data.put(
                                    "createdAt",
                                    System.currentTimeMillis()
                            );

                            db.collection("lessons")
                                    .add(data)
                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        this,
                                                        "Lesson added successfully",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                dialog.dismiss();

                                                loadLessons();
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

                            db.collection("lessons")
                                    .document(
                                            editData.documentId
                                    )
                                    .update(data)
                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        this,
                                                        "Lesson updated successfully",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                dialog.dismiss();

                                                loadLessons();
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

    private void loadChaptersForClass(
            String classId,
            Spinner chapterSpinner,
            boolean editMode,
            LessonEditData editData
    ) {

        db.collection("chapters")
                .whereEqualTo(
                        "classId",
                        classId
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            chapterIds.clear();
                            chapterNames.clear();

                            for (DocumentSnapshot document :
                                    querySnapshot.getDocuments()) {

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
                                        chapterIds.indexOf(
                                                editData.chapterId
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
            String lessonTitle
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Lesson"
                )
                .setMessage(
                        "Delete \"" +
                                lessonTitle +
                                "\"?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            db.collection("lessons")
                                    .document(
                                            documentId
                                    )
                                    .delete()
                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        this,
                                                        "Lesson deleted",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                loadLessons();
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

    private static class LessonEditData {

        String documentId;
        String title;
        String description;
        String content;
        String classId;
        String className;
        String chapterId;
        String chapterName;
        long order;
    }
}
