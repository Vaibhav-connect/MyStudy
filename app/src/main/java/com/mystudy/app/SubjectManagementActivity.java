package com.mystudy.app;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
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

public class SubjectManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout subjectContainer;

    private final List<String> classIds = new ArrayList<>();
    private final List<String> classNames = new ArrayList<>();

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
        loadSubjects();
    }

    private void createUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 30, 24, 24);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("Subject Management");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, 20);

        root.addView(title);

        Button addButton = new Button(this);
        addButton.setText("+ Add Subject");
        addButton.setAllCaps(false);
        addButton.setTextSize(16);

        addButton.setOnClickListener(v -> showAddSubjectDialog());

        root.addView(
                addButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        ScrollView scrollView = new ScrollView(this);

        subjectContainer = new LinearLayout(this);
        subjectContainer.setOrientation(LinearLayout.VERTICAL);
        subjectContainer.setPadding(0, 20, 0, 20);

        scrollView.addView(subjectContainer);

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

    private void loadSubjects() {

        subjectContainer.removeAllViews();

        db.collection("subjects")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        TextView empty = new TextView(this);
                        empty.setText("No subjects added yet.");
                        empty.setTextSize(16);
                        empty.setTextColor(
                                Color.rgb(100, 116, 139)
                        );
                        empty.setGravity(Gravity.CENTER);
                        empty.setPadding(0, 40, 0, 40);

                        subjectContainer.addView(empty);

                        return;
                    }

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        String id = document.getId();

                        String name =
                                document.getString("name");

                        String className =
                                document.getString("className");

                        String classId =
                                document.getString("classId");

                        String medium =
                                document.getString("medium");

                        Long order =
                                document.getLong("order");

                        if (name == null) {
                            name = "Unnamed Subject";
                        }

                        if (className == null) {
                            className = "Unknown Class";
                        }

                        if (medium == null ||
                                medium.trim().isEmpty()) {
                            medium = "English";
                        }

                        long subjectOrder =
                                order != null ? order : 0;

                        addSubjectCard(
                                id,
                                name,
                                classId,
                                className,
                                medium,
                                subjectOrder
                        );
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load subjects: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void addSubjectCard(
            String documentId,
            String name,
            String classId,
            String className,
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
        classText.setTextSize(15);
        classText.setTextColor(Color.rgb(79, 70, 229));

        TextView mediumText = new TextView(this);
        mediumText.setText("Medium: " + medium);
        mediumText.setTextSize(15);
        mediumText.setTextColor(Color.rgb(22, 163, 74));

        TextView orderText = new TextView(this);
        orderText.setText("Order: " + order);
        orderText.setTextSize(14);
        orderText.setTextColor(Color.rgb(100, 116, 139));

        card.addView(nameText);
        card.addView(classText);
        card.addView(mediumText);
        card.addView(orderText);

        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);

        Button editButton = new Button(this);
        editButton.setText("Edit");
        editButton.setAllCaps(false);

        final String finalName = name;
        final String finalClassId = classId;
        final String finalClassName = className;
        final String finalMedium = medium;
        final long finalOrder = order;

        editButton.setOnClickListener(v ->
                showEditSubjectDialog(
                        documentId,
                        finalName,
                        finalClassId,
                        finalClassName,
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

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, 18);

        subjectContainer.addView(card, cardParams);
    }

    private void showAddSubjectDialog() {

        loadClassesForDialog(
                false,
                null
        );
    }

    private void showEditSubjectDialog(
            String documentId,
            String oldName,
            String oldClassId,
            String oldClassName,
            String oldMedium,
            long oldOrder
    ) {

        loadClassesForDialog(
                true,
                new EditData(
                        documentId,
                        oldName,
                        oldClassId,
                        oldClassName,
                        oldMedium,
                        oldOrder
                )
        );
    }

    private void loadClassesForDialog(
            boolean editMode,
            EditData editData
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

                    showSubjectDialog(
                            editMode,
                            editData
                    );
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load classes: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void showSubjectDialog(
            boolean editMode,
            EditData editData
    ) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Subject name");
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

        EditText orderInput = new EditText(this);
        orderInput.setHint("Order e.g. 1");
        orderInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );
        orderInput.setSingleLine(true);

        layout.addView(nameInput);
        layout.addView(classSpinner);
        layout.addView(mediumSpinner);
        layout.addView(orderInput);

        if (editMode && editData != null) {

            nameInput.setText(editData.name);

            orderInput.setText(
                    String.valueOf(editData.order)
            );

            int selectedClassIndex =
                    classIds.indexOf(
                            editData.classId
                    );

            if (selectedClassIndex >= 0) {

                classSpinner.setSelection(
                        selectedClassIndex
                );
            }

            int selectedMediumIndex =
                    findMediumIndex(
                            editData.medium
                    );

            if (selectedMediumIndex >= 0) {

                mediumSpinner.setSelection(
                        selectedMediumIndex
                );
            }
        }

        String dialogTitle =
                editMode
                        ? "Edit Subject"
                        : "Add Subject";

        String positiveText =
                editMode
                        ? "Save"
                        : "Add";

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(dialogTitle)
                        .setView(layout)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                positiveText,
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
                            "Enter subject name"
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

                int selectedClassPosition =
                        classSpinner
                                .getSelectedItemPosition();

                if (selectedClassPosition < 0 ||
                        selectedClassPosition >=
                                classIds.size()) {

                    Toast.makeText(
                            this,
                            "Select a class.",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                int selectedMediumPosition =
                        mediumSpinner
                                .getSelectedItemPosition();

                if (selectedMediumPosition < 0 ||
                        selectedMediumPosition >=
                                mediums.length) {

                    Toast.makeText(
                            this,
                            "Select a medium.",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                String selectedClassId =
                        classIds.get(
                                selectedClassPosition
                        );

                String selectedClassName =
                        classNames.get(
                                selectedClassPosition
                        );

                String selectedMedium =
                        mediums[
                                selectedMediumPosition
                        ];

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
                        "order",
                        order
                );

                if (!editMode) {

                    data.put(
                            "createdAt",
                            System.currentTimeMillis()
                    );

                    db.collection("subjects")
                            .add(data)
                            .addOnSuccessListener(
                                    documentReference -> {

                                        Toast.makeText(
                                                this,
                                                "Subject added successfully",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        dialog.dismiss();
                                        loadSubjects();
                                    }
                            )
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        this,
                                        "Failed: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });

                } else {

                    db.collection("subjects")
                            .document(
                                    editData.documentId
                            )
                            .update(data)
                            .addOnSuccessListener(
                                    unused -> {

                                        Toast.makeText(
                                                this,
                                                "Subject updated successfully",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        dialog.dismiss();
                                        loadSubjects();
                                    }
                            )
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        this,
                                        "Failed: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                }
            });
        });

        dialog.show();
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
            String subjectName
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Subject")
                .setMessage(
                        "Delete \"" +
                                subjectName +
                                "\"?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            db.collection("subjects")
                                    .document(documentId)
                                    .delete()
                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        this,
                                                        "Subject deleted",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                loadSubjects();
                                            }
                                    )
                                    .addOnFailureListener(e -> {

                                        Toast.makeText(
                                                this,
                                                "Delete failed: "
                                                        + e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    });
                        })
                .show();
    }

    private static class EditData {

        String documentId;
        String name;
        String classId;
        String className;
        String medium;
        long order;

        EditData(
                String documentId,
                String name,
                String classId,
                String className,
                String medium,
                long order
        ) {

            this.documentId = documentId;
            this.name = name;
            this.classId = classId;
            this.className = className;
            this.medium = medium;
            this.order = order;
        }
    }
}
