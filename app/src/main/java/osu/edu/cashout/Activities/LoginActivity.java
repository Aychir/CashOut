package osu.edu.cashout.Activities;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import osu.edu.cashout.Fragments.LoginFragment;
import osu.edu.cashout.R;

public class LoginActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentManager fm = getSupportFragmentManager();

        LoginFragment loginFragment = new LoginFragment();
        fm.beginTransaction()
                .add(R.id.fragment_container, loginFragment)
                .commit();

    }

}
