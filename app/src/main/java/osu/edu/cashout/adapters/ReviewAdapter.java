package osu.edu.cashout.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Map;

import osu.edu.cashout.R;
import osu.edu.cashout.activities.ReviewDetailActivity;
import osu.edu.cashout.dataModels.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
    private Review[] mDataset;
    private Map<String, String> mUIDToUsername;
    private Context mContext;

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mReviewerUsername;
        TextView mReviewTitle;
        TextView mReviewRating;

        private Context mContext;
        private Review[] mReviewList;
        private Map<String, String> mUidToUsername;

         MyViewHolder(View v, Review[] reviews, Map<String, String> idToUsername, Context context){
            super(v);
            mReviewerUsername = v.findViewById(R.id.reviewer_username);
            mReviewTitle = v.findViewById(R.id.review_title);
            mReviewRating = v.findViewById(R.id.review_rating);

            mReviewList = reviews;
            mUidToUsername = idToUsername;
            mContext = context;

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            Intent reviewIntent = new Intent(mContext, ReviewDetailActivity.class);
            Review review = mReviewList[getAdapterPosition()];
            reviewIntent.putExtra("rating", review.getScore());
            reviewIntent.putExtra("title", review.getTitle());
            reviewIntent.putExtra("username", mUidToUsername.get(review.getUserId()));
            String desc;
            if(review.getDescription() != null){
                desc = review.getDescription();
            }
            else{
                desc = "";
            }
            reviewIntent.putExtra("description", desc);
            reviewIntent.putExtra("upc", review.getUpc());
            mContext.startActivity(reviewIntent);
        }

    }

    public ReviewAdapter(Review[] myDataset, Map<String, String> idToUsername, Context context) {
        mDataset = myDataset;
        mUIDToUsername = idToUsername;
        mContext = context;
    }

    @NonNull
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_review_item, parent, false);
        return new MyViewHolder(v, mDataset, mUIDToUsername, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position){
        holder.mReviewerUsername.setText(mUIDToUsername.get(mDataset[position].getUserId()));
        holder.mReviewTitle.setText(mContext.getString(R.string.productTitle, mDataset[position].getTitle()));
        if(mDataset[position].getScore() == 0.0){
            holder.mReviewRating.setText(R.string.no_average_rating);
        }
        else{
            DecimalFormat format = new DecimalFormat("#.##");
            String formatted = format.format(mDataset[position].getScore());
            holder.mReviewRating.setText(mContext.getString(R.string.given_rating, formatted));
        }
    }

    @Override
    public int getItemCount(){
        return mDataset.length;
    }

}
