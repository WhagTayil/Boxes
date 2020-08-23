package com.example.boxes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BoxViewAdapter extends RecyclerView.Adapter<BoxViewAdapter.ViewHolder> {
    private static final String LOGTAG = "BOXES:BoxViewAdapter";

    private List<Integer> mBoxes;
    private int mNumOpen;

    public BoxViewAdapter(List<Integer> boxes, int numOpen) {
        mBoxes = boxes;
        mNumOpen = numOpen;

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

        String s = "?";
        if(position < mNumOpen)
            s = Integer.toString(mBoxes.get(position));
        holder.mContentView.setText(s);
    }

    @Override
    public int getItemCount() {
        return mBoxes.size();
    }

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


/*
class BoxItem {
    public final String content;

    public BoxItem(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
*/
