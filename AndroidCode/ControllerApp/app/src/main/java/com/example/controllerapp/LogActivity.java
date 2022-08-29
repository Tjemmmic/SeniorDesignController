package com.example.controllerapp;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.controllerapp.R;

public class LogActivity extends AppCompatActivity {
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        returnToMainActivity();
    }

    private void returnToMainActivity() {
        backButton = findViewById(R.id.mainActivityButton);
        backButton.setOnClickListener(view -> finish());
    }

}
