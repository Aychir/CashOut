package osu.edu.cashout.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import osu.edu.cashout.R;
import osu.edu.cashout.dataModels.Review;
import osu.edu.cashout.activities.HistoryActivity;
import osu.edu.cashout.dataModels.Product;


public class MakeReviewFragment extends Fragment implements View.OnClickListener {
    private EditText mRating;
    private EditText mTitle;
    private EditText mDescription;
    private ImageView mProductImage;
    private TextView mProductName;
    private Review mReview;
    private String upcCode;
    private String userId;
    private FirebaseAuth mUserAuth;
    private DatabaseReference mDbReference;
    private Set<Review> setOfReviews;
    private Set<String> setOfReviewIds;

    private static final String TAG = "MakeReviewFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_make_review, container, false);

        Log.v(TAG, "onCreateView");

        //Get the current user
        mUserAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mUserAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        //Initialization of various member variables
        mReview = new Review();
        mRating = v.findViewById(R.id.rating);
        mTitle = v.findViewById(R.id.title);
        mDescription = v.findViewById(R.id.description);
        mProductImage = v.findViewById(R.id.item_icon);
        mProductName = v.findViewById(R.id.productTitle);
        setOfReviews = new HashSet<>();
        setOfReviewIds = new HashSet<>();

        //Set onclick listeners to the buttons on the screen
        Button mSaveReviewButton = v.findViewById(R.id.save_review_button);
        mSaveReviewButton.setOnClickListener(this);

        if (getActivity() != null) {
            Intent called = getActivity().getIntent();
            upcCode = called.getStringExtra("upc");
            Log.v(TAG, "getting upc " + upcCode);
        }

