package osu.edu.cashout.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import osu.edu.cashout.R;
import osu.edu.cashout.activities.MakeReviewActivity;
import osu.edu.cashout.dataModels.Product;

public class InfoFragment extends Fragment implements View.OnClickListener {
    private TextView mProductTitle;
    private TextView mHighPrice;
    private TextView mLowPrice;
    private TextView mCurrentPrice;
    private TextView mCustomerReview;
    private Button mCreateReviewButton;
    private Product mProduct;
    private String mProductUPC;
    private FirebaseAuth mUserAuth;
    private DatabaseReference mDbReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);

        mUserAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mUserAuth.getCurrentUser();

        //Set onclick listeners to the buttons on the screen
        mCreateReviewButton = v.findViewById(R.id.create_review_button);
        mCreateReviewButton.setOnClickListener(this);

        mProductTitle = v.findViewById(R.id.productTitle);
        mHighPrice = v.findViewById(R.id.highPrice);
        mLowPrice = v.findViewById(R.id.lowPrice);
        mCurrentPrice = v.findViewById(R.id.currentPrice);
        mCustomerReview = v.findViewById(R.id.customerReview);

        Bundle arguments = getArguments();
        mProductUPC = arguments.getString("upc");
        mDbReference = FirebaseDatabase.getInstance().getReference("products");
        mDbReference.orderByChild("upc").equalTo(mProductUPC).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    mProduct = data.getValue(Product.class);
                    break;
                }

                Drawable image = getContext().getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark);
                mProductTitle.setCompoundDrawablesWithIntrinsicBounds( image, null, null, null);
                mProductTitle.setText(mProduct.getName());
                mHighPrice.setText("Highest Price: $" + mProduct.getHighestPrice());
                mLowPrice.setText("Lowest Price: $" + mProduct.getLowestPrice());
                mCurrentPrice.setText("Current Price: $" + mProduct.getCurrentPrice());
                mCustomerReview.setText("Average Rating: " + mProduct.getRating() + "/5.0");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Something
            }
        });


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_review_button:
                Intent makeReviewActivity = new Intent(getContext(), MakeReviewActivity.class);
                startActivity(makeReviewActivity);
                break;
        }
    }
}
