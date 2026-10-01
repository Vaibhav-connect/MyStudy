package com.mystudy.app;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
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

public class GamificationManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout badgeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        createUI();
        loadBadges();
    }

    private void createUI() {

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                24,
                30,
                24,
                24
        );

        root.setBackgroundColor(
                Color.rgb(248, 250, 252)
        );

        TextView title =
                new TextView(this);

        title.setText(
                "Points & Badges"
        );

        title.setTextSize(28);

        title.setTextColor(
                Color.rgb(17, 24, 39)
        );

        title.setGravity(
                Gravity.CENTER
        );

        title.setPadding(
                0,
                0,
                0,
                20
        );

        root.addView(title);

        Button pointsButton =
                new Button(this);

        pointsButton.setText(
                "⚙️ Points Settings"
        );

        pointsButton.setAllCaps(false);

        pointsButton.setTextSize(16);

        pointsButton.setOnClickListener(
                v -> showPointsDialog()
        );

        root.addView(
                pointsButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        Button addButton =
                new Button(this);

        addButton.setText(
                "+ Add Badge"
        );

        addButton.setAllCaps(false);

        addButton.setTextSize(16);

        addButton.setOnClickListener(
                v -> showBadgeDialog(
                        false,
                        null
                )
        );

        root.addView(
                addButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        ScrollView scrollView =
                new ScrollView(this);

        badgeContainer =
                new LinearLayout(this);

        badgeContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        badgeContainer.setPadding(
                0,
                20,
                0,
                20
        );

        scrollView.addView(
                badgeContainer
        );

        root.addView(
                scrollView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        Button backButton =
                new Button(this);

        backButton.setText(
                "Back"
        );

        backButton.setAllCaps(false);

        backButton.setOnClickListener(
                v -> finish()
        );

        root.addView(backButton);

        setContentView(root);
    }

    private void loadBadges() {

        badgeContainer.removeAllViews();

        db.collection("badges")
                .get()
                .addOnSuccessListener(
                        snapshot -> {

                            if (snapshot.isEmpty()) {

                                TextView empty =
                                        new TextView(this);

                                empty.setText(
                                        "No badges added yet."
                                );

                                empty.setTextSize(16);

                                empty.setTextColor(
                                        Color.rgb(
                                                100,
                                                116,
                                                139
                                        )
                                );

                                empty.setGravity(
                                        Gravity.CENTER
                                );

                                empty.setPadding(
                                        0,
                                        40,
                                        0,
                                        40
                                );

                                badgeContainer.addView(
                                        empty
                                );

                                return;
                            }

                            for (DocumentSnapshot document :
                                    snapshot.getDocuments()) {

                                String id =
                                        document.getId();

                                String name =
                                        document.getString(
                                                "name"
                                        );

                                String description =
                                        document.getString(
                                                "description"
                                        );

                                String icon =
                                        document.getString(
                                                "icon"
                                        );

                                Long pointsValue =
                                        document.getLong(
                                                "requiredPoints"
                                        );

                                if (name == null) {
                                    name = "Unnamed Badge";
                                }

                                if (description == null) {
                                    description = "";
                                }

                                if (icon == null) {
                                    icon = "🏆";
                                }

                                long points =
                                        pointsValue != null
                                                ? pointsValue
                                                : 0;

                                addBadgeCard(
                                        id,
                                        name,
                                        description,
                                        icon,
                                        points
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        e ->
                                Toast.makeText(
                                        this,
                                        "Failed to load badges: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show()
                );
    }

    private void addBadgeCard(
            String documentId,
            String name,
            String description,
            String icon,
            long points
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                24,
                20,
                24,
                20
        );

        card.setBackgroundColor(
                Color.WHITE
        );

        TextView badgeText =
                new TextView(this);

        badgeText.setText(
                icon + "  " + name
        );

        badgeText.setTextSize(20);

        badgeText.setTextColor(
                Color.rgb(
                        17,
                        24,
                        39
                )
        );

        TextView descriptionText =
                new TextView(this);

        descriptionText.setText(
                description.isEmpty()
                        ? "No description"
                        : description
        );

        descriptionText.setTextSize(14);

        descriptionText.setTextColor(
                Color.rgb(
                        71,
                        85,
                        105
                )
        );

        TextView pointsText =
                new TextView(this);

        pointsText.setText(
                "Required Points: " +
                        points
        );

        pointsText.setTextSize(14);

        pointsText.setTextColor(
                Color.rgb(
                        79,
                        70,
                        229
                )
        );

        card.addView(
                badgeText
        );

        card.addView(
                descriptionText
        );

        card.addView(
                pointsText
        );

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        Button editButton =
                new Button(this);

        editButton.setText(
                "Edit"
        );

        editButton.setAllCaps(false);

        final String finalName = name;
        final String finalDescription = description;
        final String finalIcon = icon;
        final long finalPoints = points;

        editButton.setOnClickListener(
                v ->
                        showBadgeDialog(
                                true,
                                new BadgeData(
                                        documentId,
                                        finalName,
                                        finalDescription,
                                        finalIcon,
                                        finalPoints
                                )
                        )
        );

        Button deleteButton =
                new Button(this);

        deleteButton.setText(
                "Delete"
        );

        deleteButton.setAllCaps(false);

        deleteButton.setOnClickListener(
                v ->
                        confirmDelete(
                                documentId,
                                finalName
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

        params.setMargins(
                0,
                0,
                0,
                18
        );

        badgeContainer.addView(
                card,
                params
        );
    }

    private void showBadgeDialog(
            boolean editMode,
            BadgeData badgeData
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

        EditText nameInput =
                new EditText(this);

        nameInput.setHint(
                "Badge name"
        );

        EditText descriptionInput =
                new EditText(this);

        descriptionInput.setHint(
                "Badge description"
        );

        descriptionInput.setSingleLine(false);

        EditText iconInput =
                new EditText(this);

        iconInput.setHint(
                "Badge icon e.g. 🏆"
        );

        EditText pointsInput =
                new EditText(this);

        pointsInput.setHint(
                "Required points"
        );

        pointsInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        layout.addView(
                nameInput
        );

        layout.addView(
                descriptionInput
        );

        layout.addView(
                iconInput
        );

        layout.addView(
                pointsInput
        );

        if (editMode &&
                badgeData != null) {

            nameInput.setText(
                    badgeData.name
            );

            descriptionInput.setText(
                    badgeData.description
            );

            iconInput.setText(
                    badgeData.icon
            );

            pointsInput.setText(
                    String.valueOf(
                            badgeData.points
                    )
            );
        }

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                editMode
                                        ? "Edit Badge"
                                        : "Add Badge"
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
                    ).setOnClickListener(
                            v -> {

                                String name =
                                        nameInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String description =
                                        descriptionInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String icon =
                                        iconInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String pointsString =
                                        pointsInput
                                                .getText()
                                                .toString()
                                                .trim();

                                if (name.isEmpty()) {

                                    nameInput.setError(
                                            "Enter badge name"
                                    );

                                    return;
                                }

                                if (icon.isEmpty()) {

                                    icon = "🏆";
                                }

                                if (pointsString.isEmpty()) {

                                    pointsInput.setError(
                                            "Enter required points"
                                    );

                                    return;
                                }

                                long points;

                                try {

                                    points =
                                            Long.parseLong(
                                                    pointsString
                                            );

                                } catch (Exception e) {

                                    pointsInput.setError(
                                            "Enter valid number"
                                    );

                                    return;
                                }

                                Map<String, Object> data =
                                        new HashMap<>();

                                data.put(
                                        "name",
                                        name
                                );

                                data.put(
                                        "description",
                                        description
                                );

                                data.put(
                                        "icon",
                                        icon
                                );

                                data.put(
                                        "requiredPoints",
                                        points
                                );

                                if (!editMode) {

                                    data.put(
                                            "createdAt",
                                            System.currentTimeMillis()
                                    );

                                    db.collection(
                                                    "badges"
                                            )
                                            .add(data)
                                            .addOnSuccessListener(
                                                    unused -> {

                                                        Toast.makeText(
                                                                this,
                                                                "Badge added successfully",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                        dialog.dismiss();

                                                        loadBadges();
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

                                    if (badgeData == null) {
                                        return;
                                    }

                                    db.collection(
                                                    "badges"
                                            )
                                            .document(
                                                    badgeData.documentId
                                            )
                                            .update(data)
                                            .addOnSuccessListener(
                                                    unused -> {

                                                        Toast.makeText(
                                                                this,
                                                                "Badge updated successfully",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                        dialog.dismiss();

                                                        loadBadges();
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
                    );
                }
        );

        dialog.show();
    }

    private void showPointsDialog() {

        db.collection("settings")
                .document("gamification")
                .get()
                .addOnSuccessListener(
                        document -> {

                            long lessonPoints = 10;
                            long quizPoints = 20;
                            long correctAnswerPoints = 5;
                            long streakPoints = 5;

                            if (document.exists()) {

                                Long lesson =
                                        document.getLong(
                                                "lessonPoints"
                                        );

                                Long quiz =
                                        document.getLong(
                                                "quizPoints"
                                        );

                                Long correct =
                                        document.getLong(
                                                "correctAnswerPoints"
                                        );

                                Long streak =
                                        document.getLong(
                                                "streakPoints"
                                        );

                                if (lesson != null) {
                                    lessonPoints = lesson;
                                }

                                if (quiz != null) {
                                    quizPoints = quiz;
                                }

                                if (correct != null) {
                                    correctAnswerPoints =
                                            correct;
                                }

                                if (streak != null) {
                                    streakPoints = streak;
                                }
                            }

                            showPointsEditor(
                                    lessonPoints,
                                    quizPoints,
                                    correctAnswerPoints,
                                    streakPoints
                            );
                        }
                )
                .addOnFailureListener(
                        e ->
                                Toast.makeText(
                                        this,
                                        "Failed to load settings: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show()
                );
    }

    private void showPointsEditor(
            long lessonPoints,
            long quizPoints,
            long correctPoints,
            long streakPoints
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

        EditText lessonInput =
                createNumberInput(
                        "Lesson completion points",
                        lessonPoints
                );

        EditText quizInput =
                createNumberInput(
                        "Quiz completion points",
                        quizPoints
                );

        EditText correctInput =
                createNumberInput(
                        "Correct answer points",
                        correctPoints
                );

        EditText streakInput =
                createNumberInput(
                        "Daily streak points",
                        streakPoints
                );

        layout.addView(
                lessonInput
        );

        layout.addView(
                quizInput
        );

        layout.addView(
                correctInput
        );

        layout.addView(
                streakInput
        );

        new AlertDialog.Builder(this)
                .setTitle(
                        "Points Settings"
                )
                .setView(layout)
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Save",
                        (dialog, which) -> {

                            try {

                                Map<String, Object> data =
                                        new HashMap<>();

                                data.put(
                                        "lessonPoints",
                                        Long.parseLong(
                                                lessonInput
                                                        .getText()
                                                        .toString()
                                        )
                                );

                                data.put(
                                        "quizPoints",
                                        Long.parseLong(
                                                quizInput
                                                        .getText()
                                                        .toString()
                                        )
                                );

                                data.put(
                                        "correctAnswerPoints",
                                        Long.parseLong(
                                                correctInput
                                                        .getText()
                                                        .toString()
                                        )
                                );

                                data.put(
                                        "streakPoints",
                                        Long.parseLong(
                                                streakInput
                                                        .getText()
                                                        .toString()
                                        )
                                );

                                data.put(
                                        "updatedAt",
                                        System.currentTimeMillis()
                                );

                                db.collection(
                                                "settings"
                                        )
                                        .document(
                                                "gamification"
                                        )
                                        .set(data)
                                        .addOnSuccessListener(
                                                unused ->
                                                        Toast.makeText(
                                                                this,
                                                                "Points settings saved",
                                                                Toast.LENGTH_SHORT
                                                        ).show()
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

                            } catch (Exception e) {

                                Toast.makeText(
                                        this,
                                        "Enter valid numbers.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .show();
    }

    private EditText createNumberInput(
            String hint,
            long value
    ) {

        EditText input =
                new EditText(this);

        input.setHint(hint);

        input.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        input.setText(
                String.valueOf(value)
        );

        return input;
    }

    private void confirmDelete(
            String documentId,
            String badgeName
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Badge"
                )
                .setMessage(
                        "Delete \"" +
                                badgeName +
                                "\"?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            db.collection(
                                            "badges"
                                    )
                                    .document(
                                            documentId
                                    )
                                    .delete()
                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        this,
                                                        "Badge deleted",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                loadBadges();
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

    private static class BadgeData {

        String documentId;
        String name;
        String description;
        String icon;
        long points;

        BadgeData(
                String documentId,
                String name,
                String description,
                String icon,
                long points
        ) {

            this.documentId = documentId;
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.points = points;
        }
    }
}