        DatabaseReference productDatabase = FirebaseDatabase.getInstance().getReference("products");
        productDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v(TAG, "on data change product");
                for (DataSnapshot product : dataSnapshot.getChildren()) {
                    //Find the product and get its information to show in make review
                    if (product.child("upc").getValue(String.class) != null && upcCode != null) {
                        if (product.child("upc").getValue(String.class).equals(upcCode)) {
                            if(product.child("image").getValue(String.class) != null) {
                                Picasso.get().load(product.child("image").getValue(String.class)).into(mProductImage);
                            }
                            else{
                                Picasso.get().load(R.drawable.no_image).into(mProductImage);
                            }
                            mProductName.setText(product.child("name").getValue(String.class));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Log.v(TAG, "between db references");
        //Set up an instance to the database and get some of the data
        mDbReference = FirebaseDatabase.getInstance().getReference("reviews");
        mDbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v(TAG, "on data change reviews");
                for (DataSnapshot product : dataSnapshot.getChildren()) {
                    //Create the set of reviews the user has created
                    Log.v(TAG, userId + " in the reviews loop");
                    if (product.child("userId").getValue(String.class) != null && userId != null) {
                        Log.v(TAG, userId + " in the reviews loop first if");
                        if (product.child("userId").getValue(String.class).equals(userId) &&
                                product.child("upc").getValue(String.class).equals(upcCode)) {
                            Log.v(TAG, userId + " in the reviews loop second if " + product.getValue(Review.class).getTitle());
                            setOfReviews.add(product.getValue(Review.class));
                            //Add the key of each review the user has made into a set
                            setOfReviewIds.add(product.getKey());
                        }
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

    private void setEditTexts() {
        Log.v(TAG, "setting edit texts");
        //loop through the set, set edit text fields if the user has made a review for the product
        for (Review rev : setOfReviews) {
            Log.v(TAG, "in for each loop");
            if (rev.getUpc().equals(upcCode)) {
                DecimalFormat format = new DecimalFormat("#.##");
                String formatted = format.format(rev.getScore());
                mRating.setText(formatted);
                mTitle.setText(rev.getTitle());
                if (rev.getDescription() != null) {
                    mDescription.setText(rev.getDescription());
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_review_button:
                if (!validateForm()) {
                    return;
                } else {
                    //Set the values of the review made or updated
                    prepareReview();

                    //Product has been reviewed by the user if we find the upc of the product in the set of reviews they made
                    boolean isReviewed = false;
                    Log.v(TAG, "set of reviews length: " + setOfReviews.size());
                    Log.v(TAG, "set of review ids length: " + setOfReviewIds.size());
                    Iterator i = setOfReviews.iterator();
                    Iterator i2 = setOfReviewIds.iterator();
                    while (i.hasNext() && i2.hasNext()) {
                        Log.v(TAG, "iterating reviews");
                        Review r = (Review) i.next();
                        String reviewId = i2.next().toString();
                        if (r.getUpc().equals(upcCode)) {
                            Log.v(TAG, "boolean set to true");
                            isReviewed = true;
                            mReview.setReviewId(reviewId);
                        }
                    }

                    //User has not made a review of this product yet
                    if (isReviewed == false) {
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

                        DatabaseReference specificReview = FirebaseDatabase.getInstance().getReference("reviews").child(mReview.getReviewId());
                        specificReview.updateChildren(map);
                    }

                    //Set or update the average rating of a product after making the review
                    final DatabaseReference productDatabase = FirebaseDatabase.getInstance().getReference("products");
                    productDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.v(TAG, "Start of on data change");
                            for (DataSnapshot product : dataSnapshot.getChildren()) {
                                if (product.child("upc").getValue(String.class).equals(upcCode)) {
                                    Log.v(TAG, "Found a match");
                                    //Find the product user is reviewing and get its id
                                    final Product prod = product.getValue(Product.class);
                                    final String prodId = product.getKey();
                                    //Product has a rating, so we update the rating
                                    if (prod.getRating() > 0.0) {
                                        Log.v(TAG, "Object has a rating");
                                        //Get reference to entire database of reviews
                                        DatabaseReference reviewReference = FirebaseDatabase.getInstance().getReference("reviews");
                                        reviewReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int count = 0;
                                                double sum = 0.0;
                                                for (DataSnapshot review : dataSnapshot.getChildren()) {
                                                    if (review.child("upc").getValue(String.class).equals(upcCode)) {
                                                        count += 1;
                                                        sum += review.child("score").getValue(Double.class);
                                                    }
                                                }
                                                double average = sum / count;
                                                prod.setRating(average);
                                                Map<String, Object> newRating = new HashMap<>();
                                                newRating.put("rating", average);
                                                if(prodId != null) {
                                                    productDatabase.child(prodId).updateChildren(newRating);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                    //Product doesn't have a rating, so set it to the only score it has been given
                                    else {
                                        Log.v(TAG, "Object has no rating");
                                        Map<String, Object> newRating = new HashMap<>();
                                        newRating.put("rating", mReview.getScore());
                                        if(prodId != null) {
                                            productDatabase.child(prodId).updateChildren(newRating);
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    Intent historyActivity = new Intent(getContext(), HistoryActivity.class);
                    historyActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(historyActivity);
                }
                break;
        }
    }

    //Check if the review has all necessary information (title and score)
    private boolean validateForm() {
        boolean validated = true;
        if (mTitle.getText().toString().isEmpty()) {
            mTitle.setError(getString(R.string.review_needs_title));
            validated = false;
        }
        if (mRating.getText().toString().isEmpty()) {
            mRating.setError(getString(R.string.review_needs_score));
            validated = false;
        }
        try{
            double d = Double.parseDouble(mRating.getText().toString());
            if(d < 1.0 || d > 5.0){
                mRating.setError(getString(R.string.score_bounds));
                validated = false;
            }
        } catch (Exception e){
            mRating.setError(getString(R.string.score_must_be_number));
            validated = false;
        }
        return validated;
    }

    //Prepare the review to be updated or added to the database
    private void prepareReview() {
        //Set the title of the review
        mReview.setTitle(mTitle.getText().toString());

        //Set the rating
        String rating = mRating.getText().toString();
        double ratingValue = Double.parseDouble(rating);
        mReview.setScore(ratingValue);

        //Set the description
        String description = mDescription.getText().toString();
        if (!description.isEmpty()) {
            mReview.setDescription(description);
        }

        //Receiving the date that the object was reviewed on
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String date = sdf.format(calendar.getTime());
        mReview.setDate(date);

        //Set the upc of the product that was reviewed
        mReview.setUpc(upcCode);

        //Set the id of the user that made the review
        mReview.setUserId(mUserAuth.getUid());
    }
}
