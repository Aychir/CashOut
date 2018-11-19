package osu.edu.cashout.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import osu.edu.cashout.adapters.HistoryAdapter;
import osu.edu.cashout.R;
import osu.edu.cashout.activities.AccountActivity;
import osu.edu.cashout.activities.ScanActivity;
import osu.edu.cashout.dataModels.Product;

public class HistoryFragment extends Fragment implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Product[] mProductArray;
    private String mCurrentUserUID;

    private DatabaseReference mScannedProductsReference;
    private ArrayList<Product> mScannedProducts;
    private Map<String, Product> mProducts;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        Button mHistoryButton = v.findViewById(R.id.history_button);
        Button mScanButton = v.findViewById(R.id.scan_button);
        Button mAccountButton = v.findViewById(R.id.account_button);
        mHistoryButton.setOnClickListener(this);
        mScanButton.setOnClickListener(this);
        mAccountButton.setOnClickListener(this);

        mScannedProducts = new ArrayList<>();
        mProducts = new HashMap<>();

        FirebaseAuth mUserAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mUserAuth.getCurrentUser();

        if (currentUser != null) {
            mCurrentUserUID = currentUser.getUid();
            mScannedProductsReference = FirebaseDatabase.getInstance().getReference("scanned-products");
            DatabaseReference mProductsReference = FirebaseDatabase.getInstance().getReference("products");

            mProductsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot product : dataSnapshot.getChildren()) {
                        mProducts.put(product.child("upc").getValue(String.class), product.getValue(Product.class));
                    }
                    mScannedProductsReference.orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot scannedProduct : dataSnapshot.getChildren()){
                                if(scannedProduct.child("uid").getValue(String.class).equals(mCurrentUserUID)) {
                                    mScannedProducts.add(mProducts.get(scannedProduct.child("upc").getValue(String.class)));
                                }

                            }
                            //Sort the history by date
                            Collections.reverse(mScannedProducts);
                            mProductArray = mScannedProducts.toArray(new Product[mScannedProducts.size()]);
                            mRecyclerView.hasFixedSize();
                            mLayoutManager = new LinearLayoutManager(getActivity());
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            if(getActivity() != null) {
                                mAdapter = new HistoryAdapter(mProductArray, getActivity().getApplicationContext());
                            }
                            mRecyclerView.setAdapter(mAdapter);
                            DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                                    DividerItemDecoration.VERTICAL);
                            mRecyclerView.addItemDecoration(mDividerItemDecoration);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //stuff
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //stuff
                }
            });
        }
        return v;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case (R.id.scan_button):
                Intent scanIntent = new Intent(getActivity(), ScanActivity.class);
                startActivity(scanIntent);
                break;
            case (R.id.account_button):
                Intent accountIntent = new Intent(getActivity(), AccountActivity.class);
                startActivity(accountIntent);
                break;
        }
    }
}
