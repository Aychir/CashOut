package osu.edu.cashout.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import osu.edu.cashout.activities.AccountActivity;
import osu.edu.cashout.activities.LoginActivity;
import osu.edu.cashout.R;
import osu.edu.cashout.activities.ScanActivity;
import osu.edu.cashout.dataModels.Product;

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
        view.findViewById(R.id.button_capture).setOnClickListener(this);
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
        else if(id == R.id.button_capture){
            Log.v(TAG, "Temporary capture button");
        }
        else if(id == R.id.account_button){
            Intent accountIntent = new Intent(mContext, AccountActivity.class);
            startActivity(accountIntent);
        }
    }


    /*
        Use of an inner class so that we may use the FragmentActivity of ScanFragment to print out
        Toast messages and to launch a new activity based on the result of the API call.
    */
    private static class AsyncFindProduct extends AsyncTask<String, Void, Product> {

        private static final String TAG = "AsyncFindProduct";
        private static final String REQUEST_METHOD = "GET";
        private static final int READ_TIMEOUT = 15000;
        private static final int CONNECTION_TIMEOUT = 15000;

        private WeakReference<FragmentActivity> activityReference;

        private ProgressDialog progress;

        AsyncFindProduct(FragmentActivity activity){
            activityReference = new WeakReference<>(activity);
            progress = new ProgressDialog(activityReference.get());
        }

        //Maybe implement something in onPreExecute to tell the user we are searching
        @Override
        protected void onPreExecute(){
            progress.setMessage("Searching for this product...");
            progress.show();
        }

        protected Product doInBackground(String... params){

            Product product = new Product();

            try{
                //Create the connection to the url to upcitemdb first

                URL upcItemDbUrl = new URL("https://api.upcitemdb.com/prod/trial/lookup?upc=" + params[0]);
                HttpsURLConnection connection = (HttpsURLConnection) upcItemDbUrl.openConnection();

                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                connection.connect();

                //If connection to API is successful, we should attempt to get results from the UPC
                if(connection.getResponseCode() == 200){
                    Log.v(TAG, "Successful");

                    //Create a new InputStreamReader
                    InputStream streamReader = (connection.getInputStream());

                    //Get the result from the API call to a string that can be parsed
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String content = br.readLine();
                    Log.v(TAG, "Content: " + content);

                    //Turn the response string into a JSONObject we may parse
                    JSONObject object = new JSONObject(content);
                    Log.v(TAG, "Items: " + object.getString("items"));

                    //Get the first result from the array of possible results
                    JSONArray array = object.getJSONArray("items");
                    Log.v(TAG, "Title " + array.getJSONObject(0).getString("title"));

                    //Only want to create an object if there are results given from the scanning, otherwise stop creation
                    if(array.length() > 0) {
                        //Set the item from the array of results
                        JSONObject firstItem = array.getJSONObject(0);

                        //Set the upc of the product
                        product.setUpc(params[0]);

                        //Set the name of the product
                        if (firstItem.has("title")) {
                            product.setName(array.getJSONObject(0).getString("title"));
                            Log.v(TAG, "Title set");
                        }

                        //Set the lowest recorded price offered by the API
                        if (firstItem.has("lowest_recorded_price")) {
                            product.setLowestPrice(firstItem.getDouble("lowest_recorded_price"));
                            Log.v(TAG, "Lowest Price Set " + product.getLowestPrice());
                        }

                        //Set the highest recorded price offered by the API
                        if (firstItem.has("highest_recorded_price")) {
                            product.setHighestPrice(firstItem.getDouble("highest_recorded_price"));
                            Log.v(TAG, "Highest Price Set " + firstItem.getDouble("highest_recorded_price"));
                        }

                        //Set the image for the product's thumbnail
                        JSONArray imageArray = firstItem.getJSONArray("images");

                        if (imageArray.length() > 0) {
                            try {
                                URL url = new URL(imageArray.getString(0));
                                HttpURLConnection imageConnection = (HttpURLConnection) url.openConnection();
                                imageConnection.setDoInput(true);
                                imageConnection.connect();
                                InputStream input = imageConnection.getInputStream();
                                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                                product.setImage(myBitmap);
                                Log.v(TAG, "Thumbnail Set " + product.getImage().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        }
                    }
                    //Could not establish connection to the API
                    else{
                        Log.v(TAG, "No results given by API.");
                    }
                    //TODO: Check in our database for any ratings made for the product and add it to the product we are creating

                    //Close our InputStream and Buffered reader
                    streamReader.close();
                    br.close();
                }
                else{
                    Log.v(TAG, "Could not successfully connect to the API.");
                }
            } catch (Exception e){
                Log.d(TAG, "Unable to process the request");
                e.printStackTrace();
            }

            //TODO: Add the scanned product to the database under the corresponding user if the name is not null

            return product;
        }

        protected void onPostExecute(Product product){
            if(progress.isShowing()){
                progress.dismiss();
            }

            FragmentActivity referencedActivity = activityReference.get();

            String name = product.getName();
            if(name != null){
                //Launch info activity because we found a valid object
                Log.v(TAG, "Object found, launching product info");
                Toast.makeText(activityReference.get(), "Name found", Toast.LENGTH_SHORT).show();
            }
            else{
                //If it fails, we must launch scan activity again
                Log.v(TAG, "Object not found, relaunching the scanner");
                Toast.makeText(referencedActivity.getApplicationContext(), "Couldn't find that product!",
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(referencedActivity, ScanActivity.class);
                //We call finish instead of recreate() so that the camera can resynchronize with the new activity
                referencedActivity.finish();
                referencedActivity.startActivity(intent);
            }
        }


    }
}
