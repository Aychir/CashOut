package osu.edu.cashout.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import osu.edu.cashout.Fragments.ScanFragment;
import osu.edu.cashout.R;

@SuppressWarnings({"LogNotTimber"})
public class ScanActivity extends AppCompatActivity{

    private static final String TAG = "ScanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Log.v(TAG, "Logging onCreate() method");

        FragmentManager fm = getSupportFragmentManager();

        Fragment frag = fm.findFragmentById(R.id.fragment_container);

        if(frag == null) {
            ScanFragment scanFragment = new ScanFragment();

            fm.beginTransaction()
                    .add(R.id.fragment_container, scanFragment)
                    .commit();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        Log.v(TAG, "Logging onPause() method");
    }

    @Override
    protected void onStop(){
        super.onStop();

        Log.v(TAG, "Logging onStop() method");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        Log.v(TAG, "Logging onDestroy() method");
    }

}
