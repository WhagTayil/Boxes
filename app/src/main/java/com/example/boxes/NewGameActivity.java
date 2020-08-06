package com.example.boxes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class NewGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        setFinishOnTouchOutside (false);
    }

    public void onButtonStart(View view) {
        CheckBox check = findViewById(R.id.checkBoxNewGameLocked);
        if (check.isChecked()) {
            setResult(RESULT_FIRST_USER + 8000);
            finish();
        }
    }

    //@Override public void onBackPressed() {}
}