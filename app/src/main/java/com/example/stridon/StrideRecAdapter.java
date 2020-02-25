package com.example.stridon;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class StrideRecAdapter extends RecyclerView.Adapter<StrideRecAdapter.StrideViewHolder> {
    private Stride[] strideRecs;
    private int currPos = -1;

    private StrideRecItemListener mListener;

    public interface StrideRecItemListener {
        void onStrideRecItemClick(Stride stride);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class StrideViewHolder extends RecyclerView.ViewHolder {
        private TextView strideTypeTextView;
        private TextView strideDistanceTextView;
        private TextView strideEstimatedDuration;

        public StrideViewHolder(LinearLayout v) {
            super(v);
            strideTypeTextView = v.findViewById(R.id.strideType);
            strideDistanceTextView = v.findViewById(R.id.strideDistance);
            strideEstimatedDuration = v.findViewById(R.id.strideEstDuration);
        }

    }

    public StrideRecAdapter(Stride[] myDataset, StrideRecItemListener listener) {
        super();
        strideRecs = myDataset;
        mListener = listener;
    }

    public void setStrideRecs(Stride[] toChange) {
        this.strideRecs = toChange;
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
    public void onBindViewHolder(@NonNull StrideRecAdapter.StrideViewHolder holder, final int position) {
        final Stride stride = strideRecs[position];
        holder.strideTypeTextView.setText(stride.getStrideType());
        holder.strideDistanceTextView.setText(String.valueOf(stride.getDistance()));
        holder.strideEstimatedDuration.setText("estimated duration");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldPosition = currPos;
                currPos = position;
                notifyItemChanged(oldPosition);
                notifyItemChanged(currPos);
                mListener.onStrideRecItemClick(stride);
            }
        });
        Log.i("adapter position", String.valueOf(currPos) );
        Log.i("adapter position", String.valueOf(position));
        // if user clicked on this item, or user hasn't clicked any item and its the first item
        if (currPos == position || currPos == -1 && position == 0){
            holder.itemView.setBackgroundColor(Color.parseColor("#F0F0F0"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return strideRecs.length;
    }
}
