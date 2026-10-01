package com.mystudy.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
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

public class AnnouncementManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private LinearLayout announcementContainer;

    private final List<String> classIds = new ArrayList<>();
    private final List<String> classNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(0xFFF8FAFC);

        TextView title = new TextView(this);
        title.setText("Announcements");
        title.setTextSize(26);
        title.setTextColor(0xFF111827);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTypeface(null, 1);

        root.addView(title, new LinearLayout.LayoutParams(
                -1, 70
        ));

        Button addButton = new Button(this);
        addButton.setText("+ Add Announcement");
        addButton.setOnClickListener(v -> showAnnouncementDialog(null));

        root.addView(addButton, new LinearLayout.LayoutParams(
                -1, 60
        ));

        TextView info = new TextView(this);
        info.setText("Announcements can be shown to all students or selected classes.");
        info.setTextSize(14);
        info.setTextColor(0xFF64748B);
        info.setPadding(4, 16, 4, 16);

        root.addView(info);

        announcementContainer = new LinearLayout(this);
        announcementContainer.setOrientation(LinearLayout.VERTICAL);

        root.addView(announcementContainer,
                new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);

        loadClasses();
        loadAnnouncements();
    }

    private void loadClasses() {

        db.collection("classes")
                .orderBy("order")
                .get()
                .addOnSuccessListener(snapshot -> {

                    classIds.clear();
                    classNames.clear();

                    classIds.add("all");
                    classNames.add("All Students");

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        String id = doc.getId();
                        String name = doc.getString("name");

                        if (name == null || name.trim().isEmpty()) {
                            name = "Class";
                        }

                        classIds.add(id);
                        classNames.add(name);
                    }
                });
    }

    private void loadAnnouncements() {

        db.collection("announcements")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {

                    announcementContainer.removeAllViews();

                    if (snapshot.isEmpty()) {

                        TextView empty = new TextView(this);
                        empty.setText("No announcements yet.");
                        empty.setTextSize(16);
                        empty.setTextColor(0xFF64748B);
                        empty.setGravity(Gravity.CENTER);
                        empty.setPadding(20, 40, 20, 40);

                        announcementContainer.addView(empty);
                        return;
                    }

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        addAnnouncementCard(doc);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to load announcements: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void addAnnouncementCard(DocumentSnapshot doc) {

        String title = doc.getString("title");
        String message = doc.getString("message");
        String targetClass = doc.getString("targetClass");
        Boolean active = doc.getBoolean("active");

        if (title == null) title = "Announcement";
        if (message == null) message = "";
        if (targetClass == null) targetClass = "All Students";
        if (active == null) active = true;

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 20, 20, 20);
        card.setBackgroundColor(0xFFFFFFFF);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(-1, -2);

        cardParams.setMargins(0, 0, 0, 20);
        card.setLayoutParams(cardParams);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(20);
        titleView.setTextColor(0xFF111827);
        titleView.setTypeface(null, 1);

        card.addView(titleView);

        TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setTextSize(15);
        messageView.setTextColor(0xFF374151);
        messageView.setPadding(0, 10, 0, 10);

        card.addView(messageView);

        TextView targetView = new TextView(this);
        targetView.setText("Target: " + targetClass);
        targetView.setTextSize(13);
        targetView.setTextColor(0xFF4F46E5);

        card.addView(targetView);

        TextView statusView = new TextView(this);
        statusView.setText(active ? "Status: Active" : "Status: Inactive");
        statusView.setTextSize(13);
        statusView.setTextColor(active ? 0xFF16A34A : 0xFFDC2626);
        statusView.setPadding(0, 6, 0, 12);

        card.addView(statusView);

        GridLayout buttons = new GridLayout(this);
        buttons.setColumnCount(2);

        Button editButton = new Button(this);
        editButton.setText("Edit");

        editButton.setOnClickListener(v ->
                showAnnouncementDialog(doc));

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");

        deleteButton.setOnClickListener(v ->
                confirmDelete(doc.getId()));

        buttons.addView(editButton, new GridLayout.LayoutParams());

        buttons.addView(deleteButton, new GridLayout.LayoutParams());

        card.addView(buttons);

        announcementContainer.addView(card);
    }

    private void showAnnouncementDialog(DocumentSnapshot existing) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 10, 30, 10);

        EditText titleInput = new EditText(this);
        titleInput.setHint("Announcement Title");

        layout.addView(titleInput);

        EditText messageInput = new EditText(this);
        messageInput.setHint("Announcement Message");
        messageInput.setGravity(Gravity.TOP);
        messageInput.setMinLines(4);
        messageInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );

        layout.addView(messageInput);

        Spinner classSpinner = new Spinner(this);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        classNames
                );

        classSpinner.setAdapter(adapter);

        layout.addView(classSpinner);

        Spinner statusSpinner = new Spinner(this);

        String[] statusOptions = {
                "Active",
                "Inactive"
        };

        ArrayAdapter<String> statusAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        statusOptions
                );

        statusSpinner.setAdapter(statusAdapter);

        layout.addView(statusSpinner);

        if (existing != null) {

            String oldTitle = existing.getString("title");
            String oldMessage = existing.getString("message");
            String oldTarget = existing.getString("targetClass");
            Boolean oldActive = existing.getBoolean("active");

            if (oldTitle != null) {
                titleInput.setText(oldTitle);
            }

            if (oldMessage != null) {
                messageInput.setText(oldMessage);
            }

            if (oldTarget != null) {

                for (int i = 0; i < classNames.size(); i++) {

                    if (classNames.get(i).equals(oldTarget)) {
                        classSpinner.setSelection(i);
                        break;
                    }
                }
            }

            if (oldActive != null && !oldActive) {
                statusSpinner.setSelection(1);
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(existing == null
                        ? "Add Announcement"
                        : "Edit Announcement")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton(
                        existing == null ? "Add" : "Save",
                        null
                )
                .create();

        dialog.setOnShowListener(d -> {

            Button positive =
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            positive.setOnClickListener(v -> {

                String title = titleInput
                        .getText()
                        .toString()
                        .trim();

                String message = messageInput
                        .getText()
                        .toString()
                        .trim();

                if (title.isEmpty()) {
                    titleInput.setError("Enter title");
                    return;
                }

                if (message.isEmpty()) {
                    messageInput.setError("Enter message");
                    return;
                }

                if (classNames.isEmpty()) {
                    Toast.makeText(
                            this,
                            "Classes are still loading.",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                String selectedClass =
                        classNames.get(classSpinner.getSelectedItemPosition());

                boolean active =
                        statusSpinner.getSelectedItemPosition() == 0;

                saveAnnouncement(
                        existing,
                        title,
                        message,
                        selectedClass,
                        active
                );

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void saveAnnouncement(
            DocumentSnapshot existing,
            String title,
            String message,
            String targetClass,
            boolean active
    ) {

        Map<String, Object> data = new HashMap<>();

        data.put("title", title);
        data.put("message", message);
        data.put("targetClass", targetClass);
        data.put("active", active);

        if (existing == null) {

            data.put(
                    "createdAt",
                    com.google.firebase.firestore.FieldValue.serverTimestamp()
            );

            db.collection("announcements")
                    .add(data)
                    .addOnSuccessListener(documentReference -> {

                        Toast.makeText(
                                this,
                                "Announcement added",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadAnnouncements();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(
                                    this,
                                    "Failed: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show()
                    );

        } else {

            db.collection("announcements")
                    .document(existing.getId())
                    .update(data)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                this,
                                "Announcement updated",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadAnnouncements();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(
                                    this,
                                    "Failed: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show()
                    );
        }
    }

    private void confirmDelete(String documentId) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Announcement")
                .setMessage(
                        "Are you sure you want to delete this announcement?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {

                    db.collection("announcements")
                            .document(documentId)
                            .delete()
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        this,
                                        "Announcement deleted",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadAnnouncements();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(
                                            this,
                                            "Delete failed: " + e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show()
                            );
                })
                .show();
    }
}
