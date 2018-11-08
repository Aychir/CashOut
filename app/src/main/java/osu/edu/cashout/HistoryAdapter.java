package osu.edu.cashout;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

//import osu.edu.cashout.backgroundThreads.AsyncFindImage;
import java.text.DecimalFormat;

import osu.edu.cashout.activities.InfoActivity;
import osu.edu.cashout.dataModels.Product;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private Product[] mDataset;
    private Context mContext;

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mItemIcon;
        public TextView mItemName;
        public TextView mItemRating;

        private Context mContext;
        private Product[] mProductList;

        public MyViewHolder(View v, Product[] list, Context context){
            super(v);
            mItemIcon = v.findViewById(R.id.item_icon);
            mItemName = v.findViewById(R.id.item_name);
            mItemRating = v.findViewById(R.id.item_rating);

            mProductList = list;
            mContext = context;

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            Intent infoIntent = new Intent(mContext, InfoActivity.class);
            Product product = mProductList[getAdapterPosition()];
            infoIntent.putExtra("upc", product.getUpc());
            mContext.startActivity(infoIntent);
        }
    }

    public HistoryAdapter(Product[] myDataset, Context c) {
        mDataset = myDataset;
        mContext = c;
    }

    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Log.v("HistoryAdapter: ", "In onCreateVH");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_history_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v, mDataset, mContext);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Log.v("OnBindViewHolder", holder.toString());
        if(mDataset[position].getImage() != null) {
            Picasso.get().load(mDataset[position].getImage()).fit().into(holder.mItemIcon);
        }
        else {
            holder.mItemIcon.setImageResource(R.drawable.test_icon);
        }
        holder.mItemName.setText(mDataset[position].getName());
        if(mDataset[position].getRating() == 0.0){
            holder.mItemRating.setText("Average Rating: N/A");
        }
        else{
            DecimalFormat format = new DecimalFormat("#.##");
            String formatted = format.format(mDataset[position].getRating());
            holder.mItemRating.setText("Average Rating: " + formatted + "/5.0");
        }
    }

    @Override
    public int getItemCount(){
        return mDataset.length;
    }

}
