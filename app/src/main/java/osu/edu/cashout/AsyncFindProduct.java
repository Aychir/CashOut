package osu.edu.cashout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParser;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import osu.edu.cashout.dataModels.Product;


public class AsyncFindProduct extends AsyncTask<String, Void, Product> {

    private static final String TAG = "AsyncFindProduct";
    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    //Maybe implement something in onPreExecute to tell the user we are searching

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

            if(connection.getResponseCode() == 200){
                Log.v(TAG, "Successful");
            }
            else{
                Log.v(TAG, "RIP");
            }
            //Create a new InputStreamReader
            InputStream streamReader = (connection.getInputStream());

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String content = br.readLine();
            System.out.println(content);

            JSONObject object = new JSONObject(content);
            Log.v(TAG, object.getString("items"));

            //We will assume that the first item in the list is accurate for now
            JSONArray array = object.getJSONArray("items");
            Log.v(TAG, array.getJSONObject(0).getString("title"));

            //Set the item from the array of results
            JSONObject firstItem = array.getJSONObject(0);

            //Set the upc of the product
            product.setUpc(params[0]);

            //Set the name of the product
            if(firstItem.getString("title") != null){
                product.setName(array.getJSONObject(0).getString("title"));
            }

            //Set the lowest recorded proce offered by the API
            if(firstItem.getDouble("lowest_recorded_price") != 0.0){
                product.setName(array.getJSONObject(0).getString("title"));
            }

            //Set the highest recorded proce offered by the API
            if(firstItem.getDouble("highest_recorded_price") != 0.0){
                product.setName(array.getJSONObject(0).getString("title"));
            }

            //Set the image for the product's thumbnail
            JSONArray imageArray = firstItem.getJSONArray("images");

            if(imageArray != null){
                try{
                    URL url = new URL(imageArray.getString(0));
                    HttpURLConnection imageConnection = (HttpURLConnection) url.openConnection();
                    imageConnection.setDoInput(true);
                    imageConnection.connect();
                    InputStream input = imageConnection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);

                    product.setImage(myBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            //TODO: Check in our database for any reviews made for the product

            //Close our InputStream and Buffered reader
            streamReader.close();
            br.close();

        } catch (Exception e){
            Log.d(TAG, "Unable to process the request");
            e.printStackTrace();
        }

        //TODO: Add the scanned product to the database under the corresponding user if the name is not null

        return product;
    }

    protected void onPostExecute(Product product){
        //TODO: Check if product name != null, if true then launch info activity, else go back to scan activity with a toast
        String name = product.getName();
        if(name != null){
            //Launch info activity
            Log.v(TAG, "Name not null");
        }
        else{
            //Not sure if necessary, if it fails then it should go back to scan activity
        }
    }


}
