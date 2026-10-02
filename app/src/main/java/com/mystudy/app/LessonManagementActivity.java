package com.mystudy.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonManagementActivity extends AppCompatActivity {

    private static final int FILE_PICKER_REQUEST = 2001;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private LinearLayout lessonContainer;

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

    private Uri selectedFileUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

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

        addButton.setOnClickListener(v -> showAddLessonDialog());

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
        backButton.setOnClickListener(v -> finish());

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
                        empty.setTextColor(Color.rgb(100, 116, 139));
                        empty.setGravity(Gravity.CENTER);
                        empty.setPadding(0, 40, 0, 40);

                        lessonContainer.addView(empty);
                        return;
                    }

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        String documentId = document.getId();

                        String title = document.getString("title");
                        String description =
                                document.getString("description");
                        String content =
                                document.getString("content");

                        String classId =
                                document.getString("classId");
                        String className =
                                document.getString("className");

                        String medium =
                                document.getString("medium");

                        String subjectId =
                                document.getString("subjectId");
                        String subjectName =
                                document.getString("subjectName");

                        String chapterId =
                                document.getString("chapterId");
                        String chapterName =
                                document.getString("chapterName");

                        String contentType =
                                document.getString("contentType");
                        String contentUrl =
                                document.getString("contentUrl");
                        String storagePath =
                                document.getString("storagePath");

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

                        if (medium == null ||
                                medium.trim().isEmpty()) {
                            medium = "English";
                        }

                        if (subjectId == null) {
                            subjectId = "";
                        }

                        if (subjectName == null) {
                            subjectName = "Unknown Subject";
                        }

                        if (chapterId == null) {
                            chapterId = "";
                        }

                        if (chapterName == null) {
                            chapterName = "Unknown Chapter";
                        }

                        if (contentType == null ||
                                contentType.trim().isEmpty()) {
                            contentType = "text";
                        }

                        if (contentUrl == null) {
                            contentUrl = "";
                        }

                        if (storagePath == null) {
                            storagePath = "";
                        }

                        long order =
                                orderValue != null
                                        ? orderValue
                                        : 0;

                        addLessonCard(
                                documentId,
                                title,
                                description,
                                content,
                                classId,
                                className,
                                medium,
                                subjectId,
                                subjectName,
                                chapterId,
                                chapterName,
                                contentType,
                                contentUrl,
                                storagePath,
                                order
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
            String medium,
            String subjectId,
            String subjectName,
            String chapterId,
            String chapterName,
            String contentType,
            String contentUrl,
            String storagePath,
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
        classText.setText("Class: " + className);
        classText.setTextSize(14);
        classText.setTextColor(Color.rgb(79, 70, 229));

        TextView mediumText = new TextView(this);
        mediumText.setText("Medium: " + medium);
        mediumText.setTextSize(14);
        mediumText.setTextColor(Color.rgb(245, 158, 11));

        TextView subjectText = new TextView(this);
        subjectText.setText("Subject: " + subjectName);
        subjectText.setTextSize(15);
        subjectText.setTextColor(Color.rgb(14, 116, 144));

        TextView chapterText = new TextView(this);
        chapterText.setText("Chapter: " + chapterName);
        chapterText.setTextSize(15);
        chapterText.setTextColor(Color.rgb(16, 185, 129));

        TextView typeText = new TextView(this);
        typeText.setText("Content Type: " + contentType);
        typeText.setTextSize(14);
        typeText.setTextColor(Color.rgb(124, 58, 237));

        TextView descriptionText = new TextView(this);
        descriptionText.setText(
                description.isEmpty()
                        ? "No description"
                        : description
        );
        descriptionText.setTextSize(14);
        descriptionText.setTextColor(Color.rgb(71, 85, 105));
        descriptionText.setPadding(0, 8, 0, 4);

        TextView orderText = new TextView(this);
        orderText.setText("Order: " + order);
        orderText.setTextSize(14);
        orderText.setTextColor(Color.rgb(100, 116, 139));

        card.addView(titleText);
        card.addView(classText);
        card.addView(mediumText);
        card.addView(subjectText);
        card.addView(chapterText);
        card.addView(typeText);
        card.addView(descriptionText);
        card.addView(orderText);

        if (!contentUrl.isEmpty()) {

            TextView urlText = new TextView(this);
            urlText.setText("Attached: " + contentUrl);
            urlText.setTextSize(12);
            urlText.setTextColor(Color.rgb(100, 116, 139));
            urlText.setPadding(0, 6, 0, 6);

            card.addView(urlText);
        }

        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);

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
                        medium,
                        subjectId,
                        subjectName,
                        chapterId,
                        chapterName,
                        contentType,
                        contentUrl,
                        storagePath,
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

        cardParams.setMargins(0, 0, 0, 18);

        lessonContainer.addView(card, cardParams);
    }

    private void showAddLessonDialog() {
        loadClassesForDialog(false, null);
    }

    private void showEditLessonDialog(
            String documentId,
            String oldTitle,
            String oldDescription,
            String oldContent,
            String oldClassId,
            String oldClassName,
            String oldMedium,
            String oldSubjectId,
            String oldSubjectName,
            String oldChapterId,
            String oldChapterName,
            String oldContentType,
            String oldContentUrl,
            String oldStoragePath,
            long oldOrder
    ) {

        LessonEditData data = new LessonEditData();

        data.documentId = documentId;
        data.title = oldTitle;
        data.description = oldDescription;
        data.content = oldContent;
        data.classId = oldClassId;
        data.className = oldClassName;
        data.medium = oldMedium;
        data.subjectId = oldSubjectId;
        data.subjectName = oldSubjectName;
        data.chapterId = oldChapterId;
        data.chapterName = oldChapterName;
        data.contentType = oldContentType;
        data.contentUrl = oldContentUrl;
        data.storagePath = oldStoragePath;
        data.order = oldOrder;

        loadClassesForDialog(true, data);
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

        selectedFileUri = null;

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 10, 40, 10);

        ScrollView dialogScroll = new ScrollView(this);

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);

        EditText titleInput = new EditText(this);
        titleInput.setHint("Lesson title");
        titleInput.setSingleLine(true);

        EditText descriptionInput = new EditText(this);
        descriptionInput.setHint("Short description");
        descriptionInput.setSingleLine(false);

        EditText contentInput = new EditText(this);
        contentInput.setHint("Lesson text content");
        contentInput.setSingleLine(false);
        contentInput.setGravity(Gravity.TOP);
        contentInput.setMinLines(5);

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

        Spinner chapterSpinner = new Spinner(this);

        Spinner contentTypeSpinner = new Spinner(this);

        String[] contentTypes = {
                "TEXT",
                "IMAGE",
                "PDF",
                "VIDEO",
                "AUDIO",
                "LINK"
        };

        ArrayAdapter<String> contentTypeAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        contentTypes
                );

        contentTypeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        contentTypeSpinner.setAdapter(contentTypeAdapter);

        EditText linkInput = new EditText(this);
        linkInput.setHint("External learning URL");
        linkInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_URI
        );
        linkInput.setSingleLine(true);
        linkInput.setVisibility(View.GONE);

        Button fileButton = new Button(this);
        fileButton.setText("Choose File");
        fileButton.setAllCaps(false);
        fileButton.setVisibility(View.GONE);

        TextView selectedFileText = new TextView(this);
        selectedFileText.setText("No file selected");
        selectedFileText.setTextSize(13);
        selectedFileText.setTextColor(Color.rgb(100, 116, 139));
        selectedFileText.setPadding(0, 4, 0, 10);
        selectedFileText.setVisibility(View.GONE);

        EditText orderInput = new EditText(this);
        orderInput.setHint("Order e.g. 1");
        orderInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        orderInput.setSingleLine(true);

        form.addView(titleInput);
        form.addView(descriptionInput);
        form.addView(contentInput);

        addLabel(form, "Class");
        form.addView(classSpinner);

        addLabel(form, "Medium");
        form.addView(mediumSpinner);

        addLabel(form, "Subject");
        form.addView(subjectSpinner);

        addLabel(form, "Chapter");
        form.addView(chapterSpinner);

        addLabel(form, "Content Type");
        form.addView(contentTypeSpinner);

        form.addView(linkInput);
        form.addView(fileButton);
        form.addView(selectedFileText);
        form.addView(orderInput);

        dialogScroll.addView(form);
        layout.addView(dialogScroll);

        if (editMode && editData != null) {

            titleInput.setText(editData.title);
            descriptionInput.setText(editData.description);
            contentInput.setText(editData.content);

            orderInput.setText(
                    String.valueOf(editData.order)
            );

            int classPosition =
                    classIds.indexOf(editData.classId);

            if (classPosition >= 0) {
                classSpinner.setSelection(classPosition);
            }

            int mediumPosition =
                    findPosition(
                            mediums,
                            editData.medium
                    );

            if (mediumPosition >= 0) {
                mediumSpinner.setSelection(mediumPosition);
            }

            int contentTypePosition =
                    findPosition(
                            contentTypes,
                            editData.contentType
                    );

            if (contentTypePosition >= 0) {
                contentTypeSpinner.setSelection(
                        contentTypePosition
                );
            }

            if ("LINK".equalsIgnoreCase(
                    editData.contentType
            )) {

                linkInput.setVisibility(View.VISIBLE);
                linkInput.setText(editData.contentUrl);

            } else if (
                    !"TEXT".equalsIgnoreCase(
                            editData.contentType
                    )
                            && !editData.contentUrl.isEmpty()
            ) {

                selectedFileText.setVisibility(
                        View.VISIBLE
                );

                selectedFileText.setText(
                        "Existing file: "
                                + editData.contentUrl
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

                        refreshSubjects(
                                classSpinner,
                                mediumSpinner,
                                subjectSpinner,
                                chapterSpinner,
                                editMode,
                                editData
                        );
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

                        refreshSubjects(
                                classSpinner,
                                mediumSpinner,
                                subjectSpinner,
                                chapterSpinner,
                                editMode,
                                editData
                        );
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                }
        );

        subjectSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        if (position >= 0 &&
                                position < subjectIds.size() &&
                                classSpinner
                                        .getSelectedItemPosition()
                                        >= 0 &&
                                mediumSpinner
                                        .getSelectedItemPosition()
                                        >= 0) {

                            String selectedClassId =
                                    classIds.get(
                                            classSpinner
                                                    .getSelectedItemPosition()
                                    );

                            String selectedMedium =
                                    mediums[
                                            mediumSpinner
                                                    .getSelectedItemPosition()
                                    ];

                            loadChaptersForClassSubjectAndMedium(
                                    selectedClassId,
                                    subjectIds.get(position),
                                    selectedMedium,
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

        contentTypeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        if (position >= 0 &&
                                position < contentTypes.length) {

                            String selectedType =
                                    contentTypes[position];

                            updateContentControls(
                                    selectedType,
                                    linkInput,
                                    fileButton,
                                    selectedFileText,
                                    contentInput
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

        fileButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Intent.ACTION_OPEN_DOCUMENT
            );

            intent.addCategory(
                    Intent.CATEGORY_OPENABLE
            );

            intent.setType("*/*");

            startActivityForResult(
                    intent,
                    FILE_PICKER_REQUEST
            );
        });

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

        dialog.setOnShowListener(ignored -> {

            dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                String title =
                        titleInput.getText()
                                .toString()
                                .trim();

                String description =
                        descriptionInput.getText()
                                .toString()
                                .trim();

                String content =
                        contentInput.getText()
                                .toString()
                                .trim();

                String orderString =
                        orderInput.getText()
                                .toString()
                                .trim();

                int classPosition =
                        classSpinner
                                .getSelectedItemPosition();

                int mediumPosition =
                        mediumSpinner
                                .getSelectedItemPosition();

                int subjectPosition =
                        subjectSpinner
                                .getSelectedItemPosition();

                int chapterPosition =
                        chapterSpinner
                                .getSelectedItemPosition();

                int contentTypePosition =
                        contentTypeSpinner
                                .getSelectedItemPosition();

                if (title.isEmpty()) {

                    titleInput.setError(
                            "Enter lesson title"
                    );

                    return;
                }

                if (orderString.isEmpty()) {

                    orderInput.setError(
                            "Enter order"
                    );

                    return;
                }

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

                if (chapterPosition < 0 ||
                        chapterPosition >= chapterIds.size()) {

                    Toast.makeText(
                            this,
                            "Select a chapter for selected medium.",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                if (contentTypePosition < 0 ||
                        contentTypePosition >= contentTypes.length) {

                    Toast.makeText(
                            this,
                            "Select content type.",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                long order;

                try {

                    order =
                            Long.parseLong(orderString);

                } catch (Exception e) {

                    orderInput.setError(
                            "Enter valid number"
                    );

                    return;
                }

                String selectedContentType =
                        contentTypes[
                                contentTypePosition
                        ];

                if ("TEXT".equals(
                        selectedContentType
                ) && content.isEmpty()) {

                    contentInput.setError(
                            "Enter lesson content"
                    );

                    return;
                }

                String selectedLink =
                        linkInput.getText()
                                .toString()
                                .trim();

                if ("LINK".equals(
                        selectedContentType
                ) && selectedLink.isEmpty()) {

                    linkInput.setError(
                            "Enter external URL"
                    );

                    return;
                }

                if (!"TEXT".equals(
                        selectedContentType
                )
                        && !"LINK".equals(
                        selectedContentType
                )
                        && !editMode
                        && selectedFileUri == null) {

                    Toast.makeText(
                            this,
                            "Please choose a file.",
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

                String selectedChapterId =
                        chapterIds.get(chapterPosition);

                String selectedChapterName =
                        chapterNames.get(chapterPosition);

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
                        "order",
                        order
                );

                data.put(
                        "contentType",
                        selectedContentType.toLowerCase()
                );

                if ("LINK".equals(
                        selectedContentType
                )) {

                    data.put(
                            "contentUrl",
                            selectedLink
                    );

                    saveLessonData(
                            editMode,
                            editData,
                            data,
                            dialog
                    );

                    return;
                }

                if ("TEXT".equals(
                        selectedContentType
                )) {

                    data.put(
                            "contentUrl",
                            ""
                    );

                    data.put(
                            "storagePath",
                            ""
                    );

                    saveLessonData(
                            editMode,
                            editData,
                            data,
                            dialog
                    );

                    return;
                }

                if (selectedFileUri != null) {

                    uploadLessonFile(
                            selectedFileUri,
                            title,
                            selectedContentType,
                            data,
                            editMode,
                            editData,
                            dialog
                    );

                } else if (
                        editMode &&
                                editData != null
                ) {

                    data.put(
                            "contentUrl",
                            editData.contentUrl
                    );

                    data.put(
                            "storagePath",
                            editData.storagePath
                    );

                    saveLessonData(
                            editMode,
                            editData,
                            data,
                            dialog
                    );
                }
            });
        });

        dialog.show();
    }

    private void refreshSubjects(
            Spinner classSpinner,
            Spinner mediumSpinner,
            Spinner subjectSpinner,
            Spinner chapterSpinner,
            boolean editMode,
            LessonEditData editData
    ) {

        int classPosition =
                classSpinner.getSelectedItemPosition();

        int mediumPosition =
                mediumSpinner.getSelectedItemPosition();

        if (classPosition < 0 ||
                classPosition >= classIds.size()) {
            return;
        }

        if (mediumPosition < 0 ||
                mediumPosition >= mediums.length) {
            return;
        }

        String selectedClassId =
                classIds.get(classPosition);

        String selectedMedium =
                mediums[mediumPosition];

        loadSubjectsForClassAndMedium(
                selectedClassId,
                selectedMedium,
                subjectSpinner,
                chapterSpinner,
                editMode,
                editData
        );
    }

    private void updateContentControls(
            String type,
            EditText linkInput,
            Button fileButton,
            TextView selectedFileText,
            EditText contentInput
    ) {

        if ("LINK".equals(type)) {

            linkInput.setVisibility(View.VISIBLE);
            fileButton.setVisibility(View.GONE);
            selectedFileText.setVisibility(View.GONE);

            contentInput.setVisibility(View.GONE);

        } else if ("TEXT".equals(type)) {

            linkInput.setVisibility(View.GONE);
            fileButton.setVisibility(View.GONE);
            selectedFileText.setVisibility(View.GONE);

            contentInput.setVisibility(View.VISIBLE);

        } else {

            linkInput.setVisibility(View.GONE);
            fileButton.setVisibility(View.VISIBLE);
            selectedFileText.setVisibility(View.VISIBLE);

            contentInput.setVisibility(View.GONE);
        }
    }

    private void uploadLessonFile(
            Uri fileUri,
            String lessonTitle,
            String contentType,
            Map<String, Object> data,
            boolean editMode,
            LessonEditData editData,
            AlertDialog dialog
    ) {

        Toast.makeText(
                this,
                "Uploading file...",
                Toast.LENGTH_SHORT
        ).show();

        String safeTitle =
                lessonTitle.replaceAll(
                        "[^a-zA-Z0-9_-]",
                        "_"
                );

        String extension =
                getFileExtension(fileUri);

        String fileName =
                System.currentTimeMillis()
                        + "_"
                        + safeTitle;

        if (!extension.isEmpty()) {
            fileName += "." + extension;
        }

        String storagePath =
                "lessons/"
                        + data.get("classId")
                        + "/"
                        + data.get("subjectId")
                        + "/"
                        + fileName;

        StorageReference fileReference =
                storage.getReference()
                        .child(storagePath);

        fileReference.putFile(fileUri)
                .addOnSuccessListener(
                        taskSnapshot ->
                                fileReference
                                        .getDownloadUrl()
                                        .addOnSuccessListener(
                                                downloadUri -> {

                                                    data.put(
                                                            "contentUrl",
                                                            downloadUri.toString()
                                                    );

                                                    data.put(
                                                            "storagePath",
                                                            storagePath
                                                    );

                                                    saveLessonData(
                                                            editMode,
                                                            editData,
                                                            data,
                                                            dialog
                                                    );
                                                }
                                        )
                                        .addOnFailureListener(
                                                e ->
                                                        Toast.makeText(
                                                                this,
                                                                "Failed to get file URL: "
                                                                        + e.getMessage(),
                                                                Toast.LENGTH_LONG
                                                        ).show()
                                        )
                )
                .addOnFailureListener(
                        e ->
                                Toast.makeText(
                                        this,
                                        "File upload failed: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show()
                );
    }

    private void saveLessonData(
            boolean editMode,
            LessonEditData editData,
            Map<String, Object> data,
            AlertDialog dialog
    ) {

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
                    .document(editData.documentId)
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
    }

    private void loadSubjectsForClassAndMedium(
            String classId,
            String medium,
            Spinner subjectSpinner,
            Spinner chapterSpinner,
            boolean editMode,
            LessonEditData editData
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

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        subjectIds.add(
                                document.getId()
                        );

                        String name =
                                document.getString("name");

                        if (name == null) {
                            name = "Unnamed Subject";
                        }

                        subjectNames.add(name);
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

                    chapterIds.clear();
                    chapterNames.clear();

                    ArrayAdapter<String> emptyChapterAdapter =
                            new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_spinner_item,
                                    chapterNames
                            );

                    emptyChapterAdapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                    );

                    chapterSpinner.setAdapter(
                            emptyChapterAdapter
                    );

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

    private void loadChaptersForClassSubjectAndMedium(
            String classId,
            String subjectId,
            String medium,
            Spinner chapterSpinner,
            boolean editMode,
            LessonEditData editData
    ) {

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
                .addOnSuccessListener(querySnapshot -> {

                    chapterIds.clear();
                    chapterNames.clear();

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        chapterIds.add(
                                document.getId()
                        );

                        String name =
                                document.getString("name");

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

                    chapterSpinner.setAdapter(adapter);

                    if (editMode &&
                            editData != null) {

                        int position =
                                chapterIds.indexOf(
                                        editData.chapterId
                                );

                        if (position >= 0) {

                            chapterSpinner.setSelection(
                                    position
                            );
                        }
                    }

                })
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

    private void addLabel(
            LinearLayout parent,
            String text
    ) {

        TextView label = new TextView(this);
        label.setText(text);
        label.setTextSize(13);
        label.setTextColor(Color.rgb(71, 85, 105));
        label.setPadding(0, 12, 0, 4);

        parent.addView(label);
    }

    private int findPosition(
            String[] values,
            String value
    ) {

        if (value == null) {
            return -1;
        }

        for (int i = 0; i < values.length; i++) {

            if (values[i].equalsIgnoreCase(
                    value.trim()
            )) {
                return i;
            }
        }

        return -1;
    }

    private String getFileExtension(Uri uri) {

        String name =
                uri.getLastPathSegment();

        if (name == null) {
            return "";
        }

        int dot =
                name.lastIndexOf(".");

        if (dot >= 0 &&
                dot < name.length() - 1) {

            return name.substring(dot + 1);
        }

        return "";
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode ==
                FILE_PICKER_REQUEST &&
                resultCode ==
                        RESULT_OK &&
                data != null) {

            selectedFileUri =
                    data.getData();

            Toast.makeText(
                    this,
                    "File selected successfully",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void confirmDelete(
            String documentId,
            String lessonTitle
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Lesson")
                .setMessage(
                        "Delete \""
                                + lessonTitle
                                + "\"?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            db.collection("lessons")
                                    .document(documentId)
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

        String medium;

        String subjectId;
        String subjectName;

        String chapterId;
        String chapterName;

        String contentType;
        String contentUrl;
        String storagePath;

        long order;
    }
}
