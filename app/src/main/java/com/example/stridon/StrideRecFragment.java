package com.example.stridon;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StrideRecFragment.StrideRecListener} interface
 * to handle interaction events.
 * Use the {@link StrideRecFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StrideRecFragment extends Fragment implements StrideRecAdapter.StrideRecItemListener {
    private static final String TAG = "StrideRecFrag";

    private RecyclerView recyclerView;
    private StrideRecAdapter mAdapter;

    private static final String STRIDE_RECS = "stride_recs";

    Stride[] strideRecs;

    private StrideRecListener mListener;

    public StrideRecFragment() {
        // Required empty public constructor
    }

    public StrideRecFragment(Stride[] recs) {
        strideRecs = recs;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recs
     * @return A new instance of fragment StrideRecFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StrideRecFragment newInstance(Stride[] recs) {
        StrideRecFragment fragment = new StrideRecFragment(recs);
        Bundle args = new Bundle();
        args.putParcelableArray(STRIDE_RECS, recs);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StrideRecListener) {
            mListener = (StrideRecListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement StrideRecListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            strideRecs = (Stride[]) getArguments().getParcelableArray(STRIDE_RECS);
            if (recyclerView != null && recyclerView.getAdapter() != null) {
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_stride_rec, container, false);
        recyclerView = rootView.findViewById(R.id.strideRecRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        mAdapter = new StrideRecAdapter(strideRecs, this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), RecyclerView.VERTICAL);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(dividerItemDecoration);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStrideRecItemClick(Stride stride) {
        if (mListener != null){
            mListener.onStrideRecSelected(stride);
        } else {
            Log.i(TAG, "StrideRecListener is null");
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface StrideRecListener {
        // TODO: Update argument type and name
        void onStrideRecSelected(Stride stride);
    }

    public void setStrideRecs(Stride[] strideRecs) {
        this.strideRecs = strideRecs;
        mAdapter.setStrideRecs(strideRecs);
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }
}
