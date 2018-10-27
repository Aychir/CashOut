package osu.edu.cashout.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import osu.edu.cashout.R;
import osu.edu.cashout.Review;
import osu.edu.cashout.activities.HistoryActivity;
import osu.edu.cashout.activities.MakeReviewActivity;


public class MakeReviewFragment extends Fragment implements View.OnClickListener {
    private Button mSaveReviewButton;
    private EditText mRating;
    private EditText mTitle;
    private EditText mDescription;
    private Review mReview;
    private FirebaseAuth mUserAuth;
    private DatabaseReference mDbReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_info, container, false);

        mUserAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mUserAuth.getCurrentUser();

        //Set onclick listeners to the buttons on the screen
        mSaveReviewButton = v.findViewById(R.id.create_review_button);
        mSaveReviewButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.save_review_button:
                if(!validateForm()){
                    mTitle.setError("Review must have a title");
                    return;
                }
                else {
                    updateReview();
                    //TODO: Add new review to db
//                    Intent historyActivity = new Intent(getContext(), HistoryActivity.class);
//                    startActivity(historyActivity);
                }
                break;
        }
    }

    private boolean validateForm(){
        return !mTitle.getText().toString().isEmpty();
    }

    private void updateReview(){
        String title = mRating.getText().toString();
        mReview.setTitle(title);
        String rating = mRating.getText().toString();
        if(!rating.isEmpty()){
            float ratingFloat = Float.parseFloat(rating);
            mReview.setScore(ratingFloat);
        }
        String description = mRating.getText().toString();
        if(!description.isEmpty()){
            mReview.setDescription(description);
        }
    }
}
