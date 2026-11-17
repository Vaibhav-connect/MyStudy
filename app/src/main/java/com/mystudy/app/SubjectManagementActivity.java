package com.mystudy.app;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private LinearLayout subjectContainer;
    private TextView countView;
    private TextView statusView;

    private final List<String> classIds = new ArrayList<>();
    private final List<String> classNames = new ArrayList<>();

    private final String[] mediums = {
            "English",
            "Semi-English",
            "Marathi",
            "Hindi"
    };

    private final int BLUE = Color.rgb(37, 99, 235);
    private final int DARK_BLUE = Color.rgb(30, 64, 175);
    private final int YELLOW = Color.rgb(250, 204, 21);
    private final int BACKGROUND = Color.rgb(245, 248, 255);
    private final int TEXT = Color.rgb(30, 41, 59);
    private final int MUTED = Color.rgb(100, 116, 139);
    private final int RED = Color.rgb(220, 38, 38);
    private final int GREEN = Color.rgb(22, 163, 74);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        createUI();
        loadSubjects();
    }

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private GradientDrawable bordered(
            int color,
            int strokeColor,
            int radius
    ) {
        GradientDrawable drawable = rounded(color, radius);
        drawable.setStroke(dp(1), strokeColor);
        return drawable;
    }

    private TextView text(
            String value,
            int size,
            int color,
            boolean bold
    ) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }

        return view;
    }

    private Button button(
            String value,
            int backgroundColor,
            int textColor
    ) {
        Button btn = new Button(this);
        btn.setText(value);
        btn.setTextSize(13);
        btn.setAllCaps(false);
        btn.setTextColor(textColor);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setBackground(rounded(backgroundColor, 14));
        btn.setPadding(dp(12), dp(7), dp(12), dp(7));
        return btn;
    }

    private void createUI() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(BACKGROUND);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(20), dp(20), dp(30));

        // Header

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView back = text("‹", 36, BLUE, true);
        back.setGravity(Gravity.CENTER);
        back.setBackground(rounded(Color.WHITE, 14));

        header.addView(back,
                new LinearLayout.LayoutParams(dp(48), dp(48)));

        back.setOnClickListener(v -> finish());

        LinearLayout brandLayout = new LinearLayout(this);
        brandLayout.setOrientation(LinearLayout.VERTICAL);
        brandLayout.setPadding(dp(12), 0, 0, 0);

        brandLayout.addView(
                text("MyStudy", 22, DARK_BLUE, true)
        );

        brandLayout.addView(
                text("ADMINISTRATION PANEL", 10, MUTED, true)
        );

        header.addView(
                brandLayout,
                new LinearLayout.LayoutParams(0, -2, 1)
        );

        root.addView(header);

        // Title

        TextView title = text(
                "Subject Management",
                27,
                TEXT,
                true
        );

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(-1, -2);

        titleParams.topMargin = dp(28);
        root.addView(title, titleParams);

        TextView subtitle = text(
                "Manage subjects, classes and learning mediums.",
                14,
                MUTED,
                false
        );

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(-1, -2);

        subtitleParams.topMargin = dp(6);
        root.addView(subtitle, subtitleParams);

        // Counter Card

        LinearLayout countCard = new LinearLayout(this);
        countCard.setOrientation(LinearLayout.VERTICAL);
        countCard.setPadding(dp(22), dp(20), dp(22), dp(20));
        countCard.setBackground(rounded(BLUE, 22));

        LinearLayout.LayoutParams countParams =
                new LinearLayout.LayoutParams(-1, -2);

        countParams.topMargin = dp(24);
        root.addView(countCard, countParams);

        countCard.addView(
                text("TOTAL SUBJECTS", 12, Color.WHITE, true)
        );

        countView = text("0", 36, YELLOW, true);

        LinearLayout.LayoutParams numberParams =
                new LinearLayout.LayoutParams(-1, -2);

        numberParams.topMargin = dp(5);
        countCard.addView(countView, numberParams);

        countCard.addView(
                text("All subjects available in MyStudy", 12,
                        Color.WHITE, false)
        );

        // Add Subject

        Button addButton = button(
                "+  Add New Subject",
                YELLOW,
                DARK_BLUE
        );

        LinearLayout.LayoutParams addParams =
                new LinearLayout.LayoutParams(-1, dp(54));

        addParams.topMargin = dp(22);
        root.addView(addButton, addParams);

        addButton.setOnClickListener(v ->
                showAddSubjectDialog()
        );

        // Section Header

        LinearLayout section = new LinearLayout(this);
        section.setGravity(Gravity.CENTER_VERTICAL);

        TextView sectionTitle = text(
                "Available Subjects",
                19,
                TEXT,
                true
        );

        section.addView(
                sectionTitle,
                new LinearLayout.LayoutParams(0, -2, 1)
        );

        TextView badge = text("SUBJECTS", 10, BLUE, true);
        badge.setPadding(dp(12), dp(7), dp(12), dp(7));
        badge.setBackground(
                rounded(Color.rgb(219, 234, 254), 20)
        );

        section.addView(badge);

        LinearLayout.LayoutParams sectionParams =
                new LinearLayout.LayoutParams(-1, -2);

        sectionParams.topMargin = dp(28);
        root.addView(section, sectionParams);

        // Status

        statusView = text(
                "Loading subjects...",
                14,
                MUTED,
                false
        );

        statusView.setGravity(Gravity.CENTER);
        statusView.setPadding(dp(12), dp(25), dp(12), dp(25));

        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(-1, -2);

        statusParams.topMargin = dp(10);
        root.addView(statusView, statusParams);

        // Subject Container

        subjectContainer = new LinearLayout(this);
        subjectContainer.setOrientation(LinearLayout.VERTICAL);

        root.addView(subjectContainer);

        // Footer

        TextView footer = text(
                "MyStudy • Learn • Practice • Grow",
                12,
                MUTED,
                false
        );

        footer.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams footerParams =
                new LinearLayout.LayoutParams(-1, -2);

        footerParams.topMargin = dp(28);
        root.addView(footer, footerParams);

        scrollView.addView(root);
        setContentView(scrollView);
    }

    // LOAD SUBJECTS

    private void loadSubjects() {

        subjectContainer.removeAllViews();

        statusView.setVisibility(View.VISIBLE);
        statusView.setText("Loading subjects...");
        statusView.setTextColor(MUTED);

        db.collection("subjects")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    subjectContainer.removeAllViews();

                    int total = querySnapshot.size();
                    countView.setText(String.valueOf(total));

                    if (total == 0) {

                        statusView.setVisibility(View.VISIBLE);
                        statusView.setText(
                                "📚\n\nNo subjects added yet.\nTap Add New Subject to get started."
                        );

                        return;
                    }

                    statusView.setVisibility(View.GONE);

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        String name = document.getString("name");
                        String className = document.getString("className");
                        String classId = document.getString("classId");
                        String medium = document.getString("medium");

                        Long order = document.getLong("order");

                        if (name == null || name.trim().isEmpty()) {
                            name = "Unnamed Subject";
                        }

                        if (className == null || className.trim().isEmpty()) {
                            className = "Unknown Class";
                        }

                        if (medium == null || medium.trim().isEmpty()) {
                            medium = "English";
                        }

                        addSubjectCard(
                                document.getId(),
                                name,
                                classId,
                                className,
                                medium,
                                order == null ? 0 : order
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    countView.setText("--");
                    statusView.setVisibility(View.VISIBLE);
                    statusView.setTextColor(RED);
                    statusView.setText(
                            "Unable to load subjects.\nPlease try again."
                    );

                    Button retry = button(
                            "Retry",
                            BLUE,
                            Color.WHITE
                    );

                    LinearLayout.LayoutParams retryParams =
                            new LinearLayout.LayoutParams(dp(130), dp(48));

                    retryParams.gravity = Gravity.CENTER;

                    subjectContainer.addView(retry, retryParams);

                    retry.setOnClickListener(v -> loadSubjects());
                });
    }

    // SUBJECT CARD

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
        card.setPadding(dp(18), dp(18), dp(18), dp(18));

        card.setBackground(
                bordered(Color.WHITE,
                        Color.rgb(226, 232, 240), 20)
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(-1, -2);

        cardParams.setMargins(0, 0, 0, dp(15));
        subjectContainer.addView(card, cardParams);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView icon = text("📘", 25, BLUE, false);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(
                rounded(Color.rgb(239, 246, 255), 14)
        );

        top.addView(icon,
                new LinearLayout.LayoutParams(dp(52), dp(52)));

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(13), 0, 0, 0);

        details.addView(text(name, 18, TEXT, true));

        TextView classText = text(
                "Class: " + className,
                13,
                BLUE,
                true
        );

        LinearLayout.LayoutParams classParams =
                new LinearLayout.LayoutParams(-1, -2);

        classParams.topMargin = dp(5);
        details.addView(classText, classParams);

        top.addView(
                details,
                new LinearLayout.LayoutParams(0, -2, 1)
        );

        card.addView(top);

        // Medium and Order

        LinearLayout infoRow = new LinearLayout(this);
        infoRow.setOrientation(LinearLayout.HORIZONTAL);

        TextView mediumText = text(
                "Medium: " + medium,
                12,
                GREEN,
                true
        );

        mediumText.setPadding(dp(10), dp(8), dp(10), dp(8));
        mediumText.setBackground(
                rounded(Color.rgb(220, 252, 231), 10)
        );

        TextView orderText = text(
                "Order: " + order,
                12,
                MUTED,
                true
        );

        orderText.setPadding(dp(10), dp(8), dp(10), dp(8));
        orderText.setBackground(
                rounded(Color.rgb(241, 245, 249), 10)
        );

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(-1, -2);

        infoParams.topMargin = dp(14);

        infoRow.addView(mediumText);

        LinearLayout.LayoutParams orderParams =
                new LinearLayout.LayoutParams(-2, -2);

        orderParams.leftMargin = dp(8);
        infoRow.addView(orderText, orderParams);

        card.addView(infoRow);

        // Divider

        View divider = new View(this);
        divider.setBackgroundColor(
                Color.rgb(241, 245, 249)
        );

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(-1, dp(1));

        dividerParams.topMargin = dp(16);
        dividerParams.bottomMargin = dp(13);

        card.addView(divider, dividerParams);

        // Action Buttons

        LinearLayout actions = new LinearLayout(this);

        Button editButton = button(
                "✎  Edit",
                Color.rgb(219, 234, 254),
                BLUE
        );

        Button deleteButton = button(
                "Delete",
                Color.rgb(254, 226, 226),
                RED
        );

        LinearLayout.LayoutParams editParams =
                new LinearLayout.LayoutParams(0, dp(45), 1);

        editParams.rightMargin = dp(6);

        actions.addView(editButton, editParams);

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(0, dp(45), 1);

        deleteParams.leftMargin = dp(6);

        actions.addView(deleteButton, deleteParams);

        card.addView(actions);

        editButton.setOnClickListener(v ->
                showEditSubjectDialog(
                        documentId,
                        name,
                        classId,
                        className,
                        medium,
                        order
                )
        );

        deleteButton.setOnClickListener(v ->
                confirmDelete(documentId, name)
        );
    }

    // ADD SUBJECT

    private void showAddSubjectDialog() {
        loadClassesForDialog(false, null);
    }

    // EDIT SUBJECT

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

    // LOAD CLASSES

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

                        String name = document.getString("name");

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

                    showSubjectDialog(editMode, editData);
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load classes: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // SUBJECT FORM

    private void showSubjectDialog(
            boolean editMode,
            EditData editData
    ) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(22), dp(10), dp(22), dp(8));

        EditText nameInput = new EditText(this);
        nameInput.setHint("Enter subject name");
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
        orderInput.setHint("Display order e.g. 1");
        orderInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        orderInput.setSingleLine(true);

        layout.addView(nameInput);
        layout.addView(classSpinner);
        layout.addView(mediumSpinner);
        layout.addView(orderInput);

        if (editMode && editData != null) {

            nameInput.setText(editData.name);
            orderInput.setText(String.valueOf(editData.order));

            int classIndex = classIds.indexOf(editData.classId);

            if (classIndex >= 0) {
                classSpinner.setSelection(classIndex);
            }

            mediumSpinner.setSelection(
                    findMediumIndex(editData.medium)
            );
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(editMode ? "Edit Subject" : "Add Subject")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton(editMode ? "Update" : "Save", null)
                .create();

        // FIXED: unique lambda parameter name
        dialog.setOnShowListener(dialogEvent -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String name = nameInput.getText()
                                .toString().trim();

                        String orderString = orderInput.getText()
                                .toString().trim();

                        if (name.isEmpty()) {
                            nameInput.setError("Enter subject name");
                            return;
                        }

                        if (orderString.isEmpty()) {
                            orderInput.setError("Enter order");
                            return;
                        }

                        long order;

                        try {
                            order = Long.parseLong(orderString);
                        } catch (NumberFormatException e) {
                            orderInput.setError("Enter valid number");
                            return;
                        }

                        int classPosition =
                                classSpinner.getSelectedItemPosition();

                        int mediumPosition =
                                mediumSpinner.getSelectedItemPosition();

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

                        String selectedClassId =
                                classIds.get(classPosition);

                        String selectedClassName =
                                classNames.get(classPosition);

                        String selectedMedium =
                                mediums[mediumPosition];

                        Map<String, Object> data = new HashMap<>();

                        data.put("name", name);
                        data.put("classId", selectedClassId);
                        data.put("className", selectedClassName);
                        data.put("medium", selectedMedium);
                        data.put("order", order);

                        if (!editMode) {

                            data.put(
                                    "createdAt",
                                    FieldValue.serverTimestamp()
                            );

                            db.collection("subjects")
                                    .add(data)
                                    .addOnSuccessListener(addResult -> {

                                        Toast.makeText(
                                                this,
                                                "Subject added successfully.",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        dialog.dismiss();
                                        loadSubjects();
                                    })
                                    .addOnFailureListener(e -> {

                                        Toast.makeText(
                                                this,
                                                "Failed to add subject: "
                                                        + e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    });

                        } else {

                            db.collection("subjects")
                                    .document(editData.documentId)
                                    .update(data)
                                    .addOnSuccessListener(updateResult -> {

                                        Toast.makeText(
                                                this,
                                                "Subject updated successfully.",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        dialog.dismiss();
                                        loadSubjects();
                                    })
                                    .addOnFailureListener(e -> {

                                        Toast.makeText(
                                                this,
                                                "Failed to update subject: "
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

            if (mediums[i].equalsIgnoreCase(medium.trim())) {
                return i;
            }
        }

        return 0;
    }

    // DELETE SUBJECT

    private void confirmDelete(
            String documentId,
            String subjectName
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Subject?")
                .setMessage(
                        "Are you sure you want to delete \""
                                + subjectName + "\"?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> deleteSubject(documentId)
                )
                .show();
    }

    private void deleteSubject(String documentId) {

        db.collection("subjects")
                .document(documentId)
                .delete()
                .addOnSuccessListener(deleteResult -> {

                    Toast.makeText(
                            this,
                            "Subject deleted successfully.",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadSubjects();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Delete failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // EDIT DATA

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
