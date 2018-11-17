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

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import osu.edu.cashout.R;
import osu.edu.cashout.activities.AccountActivity;
import osu.edu.cashout.activities.HistoryActivity;
import osu.edu.cashout.activities.ListReviewsActivity;
import osu.edu.cashout.activities.MakeReviewActivity;
import osu.edu.cashout.dataModels.Product;

public class InfoFragment extends Fragment implements View.OnClickListener {
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

        TextView mProductTitle = v.findViewById(R.id.productTitle);
        TextView mHighPrice = v.findViewById(R.id.highPrice);
        TextView mLowPrice = v.findViewById(R.id.lowPrice);
        TextView  mCurrentPrice = v.findViewById(R.id.currentPrice);
        TextView mCustomerReview = v.findViewById(R.id.customerReview);
        ImageView mProductImage = v.findViewById(R.id.item_icon);

        Bundle arguments = getArguments();
        if(arguments != null){
            mProductUPC = arguments.getString("upc");
            mProduct = new Product();
            mProduct.setUpc(arguments.getString("upc"));
            mProduct.setName(arguments.getString("name"));
            mProduct.setStore(arguments.getString("merchant"));
            mProduct.setImage(arguments.getString("image"));
            mProduct.setRating(arguments.getDouble("rating"));
            mProduct.setCurrentPrice(arguments.getDouble("current"));
            mProduct.setLowestPrice(arguments.getDouble("lowest"));
            mProduct.setHighestPrice(arguments.getDouble("highest"));
        }

        if(mProduct != null && getContext() != null){
            if(mProduct.getImage() != null) {
                Picasso.get().load(mProduct.getImage()).into(mProductImage);
            }
            else{
                Picasso.get().load(R.drawable.no_image).into(mProductImage);
            }
            mProductTitle.setText(mProduct.getName());
            if(mProduct.getHighestPrice() == 0.0){
                mHighPrice.setText(R.string.no_highest_price);
            }
            else{
                mHighPrice.setText(getContext().getString(R.string.highest_price, mProduct.getHighestPrice()));
            }
            if(mProduct.getLowestPrice() == 0.0){
                mLowPrice.setText(R.string.no_lowest_price);
            }
            else{
                mLowPrice.setText(getContext().getString(R.string.lowest_price, mProduct.getLowestPrice()));
            }
            if(mProduct.getCurrentPrice() == 0.0){
                mCurrentPrice.setText(R.string.no_current_price);
            }
            else if (mProduct.getCurrentPrice() > 0.0 && mProduct.getStore() != null){
                mCurrentPrice.setText(getContext().getString(R.string.current_price_with_merchant, mProduct.getCurrentPrice(), mProduct.getStore()));
            }
            else if(mProduct.getCurrentPrice() > 0.0 && mProduct.getStore() == null){
                mCurrentPrice.setText(getContext().getString(R.string.current_price_no_merchant, mProduct.getCurrentPrice()));
            }
            if(mProduct.getRating() == 0.0){
                mCustomerReview.setText(R.string.no_average_rating);
            }
            else{
                DecimalFormat format = new DecimalFormat("#.##");
                String formatted = format.format(mProduct.getRating());
                mCustomerReview.setText(getContext().getString(R.string.average_rating, formatted));
            }
        }


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
