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

public class ChapterManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout chapterContainer;

    private final List<String> classIds = new ArrayList<>();
    private final List<String> classNames = new ArrayList<>();

    private final List<String> subjectIds = new ArrayList<>();
    private final List<String> subjectNames = new ArrayList<>();
    private final List<String> subjectMediums = new ArrayList<>();

    private final String[] mediums = {
            "English",
            "Semi-English",
            "Marathi",
            "Hindi"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        createUI();
        loadChapters();
    }

    private void createUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 30, 24, 24);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("Chapter Management");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 20);

        root.addView(title);

        Button addButton = new Button(this);
        addButton.setText("+ Add Chapter");
        addButton.setAllCaps(false);
        addButton.setTextSize(16);

        addButton.setOnClickListener(v -> showAddChapterDialog());

        root.addView(
                addButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        ScrollView scrollView = new ScrollView(this);

        chapterContainer = new LinearLayout(this);
        chapterContainer.setOrientation(LinearLayout.VERTICAL);
        chapterContainer.setPadding(0, 20, 0, 20);

        scrollView.addView(chapterContainer);

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
        backButton.setOnClickListener(v -> finish());

        root.addView(backButton);

        setContentView(root);
    }

    private void loadChapters() {

        chapterContainer.removeAllViews();

        db.collection("chapters")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        TextView empty = new TextView(this);
                        empty.setText("No chapters added yet.");
                        empty.setTextSize(16);
                        empty.setTextColor(
                                Color.rgb(100, 116, 139)
                        );
                        empty.setGravity(Gravity.CENTER);
                        empty.setPadding(0, 40, 0, 40);

                        chapterContainer.addView(empty);
                        return;
                    }

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        String id = document.getId();

                        String name =
                                document.getString("name");

                        String className =
                                document.getString("className");

                        String subjectName =
                                document.getString("subjectName");

                        String classId =
                                document.getString("classId");

                        String subjectId =
                                document.getString("subjectId");

                        String medium =
                                document.getString("medium");

                        Long order =
                                document.getLong("order");

                        if (name == null) {
                            name = "Unnamed Chapter";
                        }

                        if (className == null) {
                            className = "Unknown Class";
                        }

                        if (subjectName == null) {
                            subjectName = "Unknown Subject";
                        }

                        if (medium == null ||
                                medium.trim().isEmpty()) {
                            medium = "English";
                        }

                        long chapterOrder =
                                order != null ? order : 0;

                        addChapterCard(
                                id,
                                name,
                                classId,
                                className,
                                subjectId,
                                subjectName,
                                medium,
                                chapterOrder
                        );
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(
                        this,
                        "Failed to load chapters: " +
                                e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }

    private void addChapterCard(
            String documentId,
            String name,
            String classId,
            String className,
            String subjectId,
            String subjectName,
            String medium,
            long order
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 20, 24, 20);
        card.setBackgroundColor(Color.WHITE);

        TextView nameText = new TextView(this);
        nameText.setText(name);
        nameText.setTextSize(20);
        nameText.setTextColor(Color.rgb(17, 24, 39));

        TextView classText = new TextView(this);
        classText.setText("Class: " + className);
        classText.setTextSize(14);
        classText.setTextColor(Color.rgb(79, 70, 229));

        TextView mediumText = new TextView(this);
        mediumText.setText("Medium: " + medium);
        mediumText.setTextSize(14);
        mediumText.setTextColor(Color.rgb(22, 163, 74));

        TextView subjectText = new TextView(this);
        subjectText.setText("Subject: " + subjectName);
        subjectText.setTextSize(15);
        subjectText.setTextColor(Color.rgb(16, 185, 129));

        TextView orderText = new TextView(this);
        orderText.setText("Order: " + order);
        orderText.setTextSize(14);
        orderText.setTextColor(Color.rgb(100, 116, 139));

        card.addView(nameText);
        card.addView(classText);
        card.addView(mediumText);
        card.addView(subjectText);
        card.addView(orderText);

        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);

        Button editButton = new Button(this);
        editButton.setText("Edit");
        editButton.setAllCaps(false);

        final String finalName = name;
        final String finalClassId = classId;
        final String finalClassName = className;
        final String finalSubjectId = subjectId;
        final String finalSubjectName = subjectName;
        final String finalMedium = medium;
        final long finalOrder = order;

        editButton.setOnClickListener(v ->
                showEditChapterDialog(
                        documentId,
                        finalName,
                        finalClassId,
                        finalClassName,
                        finalSubjectId,
                        finalSubjectName,
                        finalMedium,
                        finalOrder
                )
        );

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setAllCaps(false);

        deleteButton.setOnClickListener(v ->
                confirmDelete(
                        documentId,
                        finalName
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

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, 18);

        chapterContainer.addView(card, params);
    }

    private void showAddChapterDialog() {

        loadClassesForDialog(false, null);
    }

    private void showEditChapterDialog(
            String documentId,
            String oldName,
            String oldClassId,
            String oldClassName,
            String oldSubjectId,
            String oldSubjectName,
            String oldMedium,
            long oldOrder
    ) {

        ChapterEditData data = new ChapterEditData();

        data.documentId = documentId;
        data.name = oldName;
        data.classId = oldClassId;
        data.className = oldClassName;
        data.subjectId = oldSubjectId;
        data.subjectName = oldSubjectName;
        data.medium = oldMedium;
        data.order = oldOrder;

        loadClassesForDialog(true, data);
    }

    private void loadClassesForDialog(
            boolean editMode,
            ChapterEditData editData
    ) {

        db.collection("classes")
                .orderBy("order")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    classIds.clear();
                    classNames.clear();

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        classIds.add(document.getId());

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

                    showChapterDialog(
                            editMode,
                            editData
                    );
                })
                .addOnFailureListener(e -> Toast.makeText(
                        this,
                        "Failed to load classes: " +
                                e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }

    private void showChapterDialog(
            boolean editMode,
            ChapterEditData editData
    ) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Chapter name");
        nameInput.setSingleLine(true);

        Spinner classSpinner = new Spinner(this);

        ArrayAdapter<String> classAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        classNames
                );

        classAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        classSpinner.setAdapter(classAdapter);

        Spinner mediumSpinner = new Spinner(this);

        ArrayAdapter<String> mediumAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        mediums
                );

        mediumAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        mediumSpinner.setAdapter(mediumAdapter);

        Spinner subjectSpinner = new Spinner(this);

        EditText orderInput = new EditText(this);
        orderInput.setHint("Order e.g. 1");
        orderInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );
        orderInput.setSingleLine(true);

        layout.addView(nameInput);
        layout.addView(classSpinner);
        layout.addView(mediumSpinner);
        layout.addView(subjectSpinner);
        layout.addView(orderInput);

        if (editMode && editData != null) {

            nameInput.setText(editData.name);

            orderInput.setText(
                    String.valueOf(editData.order)
            );

            int classPosition =
                    classIds.indexOf(editData.classId);

            if (classPosition >= 0) {
                classSpinner.setSelection(
                        classPosition
                );
            }

            int mediumPosition =
                    findMediumIndex(
                            editData.medium
                    );

            if (mediumPosition >= 0) {
                mediumSpinner.setSelection(
                        mediumPosition
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

                            String selectedMedium =
                                    mediumSpinner
                                            .getSelectedItem()
                                            .toString();

                            loadSubjectsForClassAndMedium(
                                    selectedClassId,
                                    selectedMedium,
                                    subjectSpinner,
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

        mediumSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        if (position >= 0 &&
                                position < mediums.length &&
                                classSpinner
                                        .getSelectedItemPosition() >= 0) {

                            String selectedClassId =
                                    classIds.get(
                                            classSpinner
                                                    .getSelectedItemPosition()
                                    );

                            String selectedMedium =
                                    mediums[position];

                            loadSubjectsForClassAndMedium(
                                    selectedClassId,
                                    selectedMedium,
                                    subjectSpinner,
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
                                        ? "Edit Chapter"
                                        : "Add Chapter"
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

        dialog.setOnShowListener(d -> {

            dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                String name =
                        nameInput.getText()
                                .toString()
                                .trim();

                String orderString =
                        orderInput.getText()
                                .toString()
                                .trim();

                if (name.isEmpty()) {

                    nameInput.setError(
                            "Enter chapter name"
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
                    order = Long.parseLong(orderString);
                } catch (Exception e) {

                    orderInput.setError(
                            "Enter valid number"
                    );

                    return;
                }

                int classPosition =
                        classSpinner.getSelectedItemPosition();

                int mediumPosition =
                        mediumSpinner.getSelectedItemPosition();

                int subjectPosition =
                        subjectSpinner.getSelectedItemPosition();

                if (classPosition < 0 ||
                        classPosition >= classIds.size()) {

                    Toast.makeText(
                            this,
                            "Select a class.",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                if (mediumPosition < 0 ||
                        mediumPosition >= mediums.length) {

                    Toast.makeText(
                            this,
                            "Select a medium.",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                if (subjectPosition < 0 ||
                        subjectPosition >= subjectIds.size()) {

                    Toast.makeText(
                            this,
                            "Select a subject for selected medium.",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                String selectedClassId =
                        classIds.get(classPosition);

                String selectedClassName =
                        classNames.get(classPosition);

                String selectedMedium =
                        mediums[mediumPosition];

                String selectedSubjectId =
                        subjectIds.get(subjectPosition);

                String selectedSubjectName =
                        subjectNames.get(subjectPosition);

                Map<String, Object> data =
                        new HashMap<>();

                data.put(
                        "name",
                        name
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
                        "order",
                        order
                );

                if (!editMode) {

                    data.put(
                            "createdAt",
                            System.currentTimeMillis()
                    );

                    db.collection("chapters")
                            .add(data)
                            .addOnSuccessListener(
                                    unused -> {

                                        Toast.makeText(
                                                this,
                                                "Chapter added successfully",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        dialog.dismiss();
                                        loadChapters();
                                    }
                            )
                            .addOnFailureListener(
                                    e -> Toast.makeText(
                                            this,
                                            "Failed: " +
                                                    e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show()
                            );

                } else {

                    if (editData == null) {
                        return;
                    }

                    db.collection("chapters")
                            .document(
                                    editData.documentId
                            )
                            .update(data)
                            .addOnSuccessListener(
                                    unused -> {

                                        Toast.makeText(
                                                this,
                                                "Chapter updated successfully",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        dialog.dismiss();
                                        loadChapters();
                                    }
                            )
                            .addOnFailureListener(
                                    e -> Toast.makeText(
                                            this,
                                            "Failed: " +
                                                    e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show()
                            );
                }
            });
        });

        dialog.show();
    }

    private void loadSubjectsForClassAndMedium(
            String classId,
            String medium,
            Spinner subjectSpinner,
            boolean editMode,
            ChapterEditData editData
    ) {

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
                .addOnSuccessListener(querySnapshot -> {

                    subjectIds.clear();
                    subjectNames.clear();
                    subjectMediums.clear();

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        subjectIds.add(
                                document.getId()
                        );

                        String name =
                                document.getString("name");

                        String savedMedium =
                                document.getString("medium");

                        if (name == null) {
                            name = "Unnamed Subject";
                        }

                        if (savedMedium == null ||
                                savedMedium.trim().isEmpty()) {
                            savedMedium = medium;
                        }

                        subjectNames.add(name);
                        subjectMediums.add(savedMedium);
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_spinner_item,
                                    subjectNames
                            );

                    adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                    );

                    subjectSpinner.setAdapter(adapter);

                    if (editMode &&
                            editData != null) {

                        int position =
                                subjectIds.indexOf(
                                        editData.subjectId
                                );

                        if (position >= 0) {

                            subjectSpinner.setSelection(
                                    position
                            );
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(
                        this,
                        "Failed to load subjects: " +
                                e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }

    private int findMediumIndex(String medium) {

        if (medium == null) {
            return 0;
        }

        for (int i = 0; i < mediums.length; i++) {

            if (mediums[i].equalsIgnoreCase(
                    medium.trim()
            )) {

                return i;
            }
        }

        return 0;
    }

    private void confirmDelete(
            String documentId,
            String chapterName
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Chapter")
                .setMessage(
                        "Delete \"" +
                                chapterName +
                                "\"?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            db.collection("chapters")
                                    .document(documentId)
                                    .delete()
                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        this,
                                                        "Chapter deleted",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                loadChapters();
                                            }
                                    )
                                    .addOnFailureListener(
                                            e -> Toast.makeText(
                                                    this,
                                                    "Delete failed: " +
                                                            e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show()
                                    );
                        })
                .show();
    }

    private static class ChapterEditData {

        String documentId;
        String name;
        String classId;
        String className;
        String subjectId;
        String subjectName;
        String medium;
        long order;
    }
}
