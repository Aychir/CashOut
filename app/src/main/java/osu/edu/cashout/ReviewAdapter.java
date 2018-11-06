package osu.edu.cashout;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
    private String[] mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mReviewerUsername;
        public TextView mItemName;
        public TextView mReviewRating;

        public MyViewHolder(TextView v){
            super(v);
            mReviewerUsername = v.findViewById(R.id.reviewer_username);
            mItemName = v.findViewById(R.id.item_name);
            mReviewRating = v.findViewById(R.id.review_rating);
        }
    }

    public ReviewAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.view_review_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        String str = mDataset[position];
    }

    @Override
    public int getItemCount(){
        return mDataset.length;
    }

}
