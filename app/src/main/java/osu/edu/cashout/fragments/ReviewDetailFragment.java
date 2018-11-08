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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import osu.edu.cashout.R;

public class ReviewDetailFragment extends Fragment{
    private TextView mReviewerUsername;
    private TextView mReviewTitle;
    private TextView mReviewRating;
    private TextView mReviewDescription;

    private String mProductUPC;
    private String mUID;
    private String mUsername;

    private FirebaseAuth mUserAuth;
    private DatabaseReference mReviewDbReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_review_detail, container, false);
        mReviewerUsername = v.findViewById(R.id.reviewer_username);
        mReviewTitle = v.findViewById(R.id.review_title);
        mReviewRating = v.findViewById(R.id.review_rating);
        mReviewDescription = v.findViewById(R.id.review_description);

        Bundle arguments = getArguments();
        mProductUPC = arguments.getString("upc");
        mUID = arguments.getString("uid");
        mUsername = arguments.getString("username");

        mReviewDbReference = FirebaseDatabase.getInstance().getReference("reviews");
        mReviewDbReference.orderByChild("upc").equalTo(mProductUPC).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot review : dataSnapshot.getChildren()){
                    if(review.child("userId").getValue(String.class).equals(mUID)){
                        mReviewerUsername.setText(mUsername);
                        mReviewTitle.setText(review.child("title").getValue(String.class));
                        mReviewRating.setText(Double.toString(review.child("score").getValue(Double.class)));
                        if(review.hasChild("description")){
                            mReviewDescription.setText(review.child("description").getValue(String.class));
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //:P
            }
        });

        return v;
    }

}
