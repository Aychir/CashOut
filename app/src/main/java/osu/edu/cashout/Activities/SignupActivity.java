package osu.edu.cashout.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import osu.edu.cashout.Fragments.SignupFragment;
import osu.edu.cashout.R;

public class SignupActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FragmentManager fm = getSupportFragmentManager();

        SignupFragment signupFragment = new SignupFragment();

        fm.beginTransaction()
                .add(R.id.fragment_container, signupFragment)
                .commit();

    }
}
