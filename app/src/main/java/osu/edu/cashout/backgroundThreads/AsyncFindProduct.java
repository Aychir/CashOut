package osu.edu.cashout.backgroundThreads;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import osu.edu.cashout.R;
import osu.edu.cashout.activities.ScanActivity;
import osu.edu.cashout.dataModels.Product;
import osu.edu.cashout.dataModels.ScannedProducts;
import osu.edu.cashout.fragments.ManualSearchFragment;
import osu.edu.cashout.fragments.ScanFragment;

public class AsyncFindProduct extends AsyncTask<String, Void, Product> {
    //Members of the task
    private static final String TAG = "AsyncFindProduct";
    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    private WeakReference<FragmentActivity> activityReference;
    private ProgressDialog progress;
    private DatabaseReference mReference;
    private DatabaseReference mHistory;
    private Set<String> mListOfUpcs;
    private Set<String> mListOfScans;
    private String userId;
    private Map mRatings;

    //Constructor setting all attributes necessary to save results to the necessary database tables
    public AsyncFindProduct(FragmentActivity activity, DatabaseReference reference, Set<String> listUpc,
                            DatabaseReference historyReference, Set<String> nameList, String user, Map ratings) {
        activityReference = new WeakReference<>(activity);
        progress = new ProgressDialog(activityReference.get());
        mReference = reference;
        mHistory = historyReference;
        mListOfUpcs = listUpc;
        mListOfScans = nameList;
        userId = user;
        mRatings = ratings;
    }

    @Override
    protected void onPreExecute() {
        progress.setMessage("Searching for this product...");
        progress.show();
    }

    protected Product doInBackground(String... params) {

        Product product = new Product();

        try {
            //Create the connection to the url to upcitemdb first
            URL upcItemDbUrl = new URL("https://api.upcitemdb.com/prod/trial/lookup?upc=" + params[0]);
            HttpsURLConnection connection = (HttpsURLConnection) upcItemDbUrl.openConnection();

            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            connection.connect();

            //If connection to API is successful, we should attempt to get results from the API
            if (connection.getResponseCode() == 200) {
                Log.v(TAG, "Successful");

                //Create a new InputStreamReader
                InputStream streamReader = (connection.getInputStream());

                //Get the result from the API call to a string that can be parsed
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String content = br.readLine();
                Log.v(TAG, "Content: " + content);

                //Turn the response string into a JSONObject that we may parse
                JSONObject object = new JSONObject(content);
                Log.v(TAG, "Items: " + object.getString("items") );

                //Get the array of results the API found from the UPC
                JSONArray array = object.getJSONArray("items");
                Log.v(TAG, "Title " + array.getJSONObject(0).getString("title"));

                //Only want to continue creating an object if there are results in the items array given from the scanning
                if (array.length() > 0) {
                    //Set the first item from the array of results
                    JSONObject firstItem = array.getJSONObject(0);

                    //Set the upc of the product
                    product.setUpc(params[0]);

                    //Set the name of the product if given
                    if (firstItem.has("title")) {
                        product.setName(firstItem.getString("title"));
                        Log.v(TAG, "Title set");
                    }

                    //Set the lowest recorded price offered by the API (if given)
                    if (firstItem.has("lowest_recorded_price")) {
                        product.setLowestPrice(firstItem.getDouble("lowest_recorded_price"));
                        Log.v(TAG, "Lowest Price Set " + product.getLowestPrice());
                    }

                    //Set the highest recorded price offered by the API (if given)
                    if (firstItem.has("highest_recorded_price")) {
                        product.setHighestPrice(firstItem.getDouble("highest_recorded_price"));
                        Log.v(TAG, "Highest Price Set " + firstItem.getDouble("highest_recorded_price"));
                    }

                    //Set the image for the product's thumbnail if the API offers it
                    JSONArray imageArray = firstItem.getJSONArray("images");
                    if (imageArray.length() > 0) {
                        product.setImage(imageArray.getString(0));
                    }

                    //Get the lowest price in the market currently and the store that sells it at that price
                    JSONArray merchantArray = firstItem.getJSONArray("offers");
                    if(merchantArray.length() > 0){
                        double cheapestAvailable = Double.MAX_VALUE;
                        int index = 0;
                        for(int count = 0; count < merchantArray.length(); count++){
                            JSONObject offer = merchantArray.getJSONObject(count);
                            Double temp = offer.getDouble("price");
                            if(temp != 0.0 && temp < cheapestAvailable){
                                cheapestAvailable = temp;
                                index = count;
                            }
                        }
                        product.setCurrentPrice(cheapestAvailable);
                        product.setStore(merchantArray.getJSONObject(index).getString("merchant"));
                    }

                    //Check if the product has a rating average in the database and set it
                    if(mRatings.get(product.getUpc()) != null){
                        product.setRating((Double) mRatings.get(product.getUpc()));
                        Log.v(TAG, "Rating: " + product.getRating());
                    }


                    //Add new product to the database if its UPC is not in the db and
                    // if the product has a name (verification of existence)
                    if (product.getName() != null && !mListOfUpcs.contains(product.getUpc())) {
                        mReference.push().setValue(product);
                    }

                    //User hasn't scanned this product yet, add it to the table of scanned products
                    if (!mListOfScans.contains(product.getUpc())) {
                        ScannedProducts scanned = new ScannedProducts();
                        //Receiving the date that the object was scanned on
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        String date = sdf.format(calendar.getTime());
                        scanned.setUid(userId);
                        scanned.setUpc(product.getUpc());
                        scanned.setDate(date);
                        mHistory.push().setValue(scanned);
                    }
                }
                //API did not give any results for the product
                else {
                    Log.v(TAG, "No results given by API.");
                }

                //Close our InputStream and Buffered reader
                streamReader.close();
                br.close();
            }
            //Could not establish connection to the API
            else {
                Log.v(TAG, "Could not successfully connect to the API.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return product;
    }

    protected void onPostExecute(Product product) {
        if (progress.isShowing()) {
            progress.dismiss();
        }

        FragmentActivity referencedActivity = activityReference.get();

        String name = product.getName();
        if (name != null) {
            //Launch info activity because we found a valid object
            Log.v(TAG, "Object found, launching product info");
        } else {
            //If it fails, we must launch scan activity again
            Log.v(TAG, "Object not found, relaunching the fragment the user used to find the product");
            Toast.makeText(referencedActivity.getApplicationContext(), "Couldn't find that product!",
                    Toast.LENGTH_LONG).show();

            //Find the fragment that called the asynctask and start it again
            Fragment fragment = referencedActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof ScanFragment) {
                Intent intent = new Intent(referencedActivity, ScanActivity.class);
                //We call finish instead of recreate() so that the camera can resynchronize with the new activity
                referencedActivity.finish();
                referencedActivity.startActivity(intent);
            } else if (fragment instanceof ManualSearchFragment) {
                referencedActivity.recreate();
            }
        }
    }


}
