package osu.edu.cashout.activities;

import android.os.Bundle;
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

        Log.v(TAG, "Logging onCreate() method");

        FragmentManager fm = getSupportFragmentManager();


        ManualSearchFragment searchFragment = new ManualSearchFragment();

        fm.beginTransaction()
                .add(R.id.fragment_container, searchFragment)
                .commit();

    }
}
