package com.example.boxes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OpenBoxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpenBoxFragment extends Fragment {

    private static final String LOGTAG = "BOXES:OpenBoxFragment";

    public static final String ARG_BOX_CONTENTS = "contents";
    private int mBoxContents = 7;


    public OpenBoxFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OpenBoxFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OpenBoxFragment newInstance(int boxContents) {
        OpenBoxFragment fragment = new OpenBoxFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BOX_CONTENTS, boxContents);
        fragment.setArguments(args);
        return fragment;
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

        // ToDo: move to strings.xml
        String s = "It contains a... key, yay!";
        if (mBoxContents > 0) { s = String.format("It contains a... %d, haha", mBoxContents); }

        TextView textView = getActivity().findViewById(R.id.textViewOpenBoxResult);
        textView.setText(s);
    }
}