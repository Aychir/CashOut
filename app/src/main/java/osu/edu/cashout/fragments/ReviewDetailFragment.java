package osu.edu.cashout.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import osu.edu.cashout.R;

public class ReviewDetailFragment extends Fragment{
    private TextView mReviewerUsername;
    private TextView mReviewTitle;
    private TextView mReviewRating;
    private TextView mReviewDescription;
    private ImageView mProductImage;

    private String mTitle;
    private String mRating;
    private String mUsername;
    private String mDescription;
    private String mUPC;

    private DatabaseReference mProductDbReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_review_detail, container, false);
        mReviewerUsername = v.findViewById(R.id.reviewer_username);
        mReviewTitle = v.findViewById(R.id.review_title);
        mReviewRating = v.findViewById(R.id.review_rating);
        mReviewDescription = v.findViewById(R.id.review_description);
        mProductImage = v.findViewById(R.id.product_image);

        Bundle arguments = getArguments();
        mRating = arguments.getString("rating");
        mTitle = arguments.getString("title");
        mUsername = arguments.getString("username");
        mDescription = arguments.getString("description");
        mUPC = arguments.getString("upc");

        mReviewerUsername.setText("User: " + mUsername);
        mReviewTitle.setText("Title: " + mTitle);
        mReviewRating.setText("Rating: " + mRating + "/5.0");
        if(!mDescription.equals("")){
            mReviewDescription.setText(mDescription);
        }

        mProductDbReference = FirebaseDatabase.getInstance().getReference("products");
        mProductDbReference.orderByChild("upc").equalTo(mUPC).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot product : dataSnapshot.getChildren()){
                    if(product.hasChild("image")){
                         Picasso.get().load(product.child("image").getValue(String.class)).into(mProductImage);
                    }
                    else {
                        Picasso.get().load(R.drawable.no_image).into(mProductImage);
                    }
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }

}
