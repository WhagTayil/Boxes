package com.example.boxes;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class OpenBoxFragment extends Fragment {

    private static final String LOGTAG = "BOXES:OpenBoxFragment";

    public static final String ARG_BOX_CONTENTS = "contents";
    private int mBoxContents = 7;


    public OpenBoxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOGTAG, "onCreate()");

        if (getArguments() != null) {
            mBoxContents = getArguments().getInt(ARG_BOX_CONTENTS);
        }

        Log.d(LOGTAG, " contents = " + mBoxContents);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_open_box, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOGTAG, "onViewCreated()");

        Activity activity = getActivity();
        TextView textViewResult = activity.findViewById(R.id.textViewOpenBoxResult);

        switch(mBoxContents) {
            case 0:
                // "It contains a... key, yay!"
                textViewResult.setText(R.string.text_openbox_result_key);
                break;
            case 8:
                // "It is an Infinity Box! Too bad."
                textViewResult.setText(R.string.text_openbox_result_infinity);
                break;
            default:
                // "It contains a... %d, haha"
                String s;
                s = getString(R.string.text_openbox_result_prefix) + mBoxContents + getString(R.string.text_openbox_result_postfix);
                textViewResult.setText(s);
        }

        Button buttonContinue = activity.findViewById(R.id.buttonOpenBoxContinue);
        buttonContinue.setOnClickListener(onClickButtonContinue);
    }

    // This listener is a trivial demonstration of catching a UI event at Fragment level.
    // It simply calls a handler in the parent Activity.
    public final View.OnClickListener onClickButtonContinue = new View.OnClickListener() {
        public void onClick(View v) {
            onButtonContinue(v);
        }
    };
    public void onButtonContinue(View v) {
        Log.d(LOGTAG, "onButtonContinue()");

        MainActivity activity = (MainActivity) getActivity();
        activity.onButtonOpenContinue(v);
    }
}