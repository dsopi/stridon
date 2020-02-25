package com.example.stridon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class StrideRecAdapter extends RecyclerView.Adapter<StrideRecAdapter.StrideViewHolder> {
    private Stride[] strideRecs;

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

        public void bind(final Stride stride, final StrideRecItemListener listener) {
            strideTypeTextView.setText(stride.getStrideType());
            strideDistanceTextView.setText(String.valueOf(stride.getDistance()));
            strideEstimatedDuration.setText("estimated duration");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onStrideRecItemClick(stride);
                }
            });
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
    public void onBindViewHolder(@NonNull StrideRecAdapter.StrideViewHolder holder, int position) {
        holder.bind(strideRecs[position], mListener);
    }

    @Override
    public int getItemCount() {
        return strideRecs.length;
    }
}
