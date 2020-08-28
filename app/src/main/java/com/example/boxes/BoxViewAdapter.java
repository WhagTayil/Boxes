package com.example.boxes;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class BoxViewAdapter extends RecyclerView.Adapter<BoxViewAdapter.ViewHolder> {
    private static final String LOGTAG = "BOXES:BoxViewAdapter";

    private MainViewModel mViewModel;

    private final String[] boxLabels = {null, null, null, null, null, null, null, null, null};
    private String boxLabelUnopened;
    private int colorKeyBackground;

    public BoxViewAdapter(MainViewModel viewModel, Activity activity) {
        mViewModel = viewModel;

        boxLabels[0] = activity.getString(R.string.text_box_key);
        boxLabels[1] = activity.getString(R.string.text_box_1day);
        boxLabels[2] = activity.getString(R.string.text_box_2day);
        boxLabels[3] = activity.getString(R.string.text_box_3day);
        boxLabels[4] = activity.getString(R.string.text_box_4day);
        boxLabels[5] = activity.getString(R.string.text_box_5day);
        boxLabels[6] = activity.getString(R.string.text_box_6day);
        boxLabels[7] = activity.getString(R.string.text_box_7day);
        boxLabels[8] = activity.getString(R.string.text_box_infinity);
        boxLabelUnopened = activity.getString(R.string.text_box_unopened);
        colorKeyBackground = activity.getResources().getColor(R.color.colorAccentBluu);

        Log.d(LOGTAG, "CTOR");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.d(LOGTAG, "onCreateViewHolder()");

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_box_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //Log.d(LOGTAG, "onBindViewHolder(" + Integer.toString(position) + ")");

        String s = boxLabelUnopened;
        if(position < mViewModel.getNumBoxesOpen()) {
            int contents = mViewModel.peekBox(position);
            if (contents == 0)
                holder.mContentView.setBackgroundColor(colorKeyBackground);

            s = boxLabels[contents];
        }
        holder.mContentView.setText(s);
    }

    @Override
    public int getItemCount() { return mViewModel.getNumBoxes(); }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);

            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
