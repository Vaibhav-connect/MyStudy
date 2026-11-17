package com.mystudy.app;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ClassManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private LinearLayout classContainer;
    private TextView countView;
    private TextView statusView;
    private Button addButton;

    private final int BLUE = Color.rgb(37, 99, 235);
    private final int DARK_BLUE = Color.rgb(30, 64, 175);
    private final int YELLOW = Color.rgb(250, 204, 21);
    private final int BACKGROUND = Color.rgb(245, 248, 255);
    private final int TEXT = Color.rgb(30, 41, 59);
    private final int MUTED = Color.rgb(100, 116, 139);
    private final int GREEN = Color.rgb(22, 163, 74);
    private final int RED = Color.rgb(220, 38, 38);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        createUi();
        loadClasses();
    }

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    private GradientDrawable rounded(
            int color,
            int radius
    ) {
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

    private TextView createText(
            String text,
            int size,
            int color,
            boolean bold
    ) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }

        return view;
    }

    private Button createButton(
            String text,
            int backgroundColor,
            int textColor
    ) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(13);
        button.setTextColor(textColor);
        button.setAllCaps(false);
        button.setTypeface(null, Typeface.BOLD);
        button.setBackground(rounded(backgroundColor, 14));
        button.setPadding(
                dp(12), dp(8),
                dp(12), dp(8)
        );

        return button;
    }

    private void createUi() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(BACKGROUND);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(
                dp(20), dp(20),
                dp(20), dp(30)
        );

        // Header

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView backButton = createText(
                "‹",
                36,
                BLUE,
                true
        );

        backButton.setGravity(Gravity.CENTER);
        backButton.setBackground(rounded(Color.WHITE, 14));

        LinearLayout.LayoutParams backParams =
                new LinearLayout.LayoutParams(
                        dp(48),
                        dp(48)
                );

        header.addView(backButton, backParams);

        LinearLayout brandLayout = new LinearLayout(this);
        brandLayout.setOrientation(LinearLayout.VERTICAL);
        brandLayout.setPadding(dp(12), 0, 0, 0);

        TextView brand = createText(
                "MyStudy",
                22,
                DARK_BLUE,
                true
        );

        TextView tagline = createText(
                "ADMINISTRATION PANEL",
                10,
                MUTED,
                true
        );

        brandLayout.addView(brand);
        brandLayout.addView(tagline);

        header.addView(
                brandLayout,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        root.addView(header);

        backButton.setOnClickListener(v -> finish());

        // Title

        TextView title = createText(
                "Class Management",
                27,
                TEXT,
                true
        );

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        titleParams.topMargin = dp(28);

        root.addView(title, titleParams);

        TextView subtitle = createText(
                "Create and manage student classes.",
                14,
                MUTED,
                false
        );

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(-1, -2);

        subtitleParams.topMargin = dp(6);

        root.addView(subtitle, subtitleParams);

        // Total Classes Card

        LinearLayout countCard = new LinearLayout(this);
        countCard.setOrientation(LinearLayout.VERTICAL);
        countCard.setPadding(
                dp(22), dp(20),
                dp(22), dp(20)
        );
        countCard.setBackground(rounded(BLUE, 22));

        LinearLayout.LayoutParams countCardParams =
                new LinearLayout.LayoutParams(-1, -2);

        countCardParams.topMargin = dp(24);

        root.addView(countCard, countCardParams);

        TextView countLabel = createText(
                "TOTAL CLASSES",
                12,
                Color.WHITE,
                true
        );

        countCard.addView(countLabel);

        countView = createText(
                "0",
                36,
                YELLOW,
                true
        );

        LinearLayout.LayoutParams numberParams =
                new LinearLayout.LayoutParams(-1, -2);

        numberParams.topMargin = dp(5);

        countCard.addView(countView, numberParams);

        TextView countDescription = createText(
                "Manage all available classes in MyStudy",
                12,
                Color.WHITE,
                false
        );

        countCard.addView(countDescription);

        // Add Class Button

        addButton = createButton(
                "+  Add New Class",
                YELLOW,
                DARK_BLUE
        );

        LinearLayout.LayoutParams addParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(54)
                );

        addParams.topMargin = dp(22);

        root.addView(addButton, addParams);

        addButton.setOnClickListener(v ->
                showAddClassDialog()
        );

        // Section Heading

        LinearLayout sectionHeader = new LinearLayout(this);
        sectionHeader.setGravity(Gravity.CENTER_VERTICAL);

        TextView sectionTitle = createText(
                "Available Classes",
                19,
                TEXT,
                true
        );

        sectionHeader.addView(
                sectionTitle,
                new LinearLayout.LayoutParams(
                        0, -2, 1
                )
        );

        TextView sectionBadge = createText(
                "CLASSES",
                10,
                BLUE,
                true
        );

        sectionBadge.setPadding(
                dp(12), dp(7),
                dp(12), dp(7)
        );

        sectionBadge.setBackground(
                rounded(Color.rgb(219, 234, 254), 20)
        );

        sectionHeader.addView(sectionBadge);

        LinearLayout.LayoutParams sectionParams =
                new LinearLayout.LayoutParams(-1, -2);

        sectionParams.topMargin = dp(28);

        root.addView(sectionHeader, sectionParams);

        // Status

        statusView = createText(
                "Loading classes...",
                14,
                MUTED,
                false
        );

        statusView.setGravity(Gravity.CENTER);
        statusView.setPadding(
                dp(15), dp(25),
                dp(15), dp(25)
        );

        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(-1, -2);

        statusParams.topMargin = dp(10);

        root.addView(statusView, statusParams);

        // Class Cards Container

        classContainer = new LinearLayout(this);
        classContainer.setOrientation(LinearLayout.VERTICAL);

        root.addView(classContainer);

        // Footer

        TextView footer = createText(
                "MyStudy • Learn • Practice • Grow",
                12,
                MUTED,
                false
        );

        footer.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams footerParams =
                new LinearLayout.LayoutParams(-1, -2);

        footerParams.topMargin = dp(30);

        root.addView(footer, footerParams);

        scrollView.addView(root);

        setContentView(scrollView);
    }

    // LOAD CLASSES

    private void loadClasses() {

        statusView.setVisibility(View.VISIBLE);
        statusView.setText("Loading classes...");
        classContainer.removeAllViews();

        db.collection("classes")
                .orderBy("order")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    classContainer.removeAllViews();

                    int total = querySnapshot.size();

                    countView.setText(String.valueOf(total));

                    if (total == 0) {

                        statusView.setVisibility(View.VISIBLE);
                        statusView.setText(
                                "📚\n\nNo classes added yet.\nTap Add New Class to get started."
                        );

                        return;
                    }

                    statusView.setVisibility(View.GONE);

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        String name = document.getString("name");

                        if (name == null || name.trim().isEmpty()) {
                            name = "Unnamed Class";
                        }

                        Long order = document.getLong("order");

                        addClassCard(
                                document.getId(),
                                name,
                                order == null ? 0 : order
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    countView.setText("--");

                    statusView.setVisibility(View.VISIBLE);
                    statusView.setText(
                            "Unable to load classes.\nPlease try again."
                    );

                    Button retry = createButton(
                            "Retry",
                            BLUE,
                            Color.WHITE
                    );

                    LinearLayout.LayoutParams retryParams =
                            new LinearLayout.LayoutParams(
                                    dp(130),
                                    dp(48)
                            );

                    retryParams.gravity = Gravity.CENTER;

                    statusView.setTextColor(RED);

                    classContainer.addView(retry, retryParams);

                    retry.setOnClickListener(v -> loadClasses());
                });
    }

    // CLASS CARD

    private void addClassCard(
            String documentId,
            String name,
            long order
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(
                dp(18), dp(18),
                dp(18), dp(18)
        );

        card.setBackground(
                bordered(Color.WHITE,
                        Color.rgb(226, 232, 240), 20)
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(-1, -2);

        cardParams.setMargins(0, 0, 0, dp(15));

        classContainer.addView(card, cardParams);

        // Card Header

        LinearLayout topRow = new LinearLayout(this);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView icon = createText(
                "📚",
                25,
                BLUE,
                false
        );

        icon.setGravity(Gravity.CENTER);
        icon.setBackground(
                rounded(Color.rgb(239, 246, 255), 14)
        );

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(52), dp(52)
                );

        topRow.addView(icon, iconParams);

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setPadding(dp(13), 0, 0, 0);

        TextView nameView = createText(
                name,
                18,
                TEXT,
                true
        );

        TextView orderView = createText(
                "Display Order: " + order,
                12,
                MUTED,
                false
        );

        LinearLayout.LayoutParams orderParams =
                new LinearLayout.LayoutParams(-1, -2);

        orderParams.topMargin = dp(5);

        info.addView(nameView);
        info.addView(orderView, orderParams);

        topRow.addView(
                info,
                new LinearLayout.LayoutParams(
                        0, -2, 1
                )
        );

        card.addView(topRow);

        // Divider

        View divider = new View(this);
        divider.setBackgroundColor(
                Color.rgb(241, 245, 249)
        );

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(-1, dp(1));

        dividerParams.topMargin = dp(17);
        dividerParams.bottomMargin = dp(13);

        card.addView(divider, dividerParams);

        // Action Buttons

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button editButton = createButton(
                "✎  Edit",
                Color.rgb(219, 234, 254),
                BLUE
        );

        Button deleteButton = createButton(
                "Delete",
                Color.rgb(254, 226, 226),
                RED
        );

        LinearLayout.LayoutParams actionButtonParams =
                new LinearLayout.LayoutParams(
                        0, dp(45), 1
                );

        actionButtonParams.setMargins(
                0, 0, dp(6), 0
        );

        actions.addView(editButton, actionButtonParams);

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(
                        0, dp(45), 1
                );

        deleteParams.setMargins(
                dp(6), 0, 0, 0
        );

        actions.addView(deleteButton, deleteParams);

        card.addView(actions);

        editButton.setOnClickListener(v ->
                showEditClassDialog(
                        documentId,
                        name,
                        order
                )
        );

        deleteButton.setOnClickListener(v ->
                confirmDelete(documentId, name)
        );
    }

    // ADD CLASS

    private void showAddClassDialog() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(
                dp(20), dp(10),
                dp(20), dp(5)
        );

        EditText nameInput = new EditText(this);
        nameInput.setHint("Enter class name");
        nameInput.setSingleLine(true);

        EditText orderInput = new EditText(this);
        orderInput.setHint("Enter order number");
        orderInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );
        orderInput.setSingleLine(true);

        layout.addView(nameInput);
        layout.addView(orderInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Class")
                .setView(layout)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(unused -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String name = nameInput.getText()
                                .toString().trim();

                        String orderText = orderInput.getText()
                                .toString().trim();

                        if (name.isEmpty() ||
                                orderText.isEmpty()) {

                            Toast.makeText(
                                    this,
                                    "Enter class name and order.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        try {

                            long order = Long.parseLong(orderText);

                            dialog.dismiss();

                            saveClass(name, order);

                        } catch (NumberFormatException e) {

                            Toast.makeText(
                                    this,
                                    "Invalid order number.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        });

        dialog.show();
    }

    private void saveClass(
            String name,
            long order
    ) {

        Map<String, Object> data = new HashMap<>();

        data.put("name", name);
        data.put("order", order);
        data.put(
                "createdAt",
                FieldValue.serverTimestamp()
        );

        db.collection("classes")
                .add(data)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            this,
                            "Class added successfully.",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadClasses();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to add class.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // EDIT CLASS

    private void showEditClassDialog(
            String documentId,
            String oldName,
            long oldOrder
    ) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(
                dp(20), dp(10),
                dp(20), dp(5)
        );

        EditText nameInput = new EditText(this);
        nameInput.setText(oldName);
        nameInput.setHint("Class name");
        nameInput.setSingleLine(true);

        EditText orderInput = new EditText(this);
        orderInput.setText(String.valueOf(oldOrder));
        orderInput.setHint("Order number");
        orderInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );
        orderInput.setSingleLine(true);

        layout.addView(nameInput);
        layout.addView(orderInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Class")
                .setView(layout)
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(unused -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String name = nameInput.getText()
                                .toString().trim();

                        String orderText = orderInput.getText()
                                .toString().trim();

                        if (name.isEmpty() ||
                                orderText.isEmpty()) {

                            Toast.makeText(
                                    this,
                                    "Enter all details.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        try {

                            long order = Long.parseLong(orderText);

                            dialog.dismiss();

                            updateClass(
                                    documentId,
                                    name,
                                    order
                            );

                        } catch (NumberFormatException e) {

                            Toast.makeText(
                                    this,
                                    "Invalid order number.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        });

        dialog.show();
    }

    private void updateClass(
            String documentId,
            String name,
            long order
    ) {

        Map<String, Object> updates = new HashMap<>();

        updates.put("name", name);
        updates.put("order", order);

        db.collection("classes")
                .document(documentId)
                .update(updates)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Class updated successfully.",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadClasses();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to update class.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // DELETE CLASS

    private void confirmDelete(
            String documentId,
            String name
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Class?")
                .setMessage(
                        "Are you sure you want to delete "
                                + name + "?"
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) ->
                                deleteClass(documentId)
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    private void deleteClass(String documentId) {

        db.collection("classes")
                .document(documentId)
                .delete()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Class deleted successfully.",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadClasses();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to delete class.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}
