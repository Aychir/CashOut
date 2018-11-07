package osu.edu.cashout.fragments;

import android.content.Intent;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import osu.edu.cashout.activities.AccountActivity;
import osu.edu.cashout.activities.HistoryActivity;
import osu.edu.cashout.activities.ManualSearchActivity;
import osu.edu.cashout.backgroundThreads.AsyncFindProduct;
import osu.edu.cashout.R;

public class ManualSearchFragment extends Fragment implements View.OnClickListener {
    private EditText mSearchField;
    private DatabaseReference mProductsDatabase;
    private Set<String> mListOfUpcs;
    private Set<String> mListOfUserScans;
    private String userId;
    private DatabaseReference mScannedDatabase;
    private Map mRatingMapping;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manual_search, container, false);

        Button mSearchButton = v.findViewById(R.id.manual_search_button);
        mSearchButton.setOnClickListener(this);

        Button mHistoryButton = v.findViewById(R.id.history_button);
        mHistoryButton.setOnClickListener(this);

        Button mAccountButton = v.findViewById(R.id.account_button);
        mAccountButton.setOnClickListener(this);

        mSearchField = v.findViewById(R.id.input_upc);

        mListOfUpcs = new HashSet<>();
        mListOfUserScans = new HashSet<>();
        mRatingMapping = new HashMap();

        //The database table associated with all scanned products
        mProductsDatabase = FirebaseDatabase.getInstance().getReference("products");
        mProductsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot product : dataSnapshot.getChildren()) {
                    String upc = product.child("upc").getValue(String.class);
                    //Use this to check for uniqueness of the item being scanned (to avoid adding it again)
                    mListOfUpcs.add(upc);
                    mRatingMapping.put(upc,
                            product.child("rating").getValue(Double.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //Get the current user's id
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

        //The database table associated with the products scanned and the users that scanned them
        mScannedDatabase = FirebaseDatabase.getInstance().getReference("scanned-products");
        mScannedDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot scans : dataSnapshot.getChildren()) {
                    /*
                     * Find all children in the table with the current user's ID and
                     * put all items they scanned into a list to verify uniqueness (to
                     * avoid duplication)
                     * */
                    if (scans.child("uid").getValue(String.class).equals(userId)) {
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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.history_button) {
            Intent historyIntent = new Intent(getActivity(), HistoryActivity.class);
            historyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(historyIntent);
        } else if (id == R.id.button_type_upc) {
            if (validateForm()) {
                AsyncFindProduct findProduct = new AsyncFindProduct(getActivity(), mProductsDatabase, mListOfUpcs,
                        mScannedDatabase, mListOfUserScans, userId, mRatingMapping);
                findProduct.execute(mSearchField.getText().toString());
            }
        } else if (id == R.id.account_button) {
            Intent accountIntent = new Intent(getActivity(), AccountActivity.class);
            startActivity(accountIntent);
        }
    }


    //Validate the user has attempted to search something in the first place
    private boolean validateForm() {
        boolean valid = true;
        if (mSearchField.getText().toString().isEmpty()) {
            mSearchField.setError("You must enter a code to search.");
            valid = false;
        }
        return valid;
    }
}
