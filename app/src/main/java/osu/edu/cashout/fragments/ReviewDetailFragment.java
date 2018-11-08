package osu.edu.cashout.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
    private String mTitle;
    private String mRating;
    private String mUsername;
    private String mDescription;
    private String mUPC;
    private ImageView mProductImage;

    private DatabaseReference mProductDbReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_review_detail, container, false);
        TextView mReviewerUsername = v.findViewById(R.id.reviewer_username);
        TextView mReviewTitle = v.findViewById(R.id.review_title);
        TextView mReviewRating = v.findViewById(R.id.review_rating);
        TextView mReviewDescription = v.findViewById(R.id.review_description);
        mProductImage = v.findViewById(R.id.product_image);

        Bundle arguments = getArguments();
        if(arguments != null){
            mRating = arguments.getString("rating");
            mTitle = arguments.getString("title");
            mUsername = arguments.getString("username");
            mDescription = arguments.getString("description");
            mUPC = arguments.getString("upc");
        }

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
