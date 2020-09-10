package com.example.boxes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;


public class SettingsFragment extends Fragment {

    private static final String LOGTAG = "BOXES:SettingsFragment";

    private MainViewModel mViewModel;

    private int mTotalBoxes;
    private int[] mNumberOfBoxType;
    private int mTimeStep;
    private int mTimeStepUnit;

    private TextView textViewTotalBoxes;
    private static final int[] textViewNumIDs = {
            R.id.textViewQ1, R.id.textViewQ2, R.id.textViewQ3,
            R.id.textViewQ4, R.id.textViewQ5, R.id.textViewQ6,
            R.id.textViewQ7, R.id.textViewQ8, R.id.textViewQ9
    };
    private final TextView[] textViewsNumberOfBoxType = {null, null, null, null, null, null, null, null, null};
    private static final int[] buttonIDs = {
            R.id.imageViewSettingsQ1, R.id.imageViewSettingsQ2, R.id.imageViewSettingsQ3,
            R.id.imageViewSettingsQ4, R.id.imageViewSettingsQ5, R.id.imageViewSettingsQ6,
            R.id.imageViewSettingsQ7, R.id.imageViewSettingsQ8, R.id.imageViewSettingsQ9
    };
    SwitchCompat switchCompatAddMode;

    private TextView textViewTimeStep;
    SeekBar seekBar;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


    private void readViewModel() {
        mTotalBoxes = mViewModel.getNumBoxes();
        mNumberOfBoxType = new int[9];
        for (int i = 0; i < 9; ++i) mNumberOfBoxType[i] = 0;

        int boxContents;
        for (int i = 0; i < mTotalBoxes; ++i) {
            boxContents = mViewModel.peekBox(i) - 1;
            if (boxContents == -1) boxContents = 8;
            mNumberOfBoxType[boxContents] += 1;
        }

        mTimeStep = mViewModel.getChastityTimeDuration();
        int timeUnit = mViewModel.getChastityTimeUnit();
        switch(timeUnit) {
            case Calendar.DATE:
                mTimeStepUnit = 0;
                break;
            case Calendar.HOUR:
                mTimeStepUnit = 1;
                break;
            case Calendar.MINUTE:
                mTimeStep = (mTimeStep / 5);
            case Calendar.SECOND:
                mTimeStepUnit = 2;
                break;
        }

        Log.d(LOGTAG, "readViewModel() time = " + mTimeStep + " in unit no." + mTimeStepUnit);
    }


    private void setBoxStrings(int i) {
        String s = mNumberOfBoxType[i] + getString(R.string.text_settings_Q_symbol);
        textViewsNumberOfBoxType[i].setText(s);

        s = Integer.toString(mTotalBoxes);
        textViewTotalBoxes.setText(s);
    }

    private void setTimeStepText() {
        String s = null;
        switch (mTimeStepUnit) {
            case 0:         // DATE
                if (mTimeStep == 1)
                    s = mTimeStep + getString(R.string.text_settings_time_unit_day);
                else
                    s = mTimeStep + getString(R.string.text_settings_time_unit_days);
                break;
            case 1:         // HOUR
                if (mTimeStep == 1)
                    s = mTimeStep + getString(R.string.text_settings_time_unit_hour);
                else
                    s = mTimeStep + getString(R.string.text_settings_time_unit_hours);
                break;
            case 2:         // MINUTE
                s = mTimeStep + getString(R.string.text_settings_time_unit_minutes);
                break;
        }
        textViewTimeStep.setText(s);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        readViewModel();

        FragmentActivity activity = getActivity();

        textViewTotalBoxes = activity.findViewById(R.id.textViewNumBoxes);
        String s = Integer.toString(mTotalBoxes);
        textViewTotalBoxes.setText(s);

        ImageView imageView;
        for (int i=0; i < 9; ++i) {
            textViewsNumberOfBoxType[i] = activity.findViewById(textViewNumIDs[i]);
            s = mNumberOfBoxType[i] + getString(R.string.text_settings_Q_symbol);
            textViewsNumberOfBoxType[i].setText(s);

            imageView = activity.findViewById(buttonIDs[i]);
            imageView.setOnClickListener(onClickButton);
        }

        switchCompatAddMode = activity.findViewById(R.id.switchAddMode);
        switchCompatAddMode.setChecked(true);

        textViewTimeStep = activity.findViewById(R.id.textViewTimeStep);
        seekBar = activity.findViewById(R.id.seekBarTimeStep);
        seekBar.setProgress(mTimeStep - 1);
        seekBar.setOnSeekBarChangeListener(onChangeSeekBar);
        if (mTimeStepUnit == 2)
            mTimeStep = mTimeStep * 5;
        setTimeStepText();

        Spinner spinner = (Spinner) activity.findViewById(R.id.spinnerTimeStep);
        spinner.setSelection(mTimeStepUnit);
        spinner.setOnItemSelectedListener(onTimeStepUnitSelected);

        Button button = activity.findViewById(R.id.buttonSettingsOK);
        button.setOnClickListener(onClickOK);
    }

    public final SeekBar.OnSeekBarChangeListener onChangeSeekBar = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            mTimeStep = i + 1;
            if (mTimeStepUnit == 2)
                mTimeStep *= 5;
            setTimeStepText();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    public final View.OnClickListener onClickButton = new View.OnClickListener() {
        public void onClick(View v) {
            onButton(v);
        }
    };
    public void onButton(View v) {
        int i=0;
        for (; i < 9; ++i) {
            if (v.getId() == buttonIDs[i])
                break;
        }

        //if (mAddMode) {
        if (switchCompatAddMode.isChecked()) {
            // ToDo: Lower max total boxes? (81)
            if (mNumberOfBoxType[i] < 9) {
                ++mNumberOfBoxType[i];
                ++mTotalBoxes;

                setBoxStrings(i);
            }
        } else {
            if (mNumberOfBoxType[i] > 0) {
                if ((mNumberOfBoxType[i] == 1) && (i == 8)) return;
                if ((mNumberOfBoxType[i] == 1) && (mTotalBoxes - mNumberOfBoxType[8] == 1)) return;

                --mNumberOfBoxType[i];
                --mTotalBoxes;
                
                setBoxStrings(i);
            }
        }
    }

    public final View.OnClickListener onClickOK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onButtonOK(v);
        }
    };
    public void onButtonOK(View v) {
        Log.d(LOGTAG, "onButtonOK() time = " + mTimeStep + " in unit no." + mTimeStepUnit);

        mViewModel.setBoxes(mTotalBoxes, mNumberOfBoxType);

        int timeUnit = Calendar.DATE;
        if (mTimeStepUnit == 1)
            timeUnit = Calendar.HOUR;
        else if (mTimeStepUnit == 2)
            timeUnit = Calendar.MINUTE;

        mViewModel.setChastityTime(timeUnit, mTimeStep);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.onButtonSettingsOK(v);
    }

    public class SpinnerListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            Log.d(LOGTAG, "OnItemSelectedListened() " + mTimeStepUnit + " to " + pos);

            if (mTimeStepUnit == 2)
                mTimeStep = mTimeStep / 5;
            if (pos == 2)
                mTimeStep = mTimeStep * 5;

            mTimeStepUnit = pos;
            setTimeStepText();
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
            Log.d(LOGTAG, "OnNothingSelectedListened()");
        }
    }
    SpinnerListener onTimeStepUnitSelected = new SpinnerListener();
}