package com.example.boxes;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewGameFragment extends Fragment {

    public NewGameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewGameFragment.
     */
    public static NewGameFragment newInstance() {
        return new NewGameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_game, container, false);
    }





    private View mButton;
    private boolean mChecked = false;

    public final View.OnClickListener onClickCheckBox = new View.OnClickListener() {
        public void onClick(View v) {
            onCheckBox(v);
        }
    };
    public void onCheckBox(View v) {
        mChecked = !mChecked;
        mButton.setEnabled(mChecked);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Activity activity = getActivity();
        mButton = activity.findViewById(R.id.buttonNewGameStart);
        mButton.setEnabled(false);

        View checkBox = activity.findViewById(R.id.checkBoxNewGameLocked);
        checkBox.setOnClickListener(onClickCheckBox);
    }


}