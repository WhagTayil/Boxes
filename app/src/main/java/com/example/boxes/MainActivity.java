package com.example.boxes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
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


    private static int[] boxes = {3, 2, 4, 3, 1, 2, 1, 0};
    private static Calendar nextBoxDate = Calendar.getInstance();
    private static Calendar startDate = Calendar.getInstance();
    private static int numBoxesOpen = 0;
    enum GameState { VIRGIN, START, PLAY, OPEN, FINISH }
    private static GameState currentState = GameState.VIRGIN;


    private static final int[] boxLabelIDs = {
            R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4,
            R.id.textView5, R.id.textView6, R.id.textView7, R.id.textView8
    };
    private TextView[] textViewBoxes = {null, null, null, null, null, null, null, null};
    private TextView textViewMainOpen = null;
    private TextView textViewMainStart = null;
    private Button buttonMainOpen = null;


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

                String s;
                if (deltaMillis > secondsInMilli) {
                    long hours = deltaMillis / hoursInMilli;
                    deltaMillis = deltaMillis % hoursInMilli;

                    long minutes = deltaMillis / minutesInMilli;
                    deltaMillis = deltaMillis % minutesInMilli;

                    long seconds = deltaMillis / secondsInMilli;
                    s = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    h2.postDelayed(this, 500);
                } else {
                    buttonMainOpen.setEnabled(true);
                    s = "00:00:00\no p e n";
                }
                buttonMainOpen.setText(s);
            }
        }
    };
    // ^ Background timer
    ///////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////
    // Utilty functions to update UI elements
    private void setStartDate() {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("HH:mm:ss");
        String strTime = formatter.format(startDate.getTime());
        formatter = new SimpleDateFormat("EEE, d MMM yyyy");
        String strDate = formatter.format(startDate.getTime());

        String s = strTime + " on\n" + strDate;

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


    ///////////////////////////////////////////////////////////////
    // Persistence serialization game state data
    private final String saveDataFileName = "svdt";

    private void readSaveData() {
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
            //e.printStackTrace();
            System.out.print(" - Boxes WARN ignore save-state read failure\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(" - Boxes readSaveData()\n");
        System.out.printf("  - boxes %d", boxes[0]);
        for (int i=1; i < boxes.length; ++i) {
            System.out.printf(", %d", boxes[i]);
        }
        System.out.print("\n");
        System.out.printf("  - num open = %d\n", numBoxesOpen);
    }

    private void writeSaveData() {
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
    }
    // ^ Save data (game state)
    ///////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.print(" - Boxes onCreate()\n");

        for (int i=0; i < boxes.length; ++i)
            textViewBoxes[i] = findViewById(boxLabelIDs[i]);
        textViewMainOpen = findViewById(R.id.textViewMainOpen);
        textViewMainStart = findViewById(R.id.textViewMainStart);
        buttonMainOpen = findViewById(R.id.buttonMainOpen);

        // TODO: only if doesn't exist
        //writeSaveData();
    }

    @Override
    protected void onStart() {
        super.onStart();

        System.out.print(" - Boxes onStart()\n");

        readSaveData();

        Intent intent;
        switch(currentState) {
            case VIRGIN:
                // TODO: instructions
            case START:
                // show start screen
                intent = new Intent(this, NewGameActivity.class);
                startActivityForResult(intent, 898);
                break;
            case PLAY:
                buttonMainOpen.setEnabled(false);
                setStartDate();
                setBoxStrings();
                h2.postDelayed(run, 0);
                break;
            case OPEN:
                intent = new Intent(this, OpenBoxActivity.class);
                intent.putExtra(BOX_CONTENTS, boxes[numBoxesOpen]);
                startActivityForResult(intent, 897);
                break;
            case FINISH:
                // TODO: new activity for game over screen
                setStartDate();
                setBoxStrings();
                textViewMainOpen.setText("");
                buttonMainOpen.setText("r e- s t a r t");
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        System.out.print(" - Boxes onStop()\n");

        h2.removeCallbacks(run);

        writeSaveData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.printf(" - Boxes onActivityResult(req = %d, res = %d)\n", requestCode, resultCode);

        if (requestCode == 898 && resultCode == RESULT_FIRST_USER + 8000) {
            // Success return from NewGameActivity
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

            System.out.printf("  - shuffle to %d", boxes[0]);
            for (int i=1; i < boxes.length; ++i) {
                System.out.printf(", %d", boxes[i]);
            }
            System.out.print("\n");

            numBoxesOpen = 0;
            //boxes[2] = 0;             // place key to test end game scenario
            setBoxStrings();

            startDate = Calendar.getInstance();
            setStartDate();
            nextBoxDate = Calendar.getInstance();
            nextBoxDate.add(Calendar.DATE, 1);          // WAIT 1 day
            //nextBoxDate.add(Calendar.MINUTE, 1);

            writeSaveData();
        } else if (requestCode == 897 && resultCode == RESULT_FIRST_USER + 8000) {
            // Success return from OpenBoxActivity
            if (boxes[numBoxesOpen] == 0) {
                h2.removeCallbacks(run);

                // TODO: new activity for game over screen
                currentState = GameState.FINISH;
                textViewMainOpen.setText("");
                buttonMainOpen.setText("r e-s t a r t");
            } else {
                currentState = GameState.PLAY;
                nextBoxDate = Calendar.getInstance();
                nextBoxDate.add(Calendar.DATE, boxes[numBoxesOpen]);          // WAIT n days
                //nextBoxDate.add(Calendar.MINUTE, boxes[numBoxesOpen]);
                buttonMainOpen.setEnabled(false);
            }
            ++numBoxesOpen;
            setBoxStrings();
            writeSaveData();
        }
    }

    public void onButtonOpen(View view) {
        System.out.print(" - Boxes click\n");

        if (currentState == GameState.PLAY) {
            currentState = GameState.OPEN;
            writeSaveData();
            Intent intent = new Intent(this, OpenBoxActivity.class);
            intent.putExtra(BOX_CONTENTS, boxes[numBoxesOpen]);
            startActivityForResult(intent, 897);
        } else if (currentState == GameState.FINISH) {
            currentState = GameState.START;
            writeSaveData();
            Intent intent = new Intent(this, NewGameActivity.class);
            startActivityForResult(intent, 898);
        } else {
            System.out.print("  - weird state\n");
        }
    }
}