package com.example.speedowin.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedowin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput, upiIdInput;
    private Button registerButton;
    private FirebaseAuth auth;
    private TextView loginRedirectText;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initializing UI components
        progressBar = findViewById(R.id.progressBar);
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInputR);
        passwordInput = findViewById(R.id.passwordInputR);
        upiIdInput = findViewById(R.id.upiIdInput);
        registerButton = findViewById(R.id.registerButton);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        // Firebase setup
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Register button click listener
        registerButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE); // Show ProgressBar at the start of registration
            registerUser();
        });

        // Redirect to login
        loginRedirectText.setOnClickListener(v ->
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class))
        );
    }

    private void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String upiId = upiIdInput.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(upiId)) {
            progressBar.setVisibility(View.GONE);  // Hide ProgressBar if any field is empty
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            progressBar.setVisibility(View.GONE);  // Hide ProgressBar if password is too short
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for existing username
        databaseReference.orderByChild("username").equalTo(username.toLowerCase())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            progressBar.setVisibility(View.GONE);  // Hide ProgressBar if username is taken
                            Toast.makeText(RegistrationActivity.this, "Username already taken. Please choose another.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Proceed with Firebase Authentication
                            auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(RegistrationActivity.this, task -> {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = auth.getCurrentUser();
                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            String userId = user.getUid();
                                                            Map<String, Object> userData = new HashMap<>();
                                                            userData.put("username", username.toLowerCase());
                                                            userData.put("email", email);
                                                            userData.put("upiId", upiId);
                                                            userData.put("transactionStatus", "active");
                                                            userData.put("winningAmount", 0);
                                                            userData.put("depositCash", 0);
                                                            userData.put("winningAmountStatus", "pending");
                                                            userData.put("depositCashStatus", "pending");

                                                            // Save user data to Firebase Database
                                                            databaseReference.child(userId).setValue(userData)
                                                                    .addOnCompleteListener(task2 -> {
                                                                        progressBar.setVisibility(View.GONE);  // Hide ProgressBar when data is saved
                                                                        if (task2.isSuccessful()) {
                                                                            Toast.makeText(RegistrationActivity.this,
                                                                                    "Registration successful! Verification email sent.",
                                                                                    Toast.LENGTH_SHORT).show();
                                                                            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                                                            finish();
                                                                        } else {
                                                                            Toast.makeText(RegistrationActivity.this,
                                                                                    "Failed to save user data. Please try again.",
                                                                                    Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        } else {
                                                            progressBar.setVisibility(View.GONE);  // Hide ProgressBar if email verification fails
                                                            Toast.makeText(RegistrationActivity.this,
                                                                    "Failed to send verification email.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            progressBar.setVisibility(View.GONE);  // Hide ProgressBar if registration fails
                                            String errorMsg = "Registration failed: " + task.getException().getMessage();
                                            Toast.makeText(RegistrationActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);  // Hide ProgressBar if there's a database error
                        Toast.makeText(RegistrationActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
