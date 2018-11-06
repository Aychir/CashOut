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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import osu.edu.cashout.R;
import osu.edu.cashout.Review;
import osu.edu.cashout.activities.HistoryActivity;
import osu.edu.cashout.activities.MakeReviewActivity;


public class MakeReviewFragment extends Fragment implements View.OnClickListener {
    private EditText mRating;
    private EditText mTitle;
    private EditText mDescription;
    private Review mReview;
    private String upcCode;
    private FirebaseAuth mUserAuth;
    private DatabaseReference mDbReference;
    private Set<Review> setOfReviews;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_make_review, container, false);

        //Get the current user
        mUserAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mUserAuth.getCurrentUser();

        //Initialization of various member variables
        mReview = new Review();
        mRating = v.findViewById(R.id.rating);
        mTitle = v.findViewById(R.id.review_title);
        mDescription = v.findViewById(R.id.description);
        setOfReviews = new HashSet<>();

        //Set onclick listeners to the buttons on the screen
        Button mSaveReviewButton = v.findViewById(R.id.save_review_button);
        mSaveReviewButton.setOnClickListener(this);

        if(getActivity() != null) {
            Intent called = getActivity().getIntent();
            upcCode = called.getStringExtra("upc");
        }

        //Set up an instance to the database and get some of the data
        mDbReference = FirebaseDatabase.getInstance().getReference("reviews");
        mDbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot product : dataSnapshot.getChildren()) {
                    //Create the set of reviews the user has created
                    if(product.child("userId").getValue(String.class).equals(currentUser.getUid())){
                        setOfReviews.add(product.getValue(Review.class));
                    }
                }
                //After all reviews are gathered, fill in the edit text fields if the user has already made a review for this product
                setEditTexts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return v;
    }

    private void setEditTexts(){
        //loop through the set, set edit text fields if the user has made a review for the product
        for(Review rev: setOfReviews){
            if(rev.getUpc().equals(upcCode)){
                mRating.setText(String.format(Locale.getDefault(), "%f%n", rev.getScore()));
                mTitle.setText(rev.getTitle());
                if(rev.getDescription() != null){
                    mDescription.setText(rev.getDescription());
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.save_review_button:
                if(!validateForm()){
                    return;
                }
                else {
                    //Set the values of the review made or updated
                    prepareReview();

                    //Product has been reviewed by the user if we find the upc of the product in the set of reviews they made
                    boolean isReviewed = false;
                    for(Review rev: setOfReviews){
                        if(rev.getUpc().equals(upcCode)){
                            isReviewed = true;
                        }
                    }

                    //User has not made a review of this product yet
                    if(!isReviewed) {
                        mDbReference.push().setValue(mReview);
                    }
                    //Update the review the user has made of the product
                    else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("score", mReview.getScore());
                        map.put("title", mReview.getTitle());
                        map.put("description", mReview.getDescription());
                        map.put("userId", mReview.getUserId());
                        map.put("upc", mReview.getUpc());
                        map.put("date", mReview.getDate());

                        mDbReference.updateChildren(map);
                    }
//                    Intent historyActivity = new Intent(getContext(), HistoryActivity.class);
//                    startActivity(historyActivity);
                }
                break;
        }
    }

    //Check if the review has all necessary information (title and score)
    private boolean validateForm(){
        boolean validated = true;
        if(mTitle.getText().toString().isEmpty()){
            mTitle.setError("Review must have a title");
            validated = false;
        }
        if(mRating.getText().toString().isEmpty()){
            mRating.setError("Review must have a score");
            validated = false;
        }
        return validated;
    }

    //Prepare the review to be updated or added to the database
    private void prepareReview(){
        //Set the title of the review
        mReview.setTitle(mRating.getText().toString());

        //Set the rating
        String rating = mRating.getText().toString();
        float ratingFloat = Float.parseFloat(rating);
        mReview.setScore(ratingFloat);

        //Set the description
        String description = mRating.getText().toString();
        if(!description.isEmpty()){
            mReview.setDescription(description);
        }

        //Receiving the date that the object was reviewed on
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String date = sdf.format(calendar.getTime());
        mReview.setDate(date);

        //Set the upc of the product that was reviewed
        mReview.setUpc(upcCode);

        //Set the id of the user that made the review
        mReview.setUserId(mUserAuth.getUid());
    }
}
