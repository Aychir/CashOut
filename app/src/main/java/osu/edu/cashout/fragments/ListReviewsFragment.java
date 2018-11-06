package osu.edu.cashout.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import osu.edu.cashout.R;

public class ListReviewsFragment extends Fragment{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView mAverageRating;

    private FirebaseAuth mUserAuth;
    private DatabaseReference mDbReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_list_reviews, container, false);

        mAverageRating = v.findViewById(R.id.average_rating);

        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        mRecyclerView.hasFixedSize();
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mAdapter = new ReviewAdapter(data);
//        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

}
