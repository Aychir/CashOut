package osu.edu.cashout.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.HashSet;
import java.util.Set;

import osu.edu.cashout.backgroundThreads.AsyncFindProduct;
import osu.edu.cashout.activities.AccountActivity;
import osu.edu.cashout.activities.LoginActivity;
import osu.edu.cashout.R;
import osu.edu.cashout.activities.ManualSearchActivity;

//TODO: Check what happens when a user denies access to camera

//TODO: Abstract the database stuff out between here and manual search

@SuppressWarnings({"LogNotTimber"})
public class ScanFragment extends Fragment implements View.OnClickListener {
    private static final int CAMERA_PERMISSION = 200;

    private static final String TAG = "ScanFragment";
    private Context mContext;
    private CodeScanner mCodeScanner;
    private DatabaseReference mProductsDatabase;
    private Set<String> mListOfUpcs;
    private DatabaseReference mScannedDatabase;
    private String userId;
    private Set<String> mListOfUserScans;

    @Override
    public void onAttach(Context c) {
        super.onAttach(getContext());

        Log.v(TAG, "Logging onAttach()");

        mContext = c;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "Logging onCreateView()");
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        view.findViewById(R.id.signout_button).setOnClickListener(this);
        view.findViewById(R.id.button_type_upc).setOnClickListener(this);
        view.findViewById(R.id.account_button).setOnClickListener(this);

        mListOfUpcs = new HashSet<>();
        mListOfUserScans = new HashSet<>();

        //The database table associated with all scanned products
        mProductsDatabase = FirebaseDatabase.getInstance().getReference("products");
        mProductsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot product : dataSnapshot.getChildren()) {
                    //Use this to check for uniqueness of the item being scanned (to avoid adding it again)
                    mListOfUpcs.add(product.child("upc").getValue(String.class));
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

        //Code necessary to use the barcode scanning API
        CodeScannerView scannerView = view.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(getActivity(), scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v(TAG, result.getText());
                        AsyncFindProduct findProduct = new AsyncFindProduct(getActivity(), mProductsDatabase,
                                mListOfUpcs, mScannedDatabase, mListOfUserScans, userId);
                        findProduct.execute(result.getText());
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Logging onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "Logging onResume()");
        if (getActivity() != null) {
            //In the case that the user hasn't permitted the camera at this point
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION);

            }
            //If permission was granted then start the preview of the scanner
            else {
                mCodeScanner.startPreview();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Logging onPause()");
        mCodeScanner.releaseResources();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Logging onStop()");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, open scanner preview
                    mCodeScanner.startPreview();
                } else {
                    // permission denied, tell the user we failed to access camera
                    Toast.makeText(mContext, "Failed to access camera",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.signout_button) {
            FirebaseAuth.getInstance().signOut();

            Intent loginIntent = new Intent(mContext, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
        } else if (id == R.id.button_type_upc) {
            Intent manualIntent = new Intent(mContext, ManualSearchActivity.class);
            startActivity(manualIntent);
        } else if (id == R.id.account_button) {
            Intent accountIntent = new Intent(mContext, AccountActivity.class);
            startActivity(accountIntent);
        }
    }

}
