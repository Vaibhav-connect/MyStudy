package com.mystudy.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout adminContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        createScreen();
        loadAdmins();
    }

    private void createScreen() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(0xFFF8FAFC);

        TextView title = new TextView(this);
        title.setText("👑 Admin Management");
        title.setTextSize(26);
        title.setTextColor(0xFF111827);
        title.setTypeface(null, 1);
        title.setGravity(Gravity.CENTER_VERTICAL);

        root.addView(title, new LinearLayout.LayoutParams(
                -1,
                70
        ));

        TextView info = new TextView(this);
        info.setText(
                "Only Main Admin can manage administrators.\n" +
                "The user must already have a MyStudy account."
        );
        info.setTextSize(14);
        info.setTextColor(0xFF64748B);
        info.setPadding(4, 10, 4, 20);

        root.addView(info);

        Button addAdminButton = new Button(this);
        addAdminButton.setText("+ Add Admin");
        addAdminButton.setAllCaps(false);
        addAdminButton.setOnClickListener(v ->
                showAddAdminDialog()
        );

        root.addView(addAdminButton);

        adminContainer = new LinearLayout(this);
        adminContainer.setOrientation(LinearLayout.VERTICAL);

        root.addView(
                adminContainer,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        setContentView(root);
    }

    private void loadAdmins() {

        db.collection("users")
                .whereIn("role",
                        java.util.Arrays.asList(
                                "main_admin",
                                "admin"
                        ))
                .get()
                .addOnSuccessListener(snapshot -> {

                    adminContainer.removeAllViews();

                    if (snapshot.isEmpty()) {

                        TextView empty = new TextView(this);
                        empty.setText("No administrators found.");
                        empty.setTextSize(16);
                        empty.setTextColor(0xFF64748B);
                        empty.setGravity(Gravity.CENTER);
                        empty.setPadding(20, 40, 20, 40);

                        adminContainer.addView(empty);
                        return;
                    }

                    for (DocumentSnapshot doc :
                            snapshot.getDocuments()) {

                        addAdminCard(doc);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to load admins: " +
                                        e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void addAdminCard(DocumentSnapshot doc) {

        String name = doc.getString("name");
        String email = doc.getString("email");
        String role = doc.getString("role");

        if (name == null || name.trim().isEmpty()) {
            name = "Admin";
        }

        if (email == null) {
            email = "";
        }

        if (role == null) {
            role = "admin";
        }

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 20, 20, 20);
        card.setBackgroundColor(0xFFFFFFFF);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        cardParams.setMargins(0, 0, 0, 20);

        card.setLayoutParams(cardParams);

        TextView nameView = new TextView(this);
        nameView.setText("👤 " + name);
        nameView.setTextSize(19);
        nameView.setTextColor(0xFF111827);
        nameView.setTypeface(null, 1);

        card.addView(nameView);

        TextView emailView = new TextView(this);
        emailView.setText("📧 " + email);
        emailView.setTextSize(14);
        emailView.setTextColor(0xFF475569);
        emailView.setPadding(0, 8, 0, 4);

        card.addView(emailView);

        TextView roleView = new TextView(this);
        roleView.setText(
                "Role: " +
                        ("main_admin".equals(role)
                                ? "Main Admin"
                                : "Admin")
        );
        roleView.setTextSize(14);
        roleView.setTextColor(0xFF4F46E5);
        roleView.setPadding(0, 4, 0, 12);

        card.addView(roleView);

        if ("main_admin".equals(role)) {

            TextView protectedText = new TextView(this);
            protectedText.setText(
                    "🔒 Main Admin account cannot be removed."
            );
            protectedText.setTextSize(13);
            protectedText.setTextColor(0xFF16A34A);

            card.addView(protectedText);

        } else {

            Button removeButton = new Button(this);
            removeButton.setText("Remove Admin");
            removeButton.setAllCaps(false);

            final String adminId = doc.getId();

            removeButton.setOnClickListener(v ->
                    confirmRemoveAdmin(adminId, name)
            );

            card.addView(removeButton);
        }

        adminContainer.addView(card);
    }

    private void showAddAdminDialog() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 10, 30, 10);

        EditText emailInput = new EditText(this);
        emailInput.setHint("Registered user's email");
        emailInput.setSingleLine(true);

        layout.addView(emailInput);

        TextView help = new TextView(this);
        help.setText(
                "Example: user@gmail.com\n\n" +
                "The account must already exist in MyStudy."
        );
        help.setTextSize(13);
        help.setTextColor(0xFF64748B);
        help.setPadding(0, 12, 0, 4);

        layout.addView(help);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Admin")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Promote", null)
                .create();

        dialog.setOnShowListener(d -> {

            Button promoteButton =
                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    );

            promoteButton.setOnClickListener(v -> {

                String email =
                        emailInput
                                .getText()
                                .toString()
                                .trim()
                                .toLowerCase();

                if (email.isEmpty()) {
                    emailInput.setError(
                            "Enter email address"
                    );
                    return;
                }

                promoteUser(email);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void promoteUser(String email) {

        db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.isEmpty()) {

                        Toast.makeText(
                                this,
                                "No registered user found with this email.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    DocumentSnapshot user =
                            snapshot.getDocuments().get(0);

                    String currentRole =
                            user.getString("role");

                    if ("main_admin".equals(currentRole)) {

                        Toast.makeText(
                                this,
                                "This user is already Main Admin.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    if ("admin".equals(currentRole)) {

                        Toast.makeText(
                                this,
                                "This user is already an Admin.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    db.collection("users")
                            .document(user.getId())
                            .update(
                                    "role",
                                    "admin"
                            )
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        this,
                                        "User promoted to Admin.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadAdmins();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(
                                            this,
                                            "Promotion failed: " +
                                                    e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Search failed: " +
                                        e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void confirmRemoveAdmin(
            String adminId,
            String adminName
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Remove Admin")
                .setMessage(
                        "Remove " +
                                adminName +
                                " admin access?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Remove",
                        (dialog, which) -> {

                            db.collection("users")
                                    .document(adminId)
                                    .update(
                                            "role",
                                            "student"
                                    )
                                    .addOnSuccessListener(unused -> {

                                        Toast.makeText(
                                                this,
                                                "Admin access removed.",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        loadAdmins();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(
                                                    this,
                                                    "Failed: " +
                                                            e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show()
                                    );
                        }
                )
                .show();
    }
}
