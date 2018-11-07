package osu.edu.cashout;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import osu.edu.cashout.dataModels.Product;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private Product[] mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView mItemIcon;
        public TextView mItemName;
        public TextView mItemRating;

        public MyViewHolder(TextView v){
            super(v);
            mItemIcon = v.findViewById(R.id.item_icon);
            mItemName = v.findViewById(R.id.item_name);
            mItemRating = v.findViewById(R.id.item_rating);
        }
    }

    public HistoryAdapter(Product[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public HistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.view_history_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Log.v("OnBindViewHolder", holder.toString());
//        holder.mItemIcon.setImageResource(R.drawable.common_full_open_on_phone);
//        holder.mItemName.setText();
//        holder.mItemRating.setText();
    }

    @Override
    public int getItemCount(){
        return mDataset.length;
    }

}
