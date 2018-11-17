package osu.edu.cashout.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import osu.edu.cashout.R;
import osu.edu.cashout.fragments.HistoryFragment;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setTitle(R.string.historyActivity);

        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.fragment_container);
        if(frag == null){
            HistoryFragment historyFragment = new HistoryFragment();
            fm.beginTransaction().add(R.id.fragment_container, historyFragment).commit();
        }
    }
}
