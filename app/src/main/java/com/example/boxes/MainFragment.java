package com.example.boxes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;


public class MainFragment extends Fragment {

    public MainFragment() { }

    public static MainFragment newInstance() {
        return new MainFragment();
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

    private TextView textViewMainOpen = null;
    private TextView textViewMainStart = null;
    private Button buttonMainOpen = null;

    private TextView textViewMainDay = null;
    private TextView textViewMainSince = null;

    private MainViewModel mViewModel;

    private RecyclerView recyclerView;
    private final ValueAnimator animation = ValueAnimator.ofFloat(1.0f, 0.0f);
    private ImageView imageViewBox;


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
                    s = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);

                    handler.postDelayed(this, 500);
                } else {
                    if (mViewModel.getNumBoxesOpen() == 0)
                        textViewMainOpen.setText(R.string.text_main_open_first_now);
                    else
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


    private void setStartDate() {
        String s = mViewModel.getStartTime() + getString(R.string.text_main_start_on) + mViewModel.getStartDate();
        textViewMainStart.setText(s);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        FragmentActivity activity = getActivity();

        textViewMainDay = activity.findViewById(R.id.textViewMainDay);
        textViewMainSince = activity.findViewById(R.id.textViewMainSince);
        textViewMainStart = activity.findViewById(R.id.textViewMainStart);
        textViewMainOpen = activity.findViewById(R.id.textViewMainOpen);
        buttonMainOpen = activity.findViewById(R.id.buttonMainOpen);

        BoxViewAdapter adapter = new BoxViewAdapter(mViewModel);
        recyclerView = (RecyclerView) activity.findViewById(R.id.listBoxes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
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
                if (mViewModel.getNumBoxesOpen() == 0)
                    textViewMainOpen.setText(R.string.text_main_open_first_in);
                else
                    textViewMainOpen.setText(R.string.text_main_open_in);
                buttonMainOpen.setEnabled(false);
                buttonMainOpen.setOnClickListener(onClickButtonOpen);
                setStartDate();

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
                buttonMainOpen.setOnClickListener(activity.onClickButtonMainRestart);
                setStartDate();
                break;
            case INFINITY:
                textViewMainOpen.setText(R.string.text_main_infinity);
                buttonMainOpen.setText(R.string.button_main_open_infinity);
                buttonMainOpen.setEnabled(true);
                buttonMainOpen.setOnClickListener(activity.onClickButtonMainInfinity);
                setStartDate();
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(LOGTAG, "onStop()");

        animation.removeAllListeners();
        animation.cancel();

        handler.removeCallbacks(run);
        if (BuildConfig.DEBUG)
            Log.v(LOGTAG, " remove runnable callbacks in onStop()");
    }


    public final View.OnClickListener onClickButtonOpen = new View.OnClickListener() {
        public void onClick(View v) {
            onButtonOpen(v);
        }
    };
    public void onButtonOpen(View v) {
        buttonMainOpen.setEnabled(false);
        buttonMainOpen.setVisibility(View.INVISIBLE);
        Log.d(LOGTAG, "onButtonOpen()");


        // Animate box opening
        BoxViewAdapter.ViewHolder holder = (BoxViewAdapter.ViewHolder)
                recyclerView.findViewHolderForAdapterPosition(mViewModel.getNumBoxesOpen());
        if (holder == null) {
            MainActivity activity = (MainActivity) getActivity();
            activity.onButtonMainOpen(buttonMainOpen);
            return;
        }
        imageViewBox = holder.mContentView;

        animation.setDuration(3000);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue = (float)updatedAnimation.getAnimatedValue();
                imageViewBox.setAlpha(animatedValue);
            }
        });
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Log.d(LOGTAG, " anim end");

                MainActivity activity = (MainActivity) getActivity();
                activity.onButtonMainOpen(buttonMainOpen);
            }
        });
        animation.start();
    }
}