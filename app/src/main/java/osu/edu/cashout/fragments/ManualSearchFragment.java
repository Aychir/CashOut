package osu.edu.cashout.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

import osu.edu.cashout.backgroundThreads.AsyncFindProduct;
import osu.edu.cashout.R;

public class ManualSearchFragment extends Fragment implements View.OnClickListener{

    private EditText mSearchField;
    private DatabaseReference mProductsDatabase;
    private Set<String> mListOfUpcs;
    private Set<String> mListOfNames;
    private Set<String> mListOfUserScans;
    private String userId;

    private DatabaseReference mScannedDatabase;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manual_search, container, false);

        Button mSearchButton = v.findViewById(R.id.manual_search_button);
        mSearchButton.setOnClickListener(this);

        mSearchField = v.findViewById(R.id.input_upc);

        mListOfUpcs = new HashSet<>();
        mListOfNames = new HashSet<>();
        mListOfUserScans = new HashSet<>();

        mProductsDatabase = FirebaseDatabase.getInstance().getReference("products");
        mProductsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot product: dataSnapshot.getChildren()){
                    mListOfUpcs.add(product.child("upc").getValue(String.class));
                    mListOfNames.add(product.child("name").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Get the current user's id
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

        mScannedDatabase = FirebaseDatabase.getInstance().getReference("scanned-products");
        mScannedDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot scans: dataSnapshot.getChildren()){
                    if(scans.child("uid").getValue(String.class).equals(userId)){
                        mListOfUserScans.add(scans.child("upc").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }

    @Override
    public void onClick(View v){
        if(validateForm()){
            AsyncFindProduct findProduct = new AsyncFindProduct(getActivity(), mProductsDatabase, mListOfUpcs,
                    mScannedDatabase, mListOfUserScans, userId);
            findProduct.execute(mSearchField.getText().toString());
        }
    }

    private boolean validateForm(){
        boolean valid = true;
        if(mSearchField.getText().toString().isEmpty()){
            valid = false;
        }
        return valid;
    }
}
