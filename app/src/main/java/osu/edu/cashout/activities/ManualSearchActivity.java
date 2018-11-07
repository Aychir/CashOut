package osu.edu.cashout.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import osu.edu.cashout.R;
import osu.edu.cashout.fragments.ManualSearchFragment;

public class ManualSearchActivity extends AppCompatActivity {
    private static final String TAG = "ManualSearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_search);
        setTitle("Manual Product Search");
        Log.v(TAG, "Logging onCreate() method");

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null) {
            ManualSearchFragment searchFragment = new ManualSearchFragment();

            fm.beginTransaction()
                    .add(R.id.fragment_container, searchFragment)
                    .commit();
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Read values from the "savedInstanceState"-object and put them in your textview
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the values you need from your textview into "outState"-object
        super.onSaveInstanceState(outState);
    }
}
