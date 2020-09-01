package com.example.boxes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;


public class BoxViewAdapter extends RecyclerView.Adapter<BoxViewAdapter.ViewHolder> {
    private static final String LOGTAG = "BOXES:BoxViewAdapter";

    private final MainViewModel mViewModel;

    private final int[] mOpenBoxImageIDs = {
            R.drawable.ic_lock_open_black_48dp_2tone,
            R.drawable.ic_looks_one_black_48dp_2tone,
            R.drawable.ic_looks_two_black_48dp_2tone,
            R.drawable.ic_looks_3_black_48dp_2tone,
            R.drawable.ic_looks_4_black_48dp_2tone,
            R.drawable.ic_looks_5_black_48dp_2tone,
            R.drawable.ic_looks_6_black_48dp_2tone,
            R.drawable.ic_slideshow_black_48dp_2tone,
            R.drawable.ic_pages_black_48dp
    };


    public BoxViewAdapter(MainViewModel viewModel) {
        mViewModel = viewModel;
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

        ImageView imageView = holder.mContentView;

        if(position < mViewModel.getNumBoxesOpen()) {
            int contents = mViewModel.peekBox(position);

            imageView.setImageResource(mOpenBoxImageIDs[contents]);
            if (contents == 0)
                imageView.setTranslationY(4);
            else
                imageView.setTranslationY(6);
            imageView.setScaleY(1);
        } else {
            imageView.setImageResource(R.drawable.ic_redeem_black_48dp_2tone);
            imageView.setTranslationY(0);
            imageView.setScaleY(1.2f);
        }
    }

    @Override
    public int getItemCount() { return mViewModel.getNumBoxes(); }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView mContentView;

        public ViewHolder(View view) {
            super(view);

            mContentView = (ImageView) view.findViewById(R.id.content);
        }
    }
}
