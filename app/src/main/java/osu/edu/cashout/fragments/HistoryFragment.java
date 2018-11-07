package osu.edu.cashout.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import osu.edu.cashout.HistoryAdapter;
import osu.edu.cashout.R;
import osu.edu.cashout.dataModels.Product;

public class HistoryFragment extends Fragment implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button mHistoryButton;
    private Button mScanButton;
    private Button mAccountButton;

    private FirebaseAuth mUserAuth;
    private DatabaseReference mScannedProductsReference;
    private Set<String> mScannedProducts;
    private DatabaseReference mProductsReference;
    private Set<Product> mProducts;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        mHistoryButton = v.findViewById(R.id.history_button);
        mScanButton = v.findViewById(R.id.scan_button);
        mAccountButton = v.findViewById(R.id.account_button);
        mHistoryButton.setOnClickListener(this);
        mScanButton.setOnClickListener(this);
        mAccountButton.setOnClickListener(this);

        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        mRecyclerView.hasFixedSize();
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mScannedProducts = new HashSet<>();
        mProducts = new HashSet<>();

        mUserAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mUserAuth.getCurrentUser();
        if(currentUser != null) {
            mScannedProductsReference = FirebaseDatabase.getInstance().getReference("scanned-products");
            mProductsReference = FirebaseDatabase.getInstance().getReference("products");
            //mRatingsReference = FirebaseDatabase.getInstance().getReference("reviews");

            //
            mScannedProductsReference.orderByChild("uid").equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot sp : dataSnapshot.getChildren()) {
                        mScannedProducts.add(sp.getValue(String.class));
                        mProductsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot product : dataSnapshot.getChildren()) {
                                    if (mScannedProducts.contains(product.child("upc").getValue(String.class))) {
                                        mProducts.add(product.getValue(Product.class));
                                    }
                                }
                                Product[] data = mProducts.toArray(new Product[mProducts.size()]);
                                mAdapter = new HistoryAdapter(data);
                                mRecyclerView.setAdapter(mAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //Lol
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Do something here?
                }
            });
        }

        return v;
    }

    @Override
    public void onClick(View v) {

    }
}
