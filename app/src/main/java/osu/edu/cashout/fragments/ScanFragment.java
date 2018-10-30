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
import com.google.zxing.Result;

import osu.edu.cashout.AsyncFindProduct;
import osu.edu.cashout.activities.AccountActivity;
import osu.edu.cashout.activities.LoginActivity;
import osu.edu.cashout.R;


//TODO: Implementation of a button to manually type in upc code if camera does not work

@SuppressWarnings({"LogNotTimber"})
public class ScanFragment extends Fragment implements View.OnClickListener{
    private static final int CAMERA_PERMISSION = 200;

    private static final String TAG = "ScanFragment";
    private Context mContext;
    private CodeScanner mCodeScanner;

    @Override
    public void onAttach(Context c){
        super.onAttach(getContext());

        Log.v(TAG, "Logging onAttach()");

        mContext = c;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.v(TAG, "Logging onCreateView()");
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        view.findViewById(R.id.signout_button).setOnClickListener(this);
        view.findViewById(R.id.button_type_upc).setOnClickListener(this);
        view.findViewById(R.id.account_button).setOnClickListener(this);

        CodeScannerView scannerView = view.findViewById(R.id.scanner_view);

        mCodeScanner = new CodeScanner(getActivity(), scannerView);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v(TAG, result.getText());
                        AsyncFindProduct findProduct = new AsyncFindProduct(getActivity());
                        findProduct.execute(result.getText());
                        Log.v(TAG, "Logging finished asynctask?");
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
    public void onStart(){
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
                    // permission was granted, open camera preview
                    //startCameraPreview(view);
                    mCodeScanner.startPreview();
                } else {
                    // permission denied, tell the user we failed to access camera
                    Toast.makeText( mContext,"Failed to access camera",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v){
        int id = v.getId();

        if(id == R.id.signout_button){
            FirebaseAuth.getInstance().signOut();

            Intent loginIntent = new Intent(mContext, LoginActivity.class);
            startActivity(loginIntent);
        }
        else if(id == R.id.button_type_upc){
            //TODO: create an activity to type in the code
        }
        else if(id == R.id.account_button){
            Intent accountIntent = new Intent(mContext, AccountActivity.class);
            startActivity(accountIntent);
        }
    }

}
