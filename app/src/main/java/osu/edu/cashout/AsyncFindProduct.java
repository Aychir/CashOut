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

        try{
            //Create the connection to the url
            URL url = new URL("https://api.upcitemdb.com/prod/trial/lookup?upc=" + params[0]);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

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

            JsonParser parser = new JsonParser();
            Object object = parser.parse(content);
            JSONArray array = (JSONArray) object;
            Log.v(TAG, array.get(0).toString());


            //Close our InputStream and Buffered reader
            streamReader.close();

        } catch (Exception e){
            Log.d(TAG, "Unable to process the request");
            e.printStackTrace();
        }

        return new Product();
    }


}
