package com.example.boxes;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = "BOXES:MainActivity";

    private MainViewModel mViewModel;

    ///////////////////////////////////////////////////////////////
    // Persistence serialization game state data
    private final String saveDataFileName = "svdt";

    private void readSaveData() {
        Log.d(LOGTAG, "readSaveData()");

        Context context = getApplicationContext();
        try {
            FileInputStream fis = context.openFileInput(saveDataFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);

            mViewModel.readFromFile(ois);

            fis.close();
            ois.close();
        } catch (FileNotFoundException e) {
            Log.d(LOGTAG, "readSaveData() FileNotFound", e);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG)
            mViewModel.log(LOGTAG);
    }

    private void writeSaveData() {
        Log.d(LOGTAG, "writeSaveData()");

        Context context = getApplicationContext();
        try {
            FileOutputStream fos = context.openFileOutput(saveDataFileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            mViewModel.writeToFile(oos);

            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG)
            mViewModel.log(LOGTAG);
    }
    // ^ Save data (game state)
    ///////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }

        Log.d(LOGTAG, "onCreate()");
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        readSaveData();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOGTAG, "onStop()");

        writeSaveData();
    }



    public void onButtonPopCherry(View v) {
        Log.d(LOGTAG, "onButtonPopCherry()");

        mViewModel.startGame();
        writeSaveData();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    public void onButtonReady(View view) {
        Log.d(LOGTAG, "onButtonReady()");

        mViewModel.startGame();
        writeSaveData();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    public void onButtonMainOpen(View v) {
        Log.d(LOGTAG, "onButtonMainOpen()");

        int boxContents = mViewModel.peekNextBox();
        OpenBoxFragment fragment = new OpenBoxFragment();
        Bundle args = new Bundle();
        args.putInt(OpenBoxFragment.ARG_BOX_CONTENTS, boxContents);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void onButtonOpenContinue(View v) {
        Log.d(LOGTAG, "onButtonOpenContinue()");

        mViewModel.openBox();
        writeSaveData();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    public final View.OnClickListener onClickButtonMainRestart = new View.OnClickListener() {
        public void onClick(View v) { onButtonMainRestart(v); }
    };
    public void onButtonMainRestart(View view) {
        Log.d(LOGTAG, "onButtonMainRestart()");

        showStartScreen();
    }


    public void showInstructions() {
        Log.d(LOGTAG, "showInstructions()");

        ShowInstructionsFragment fragment = new ShowInstructionsFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void showStartScreen() {
        Log.d(LOGTAG, "showStartScreen()");

        NewGameFragment fragment = new NewGameFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void showSettings() {
        Log.d(LOGTAG, "showSettings");

        SettingsFragment fragment = new SettingsFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void onButtonSettingsOK(View v) {
        Log.d(LOGTAG, "onButtonSettingsOK()");

        writeSaveData();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
    }
}