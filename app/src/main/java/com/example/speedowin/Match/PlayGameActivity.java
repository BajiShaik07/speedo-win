package com.example.speedowin.Match;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.speedowin.R;

public class PlayGameActivity extends AppCompatActivity {

    private TextView betAmountTextView; // TextView to display the bet amount

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game); // Make sure this matches your XML layout

        // Initialize the TextView
        betAmountTextView = findViewById(R.id.betAmountTextView);

        // Get the bet amount passed from AtoZGameActivity
        int betAmount = getIntent().getIntExtra("betAmount", 0); // Default value is 0 if not found

        if (betAmount > 0) {
            // Display the bet amount
            betAmountTextView.setText("Bet Amount: ₹" + betAmount);
        } else {
            // Handle the case where the bet amount is invalid
            Toast.makeText(this, "Invalid bet amount.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if the bet amount is invalid
        }

        // Add your game logic here
    }
}
