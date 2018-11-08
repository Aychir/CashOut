package osu.edu.cashout.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import osu.edu.cashout.R;

public class ReviewDetailFragment extends Fragment{
    private String mTitle;
    private String mRating;
    private String mUsername;
    private String mDescription;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_review_detail, container, false);
        TextView mReviewerUsername = v.findViewById(R.id.reviewer_username);
        TextView mReviewTitle = v.findViewById(R.id.review_title);
        TextView mReviewRating = v.findViewById(R.id.review_rating);
        TextView mReviewDescription = v.findViewById(R.id.review_description);

        Bundle arguments = getArguments();
        if(arguments != null){
            mRating = arguments.getString("rating");
            mTitle = arguments.getString("title");
            mUsername = arguments.getString("username");
            mDescription = arguments.getString("description");
        }

        mReviewerUsername.setText(mUsername);
        mReviewTitle.setText(mTitle);
        mReviewRating.setText(mRating);
        if(!mDescription.equals("")){
            mReviewDescription.setText(mDescription);
        }

        return v;
    }

}
