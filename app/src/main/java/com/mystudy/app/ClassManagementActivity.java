package com.mystudy.app;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ClassManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout classContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        createUi();
        loadClasses();
    }

    private void createUi() {

        ScrollView scrollView = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(30, 35, 30, 35);
        root.setBackgroundColor(Color.rgb(248, 250, 252));

        TextView title = new TextView(this);
        title.setText("Class Management");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(17, 24, 39));
        title.setGravity(Gravity.CENTER);

        root.addView(title);

        Button addButton = new Button(this);
        addButton.setText("+ Add Class");

        LinearLayout.LayoutParams addParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        addParams.topMargin = 25;

        root.addView(addButton, addParams);

        classContainer = new LinearLayout(this);
        classContainer.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams containerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        containerParams.topMargin = 20;

        root.addView(classContainer, containerParams);

        addButton.setOnClickListener(v -> showAddClassDialog());

        scrollView.addView(root);

        setContentView(scrollView);
    }

    private void loadClasses() {

        db.collection("classes")
                .orderBy("order")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    classContainer.removeAllViews();

                    if (querySnapshot.isEmpty()) {

                        TextView empty = new TextView(this);
                        empty.setText("No classes added yet.");
                        empty.setTextSize(16);
                        empty.setTextColor(
                                Color.rgb(100, 116, 139)
                        );
                        empty.setGravity(Gravity.CENTER);

                        classContainer.addView(empty);

                        return;
                    }

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        String name =
                                document.getString("name");

                        Long order =
                                document.getLong("order");

                        addClassCard(
                                document.getId(),
                                name,
                                order == null ? 0 : order
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Unable to load classes.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void addClassCard(
            String documentId,
            String name,
            long order
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(25, 25, 25, 25);
        card.setBackgroundColor(Color.WHITE);

        TextView nameView = new TextView(this);
        nameView.setText(name);
        nameView.setTextSize(20);
        nameView.setTextColor(Color.rgb(17, 24, 39));

        card.addView(nameView);

        TextView orderView = new TextView(this);
        orderView.setText("Order: " + order);
        orderView.setTextSize(14);
        orderView.setTextColor(Color.rgb(100, 116, 139));

        LinearLayout.LayoutParams orderParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        orderParams.topMargin = 5;

        card.addView(orderView, orderParams);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button editButton = new Button(this);
        editButton.setText("Edit");

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");

        actions.addView(
                editButton,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        actions.addView(
                deleteButton,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        LinearLayout.LayoutParams actionsParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        actionsParams.topMargin = 10;

        card.addView(actions, actionsParams);

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

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, 20);

        classContainer.addView(card, cardParams);
    }

    private void showAddClassDialog() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 10, 40, 10);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Class name");

        EditText orderInput = new EditText(this);
        orderInput.setHint("Order number");
        orderInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );

        layout.addView(nameInput);
        layout.addView(orderInput);

        new AlertDialog.Builder(this)
                .setTitle("Add Class")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {

                    String name =
                            nameInput.getText()
                                    .toString()
                                    .trim();

                    String orderText =
                            orderInput.getText()
                                    .toString()
                                    .trim();

                    if (name.isEmpty() ||
                            orderText.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Enter class name and order.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    long order;

                    try {
                        order = Long.parseLong(orderText);
                    } catch (NumberFormatException e) {
                        Toast.makeText(
                                this,
                                "Invalid order number.",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    saveClass(name, order);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveClass(String name, long order) {

        Map<String, Object> data =
                new HashMap<>();

        data.put("name", name);
        data.put("order", order);
        data.put("createdAt",
                com.google.firebase.firestore.FieldValue.serverTimestamp());

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

    private void showEditClassDialog(
            String documentId,
            String oldName,
            long oldOrder
    ) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 10, 40, 10);

        EditText nameInput = new EditText(this);
        nameInput.setText(oldName);
        nameInput.setHint("Class name");

        EditText orderInput = new EditText(this);
        orderInput.setText(String.valueOf(oldOrder));
        orderInput.setHint("Order number");
        orderInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );

        layout.addView(nameInput);
        layout.addView(orderInput);

        new AlertDialog.Builder(this)
                .setTitle("Edit Class")
                .setView(layout)
                .setPositiveButton("Update", (dialog, which) -> {

                    String name =
                            nameInput.getText()
                                    .toString()
                                    .trim();

                    String orderText =
                            orderInput.getText()
                                    .toString()
                                    .trim();

                    if (name.isEmpty() ||
                            orderText.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Enter all details.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    long order;

                    try {
                        order = Long.parseLong(orderText);
                    } catch (NumberFormatException e) {
                        Toast.makeText(
                                this,
                                "Invalid order number.",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    updateClass(
                            documentId,
                            name,
                            order
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateClass(
            String documentId,
            String name,
            long order
    ) {

        Map<String, Object> updates =
                new HashMap<>();

        updates.put("name", name);
        updates.put("order", order);

        db.collection("classes")
                .document(documentId)
                .update(updates)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Class updated.",
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

    private void confirmDelete(
            String documentId,
            String name
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage(
                        "Delete " + name + "?"
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
                            "Class deleted.",
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
