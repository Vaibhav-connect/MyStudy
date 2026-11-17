package com.mystudy.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

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

    private final int BLUE = Color.rgb(37, 99, 235);
    private final int DARK = Color.rgb(30, 41, 59);
    private final int YELLOW = Color.rgb(250, 204, 21);
    private final int BG = Color.rgb(245, 248, 255);
    private final int GRAY = Color.rgb(100, 116, 139);

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private LinearLayout lessonContainer;
    private TextView countText;
    private TextView statusText;
    private ProgressBar progressBar;

    private final List<String> classIds = new ArrayList<>();
    private final List<String> classNames = new ArrayList<>();

    private final List<String> subjectIds = new ArrayList<>();
    private final List<String> subjectNames = new ArrayList<>();

    private final List<String> chapterIds = new ArrayList<>();
    private final List<String> chapterNames = new ArrayList<>();

    private final String[] mediums = {
            "English", "Semi-English", "Marathi", "Hindi"
    };

    private final String[] contentTypes = {
            "TEXT", "IMAGE", "PDF", "VIDEO", "AUDIO", "LINK"
    };

    private Uri selectedFileUri;
    private TextView selectedFileText;
    private AlertDialog activeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        createUI();
        loadLessons();
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }

        return view;
    }

    private Button button(String label) {
        Button btn = new Button(this);
        btn.setText(label);
        btn.setAllCaps(false);
        btn.setTextSize(14);
        return btn;
    }

    private void createUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(20), dp(22), dp(20), dp(22));
        header.setBackground(rounded(BLUE, 22));

        TextView brand = text("📘 MyStudy", 17, Color.WHITE, true);
        TextView title = text("Lesson Management", 25, Color.WHITE, true);
        TextView subtitle = text(
                "Create and manage learning lessons",
                13,
                Color.WHITE,
                false
        );

        header.addView(brand);
        header.addView(title);
        header.addView(subtitle);

        LinearLayout.LayoutParams headerParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        headerParams.setMargins(dp(16), dp(16), dp(16), dp(12));
        root.addView(header, headerParams);

        LinearLayout countCard = new LinearLayout(this);
        countCard.setOrientation(LinearLayout.VERTICAL);
        countCard.setPadding(dp(18), dp(15), dp(18), dp(15));
        countCard.setBackground(rounded(Color.WHITE, 18));

        countText = text("Total Lessons: 0", 18, DARK, true);
        countCard.addView(countText);

        LinearLayout.LayoutParams countParams =
                new LinearLayout.LayoutParams(-1, -2);

        countParams.setMargins(dp(16), dp(4), dp(16), dp(12));
        root.addView(countCard, countParams);

        Button addButton = button("＋ Add New Lesson");
        addButton.setTextColor(Color.WHITE);
        addButton.setBackground(rounded(BLUE, 14));
        addButton.setOnClickListener(v -> showAddLessonDialog());

        LinearLayout.LayoutParams addParams =
                new LinearLayout.LayoutParams(-1, dp(52));

        addParams.setMargins(dp(16), 0, dp(16), dp(12));
        root.addView(addButton, addParams);

        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);

        root.addView(progressBar);

        statusText = text("", 14, GRAY, false);
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(dp(16), dp(12), dp(16), dp(12));
        statusText.setVisibility(View.GONE);

        root.addView(statusText);

        ScrollView scrollView = new ScrollView(this);

        lessonContainer = new LinearLayout(this);
        lessonContainer.setOrientation(LinearLayout.VERTICAL);
        lessonContainer.setPadding(dp(16), dp(8), dp(16), dp(20));

        scrollView.addView(lessonContainer);

        root.addView(
                scrollView,
                new LinearLayout.LayoutParams(0, 0, 1)
        );

        Button backButton = button("← Back to Dashboard");
        backButton.setOnClickListener(v -> finish());

        LinearLayout.LayoutParams backParams =
                new LinearLayout.LayoutParams(-1, dp(48));

        backParams.setMargins(dp(16), dp(6), dp(16), dp(14));
        root.addView(backButton, backParams);

        setContentView(root);
    }

    private void showStatus(String message, boolean loading) {

        statusText.setText(message);
        statusText.setVisibility(message.isEmpty() ? View.GONE : View.VISIBLE);

        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void loadLessons() {

        lessonContainer.removeAllViews();
        showStatus("Loading lessons...", true);

        db.collection("lessons")
                .get()
                .addOnSuccessListener(snapshot -> {

                    showStatus("", false);

                    int total = snapshot.size();
                    countText.setText("Total Lessons: " + total);

                    if (total == 0) {
                        showEmptyState();
                        return;
                    }

                    for (DocumentSnapshot document : snapshot.getDocuments()) {

                        String id = document.getId();

                        String title = value(document, "title", "Untitled Lesson");
                        String description = value(document, "description", "");
                        String content = value(document, "content", "");

                        String classId = value(document, "classId", "");
                        String className = value(document, "className", "Unknown Class");
                        String medium = value(document, "medium", "English");

                        String subjectId = value(document, "subjectId", "");
                        String subjectName = value(document, "subjectName", "Unknown Subject");

                        String chapterId = value(document, "chapterId", "");
                        String chapterName = value(document, "chapterName", "Unknown Chapter");

                        String type = value(document, "contentType", "text");
                        String url = value(document, "contentUrl", "");
                        String path = value(document, "storagePath", "");

                        Long orderValue = document.getLong("order");
                        long order = orderValue != null ? orderValue : 0;

                        addLessonCard(
                                id, title, description, content,
                                classId, className, medium,
                                subjectId, subjectName,
                                chapterId, chapterName,
                                type, url, path, order
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    showStatus("Unable to load lessons.", false);

                    Button retry = button("Retry");
                    retry.setOnClickListener(v -> loadLessons());

                    lessonContainer.addView(retry);
                });
    }

    private String value(DocumentSnapshot document, String field, String fallback) {
        String result = document.getString(field);
        return result == null ? fallback : result;
    }

    private void showEmptyState() {

        LinearLayout empty = new LinearLayout(this);
        empty.setOrientation(LinearLayout.VERTICAL);
        empty.setGravity(Gravity.CENTER);
        empty.setPadding(dp(20), dp(45), dp(20), dp(45));
        empty.setBackground(rounded(Color.WHITE, 20));

        TextView icon = text("📚", 42, DARK, false);
        icon.setGravity(Gravity.CENTER);

        TextView title = text("No Lessons Yet", 20, DARK, true);
        title.setGravity(Gravity.CENTER);

        TextView message = text(
                "Add your first lesson to get started.",
                14,
                GRAY,
                false
        );
        message.setGravity(Gravity.CENTER);

        empty.addView(icon);
        empty.addView(title);
        empty.addView(message);

        lessonContainer.addView(empty);
    }

    private void addLessonCard(
            String id,
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
        card.setPadding(dp(18), dp(16), dp(18), dp(16));
        card.setBackground(rounded(Color.WHITE, 20));

        TextView lessonTitle = text(title, 19, DARK, true);

        TextView classInfo = text(
                "Class: " + className + "  •  " + medium,
                13,
                BLUE,
                true
        );

        TextView subjectInfo = text(
                "Subject: " + subjectName,
                14,
                DARK,
                false
        );

        TextView chapterInfo = text(
                "Chapter: " + chapterName,
                14,
                DARK,
                false
        );

        TextView typeInfo = text(
                "Content Type: " + contentType.toUpperCase(),
                13,
                BLUE,
                true
        );

        TextView descriptionInfo = text(
                description.isEmpty() ? "No description available" : description,
                13,
                GRAY,
                false
        );

        TextView orderInfo = text("Lesson Order: " + order, 12, GRAY, false);

        card.addView(lessonTitle);
        card.addView(classInfo);
        card.addView(subjectInfo);
        card.addView(chapterInfo);
        card.addView(typeInfo);
        card.addView(descriptionInfo);
        card.addView(orderInfo);

        if (!contentUrl.isEmpty()) {
            TextView attached = text("📎 Learning file attached", 12, BLUE, false);
            card.addView(attached);
        }

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button edit = button("Edit");
        Button delete = button("Delete");

        edit.setOnClickListener(v -> showEditLessonDialog(
                id, title, description, content,
                classId, className, medium,
                subjectId, subjectName,
                chapterId, chapterName,
                contentType, contentUrl, storagePath, order
        ));

        delete.setOnClickListener(v -> confirmDelete(id, title));

        actions.addView(edit, new LinearLayout.LayoutParams(0, dp(45), 1));
        actions.addView(delete, new LinearLayout.LayoutParams(0, dp(45), 1));

        card.addView(actions);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);

        params.setMargins(0, 0, 0, dp(14));

        lessonContainer.addView(card, params);
    }

    private void showAddLessonDialog() {
        loadClassesForDialog(false, null);
    }

    private void showEditLessonDialog(
            String id,
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

        LessonEditData data = new LessonEditData();

        data.documentId = id;
        data.title = title;
        data.description = description;
        data.content = content;
        data.classId = classId;
        data.className = className;
        data.medium = medium;
        data.subjectId = subjectId;
        data.subjectName = subjectName;
        data.chapterId = chapterId;
        data.chapterName = chapterName;
        data.contentType = contentType;
        data.contentUrl = contentUrl;
        data.storagePath = storagePath;
        data.order = order;

        loadClassesForDialog(true, data);
    }

    private void loadClassesForDialog(boolean editMode, LessonEditData editData) {

        db.collection("classes")
                .orderBy("order")
                .get()
                .addOnSuccessListener(snapshot -> {

                    classIds.clear();
                    classNames.clear();

                    for (DocumentSnapshot document : snapshot.getDocuments()) {

                        classIds.add(document.getId());
                        classNames.add(value(document, "name", "Unnamed Class"));
                    }

                    if (classIds.isEmpty()) {
                        Toast.makeText(this, "Please add a class first.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    showLessonDialog(editMode, editData);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load classes: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    private void showLessonDialog(boolean editMode, LessonEditData editData) {

        selectedFileUri = null;

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(8), dp(20), dp(8));

        ScrollView scroll = new ScrollView(this);
        scroll.addView(form);

        EditText titleInput = new EditText(this);
        titleInput.setHint("Lesson title");
        titleInput.setSingleLine(true);

        EditText descriptionInput = new EditText(this);
        descriptionInput.setHint("Short description");
        descriptionInput.setMinLines(2);

        EditText contentInput = new EditText(this);
        contentInput.setHint("Lesson text content");
        contentInput.setMinLines(4);
        contentInput.setGravity(Gravity.TOP);

        Spinner classSpinner = new Spinner(this);
        Spinner mediumSpinner = new Spinner(this);
        Spinner subjectSpinner = new Spinner(this);
        Spinner chapterSpinner = new Spinner(this);
        Spinner typeSpinner = new Spinner(this);

        setSpinner(classSpinner, classNames);
        setSpinner(mediumSpinner, java.util.Arrays.asList(mediums));
        setSpinner(typeSpinner, java.util.Arrays.asList(contentTypes));

        EditText linkInput = new EditText(this);
        linkInput.setHint("External learning URL");
        linkInput.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        linkInput.setSingleLine(true);

        Button fileButton = button("Choose File");
        selectedFileText = text("No file selected", 12, GRAY, false);

        EditText orderInput = new EditText(this);
        orderInput.setHint("Lesson order e.g. 1");
        orderInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        orderInput.setSingleLine(true);

        addField(form, "Lesson Title", titleInput);
        addField(form, "Description", descriptionInput);
        addField(form, "Text Content", contentInput);
        addField(form, "Class", classSpinner);
        addField(form, "Medium", mediumSpinner);
        addField(form, "Subject", subjectSpinner);
        addField(form, "Chapter", chapterSpinner);
        addField(form, "Content Type", typeSpinner);

        form.addView(linkInput);
        form.addView(fileButton);
        form.addView(selectedFileText);

        addField(form, "Display Order", orderInput);

        linkInput.setVisibility(View.GONE);
        fileButton.setVisibility(View.GONE);
        selectedFileText.setVisibility(View.GONE);

        if (editMode && editData != null) {

            titleInput.setText(editData.title);
            descriptionInput.setText(editData.description);
            contentInput.setText(editData.content);
            orderInput.setText(String.valueOf(editData.order));

            int cp = classIds.indexOf(editData.classId);
            if (cp >= 0) classSpinner.setSelection(cp);

            int mp = findPosition(mediums, editData.medium);
            if (mp >= 0) mediumSpinner.setSelection(mp);

            int tp = findPosition(contentTypes, editData.contentType);
            if (tp >= 0) typeSpinner.setSelection(tp);

            if ("LINK".equalsIgnoreCase(editData.contentType)) {
                linkInput.setText(editData.contentUrl);
            } else if (!"TEXT".equalsIgnoreCase(editData.contentType)
                    && !editData.contentUrl.isEmpty()) {
                selectedFileText.setText("Existing file attached");
            }
        }

        classSpinner.setOnItemSelectedListener(new SimpleListener(() ->
                refreshSubjects(classSpinner, mediumSpinner, subjectSpinner,
                        chapterSpinner, editMode, editData)
        ));

        mediumSpinner.setOnItemSelectedListener(new SimpleListener(() ->
                refreshSubjects(classSpinner, mediumSpinner, subjectSpinner,
                        chapterSpinner, editMode, editData)
        ));

        subjectSpinner.setOnItemSelectedListener(new SimpleListener(() -> {

            int cp = classSpinner.getSelectedItemPosition();
            int mp = mediumSpinner.getSelectedItemPosition();
            int sp = subjectSpinner.getSelectedItemPosition();

            if (cp >= 0 && mp >= 0 && sp >= 0
                    && cp < classIds.size() && sp < subjectIds.size()) {

                loadChapters(
                        classIds.get(cp),
                        subjectIds.get(sp),
                        mediums[mp],
                        chapterSpinner,
                        editMode,
                        editData
                );
            }
        }));

        typeSpinner.setOnItemSelectedListener(new SimpleListener(() -> {

            int position = typeSpinner.getSelectedItemPosition();

            if (position < 0 || position >= contentTypes.length) return;

            String type = contentTypes[position];

            contentInput.setVisibility("TEXT".equals(type) ? View.VISIBLE : View.GONE);
            linkInput.setVisibility("LINK".equals(type) ? View.VISIBLE : View.GONE);

            boolean fileType = !type.equals("TEXT") && !type.equals("LINK");

            fileButton.setVisibility(fileType ? View.VISIBLE : View.GONE);
            selectedFileText.setVisibility(fileType ? View.VISIBLE : View.GONE);
        }));

        fileButton.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");

            startActivityForResult(intent, FILE_PICKER_REQUEST);
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(editMode ? "Edit Lesson" : "Add Lesson")
                .setView(scroll)
                .setNegativeButton("Cancel", null)
                .setPositiveButton(editMode ? "Save Changes" : "Add Lesson", null)
                .create();

        activeDialog = dialog;

        dialog.setOnShowListener(dialogEvent -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

                String title = titleInput.getText().toString().trim();
                String description = descriptionInput.getText().toString().trim();
                String content = contentInput.getText().toString().trim();
                String orderString = orderInput.getText().toString().trim();

                int cp = classSpinner.getSelectedItemPosition();
                int mp = mediumSpinner.getSelectedItemPosition();
                int sp = subjectSpinner.getSelectedItemPosition();
                int chp = chapterSpinner.getSelectedItemPosition();
                int tp = typeSpinner.getSelectedItemPosition();

                if (title.isEmpty()) {
                    titleInput.setError("Enter lesson title");
                    return;
                }

                if (orderString.isEmpty()) {
                    orderInput.setError("Enter lesson order");
                    return;
                }

                if (cp < 0 || cp >= classIds.size()
                        || mp < 0 || mp >= mediums.length
                        || sp < 0 || sp >= subjectIds.size()
                        || chp < 0 || chp >= chapterIds.size()
                        || tp < 0 || tp >= contentTypes.length) {

                    Toast.makeText(this, "Please select all required options.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                long order;

                try {
                    order = Long.parseLong(orderString);
                } catch (Exception e) {
                    orderInput.setError("Enter a valid number");
                    return;
                }

                String type = contentTypes[tp];
                String link = linkInput.getText().toString().trim();

                if ("TEXT".equals(type) && content.isEmpty()) {
                    contentInput.setError("Enter lesson content");
                    return;
                }

                if ("LINK".equals(type) && link.isEmpty()) {
                    linkInput.setError("Enter external URL");
                    return;
                }

                if (!"TEXT".equals(type) && !"LINK".equals(type)
                        && selectedFileUri == null
                        && (!editMode || editData == null
                        || editData.contentUrl.isEmpty())) {

                    Toast.makeText(this, "Please choose a file.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> data = new HashMap<>();

                data.put("title", title);
                data.put("description", description);
                data.put("content", content);

                data.put("classId", classIds.get(cp));
                data.put("className", classNames.get(cp));
                data.put("medium", mediums[mp]);

                data.put("subjectId", subjectIds.get(sp));
                data.put("subjectName", subjectNames.get(sp));

                data.put("chapterId", chapterIds.get(chp));
                data.put("chapterName", chapterNames.get(chp));

                data.put("order", order);
                data.put("contentType", type.toLowerCase());

                if ("TEXT".equals(type)) {
                    data.put("contentUrl", "");
                    data.put("storagePath", "");

                    saveLessonData(editMode, editData, data, dialog);
                    return;
                }

                if ("LINK".equals(type)) {
                    data.put("contentUrl", link);
                    data.put("storagePath", "");

                    saveLessonData(editMode, editData, data, dialog);
                    return;
                }

                if (selectedFileUri != null) {

                    uploadLessonFile(
                            selectedFileUri,
                            title,
                            type,
                            data,
                            editMode,
                            editData,
                            dialog
                    );

                } else if (editMode && editData != null) {

                    data.put("contentUrl", editData.contentUrl);
                    data.put("storagePath", editData.storagePath);

                    saveLessonData(editMode, editData, data, dialog);
                }
            });
        });

        dialog.show();
    }

    private void setSpinner(Spinner spinner, List<String> values) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                values
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }

    private void addField(LinearLayout parent, String label, View field) {

        TextView heading = text(label, 13, DARK, true);
        heading.setPadding(0, dp(10), 0, dp(2));

        parent.addView(heading);
        parent.addView(field);
    }

    private void refreshSubjects(
            Spinner classSpinner,
            Spinner mediumSpinner,
            Spinner subjectSpinner,
            Spinner chapterSpinner,
            boolean editMode,
            LessonEditData editData
    ) {

        int cp = classSpinner.getSelectedItemPosition();
        int mp = mediumSpinner.getSelectedItemPosition();

        if (cp < 0 || cp >= classIds.size()
                || mp < 0 || mp >= mediums.length) return;

        loadSubjects(
                classIds.get(cp),
                mediums[mp],
                subjectSpinner,
                chapterSpinner,
                editMode,
                editData
        );
    }

    private void loadSubjects(
            String classId,
            String medium,
            Spinner subjectSpinner,
            Spinner chapterSpinner,
            boolean editMode,
            LessonEditData editData
    ) {

        subjectIds.clear();
        subjectNames.clear();
        chapterIds.clear();
        chapterNames.clear();

        setSpinner(subjectSpinner, subjectNames);
        setSpinner(chapterSpinner, chapterNames);

        db.collection("subjects")
                .whereEqualTo("classId", classId)
                .whereEqualTo("medium", medium)
                .get()
                .addOnSuccessListener(snapshot -> {

                    subjectIds.clear();
                    subjectNames.clear();

                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        subjectIds.add(document.getId());
                        subjectNames.add(value(document, "name", "Unnamed Subject"));
                    }

                    setSpinner(subjectSpinner, subjectNames);

                    if (editMode && editData != null) {
                        int position = subjectIds.indexOf(editData.subjectId);
                        if (position >= 0) subjectSpinner.setSelection(position);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load subjects: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    private void loadChapters(
            String classId,
            String subjectId,
            String medium,
            Spinner chapterSpinner,
            boolean editMode,
            LessonEditData editData
    ) {

        chapterIds.clear();
        chapterNames.clear();
        setSpinner(chapterSpinner, chapterNames);

        db.collection("chapters")
                .whereEqualTo("classId", classId)
                .whereEqualTo("subjectId", subjectId)
                .whereEqualTo("medium", medium)
                .get()
                .addOnSuccessListener(snapshot -> {

                    chapterIds.clear();
                    chapterNames.clear();

                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        chapterIds.add(document.getId());
                        chapterNames.add(value(document, "name", "Unnamed Chapter"));
                    }

                    setSpinner(chapterSpinner, chapterNames);

                    if (editMode && editData != null) {
                        int position = chapterIds.indexOf(editData.chapterId);
                        if (position >= 0) chapterSpinner.setSelection(position);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load chapters: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
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

        Toast.makeText(this, "Uploading lesson file...", Toast.LENGTH_SHORT).show();

        String safeTitle = lessonTitle.replaceAll("[^a-zA-Z0-9_-]", "_");

        String extension = getFileExtension(fileUri);

        String fileName = System.currentTimeMillis() + "_" + safeTitle;

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

        StorageReference reference = storage.getReference().child(storagePath);

        reference.putFile(fileUri)
                .addOnSuccessListener(task ->

                        reference.getDownloadUrl()
                                .addOnSuccessListener(downloadUri -> {

                                    data.put("contentUrl", downloadUri.toString());
                                    data.put("storagePath", storagePath);

                                    saveLessonData(editMode, editData, data, dialog);
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this,
                                                "Failed to get file URL: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show()
                                )
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "File upload failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    private void saveLessonData(
            boolean editMode,
            LessonEditData editData,
            Map<String, Object> data,
            AlertDialog dialog
    ) {

        if (!editMode) {

            data.put("createdAt", System.currentTimeMillis());

            db.collection("lessons")
                    .add(data)
                    .addOnSuccessListener(result -> {

                        Toast.makeText(this, "Lesson added successfully!",
                                Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                        loadLessons();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show()
                    );

        } else {

            if (editData == null) return;

            db.collection("lessons")
                    .document(editData.documentId)
                    .update(data)
                    .addOnSuccessListener(result -> {

                        Toast.makeText(this, "Lesson updated successfully!",
                                Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                        loadLessons();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show()
                    );
        }
    }

    private int findPosition(String[] values, String value) {

        if (value == null) return -1;

        for (int i = 0; i < values.length; i++) {
            if (values[i].equalsIgnoreCase(value.trim())) return i;
        }

        return -1;
    }

    private String getFileExtension(Uri uri) {

        String name = uri.getLastPathSegment();

        if (name == null) return "";

        int dot = name.lastIndexOf(".");

        if (dot >= 0 && dot < name.length() - 1) {
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

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST
                && resultCode == RESULT_OK
                && data != null) {

            selectedFileUri = data.getData();

            if (selectedFileUri != null) {

                try {
                    getContentResolver().takePersistableUriPermission(
                            selectedFileUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                } catch (Exception ignored) {
                }

                if (selectedFileText != null) {
                    selectedFileText.setText("✓ New file selected successfully");
                }

                Toast.makeText(this, "File selected successfully",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void confirmDelete(String documentId, String lessonTitle) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Lesson?")
                .setMessage("Are you sure you want to delete \"" + lessonTitle + "\"?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {

                    db.collection("lessons")
                            .document(documentId)
                            .delete()
                            .addOnSuccessListener(result -> {

                                Toast.makeText(this, "Lesson deleted",
                                        Toast.LENGTH_SHORT).show();

                                loadLessons();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this,
                                            "Delete failed: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show()
                            );
                })
                .show();
    }

    private static class SimpleListener
            implements AdapterView.OnItemSelectedListener {

        private final Runnable action;

        SimpleListener(Runnable action) {
            this.action = action;
        }

        @Override
        public void onItemSelected(
                AdapterView<?> parent,
                View view,
                int position,
                long id
        ) {
            action.run();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
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
