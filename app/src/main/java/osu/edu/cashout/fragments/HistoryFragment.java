package osu.edu.cashout.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import osu.edu.cashout.R;

public class HistoryFragment extends Fragment implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button mHistoryButton;
    private Button mScanButton;
    private Button mAccountButton;

    private FirebaseAuth mUserAuth;
    private DatabaseReference mDbReference;

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
//        mAdapter = new HistoryAdapter(data);
//        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onClick(View v) {

    }
}
