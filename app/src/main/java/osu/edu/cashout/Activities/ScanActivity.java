package osu.edu.cashout.Activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import osu.edu.cashout.Fragments.ScanFragment;
import osu.edu.cashout.R;

public class ScanActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        FragmentManager fm = getSupportFragmentManager();
        ScanFragment scanFragment = new ScanFragment();

        fm.beginTransaction().add(R.id.fragment_container, scanFragment).commit();
    }


}
