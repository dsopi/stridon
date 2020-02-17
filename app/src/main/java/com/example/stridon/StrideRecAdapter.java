package com.example.stridon;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class StrideRecAdapter extends RecyclerView.Adapter<StrideRecAdapter.StrideViewHolder> {
    private String[] strideRecs;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class StrideViewHolder extends RecyclerView.ViewHolder {
        public TextView strideTypeTextView;

        public StrideViewHolder(LinearLayout v) {
            super(v);
            strideTypeTextView = v.findViewById(R.id.strideType);
        }
    }


    public StrideRecAdapter(String[] myDataset) {
        super();
        strideRecs = myDataset;
    }

    @NonNull
    @Override
    public StrideRecAdapter.StrideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stride_rec_item, parent, false);

        StrideViewHolder vh = new StrideViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull StrideRecAdapter.StrideViewHolder holder, int position) {
        holder.strideTypeTextView.setText(strideRecs[position]);
    }

    @Override
    public int getItemCount() {
        return strideRecs.length;
    }
}
