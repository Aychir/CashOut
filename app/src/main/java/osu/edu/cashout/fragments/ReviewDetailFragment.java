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

public class ReviewDetailFragment extends Fragment{
    private TextView mReviewerUsername;
    private TextView mReviewTitle;
    private TextView mReviewRating;
    private TextView mReviewDescription;

    private FirebaseAuth mUserAuth;
    private DatabaseReference mDbReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_review_detail, container, false);
        mReviewerUsername = v.findViewById(R.id.reviewer_username);
        mReviewTitle = v.findViewById(R.id.review_title);
        mReviewRating = v.findViewById(R.id.review_rating);
        mReviewDescription = v.findViewById(R.id.review_description);
        return v;
    }

}
