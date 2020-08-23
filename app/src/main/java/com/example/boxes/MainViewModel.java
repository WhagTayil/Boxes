package com.example.boxes;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class MainViewModel extends ViewModel implements Parcelable {
    // TODO: Implement the ViewModel


    private static final int chastityTimeUnit = Calendar.SECOND;    // Calendar.DATE or Calendar.SECOND
    private static final int chastityTimeDuration = 10;             //      1        or     30

    public static final int[] boxes = {1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 0};
    private static Calendar nextBoxDate = Calendar.getInstance();
    private static Calendar startDate = Calendar.getInstance();
    private static int numBoxesOpen = 0;
    public enum GameState { VIRGIN, START, PLAY, FINISH }
    private static GameState currentState = GameState.VIRGIN;


    public MainViewModel() { }


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        for (int i=0; i < boxes.length; ++i)
            out.writeInt(boxes[i]);
        out.writeLong(nextBoxDate.getTimeInMillis());
        out.writeLong(startDate.getTimeInMillis());
        out.writeInt(numBoxesOpen);
        out.writeInt(currentState.ordinal());
    }

    public static final Parcelable.Creator<MainViewModel> CREATOR
            = new Parcelable.Creator<MainViewModel>() {
        public MainViewModel createFromParcel(Parcel in) {
            return new MainViewModel(in);
        }

        public MainViewModel[] newArray(int size) {
            return new MainViewModel[size];
        }
    };

    private MainViewModel(Parcel in) {
        for (int i=0; i < boxes.length; ++i)
            boxes[i] = in.readInt();
        long l = in.readLong();
        nextBoxDate.setTimeInMillis(l);
        l = in.readLong();
        startDate.setTimeInMillis(l);
        numBoxesOpen = in.readInt();
        int i = in.readInt();
        currentState = GameState.values()[i];
    }



    public void readFromFile(ObjectInputStream ois) throws IOException {
        for (int i=0; i < boxes.length; ++i)
            boxes[i] = ois.readInt();
        long l = ois.readLong();
        nextBoxDate.setTimeInMillis(l);
        l = ois.readLong();
        startDate.setTimeInMillis(l);
        numBoxesOpen = ois.readInt();
        int i = ois.readInt();
        currentState = GameState.values()[i];
    }

    public void writeToFile(ObjectOutputStream oos) throws IOException {
        for (int i=0; i < boxes.length; ++i)
            oos.writeInt(boxes[i]);
        oos.writeLong(nextBoxDate.getTimeInMillis());
        oos.writeLong(startDate.getTimeInMillis());
        oos.writeInt(numBoxesOpen);
        oos.writeInt(currentState.ordinal());
    }










    private void setNextBoxDate(int numUnits) {
        nextBoxDate = Calendar.getInstance();
        nextBoxDate.add(chastityTimeUnit, chastityTimeDuration * numUnits);
    }
    private void setNextBoxDate() { setNextBoxDate(1); }



    public void startGame() {
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
        setNextBoxDate();

        log("BOXES:MainViewModel.startGame()");
    }

    public int openBox() {
        int contents = boxes[numBoxesOpen++];

        if (contents == 0) {
            currentState = GameState.FINISH;
        } else {
            currentState = GameState.PLAY;
            setNextBoxDate(contents);
        }

        return contents;
    }


    public GameState getState() {
        return currentState;
    }

    public String getStartTime() {
        return getTimeString(startDate);
    }
    public String getStartDate() {
        return getDateString(startDate);
    }

    public long getTimeToNextBox() {
        Calendar now = Calendar.getInstance();
        return nextBoxDate.getTimeInMillis() - now.getTimeInMillis();
    }

    public int getNumBoxes() {
        return boxes.length;
    }
    public int getNumBoxesOpen() {
        return numBoxesOpen;
    }

    public int peekBox(int i) {
        return boxes[i];
    }
    public int peekNextBox() {
        return peekBox(numBoxesOpen);
    }


    public void log(String LOGTAG) {
        if (BuildConfig.DEBUG) {
            StringBuilder s = new StringBuilder("boxes:- " + boxes[0]);
            for (int i=1; i < boxes.length; ++i)
                s.append(", ").append(boxes[i]);
            s.append(". (").append(numBoxesOpen).append(" open)");
            Log.v(LOGTAG, s.toString());
            Log.v(LOGTAG, "Start - " + getStartTime() + " " + getStartDate());
            Log.v(LOGTAG, " Next - " + getTimeString(nextBoxDate) + " " + getDateString(nextBoxDate));
            Log.v(LOGTAG, "State - " + currentState.name());
        }
    }



    private String getTimeString(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(calendar.getTime());
    }

    private String getDateString(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");
        return formatter.format(calendar.getTime());
    }


}