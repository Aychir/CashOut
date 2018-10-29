package osu.edu.cashout;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParser;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

            //Set the upc of the product
            product.setUpc(params[0]);

            //If a name is found, put it into the product
            if(array.getJSONObject(0).getString("title") != null){
                product.setName(array.getJSONObject(0).getString("title"));
            }
            //If a name cannot be found, then we cannot save the object

            //Close our InputStream and Buffered reader
            streamReader.close();
            br.close();

        } catch (Exception e){
            Log.d(TAG, "Unable to process the request");
            e.printStackTrace();
        }

        return product;
    }


}
