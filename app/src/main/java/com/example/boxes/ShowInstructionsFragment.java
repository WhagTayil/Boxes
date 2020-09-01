package com.example.boxes;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class ShowInstructionsFragment extends Fragment {

    public ShowInstructionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_instructions, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rare_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_settings:
                MainActivity activity = (MainActivity)getActivity();
                activity.showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        mButton = activity.findViewById(R.id.buttonWelcomeStart);
        mButton.setEnabled(false);

        View checkBox = activity.findViewById(R.id.checkBoxWelcomeLocked);
        checkBox.setOnClickListener(onClickCheckBox);
    }
}