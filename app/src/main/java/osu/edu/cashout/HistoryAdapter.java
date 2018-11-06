package osu.edu.cashout;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private String[] mDataset;

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

    public HistoryAdapter(String[] myDataset) {
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
        String str = mDataset[position];
    }

    @Override
    public int getItemCount(){
        return mDataset.length;
    }

}
