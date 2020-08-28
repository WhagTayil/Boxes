package com.example.boxes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    //private static final String LOGTAG = "BOXES:SettingsFragment";

    private int mTotalBoxes = 15;
    private int[] mNumberOfBoxType = { 3, 3, 2, 2, 1, 1, 1, 1, 1 };
    private boolean mAddMode = true;
    private int mTimeStep = 2;

    private TextView textViewTotalBoxes;
    private static final int[] textViewNumIDs = {
            R.id.textViewQ1, R.id.textViewQ2, R.id.textViewQ3,
            R.id.textViewQ4, R.id.textViewQ5, R.id.textViewQ6,
            R.id.textViewQ7, R.id.textViewQ8, R.id.textViewQ9
    };
    private TextView[] textViewsNumberOfBoxType = {null, null, null, null, null, null, null, null, null};
    private static final int[] buttonIDs = {
            R.id.imageViewSettingsQ1, R.id.imageViewSettingsQ2, R.id.imageViewSettingsQ3,
            R.id.imageViewSettingsQ4, R.id.imageViewSettingsQ5, R.id.imageViewSettingsQ6,
            R.id.imageViewSettingsQ7, R.id.imageViewSettingsQ8, R.id.imageViewSettingsQ9
    };
    private TextView textViewTimeStep;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = getActivity();

        textViewTotalBoxes = activity.findViewById(R.id.textViewNumBoxes);

        ImageView imageView;
        for (int i=0; i < 9; ++i) {
            textViewsNumberOfBoxType[i] = activity.findViewById(textViewNumIDs[i]);
            imageView = activity.findViewById(buttonIDs[i]);
            imageView.setOnClickListener(onClickButton);
        }

        SwitchCompat switchCompat = activity.findViewById(R.id.switchAddMode);
        switchCompat.setOnClickListener(onClickSwitch);

        SeekBar seekBar = activity.findViewById(R.id.seekBarTimeStep);
        seekBar.setOnSeekBarChangeListener(onChangeSeekBar);
        textViewTimeStep = activity.findViewById(R.id.textViewTimeStep);
    }


    public final SeekBar.OnSeekBarChangeListener onChangeSeekBar = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            mTimeStep = i + 1;
            String s;
            if (mTimeStep == 1)
                s = Integer.toString(mTimeStep) + "hr";
            else
                s = Integer.toString(mTimeStep) + "hrs";
            textViewTimeStep.setText(s);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
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

        if (mAddMode) {
            if (mNumberOfBoxType[i] < 9) {
                ++mNumberOfBoxType[i];
                ++mTotalBoxes;
            }
        } else {
            if (mNumberOfBoxType[i] > 0) {
                --mNumberOfBoxType[i];
                --mTotalBoxes;
            }
        }

        String s = Integer.toString(mNumberOfBoxType[i]) + "x";
        textViewsNumberOfBoxType[i].setText(s);

        s = Integer.toString(mTotalBoxes);
        textViewTotalBoxes.setText(s);
    }

    public final View.OnClickListener onClickSwitch = new View.OnClickListener() {
        public void onClick(View v) {
            onSwitch(v);
        }
    };
    public void onSwitch(View v) {
        mAddMode = !mAddMode;
    }
}