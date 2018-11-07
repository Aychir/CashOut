package osu.edu.cashout.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import osu.edu.cashout.fragments.AccountFragment;
import osu.edu.cashout.R;

public class AccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setTitle("Account Information");
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.fragment_container);
        if (frag == null) {
            AccountFragment accountFragment = new AccountFragment();
            fm.beginTransaction().add(R.id.fragment_container, accountFragment).commit();
        }
    }
}
