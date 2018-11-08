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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Locale;

import osu.edu.cashout.R;
import osu.edu.cashout.activities.AccountActivity;
import osu.edu.cashout.activities.HistoryActivity;
import osu.edu.cashout.activities.ListReviewsActivity;
import osu.edu.cashout.activities.MakeReviewActivity;
import osu.edu.cashout.dataModels.Product;

public class InfoFragment extends Fragment implements View.OnClickListener {
    private TextView mProductTitle;
    private TextView mHighPrice;
    private TextView mLowPrice;
    private TextView mCurrentPrice;
    private TextView mCustomerReview;
    private ImageView mProductImage;
    private Product mProduct;
    private String mProductUPC;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);

        //Set onclick listeners to the buttons on the screen
        Button mReadReviewsButton = v.findViewById(R.id.read_reviews_button);
        mReadReviewsButton.setOnClickListener(this);
        Button mCreateReviewButton = v.findViewById(R.id.create_review_button);
        mCreateReviewButton.setOnClickListener(this);

        Button mHistoryButton = v.findViewById(R.id.history_button);
        mHistoryButton.setOnClickListener(this);

        Button mAccountButton = v.findViewById(R.id.account_button);
        mAccountButton.setOnClickListener(this);

        mProductTitle = v.findViewById(R.id.productTitle);
        mHighPrice = v.findViewById(R.id.highPrice);
        mLowPrice = v.findViewById(R.id.lowPrice);
        mCurrentPrice = v.findViewById(R.id.currentPrice);
        mCustomerReview = v.findViewById(R.id.customerReview);
        mProductImage = v.findViewById(R.id.item_icon);

        Bundle arguments = getArguments();
        if(arguments != null){
            mProductUPC = arguments.getString("upc");
        }
        DatabaseReference mDbReference = FirebaseDatabase.getInstance().getReference("products");
        mDbReference.orderByChild("upc").equalTo(mProductUPC).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    mProduct = data.getValue(Product.class);
                    break;
                }

                if(mProduct != null){
                    if(mProduct.getImage() != null) {
                        Picasso.get().load(mProduct.getImage()).into(mProductImage);
                    }
                    else{
                        Picasso.get().load(R.drawable.test_icon).into(mProductImage);
                    }
                    mProductTitle.setText(mProduct.getName());
                    if(mProduct.getHighestPrice() == 0.0){
                        mHighPrice.setText(R.string.no_highest_price);
                    }
                    else{
                        mHighPrice.setText(String.format(Locale.getDefault(),"Highest Price: $%.2f", mProduct.getHighestPrice()));
                    }
                    if(mProduct.getLowestPrice() == 0.0){
                        mLowPrice.setText(R.string.no_lowest_price);
                    }
                    else{
                        mLowPrice.setText(String.format(Locale.getDefault(),"Lowest Price: $%.2f", mProduct.getLowestPrice()));
                    }
                    if(mProduct.getCurrentPrice() == 0.0){
                        mCurrentPrice.setText(R.string.no_current_price);
                    }
                    else if (mProduct.getCurrentPrice() > 0.0 && mProduct.getStore() != null){
                        mCurrentPrice.setText(String.format(Locale.getDefault(),"Current Price: $%.2f", mProduct.getCurrentPrice()) + " at " + mProduct.getStore());
                    }
                    else if(mProduct.getCurrentPrice() > 0.0 && mProduct.getStore() == null){
                        mCurrentPrice.setText(String.format(Locale.getDefault(),"Current Price: $%.2f", mProduct.getCurrentPrice()));
                    }
                    if(mProduct.getRating() == 0.0){
                        mCustomerReview.setText(R.string.no_average_rating);
                    }
                    else{
                        DecimalFormat format = new DecimalFormat("#.##");
                        String formatted = format.format(mProduct.getRating());
                        mCustomerReview.setText("Average Rating: " + formatted + "/5.0");
                    }
                }

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
        Log.v("InfoFragment", v.toString() + "");
        int id = v.getId();
        switch (id) {
            case R.id.history_button:
                Log.v("InfoFragment", "in history");
                Intent historyIntent = new Intent(getContext(), HistoryActivity.class);
                startActivity(historyIntent);
                break;
            case R.id.account_button:
                Log.v("InfoFragment", "in account");
                Intent accountIntent = new Intent(getContext(), AccountActivity.class);
                startActivity(accountIntent);
                break;
            case R.id.create_review_button:
                Log.v("InfoFragment", "in create review");
                Intent makeReviewActivity = new Intent(getContext(), MakeReviewActivity.class);
                makeReviewActivity.putExtra("upc", mProductUPC);
                startActivity(makeReviewActivity);
                break;
            case R.id.read_reviews_button:
                Intent listReviewsActivity = new Intent(getContext(), ListReviewsActivity.class);
                listReviewsActivity.putExtra("upc", mProductUPC);
                startActivity(listReviewsActivity);
                break;
        }
    }
}
