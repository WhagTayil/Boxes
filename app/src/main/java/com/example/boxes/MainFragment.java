package com.example.boxes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainFragment extends Fragment {

    public MainFragment() { }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }



    private static final String LOGTAG = "BOXES:MainFragment";

    private static final int[] boxLabelIDs = {
            R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4,
            R.id.textView5, R.id.textView6, R.id.textView7, R.id.textView8
    };
    private final TextView[] textViewBoxes = {null, null, null, null, null, null, null, null};
    private TextView textViewMainOpen = null;
    private TextView textViewMainStart = null;
    private Button buttonMainOpen = null;

    private TextView textViewMainDay = null;
    private TextView textViewMainSince = null;


    private MainViewModel mViewModel;



    ///////////////////////////////////////////////////////////////
    // Utilty functions to update UI elements
    private void setStartDate() {
        String s = mViewModel.getStartTime() + " on\n" + mViewModel.getStartDate();
        textViewMainStart.setText(s);
    }

    private void setBoxStrings() {
        String s;
        int numBoxesOpen = mViewModel.getNumBoxesOpen();
        int i = 0;
        int contents;
        for (; i < numBoxesOpen; ++i) {
            contents = mViewModel.peekBox(i);
            s = Integer.toString(contents);
            if (contents == 0) s = "key";

            textViewBoxes[i].setText(s);
        }

        s = getString(R.string.text_box_unopened);
        for (; i < mViewModel.getNumBoxes(); ++i) {
            textViewBoxes[i].setText(s);
        }
    }
    // ^ UI update utilities
    ///////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////
    // Background "thread" for countdown to Open
    final Handler handler = new Handler();
    final Runnable run = new Runnable() {
        @Override
        public void run() {
            MainViewModel.GameState currentState = mViewModel.getState();
            if (currentState == MainViewModel.GameState.PLAY) {
                long deltaMillis = mViewModel.getTimeToNextBox();

                final long secondsInMilli = 1000;
                final long minutesInMilli = secondsInMilli * 60;
                final long hoursInMilli = minutesInMilli * 60;

                String s = getString(R.string.button_main_open_now);
                if (deltaMillis > secondsInMilli) {
                    long hours = deltaMillis / hoursInMilli;
                    deltaMillis = deltaMillis % hoursInMilli;
                    long minutes = deltaMillis / minutesInMilli;
                    deltaMillis = deltaMillis % minutesInMilli;
                    long seconds = deltaMillis / secondsInMilli;
                    s = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                    handler.postDelayed(this, 500);
                } else {
                    textViewMainOpen.setText(R.string.text_main_open_now);
                    buttonMainOpen.setEnabled(true);

                    if (BuildConfig.DEBUG)
                        Log.v(LOGTAG, " no more bg runnable");
                }
                buttonMainOpen.setText(s);
            }
        }
    };
    // ^ Background "thread"
    ///////////////////////////////////////////////////////////////

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        FragmentActivity activity = getActivity();
        for (int i=0; i < mViewModel.getNumBoxes(); ++i)
            textViewBoxes[i] = activity.findViewById(boxLabelIDs[i]);
        textViewMainOpen = activity.findViewById(R.id.textViewMainOpen);
        textViewMainStart = activity.findViewById(R.id.textViewMainStart);
        buttonMainOpen = activity.findViewById(R.id.buttonMainOpen);

        textViewMainDay = activity.findViewById(R.id.textViewMainDay);
        textViewMainSince = activity.findViewById(R.id.textViewMainSince);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOGTAG, "onStart()");

        MainActivity activity = (MainActivity) getActivity();


        switch (mViewModel.getState()) {
            case VIRGIN:
                activity.showInstructions();
                break;
            case START:
                activity.showStartScreen();
                break;
            case PLAY:
                buttonMainOpen.setEnabled(false);
                buttonMainOpen.setOnClickListener(activity.onClickButtonOpen);
                setStartDate();
                setBoxStrings();

                handler.postDelayed(run, 0);
                if (BuildConfig.DEBUG)
                    Log.v(LOGTAG, " launch bg runnable from onStart()");
                break;
            case FINISH:
                textViewMainDay.setText(R.string.text_main_day_end);
                textViewMainSince.setText(R.string.text_main_since_end);
                textViewMainOpen.setText(R.string.text_main_restart);
                buttonMainOpen.setText(R.string.button_main_open_restart);
                buttonMainOpen.setEnabled(true);
                buttonMainOpen.setOnClickListener(activity.onClickButtonRestart);
                setStartDate();
                setBoxStrings();
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(LOGTAG, "onStop()");

        handler.removeCallbacks(run);
        if (BuildConfig.DEBUG)
            Log.v(LOGTAG, " remove runnable callbacks in onStop()");
    }
}