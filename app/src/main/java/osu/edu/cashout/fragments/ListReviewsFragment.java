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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import osu.edu.cashout.R;
import osu.edu.cashout.Review;
import osu.edu.cashout.ReviewAdapter;

public class ListReviewsFragment extends Fragment{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private Set<Review> mReviews;
    private Map<String, String> mUIDToUsername;
    private Review[] mReviewsArr;
    private String mProductUPC;

    private TextView mAverageRating;
    private TextView mProductName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_list_reviews, container, false);

        mAverageRating = v.findViewById(R.id.average_rating);
        mProductName = v.findViewById(R.id.product_name);

        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        mRecyclerView.hasFixedSize();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Bundle arguments = getArguments();
        if(arguments != null) {
            mProductUPC = arguments.getString("upc");
        }

        mReviews = new HashSet<>();
        mUIDToUsername = new HashMap<>();

        DatabaseReference mUsersDbReference = FirebaseDatabase.getInstance().getReference("users");
        mUsersDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot user : dataSnapshot.getChildren()){
                    mUIDToUsername.put(user.getKey(), user.child("username").getValue(String.class));
                }

                DatabaseReference mReviewsDbReference = FirebaseDatabase.getInstance().getReference("reviews");
                mReviewsDbReference.orderByChild("upc").equalTo(mProductUPC).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot review : dataSnapshot.getChildren()) {
                            mReviews.add(review.getValue(Review.class));
                        }
                        mReviewsArr = mReviews.toArray(new Review[mReviews.size()]);
                        mAdapter = new ReviewAdapter(mReviewsArr, mUIDToUsername, getContext());
                        mRecyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //:P
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //:P
            }
        });

        DatabaseReference mProductDbReference = FirebaseDatabase.getInstance().getReference("products");
        mProductDbReference.orderByChild("upc").equalTo(mProductUPC).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot product : dataSnapshot.getChildren()){
                    mAverageRating.setText(Double.toString(product.child("rating").getValue(Double.class)));
                    mProductName.setText(product.child("name").getValue(String.class));
                    break;
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
