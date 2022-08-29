package com.example.controllerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.controllerapp.R;

public class OptionsActivity extends AppCompatActivity {
    Button backButton;
    EditText hostOption;
    EditText portOption;
    Intent returnStatesIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        returnStatesIntent = getIntent();
        hostOption = findViewById(R.id.hostText);
        portOption = findViewById(R.id.portText);
        setOptionsSaved();
        returnToMainActivity();
        optionCheckBoxEvent();
    }

    private void setOptionsSaved() {
        CheckBox optionOne = findViewById(R.id.optionOneCheckBox);
        TextView optionOneText = findViewById(R.id.optionOneText);
        CheckBox optionTwo = findViewById(R.id.optionTwoCheckBox);
        TextView optionTwoText = findViewById(R.id.optionTwoText);

        Bundle extras = getIntent().getExtras();
        Log.d("Checkpoint", "Test Checkpoint 1");
        if (extras != null) {
            Log.d("Checkpoint", "Test Checkpoint 2");
            boolean optionOneState = extras.getBoolean("optionOneState");
            optionOne.setChecked(optionOneState);
            Log.d("Checkpoint", "Out: " + optionOneState);
            if (optionOne.isChecked()) {
                optionOneText.setText("Checked");
            } else {
                optionOneText.setText("Unchecked");
            }

            boolean optionTwoState = extras.getBoolean("optionTwoState");
            optionTwo.setChecked(optionTwoState);
            if (optionTwo.isChecked()) {
                optionTwoText.setText("Checked");
            } else {
                optionTwoText.setText("Unchecked");
            }

            Log.d("Checkpoint", "Test Checkpoint 2");
            hostOption.setText(extras.getString("hostnameText"));
            portOption.setText(extras.getString("portText"));
        }
    }

    private void optionCheckBoxEvent() {
        CheckBox optionOne = findViewById(R.id.optionOneCheckBox);
        TextView optionOneText = findViewById(R.id.optionOneText);
        CheckBox optionTwo = findViewById(R.id.optionTwoCheckBox);
        TextView optionTwoText = findViewById(R.id.optionTwoText);

        optionOne.setOnClickListener(view -> {
            if (optionOne.isChecked()) {
                optionOneText.setText("Checked");
            } else {
                optionOneText.setText("Unchecked");
            }
            returnStatesIntent.putExtra("optionOneCheckBox", optionOne.isChecked());
        });
        optionTwo.setOnClickListener(view -> {
            if (optionTwo.isChecked()) {
                optionTwoText.setText("Checked");
            } else {
                optionTwoText.setText("Unchecked");
            }
            returnStatesIntent.putExtra("optionTwoCheckBox", optionTwo.isChecked());
        });
        setResult(Activity.RESULT_OK, returnStatesIntent);
    }

    private void returnToMainActivity() {
        backButton = findViewById(R.id.mainActivityButton);
        backButton.setOnClickListener(view -> {
            returnStatesIntent.putExtra("hostname", String.valueOf(hostOption.getText()));
            returnStatesIntent.putExtra("port", String.valueOf(portOption.getText()));
            setResult(Activity.RESULT_OK, returnStatesIntent);
            finish();
        });
    }
}
