package com.example.boxes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class OpenBoxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_box);

        Intent intent = getIntent();

        int contents = intent.getIntExtra(MainActivity.BOX_CONTENTS, 5);
        String s = "It contains a... key, yay!";
        if (contents > 0) {
            s = String.format("It contains a... %d, haha", contents);
        }

        TextView textView = findViewById(R.id.textViewOpenBoxResult);
        textView.setText(s);
    }

    public void onButtonBack(View view) {
        setResult(RESULT_FIRST_USER + 8000);
        finish();
    }
}