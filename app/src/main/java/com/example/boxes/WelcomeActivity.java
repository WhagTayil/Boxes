package com.example.boxes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        setFinishOnTouchOutside (false);
    }

    public void onButtonStart(View view) {
        CheckBox check = findViewById(R.id.checkBoxWelcomeLocked);
        if (check.isChecked()) {
            setResult(RESULT_FIRST_USER + 8000);
            finish();
        }
    }
}