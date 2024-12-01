package com.example.group4f24.util;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class VerificationActivity {
    private EditText codeBox1, codeBox2, codeBox3, codeBox4;
    private Button verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password2);

        // Initialize views
        codeBox1 = findViewById(R.id.code_box1);
        codeBox2 = findViewById(R.id.code_box2);
        codeBox3 = findViewById(R.id.code_box3);
        codeBox4 = findViewById(R.id.code_box4);
        verifyButton = findViewById(R.id.verify);

        // Handle verify button click
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeBox1.getText().toString().trim() +
                        codeBox2.getText().toString().trim() +
                        codeBox3.getText().toString().trim() +
                        codeBox4.getText().toString().trim();

                if (code.length() == 4) {
                    verifyCode(code);
                } else {
                    Toast.makeText(VerificationActivity.this, "Enter a valid 4-digit code.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verifyCode(String code) {
        // Simulate verification (replace this with actual backend logic)
        if (code.equals("1234")) { // Example: 1234 is the correct code
            Toast.makeText(this, "Verification successful!", Toast.LENGTH_SHORT).show();
            // Navigate to reset password screen or next step
        } else {
            Toast.makeText(this, "Invalid code. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
