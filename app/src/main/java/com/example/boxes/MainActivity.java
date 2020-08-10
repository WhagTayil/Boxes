package com.example.boxes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // key for parameter passed to Open Box Activity
    public static final String BOX_CONTENTS = "com.example.boxes.BOX_CONTENTS";

    private static final int chastityTimeUnit = Calendar.DATE;
    private static final int chastityTimeDuration = 1;
    //private static final int chastityTimeUnit = Calendar.SECOND;
    //private static final int chastityTimeDuration = 30;

    private static int[] boxes = {3, 2, 4, 3, 1, 2, 1, 0};
    private static Calendar nextBoxDate = Calendar.getInstance();
    private static Calendar startDate = Calendar.getInstance();
    private static int numBoxesOpen = 0;
    enum GameState { VIRGIN, START, PLAY, FINISH }
    private static GameState currentState = GameState.VIRGIN;


    private static final int[] boxLabelIDs = {
            R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4,
            R.id.textView5, R.id.textView6, R.id.textView7, R.id.textView8
    };
    private TextView[] textViewBoxes = {null, null, null, null, null, null, null, null};
    private TextView textViewMainOpen = null;
    private TextView textViewMainStart = null;
    private Button buttonMainOpen = null;


    private static final String LOGTAG = "BOXES:MyActivity";

    ///////////////////////////////////////////////////////////////
    // Background timer https://stackoverflow.com/questions/4597690/how-to-set-timer-in-android
    Handler h2 = new Handler();
    Runnable run = new Runnable()
    {
        @Override
        public void run() {
            if (currentState == GameState.PLAY) {
                Calendar now = Calendar.getInstance();
                long deltaMillis = nextBoxDate.getTimeInMillis() - now.getTimeInMillis();

                long secondsInMilli = 1000;
                long minutesInMilli = secondsInMilli * 60;
                long hoursInMilli = minutesInMilli * 60;

                String s = "00:00:00\no p e n";
                if (deltaMillis > secondsInMilli) {
                    long hours = deltaMillis / hoursInMilli;
                    deltaMillis = deltaMillis % hoursInMilli;

                    long minutes = deltaMillis / minutesInMilli;
                    deltaMillis = deltaMillis % minutesInMilli;

                    long seconds = deltaMillis / secondsInMilli;
                    s = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    h2.postDelayed(this, 500);

                    //if (BuildConfig.DEBUG)
                    //    Log.v(LOGTAG, " launch bg runnable from runnable");
                } else {
                    buttonMainOpen.setEnabled(true);

                    if (BuildConfig.DEBUG)
                        Log.v(LOGTAG, " no more bg runnable");
                }
                buttonMainOpen.setText(s);
            }
        }
    };
    // ^ Background timer
    ///////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////
    // Utilty functions to update UI elements
    private String getTimeString(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(calendar.getTime());
    }

    private String getDateString(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");
        return formatter.format(calendar.getTime());
    }

    private void setStartDate() {
        String s = getTimeString(startDate) + " on\n" + getDateString(startDate);
        textViewMainStart.setText(s);
    }

    private void setBoxStrings() {
        String s;
        int i = 0;
        for (; i < numBoxesOpen; ++i) {
            switch(boxes[i]) {
                case 0:
                    s = getString(R.string.text_box_key);
                    break;
                case 1:
                    s = getString(R.string.text_box_1day);
                    break;
                case 2:
                    s = getString(R.string.text_box_2day);
                    break;
                case 3:
                    s = getString(R.string.text_box_3day);
                    break;
                case 4:
                    s = getString(R.string.text_box_4day);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + boxes[i]);
            }
            textViewBoxes[i].setText(s);
        }

        s = getString(R.string.text_box_unopened);
        for (; i < boxes.length; ++i) {
            textViewBoxes[i].setText(s);
        }
    }
    // ^ UI update utilities
    ///////////////////////////////////////////////////////////////


    private void logBoxData() {
        if (BuildConfig.DEBUG) {
            StringBuilder s = new StringBuilder("boxes:- " + boxes[0]);
            for (int i=1; i < boxes.length; ++i)
                s.append(", ").append(boxes[i]);
            s.append(". (").append(numBoxesOpen).append(" open)");
            Log.v(LOGTAG, s.toString());
            Log.v(LOGTAG, "Start - " + getTimeString(startDate) + " " + getDateString(startDate));
            Log.v(LOGTAG, " Next - " + getTimeString(nextBoxDate) + " " + getDateString(nextBoxDate));
            Log.v(LOGTAG, "State - " + currentState.name());
        }
    }


    ///////////////////////////////////////////////////////////////
    // Persistence serialization game state data
    private final String saveDataFileName = "svdt";

    private void readSaveData() {
        Log.d(LOGTAG, "readSaveData()");
        Context context = getApplicationContext();

        try {
            FileInputStream fis = context.openFileInput(saveDataFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);

            for (int i=0; i < boxes.length; ++i)
                boxes[i] = ois.readInt();
            long l = ois.readLong();
            nextBoxDate.setTimeInMillis(l);
            l = ois.readLong();
            startDate.setTimeInMillis(l);
            numBoxesOpen = ois.readInt();
            int i = ois.readInt();
            currentState = GameState.values()[i];

            fis.close();
            ois.close();
        } catch (FileNotFoundException e) {
            Log.d(LOGTAG, "readSaveData() FileNotFound", e);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logBoxData();
    }

    private void writeSaveData() {
        Log.d(LOGTAG, "writeSaveData()");

        Context context = getApplicationContext();

        try {
            FileOutputStream fos = context.openFileOutput(saveDataFileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            for (int i=0; i < boxes.length; ++i)
                oos.writeInt(boxes[i]);
            oos.writeLong(nextBoxDate.getTimeInMillis());
            oos.writeLong(startDate.getTimeInMillis());
            oos.writeInt(numBoxesOpen);
            oos.writeInt(currentState.ordinal());

            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logBoxData();
    }
    // ^ Save data (game state)
    ///////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOGTAG, "onCreate()");

        for (int i=0; i < boxes.length; ++i)
            textViewBoxes[i] = findViewById(boxLabelIDs[i]);
        textViewMainOpen = findViewById(R.id.textViewMainOpen);
        textViewMainStart = findViewById(R.id.textViewMainStart);
        buttonMainOpen = findViewById(R.id.buttonMainOpen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOGTAG, "onStart()");

        readSaveData();

        Intent intent;
        switch(currentState) {
            case VIRGIN:
                // show instructions start screen
                intent = new Intent(this, WelcomeActivity.class);
                startActivityForResult(intent, 898);
                break;
            case START:
                // show start screen
                intent = new Intent(this, NewGameActivity.class);
                startActivityForResult(intent, 898);
                break;
            case PLAY:
                setStartDate();
                buttonMainOpen.setEnabled(false);
                setBoxStrings();
                h2.postDelayed(run, 0);
                if (BuildConfig.DEBUG)
                    Log.v(LOGTAG, " launch bg runnable from onStart()");
                break;
            case FINISH:
                // TODO: new activity for game over screen
                setStartDate();
                textViewMainOpen.setText("");
                buttonMainOpen.setText("r e- s t a r t");
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOGTAG, "onStop()");

        h2.removeCallbacks(run);
        if (BuildConfig.DEBUG)
            Log.v(LOGTAG, " remove runnable callbacks");

        writeSaveData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String s = "onActivityResult(req = " + requestCode + ", res = " + resultCode + ")";
        Log.d(LOGTAG, s);

        if (requestCode == 898 && resultCode == RESULT_FIRST_USER + 8000) {
            // Success return from NewGameActivity or WelcomeActivity
            currentState = GameState.PLAY;

            // Shuffle boxes
            Random rnd = new Random();
            for (int i = boxes.length - 1; i > 0; i--) {
                int index = rnd.nextInt(i + 1);
                // Simple swap
                int a = boxes[index];
                boxes[index] = boxes[i];
                boxes[i] = a;
            }
            numBoxesOpen = 0;

            startDate = Calendar.getInstance();
            nextBoxDate = Calendar.getInstance();
            nextBoxDate.add(chastityTimeUnit, chastityTimeDuration);

            setStartDate();
            textViewMainOpen.setText(R.string.text_main_open);
            setBoxStrings();

            writeSaveData();
        }
        else if (requestCode == 897 && resultCode == RESULT_FIRST_USER + 8000) {
            // Success return from OpenBoxActivity
            if (boxes[numBoxesOpen] == 0) {
                h2.removeCallbacks(run);
                if (BuildConfig.DEBUG)
                    Log.v(LOGTAG, " remove runnable callbacks [KEY]");

                currentState = GameState.FINISH;
                textViewMainOpen.setText("");
                buttonMainOpen.setText("r e-s t a r t");
            }
            else {
                currentState = GameState.PLAY;
                nextBoxDate = Calendar.getInstance();
                nextBoxDate.add(chastityTimeUnit, chastityTimeDuration * boxes[numBoxesOpen]);
                buttonMainOpen.setEnabled(false);
            }

            ++numBoxesOpen;
            setBoxStrings();
            writeSaveData();
        }
    }

    public void onButtonOpen(View view) {
        Log.d(LOGTAG, "onButtonOpen()");

        if (currentState == GameState.PLAY) {
            Intent intent = new Intent(this, OpenBoxActivity.class);
            intent.putExtra(BOX_CONTENTS, boxes[numBoxesOpen]);
            startActivityForResult(intent, 897);
        } else if (currentState == GameState.FINISH) {
            Intent intent = new Intent(this, NewGameActivity.class);
            startActivityForResult(intent, 898);
        } else {
            Log.w(LOGTAG, "  - weird state");
        }
    }
}